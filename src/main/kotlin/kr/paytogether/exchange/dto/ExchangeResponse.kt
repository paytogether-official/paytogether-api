package kr.paytogether.exchange.dto

import java.math.BigDecimal
import java.time.LocalDate

data class ExchangeResponse(
    val date: LocalDate?,

    val currency: String,

    val exchangeRate: BigDecimal?,
) {

    companion object {
        fun empty(date: LocalDate, currency: String) = ExchangeResponse(date, currency, null)
    }
}
