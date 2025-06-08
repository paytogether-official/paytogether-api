package kr.paytogether.journey

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kr.paytogether.journey.dto.*
import kr.paytogether.journey.entity.JourneySettlement
import kr.paytogether.journey.projection.JourneyLedgerSumProjection
import kr.paytogether.journey.repository.*
import kr.paytogether.shared.exception.BadRequestException
import kr.paytogether.shared.exception.ErrorCode
import kr.paytogether.shared.exception.ErrorCode.DUPLICATE
import kr.paytogether.shared.exception.NotFoundException
import kr.paytogether.shared.utils.isZero
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.security.MessageDigest
import java.time.Instant

@Service
class JourneyService(
    private val journeyRepository: JourneyRepository,
    private val journeyMemberRepository: JourneyMemberRepository,
    private val journeySettlementRepository: JourneySettlementRepository,
    private val journeyMemberLedgerRepository: JourneyMemberLedgerRepository,
    private val journeyExpenseRepository: JourneyExpenseRepository,
) {

    @Transactional
    suspend fun createJourney(create: JourneyCreate): JourneyResponse {
        val journeyId = generateSlug()
        journeyRepository.create(create.toEntity(journeyId))
        val journey = journeyRepository.findByJourneyId(journeyId) ?: throw NotFoundException("Journey not found by journeyId: $journeyId")
        val members = create.members.map { it.toEntity(journey.journeyId) }.let {
            journeyMemberRepository.saveAll(it).map { member -> JourneyMemberResponse.from(member) }.toList()
        }

        return JourneyResponse.of(journey, members)
    }

    @Transactional(readOnly = true)
    suspend fun getJourney(journeyId: String, quoteCurrency: String): JourneyResponse {
        val journey = journeyRepository.findByJourneyId(journeyId) ?: throw NotFoundException("Journey not found by journeyId: $journeyId")
        val members = journeyMemberRepository.findByJourneyId(journey.journeyId).map { JourneyMemberResponse.from(it) }.toList()
        val (totalExpenseAmount, totalExpenseCount) = journeyExpenseRepository.findByJourneyIdAndDeletedAtIsNull(journey.journeyId).toList()
            .let { expenses ->
                val exchangeRate = when {
                    journey.baseCurrency == quoteCurrency -> BigDecimal.ONE
                    else -> journey.exchangeRate
                }
                Pair(expenses.sumOf { it.amount } * exchangeRate, expenses.size)
            }
        return JourneyResponse.of(journey, members, totalExpenseAmount, totalExpenseCount)
    }

    @Transactional(readOnly = true)
    suspend fun getJourneys(journeyIds: List<String>): List<JourneyResponse> {
        val journeys = journeyRepository.findByJourneyIdIn(journeyIds)
        val memberMap = journeyMemberRepository.findByJourneyIdIn(journeyIds).groupBy { it.journeyId }
            .mapValues { (_, members) -> members.map { JourneyMemberResponse.from(it) } }
        val journeyExpenseMap = journeyExpenseRepository.findByJourneyIdInAndDeletedAtIsNull(journeyIds)
            .groupBy { it.journeyId }

        return journeys.map { journey ->
            val members = memberMap[journey.journeyId] ?: emptyList()
            val expenses = journeyExpenseMap[journey.journeyId] ?: emptyList()
            JourneyResponse.of(journey, members, expenses.sumOf { it.amount }, expenses.size)
        }
    }

    @Transactional
    suspend fun updateJourney(journeyId: String, update: JourneyUpdate): JourneyResponse {
        val journey = journeyRepository.findByJourneyId(journeyId) ?: throw NotFoundException("Journey not found by journeyId: $journeyId")
        if (journey.closedAt != null) {
            throw BadRequestException(ErrorCode.VALIDATION_ERROR, "Journey already closed for journeyId: $journeyId")
        }

        val memberNameSet = journeyMemberRepository.findByJourneyId(journey.journeyId)
            .map { it.name }
            .toSet()

        update.members
            .filterNot { memberNameSet.contains(it.name) }
            .map { it.toEntity(journeyId) }
            .let { journeyMemberRepository.saveAll(it) }
            .collect()

        return JourneyResponse.of(
            journeyRepository.save(journey.copy(startDate = update.startDate, endDate = update.endDate)),
            journeyMemberRepository.findByJourneyId(journey.journeyId)
                .map { JourneyMemberResponse.from(it) }
        )
    }

    @Transactional
    suspend fun closeJourney(journeyId: String) {
        val journey = journeyRepository.findByJourneyId(journeyId) ?: throw NotFoundException("Journey not found by journeyId: $journeyId")
        if (journeySettlementRepository.existsByJourneyId(journeyId)) {
            throw BadRequestException(DUPLICATE, "Journey already settled for journeyId: $journeyId")
        }
        val totalRemainingAmount = journeyExpenseRepository.findByJourneyIdAndDeletedAtIsNull(journeyId).toList().sumOf { it.remainingAmount }
        val members = journeyMemberRepository.findByJourneyId(journey.journeyId)
        val ledgers = journeyMemberLedgerRepository.findJourneyLedgerSum(journey.journeyId)
            .map { JourneyLedgerSum.from(it) }
            .toMutableList()
            .apply {
                addAll(members.map {
                    JourneyLedgerSum(
                        it.journeyMemberId!!,
                        totalRemainingAmount.divide(members.size.toBigDecimal(), 2, RoundingMode.FLOOR).negate(),
                    )
                })
                add(
                    JourneyLedgerSum(
                        members.first().journeyMemberId!!,
                        totalRemainingAmount - totalRemainingAmount.divide(members.size.toBigDecimal(), 2, RoundingMode.FLOOR).negate()
                    )
                )
            }
            .groupBy { it.journeyMemberId }
            .map { (journeyMemberId, ledgers) ->
                JourneyLedgerSum(
                    journeyMemberId = journeyMemberId,
                    amount = ledgers.sumOf { it.amount }
                )
            }

        // 양수면 받게될 금액(채권), 음수면 지불해야할 금액(채무)
        val creditors = ledgers.filter { it.amount > BigDecimal.ZERO }.sortedByDescending { it.amount }.toMutableList()
        val debtors = ledgers.filter { it.amount < BigDecimal.ZERO }.sortedBy { it.amount }.toMutableList()

        val settlements = mutableListOf<JourneySettlement>()
        var debtorIndex = 0
        var creditorIndex = 0

        // 방어코드
        var MAX_LOOP = 1000

        while (debtorIndex < debtors.size && creditorIndex < creditors.size) {
            val debtor = debtors[debtorIndex]
            val creditor = creditors[creditorIndex]

            // 정산 금액은 채무자 절대값과 채권자 금액 중 작은 값
            val settlementAmount = minOf(-debtor.amount, creditor.amount)
            settlements.add(
                JourneySettlement(
                    journeyId = journey.journeyId,
                    fromMemberId = debtor.journeyMemberId,
                    toMemberId = creditor.journeyMemberId,
                    amount = settlementAmount
                )
            )

            // 각 잔액 업데이트
            debtor.amount += settlementAmount  // 채무 감소
            creditor.amount -= settlementAmount  // 채권 감소

            // 채무가 모두 정산되었으면 다음 채무자로 이동
            if (debtor.amount.isZero()) {
                debtorIndex++
            }
            // 채권이 모두 정산되었으면 다음 채권자로 이동
            if (creditor.amount.isZero()) {
                creditorIndex++
            }

            // 방어코드
            if (MAX_LOOP-- <= 0) {
                throw IllegalStateException("Infinite loop detected")
            }
        }

        // 정산 내역 저장
        journeySettlementRepository.saveAll(settlements).collect()

        // 여정 종료
        journeyRepository.save(journey.copy(closedAt = Instant.now()))
    }

    @Transactional
    suspend fun reopen(journeyId: String) {
        val journey = journeyRepository.findByJourneyId(journeyId) ?: throw NotFoundException("Journey not found by journeyId: $journeyId")
        if (journey.closedAt == null) {
            throw BadRequestException(ErrorCode.VALIDATION_ERROR, "Journey already opened for journeyId: $journeyId")
        }
        journeySettlementRepository.deleteByJourneyId(journeyId)
        journeyRepository.save(journey.copy(closedAt = null))
    }

    suspend fun getSettlement(journeyId: String): JourneySettlementResultResponse {
        val settlement = journeySettlementRepository.findByJourneyId(journeyId)
        val memberMap = journeyMemberRepository.findByJourneyId(journeyId).associateBy { it.journeyMemberId }
        val expenses = journeyExpenseRepository.findByJourneyIdAndDeletedAtIsNull(journeyId)
        val totalAmount = expenses.toList().sumOf { it.amount }
        val memberExpenseMap = expenses.toList()
            .groupBy { it.expensePayerId }
            .map { (expensePayerId, expenses) ->
                JourneyLedgerSum(
                    journeyMemberId = expensePayerId,
                    amount = expenses.sumOf { it.amount }
                )
            }
            .associateBy { it.journeyMemberId }
        return JourneySettlementResultResponse.of(
            journeyId = journeyId,
            settlements = settlement.map {
                JourneySettlementResponse.of(
                    settlement = it,
                    fromMemberName = memberMap[it.fromMemberId]?.name
                        ?: throw NotFoundException("Member not found by memberId: ${it.fromMemberId}"),
                    toMemberName = memberMap[it.toMemberId]?.name
                        ?: throw NotFoundException("Member not found by memberId: ${it.toMemberId}")
                )
            },
            expenseCategories = expenses.toList().groupBy { it.category }
                .map { (category, expenses) ->
                    val amount = expenses.sumOf { it.amount }
                    val percentage = amount.divide(totalAmount, 4, RoundingMode.HALF_UP)
                    ExpenseCategoryResponse.of(
                        name = category,
                        amount = amount,
                        percentage = percentage.multiply(BigDecimal(100))
                    )
                }
                .sortedByDescending { it.percentage },
            memberExpenses = memberMap.values.map {
                val expense = memberExpenseMap[it.journeyMemberId]
                MemberExpenseResponse.of(
                    name = it.name,
                    amount = expense?.amount ?: BigDecimal.ZERO
                )
            }.sortedByDescending { it.amount }
        )
    }

    private suspend fun generateSlug(): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hashBytes = md.digest(Instant.now().toEpochMilli().toString().toByteArray())
        val hashHex = hashBytes.joinToString("") { "%02x".format(it) }
        val slug = hashHex.substring(0, 8)
        return if (journeyRepository.existsByJourneyId(slug)) {
            generateSlug()
        } else {
            slug
        }
    }

    private data class JourneyLedgerSum(
        val journeyMemberId: Long,
        var amount: BigDecimal,
    ) {
        companion object {
            fun from(projection: JourneyLedgerSumProjection): JourneyLedgerSum =
                JourneyLedgerSum(
                    journeyMemberId = projection.journeyMemberId,
                    amount = projection.amount
                )
        }
    }
}