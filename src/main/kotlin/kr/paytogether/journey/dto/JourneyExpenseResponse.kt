package kr.paytogether.journey.dto

import kr.paytogether.journey.entity.JourneyExpense
import java.math.BigDecimal
import java.time.LocalDate

data class JourneyExpenseResponse(
    val journeyExpenseId: Long,

    val journeyId: String,

    val payerName: String,

    val expenseDate: LocalDate,

    val category: String,

    val categoryDescription: String,

    val baseCurrency: String,

    val amount: BigDecimal,

    val remainingAmount: BigDecimal,

    val memo: String,
) {
    companion object {
        fun of(expense: JourneyExpense, payerName: String) = JourneyExpenseResponse(
            journeyExpenseId = expense.journeyExpenseId!!,
            journeyId = expense.journeyId,
            payerName = payerName,
            expenseDate = expense.expenseDate,
            category = expense.category,
            categoryDescription = expense.categoryDescription,
            baseCurrency = expense.baseCurrency,
            amount = expense.amount.setScale(2),
            remainingAmount = expense.remainingAmount.setScale(2),
            memo = expense.memo,
        )
    }
}