package kr.paytogether.exchange.feign.twelvedata.dto

import java.time.LocalDate

data class ExchangeRateQuery(
    val symbols: List<String>,

    val date: LocalDate,

    val timezone: String = "Asia/Seoul",
)
