package kr.paytogether.exchange.feign.twelvedata.dto

import java.math.BigDecimal

/**
 * "symbol": "EUR/KRW",
 * "rate": 1589.63,
 * "timestamp": 1742799600
 */

sealed class ExchangeRateResponse

data class ExchangeRateError(
    val code: Int,
    val message: String,
    val status: String,
) : ExchangeRateResponse()

data class ExchangeRateSuccess(
    val symbol: String,
    val rate: BigDecimal,
    val timestamp: Long,
) : ExchangeRateResponse()

//data class ExchangeRateResponse(
//    val symbol: String,
//    val rate: BigDecimal,
//    val timestamp: Long,
//)
