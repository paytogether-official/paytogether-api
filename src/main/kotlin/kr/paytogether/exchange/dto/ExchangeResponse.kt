package kr.paytogether.exchange.dto

import kr.paytogether.exchange.entity.ExchangeRate
import java.math.BigDecimal
import java.time.LocalDate

data class ExchangeResponse(
    val date: LocalDate?,

    val currency: String,

    val exchangeRate: BigDecimal?,
) {

    companion object {
        fun from(exchangeRate: ExchangeRate) = ExchangeResponse(
            date = exchangeRate.date,
            currency = exchangeRate.baseCurrency,
            exchangeRate = exchangeRate.rate.stripTrailingZeros()
        )
    }
}
