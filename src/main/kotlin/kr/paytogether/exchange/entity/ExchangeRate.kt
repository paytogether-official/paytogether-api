package kr.paytogether.exchange.entity

import kr.paytogether.exchange.enums.ExchangeRateProvider
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Table("exchange_rate")
data class ExchangeRate(
    @Id
    val exchangeRateId: Long? = null,

    val date: LocalDate,

    val provider: ExchangeRateProvider,

    val baseCurrency: String,

    val quoteCurrency: String,

    val rate: BigDecimal,

    @CreatedDate
    val createdAt: LocalDateTime? = null,
)
