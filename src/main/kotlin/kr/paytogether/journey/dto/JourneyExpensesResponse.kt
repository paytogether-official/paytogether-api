package kr.paytogether.journey.dto

import java.math.BigDecimal

data class JourneyExpensesResponse(
    val totalAmount: BigDecimal,

    val quoteCurrency: String,

    val expenses: List<JourneyExpenseWithMembersResponse>,
) {
    companion object {
        fun of(
            totalAmount: BigDecimal,
            quoteCurrency: String,
            expenses: List<JourneyExpenseWithMembersResponse>,
        ): JourneyExpensesResponse =
            JourneyExpensesResponse(
                totalAmount = totalAmount.setScale(2),
                quoteCurrency = quoteCurrency,
                expenses = expenses,
            )
    }
}
