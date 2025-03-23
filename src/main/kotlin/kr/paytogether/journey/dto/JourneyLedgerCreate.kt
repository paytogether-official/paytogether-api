package kr.paytogether.journey.dto

import java.math.BigDecimal

data class JourneyLedgerCreate(
    val name: String,

    val amount: BigDecimal,
)
