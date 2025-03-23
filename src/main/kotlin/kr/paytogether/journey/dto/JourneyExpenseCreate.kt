package kr.paytogether.journey.dto

import kr.paytogether.journey.entity.JourneyExpense
import java.math.BigDecimal
import java.time.LocalDate

data class JourneyExpenseCreate(
    val payerName: String,

    val category: String,

    val expenseDate: LocalDate,

    val currency: String,

    val amount: BigDecimal,

    val memo: String = "",

    val members: List<JourneyLedgerCreate>,
) {
    fun toEntity(journeyId: Long, expensePayerId: Long): JourneyExpense {
        return JourneyExpense(
            journeyId = journeyId,
            expensePayerId = expensePayerId,
            category = category,
            expenseDate = expenseDate,
            currency = currency,
            amount = amount,
            memo = memo,
        )
    }
}
