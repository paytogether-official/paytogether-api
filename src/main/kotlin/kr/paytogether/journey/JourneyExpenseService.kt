package kr.paytogether.journey

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kr.paytogether.journey.dto.JourneyExpenseCreate
import kr.paytogether.journey.dto.JourneyExpenseResponse
import kr.paytogether.journey.dto.JourneyExpenseUpdate
import kr.paytogether.journey.dto.JourneyExpenseWithMembersResponse
import kr.paytogether.journey.entity.JourneyMemberLedger
import kr.paytogether.journey.enums.Category
import kr.paytogether.journey.repository.JourneyExpenseRepository
import kr.paytogether.journey.repository.JourneyMemberLedgerRepository
import kr.paytogether.journey.repository.JourneyMemberRepository
import kr.paytogether.journey.repository.JourneyRepository
import kr.paytogether.shared.exception.BadRequestException
import kr.paytogether.shared.exception.ErrorCode
import kr.paytogether.shared.exception.NotFoundException
import kr.paytogether.shared.utils.notEqIgnoreScale
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class JourneyExpenseService(
    private val journeyRepository: JourneyRepository,
    private val journeyMemberRepository: JourneyMemberRepository,
    private val journeyExpenseRepository: JourneyExpenseRepository,
    private val journeyMemberLedgerRepository: JourneyMemberLedgerRepository,
) {

    @Transactional
    suspend fun createExpense(journeyId: String, create: JourneyExpenseCreate): JourneyExpenseResponse {
        val journey = journeyRepository.findByJourneyId(journeyId) ?: throw NotFoundException("Journey not found by id: $journeyId")
        if (journey.closedAt != null) {
            throw BadRequestException(ErrorCode.VALIDATION_ERROR, "Journey is already closed: $journeyId")
        }

        val memberMap = journeyMemberRepository.findByJourneyId(journeyId).associateBy { it.name }
        val payer = memberMap[create.payerName] ?: throw NotFoundException("Payer not found by name: ${create.payerName}")
        require(payer.journeyMemberId != null) { "Payer id is null" }

        val expense = journeyExpenseRepository.save(create.toEntity(journeyId, journey.baseCurrency, payer.journeyMemberId))
        require(expense.journeyExpenseId != null) { "Expense id is null" }

        val ledgers = listOf(
            JourneyMemberLedger(
                journeyId = journeyId,
                journeyMemberId = payer.journeyMemberId,
                journeyExpenseId = expense.journeyExpenseId,
                amount = expense.amount,
                note = "${payer.name} paid",
            ),
        ) + create.members.map {
            val member = memberMap[it.name] ?: throw NotFoundException("Member not found by name: ${it.name}")
            JourneyMemberLedger(
                journeyId = journeyId,
                journeyMemberId = member.journeyMemberId!!,
                journeyExpenseId = expense.journeyExpenseId,
                amount = -it.amount,
                note = "${payer.name} paid, Share of expense",
            )
        }

        journeyMemberLedgerRepository.saveAll(ledgers).collect()

        return JourneyExpenseResponse.of(expense, payer.name)
    }

    @Transactional(readOnly = true)
    suspend fun getExpenses(
        journeyId: String,
        quoteCurrency: String,
        category: String?,
        expenseDate: String?, // yyyy-MM-dd | "OTHER"
        pageable: Pageable,
    ): Flow<JourneyExpenseWithMembersResponse> {

        val memberMap = journeyMemberRepository.findByJourneyId(journeyId).associateBy { it.journeyMemberId }
        val ledgerMap = journeyMemberLedgerRepository.findByJourneyIdAndDeletedAtIsNull(journeyId)
            .groupBy { it.journeyExpenseId }

        val journey = journeyRepository.findByJourneyId(journeyId) ?: throw NotFoundException("Journey not found by id: $journeyId")

        return journeyExpenseRepository.findByJourneyIdAndDeletedAtIsNull(journeyId, category, pageable)
            .filter { category == null || it.category == category }
            .filter {
                expenseDate == null || it.expenseDate.toString() == expenseDate || (expenseDate == "OTHER" && (journey.startDate..journey.endDate).contains(
                    it.expenseDate
                ).not())
            }
            .map {
                JourneyExpenseWithMembersResponse.of(
                    expense = it,
                    quoteCurrency = quoteCurrency,
                    exchangeRate = when {
                        journey.baseCurrency == quoteCurrency -> BigDecimal.ONE
                        else -> journey.exchangeRate
                    },
                    payerName = memberMap[it.expensePayerId]?.name ?: throw NotFoundException("Payer not found by id: ${it.expensePayerId}"),
                    members = ledgerMap[it.journeyExpenseId]?.filter { ledger -> ledger.amount < BigDecimal.ZERO }
                        ?.map { ledger ->
                            JourneyExpenseWithMembersResponse.JourneyExpenseMemberResponse.of(
                                journeyMemberId = ledger.journeyMemberId,
                                name = memberMap[ledger.journeyMemberId]?.name
                                    ?: throw NotFoundException("Member not found by id: ${ledger.journeyMemberId}"),
                                amount = (ledger.amount * (if (journey.baseCurrency == quoteCurrency) BigDecimal.ONE else journey.exchangeRate)).negate()
                                    .setScale(
                                        2,
                                        RoundingMode.HALF_UP
                                    ),
                            )
                        } ?: emptyList()
                )
            }
    }

    @Transactional(readOnly = true)
    suspend fun getExpense(journeyId: String, journeyExpenseId: Long, quoteCurrency: String): JourneyExpenseWithMembersResponse {
        val expense = journeyExpenseRepository.findByJourneyIdAndJourneyExpenseIdAndDeletedAtIsNull(journeyId, journeyExpenseId)
            ?: throw NotFoundException("Expense not found by id: $journeyExpenseId")

        require(expense.journeyExpenseId != null) { "Expense id is null" }
        val ledgers = journeyMemberLedgerRepository.findByJourneyExpenseIdAndDeletedAtIsNull(expense.journeyExpenseId)
        val memberMap = journeyMemberRepository.findByJourneyId(journeyId).associateBy { it.journeyMemberId }

        val journey = journeyRepository.findByJourneyId(journeyId) ?: throw NotFoundException("Journey not found by id: $journeyId")

        return JourneyExpenseWithMembersResponse.of(
            expense = expense,
            quoteCurrency = quoteCurrency,
            exchangeRate = when {
                journey.baseCurrency == quoteCurrency -> BigDecimal.ONE
                else -> journey.exchangeRate
            },
            payerName = memberMap[expense.expensePayerId]?.name ?: throw NotFoundException("Payer not found by id: ${expense.expensePayerId}"),
            members = ledgers
                .filter { it.amount < BigDecimal.ZERO }
                .map {
                    JourneyExpenseWithMembersResponse.JourneyExpenseMemberResponse.of(
                        journeyMemberId = it.journeyMemberId,
                        name = memberMap[it.journeyMemberId]?.name ?: throw NotFoundException("Member not found by id: ${it.journeyMemberId}"),
                        amount = (it.amount * (if (journey.baseCurrency == quoteCurrency) BigDecimal.ONE else journey.exchangeRate)).negate()
                            .setScale(2, RoundingMode.HALF_UP),
                    )
                }
        )
    }

    @Transactional
    suspend fun updateExpense(journeyId: String, expenseId: Long, update: JourneyExpenseUpdate): JourneyExpenseResponse {
        val expense = journeyExpenseRepository.findByJourneyIdAndJourneyExpenseIdAndDeletedAtIsNull(journeyId, expenseId)
            ?: throw NotFoundException("Expense not found by id: $expenseId")
        require(expense.journeyExpenseId != null) { "Expense id is null" }
        journeyMemberLedgerRepository.deleteByJourneyExpenseId(expense.journeyExpenseId)

        val members = journeyMemberRepository.findByJourneyId(journeyId)
        val memberMap = members.associateBy { it.name }
        val payer = memberMap[update.payerName ?: members.find { it.journeyMemberId == expense.expensePayerId }?.name]
            ?: throw NotFoundException("Payer not found by name: ${update.payerName}")
        require(payer.journeyMemberId != null) { "Payer id is null" }

        // 요청 받은 금액과 멤버 금액 합계가 다름
        if (update.amount != null && update.amount notEqIgnoreScale update.members.sumOf { it.amount })
            throw BadRequestException(
                ErrorCode.VALIDATION_ERROR,
                "Amount is not matched, expected: ${update.amount}, actual: ${update.members.sumOf { it.amount }}"
            )

        val updatedExpense = journeyExpenseRepository.save(
            expense.copy(
                expensePayerId = payer.journeyMemberId,
                category = update.category ?: expense.category,
                expenseDate = update.expenseDate ?: expense.expenseDate,
                amount = update.amount ?: expense.amount,
                memo = update.memo ?: expense.memo,
            )
        )

        require(updatedExpense.journeyExpenseId != null) { "Expense id is null" }

        val ledgers = listOf(
            JourneyMemberLedger(
                journeyId = journeyId,
                journeyMemberId = payer.journeyMemberId,
                journeyExpenseId = updatedExpense.journeyExpenseId,
                amount = updatedExpense.amount,
                note = "${payer.name} paid",
            ),
        ) + update.members.map {
            val member = memberMap[it.name] ?: throw NotFoundException("Member not found by name: ${it.name}")
            JourneyMemberLedger(
                journeyId = journeyId,
                journeyMemberId = member.journeyMemberId!!,
                journeyExpenseId = updatedExpense.journeyExpenseId,
                amount = -it.amount,
                note = "${payer.name} paid, Share of expense",
            )
        }

        journeyMemberLedgerRepository.saveAll(ledgers).collect()

        return JourneyExpenseResponse.of(updatedExpense, payer.name)
    }

    @Transactional
    suspend fun deleteExpense(journeyId: String, journeyExpenseId: Long) {
        val expense = journeyExpenseRepository.findByJourneyIdAndJourneyExpenseIdAndDeletedAtIsNull(journeyId, journeyExpenseId)
            ?: throw NotFoundException("Expense not found by id: $journeyExpenseId")
        require(expense.journeyExpenseId != null) { "Expense id is null" }
        journeyMemberLedgerRepository.deleteByJourneyExpenseId(expense.journeyExpenseId)
        journeyExpenseRepository.deleteById(expense.journeyExpenseId)
    }

    @Transactional(readOnly = true)
    suspend fun getExpenseCategories(journeyId: String): List<String> =
        journeyExpenseRepository.findDistinctByJourneyIdAndDeletedAtIsNull(journeyId)
            .map { Category.fromValue(it) }
            .sortedBy { it.sort }
            .map { it.value }
}