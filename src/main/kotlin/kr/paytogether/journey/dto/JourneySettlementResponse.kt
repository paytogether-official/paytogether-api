package kr.paytogether.journey.dto

import java.math.BigDecimal

data class JourneySettlementResponse(
    val journeyId: String,
    val fromMemberId: Long,
    val fromMemberName: String,
    val toMemberId: Long,
    val toMemberName: String,
    val amount: BigDecimal,
)
