package kr.paytogether.journey

import kotlinx.coroutines.flow.collect
import kr.paytogether.journey.dto.*
import kr.paytogether.journey.entity.JourneyMemberLedger
import kr.paytogether.journey.repository.*
import kr.paytogether.shared.exception.BadRequestException
import kr.paytogether.shared.exception.ErrorCode
import kr.paytogether.shared.exception.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class JourneyExpenseService(
    private val journeyRepository: JourneyRepository,
    private val journeyMemberRepository: JourneyMemberRepository,
    private val journeyExpenseRepository: JourneyExpenseRepository,
    private val journeyMemberLedgerRepository: JourneyMemberLedgerRepository,
) {

    @Transactional
    suspend fun createExpense(journeyId: String, create: JourneyExpenseCreate): JourneyExpenseResponse {
        if (journeyRepository.existsByJourneyId(journeyId).not()) {
            throw NotFoundException("Journey not found by id: $journeyId")
        }

        val memberMap = journeyMemberRepository.findByJourneyId(journeyId).associateBy { it.name }
        val payer = memberMap[create.payerName] ?: throw NotFoundException("Payer not found by name: ${create.payerName}")
        require(payer.journeyMemberId != null) { "Payer id is null" }

        // 요청 받은 멤버 수와 실제 멤버 수가 다름
        if (create.members.size != memberMap.size)
            throw BadRequestException(
                ErrorCode.VALIDATION_ERROR,
                "Members count is not matched, expected: ${memberMap.size}, actual: ${create.members.size}"
            )

        // 요청 받은 금액과 멤버 금액 합계가 다름
        if (create.amount != create.members.sumOf { it.amount })
            throw BadRequestException(
                ErrorCode.VALIDATION_ERROR,
                "Amount is not matched, expected: ${create.amount}, actual: ${create.members.sumOf { it.amount }}"
            )

        val expense = journeyExpenseRepository.save(create.toEntity(journeyId, payer.journeyMemberId))
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
    suspend fun getExpenses(journeyId: String): List<JourneyExpenseResponse> {
        if (journeyRepository.existsByJourneyId(journeyId).not()) {
            throw NotFoundException("Journey not found by id: $journeyId")
        }
        val memberMap = journeyMemberRepository.findByJourneyId(journeyId).associateBy { it.journeyMemberId }

        return journeyExpenseRepository.findByJourneyIdAndDeletedAtIsNull(journeyId).map {
            JourneyExpenseResponse.of(
                expense = it,
                payerName = memberMap[it.expensePayerId]?.name ?: throw NotFoundException("Payer not found by id: ${it.expensePayerId}"),
            )
        }
    }

    @Transactional(readOnly = true)
    suspend fun getExpense(journeyId: String, journeyExpenseId: Long): JourneyExpenseWithMembersResponse {
        val expense = journeyExpenseRepository.findByJourneyIdAndJourneyExpenseIdAndDeletedAtIsNull(journeyId, journeyExpenseId)
            ?: throw NotFoundException("Expense not found by id: $journeyExpenseId")

        require(expense.journeyExpenseId != null) { "Expense id is null" }
        val ledgers = journeyMemberLedgerRepository.findByJourneyExpenseIdAndDeletedAtIsNull(expense.journeyExpenseId)
        val memberMap = journeyMemberRepository.findByJourneyId(journeyId).associateBy { it.journeyMemberId }

        return JourneyExpenseWithMembersResponse.of(
            expense = expense,
            payerName = journeyMemberRepository.findById(expense.expensePayerId)?.name
                ?: throw NotFoundException("Payer not found by id: ${expense.expensePayerId}"),
            members = ledgers
                .filter { it.amount < BigDecimal.ZERO }
                .map {
                JourneyExpenseWithMembersResponse.JourneyExpenseMemberResponse.of(
                    ledger = it,
                    name = memberMap[it.journeyMemberId]?.name ?: throw NotFoundException("Member not found by id: ${it.journeyMemberId}"),
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

        // 요청 받은 멤버 수와 실제 멤버 수가 다름
        if (update.members.isNotEmpty() && update.members.size != memberMap.size)
            throw BadRequestException(
                ErrorCode.VALIDATION_ERROR,
                "Members count is not matched, expected: ${memberMap.size}, actual: ${update.members.size}"
            )

        // 요청 받은 금액과 멤버 금액 합계가 다름
        if (update.amount != null && update.amount != update.members.sumOf { it.amount })
            throw BadRequestException(
                ErrorCode.VALIDATION_ERROR,
                "Amount is not matched, expected: ${update.amount}, actual: ${update.members.sumOf { it.amount }}"
            )

        val updatedExpense = journeyExpenseRepository.save(
            expense.copy(
                expensePayerId = payer.journeyMemberId,
                category = update.category ?: expense.category,
                expenseDate = update.expenseDate ?: expense.expenseDate,
                currency = update.currency ?: expense.currency,
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

}