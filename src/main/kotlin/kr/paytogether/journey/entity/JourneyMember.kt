package kr.paytogether.journey.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

import java.time.LocalDateTime

@Table("journey_member")
data class JourneyMember(
    @Id val journeyMemberId: Long? = null,

    val journeyId: Long,

    val name: String,

    @CreatedDate
    val createdAt: LocalDateTime? = null,
)
