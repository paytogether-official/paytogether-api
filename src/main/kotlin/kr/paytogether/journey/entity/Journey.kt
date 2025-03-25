package kr.paytogether.journey.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Table("journey")
data class Journey(
    @Id val journeyId: String,

    val baseCurrency: String,

    val quoteCurrency: String,

    val exchangeRate: BigDecimal,

    val title: String,

    val startDate: LocalDate,

    val endDate: LocalDate,

    val localeCode: String,

    val closedAt: LocalDateTime? = null,

    @CreatedDate
    val createdAt: LocalDateTime? = null,
)