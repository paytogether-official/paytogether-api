package kr.paytogether.journey.projection

import java.math.BigDecimal

interface JourneyLedgerSumProjection {
    val journeyMemberId: Long

    val amount: BigDecimal
}