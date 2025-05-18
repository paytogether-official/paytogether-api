package kr.paytogether.exchange.dto

import kr.paytogether.exchange.entity.ExchangeRate
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP
import java.time.LocalDate

data class ExchangeResponse(
    val date: LocalDate?,

    val currency: String,

    val exchangeRate: BigDecimal,
) {

    companion object {
        fun from(exchangeRate: ExchangeRate) = ExchangeResponse(
            date = exchangeRate.date,
            currency = exchangeRate.baseCurrency,
            exchangeRate = exchangeRate.rate.setScale(2, HALF_UP),
        )
    }
}
