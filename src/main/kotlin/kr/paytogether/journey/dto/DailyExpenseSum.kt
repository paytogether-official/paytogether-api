package kr.paytogether.journey.dto

import java.math.BigDecimal
import java.time.LocalDate

data class DailyExpenseSum(
    val date: LocalDate,
    val totalAmount: BigDecimal,
)
