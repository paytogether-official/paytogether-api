package kr.paytogether.journey.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table(name = "journey_member_ledger")
data class JourneyMemberLedger(
    @Id
    val journeyMemberLedgerId: Long? = null,

    val journeyId: String,

    val journeyMemberId: Long,

    val journeyExpenseId: Long,

    val amount: BigDecimal,

    val note: String,

    @CreatedDate
    val createdAt: LocalDateTime? = null,

    val deletedAt: LocalDateTime? = null,
)
