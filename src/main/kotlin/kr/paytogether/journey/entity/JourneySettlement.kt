package kr.paytogether.journey.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("journey_settlement")
data class JourneySettlement(
    val journeySettlementId: Long? = null,

    val journeyId: String,

    val fromMemberId: Long,

    val toMemberId: Long,

    val amount: BigDecimal,

    @CreatedDate
    val createdAt: LocalDateTime? = null
)
