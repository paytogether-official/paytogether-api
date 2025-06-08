package kr.paytogether.journey.dto

import java.math.BigDecimal

data class DailyExpenseSum(
    val date: String, // LocalDate | "OTHER"
    val totalAmount: BigDecimal,
)
