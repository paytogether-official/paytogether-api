package kr.paytogether.journey.dto

import kr.paytogether.exchange.entity.ExchangeRate
import kr.paytogether.journey.entity.JourneyExpense
import kr.paytogether.journey.entity.JourneyMemberLedger
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

data class JourneyExpenseWithMembersResponse(
    val journeyExpenseId: Long,

    val journeyId: String,

    val payerName: String,

    val expenseDate: LocalDate,

    val category: String,

    val baseCurrency: String,

    val quoteCurrency: String,

    val amount: BigDecimal,

    val remainingAmount: BigDecimal,

    val memo: String,

    val members: List<JourneyExpenseMemberResponse>,
) {
    companion object {
        fun of(expense: JourneyExpense, exchangeRate: ExchangeRate, payerName: String, members: List<JourneyExpenseMemberResponse>) =
            JourneyExpenseWithMembersResponse(
                journeyExpenseId = expense.journeyExpenseId!!,
                journeyId = expense.journeyId,
                payerName = payerName,
                expenseDate = expense.expenseDate,
                category = expense.category,
                baseCurrency = expense.currency,
                quoteCurrency = exchangeRate.quoteCurrency,
                amount = (expense.amount * exchangeRate.rate).setScale(2, RoundingMode.FLOOR),
                remainingAmount = (expense.remainingAmount * exchangeRate.rate).setScale(2, RoundingMode.FLOOR),
                memo = expense.memo,
                members = members,
            )
    }

    data class JourneyExpenseMemberResponse(
        val journeyMemberId: Long,

        val name: String,

        val amount: BigDecimal,
    ) {
        companion object {
            fun of(ledger: JourneyMemberLedger, name: String) = JourneyExpenseMemberResponse(
                journeyMemberId = ledger.journeyMemberId,
                name = name,
                amount = ledger.amount.negate().stripTrailingZeros(),
            )
        }
    }
}