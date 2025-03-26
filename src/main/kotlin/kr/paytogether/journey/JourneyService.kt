package kr.paytogether.journey

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kr.paytogether.journey.dto.*
import kr.paytogether.journey.entity.JourneySettlement
import kr.paytogether.journey.projection.JourneyLedgerSumProjection
import kr.paytogether.journey.repository.*
import kr.paytogether.shared.exception.NotFoundException
import kr.paytogether.shared.utils.isZero
import java.math.BigDecimal
import java.security.MessageDigest
import java.time.Instant
import java.time.LocalDateTime

@Service
class JourneyService(
    private val journeyRepository: JourneyRepository,
    private val journeyMemberRepository: JourneyMemberRepository,
    private val journeyExpenseRepository: JourneyExpenseRepository,
    private val journeySettlementRepository: JourneySettlementRepository,
    private val journeyMemberLedgerRepository: JourneyMemberLedgerRepository,
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
    suspend fun getJourney(journeyId: String): JourneyResponse {
        val journey = journeyRepository.findByJourneyId(journeyId) ?: throw NotFoundException("Journey not found by journeyId: $journeyId")
        val members = journeyMemberRepository.findByJourneyId(journey.journeyId).map { JourneyMemberResponse.from(it) }.toList()
        return JourneyResponse.of(journey, members)
    }

    @Transactional(readOnly = true)
    suspend fun getJourneys(journeyIds: List<String>): List<JourneyResponse> {
        val journeys = journeyRepository.findByJourneyIdIn(journeyIds)
        val memberMap = journeyMemberRepository.findByJourneyIdIn(journeyIds).groupBy { it.journeyId }

        return journeys.map { journey ->
            val members = memberMap[journey.journeyId]?.map { JourneyMemberResponse.from(it) } ?: emptyList()
            JourneyResponse.of(journey, members)
        }
    }

    @Transactional
    suspend fun closeJourney(journeyId: String) {
        val journey = journeyRepository.findByJourneyId(journeyId) ?: throw NotFoundException("Journey not found by journeyId: $journeyId")
        val expense = journeyExpenseRepository.findByJourneyId(journey.journeyId).toList()
        val members = journeyMemberRepository.findByJourneyId(journey.journeyId)
        val ledgers = journeyMemberLedgerRepository.findJourneyLedgerSum(journey.journeyId).map { JourneyLedgerSum.from(it) }

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
        journeyRepository.save(journey.copy(closedAt = LocalDateTime.now()))
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