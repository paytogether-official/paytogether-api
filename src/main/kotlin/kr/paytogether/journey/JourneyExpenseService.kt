package kr.paytogether.journey

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kr.paytogether.journey.dto.*
import kr.paytogether.journey.entity.JourneyMemberLedger
import kr.paytogether.journey.repository.*
import kr.paytogether.shared.exception.BadRequestException
import kr.paytogether.shared.exception.ErrorCode
import kr.paytogether.shared.exception.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
    suspend fun getExpenses(journeyId: String): Flow<JourneyExpenseResponse> {
        if (journeyRepository.existsByJourneyId(journeyId).not()) {
            throw NotFoundException("Journey not found by id: $journeyId")
        }
        val memberMap = journeyMemberRepository.findByJourneyId(journeyId).associateBy { it.journeyMemberId }

        return journeyExpenseRepository.findByJourneyId(journeyId).map {
            JourneyExpenseResponse.of(
                expense = it,
                payerName = memberMap[it.expensePayerId]?.name ?: throw NotFoundException("Payer not found by id: ${it.expensePayerId}"),
            )
        }
    }

    @Transactional(readOnly = true)
    suspend fun getExpense(journeyId: String, journeyExpenseId: Long): JourneyExpenseResponse {
        val expense = journeyExpenseRepository.findByJourneyIdAndJourneyExpenseId(journeyId, journeyExpenseId)
            ?: throw NotFoundException("Expense not found by id: $journeyExpenseId")

        return JourneyExpenseResponse.of(
            expense = expense,
            payerName = journeyMemberRepository.findById(expense.expensePayerId)?.name ?: throw NotFoundException("Payer not found by id: ${expense.expensePayerId}"),
        )
    }

}