package kr.paytogether.journey.dto

import kr.paytogether.journey.entity.JourneyExpense
import java.math.BigDecimal
import java.time.LocalDate

data class JourneyExpenseResponse(
    val slug: String,

    val payerName: String,

    val expenseDate: LocalDate,

    val category: String,

    val currency: String,

    val amount: BigDecimal,

    val memo: String,
) {
    companion object {
        fun of(expense: JourneyExpense, slug: String, payerName: String) = JourneyExpenseResponse(
            slug = slug,
            payerName = payerName,
            expenseDate = expense.expenseDate,
            category = expense.category,
            currency = expense.currency,
            amount = expense.amount,
            memo = expense.memo,
        )
    }
}