package kr.paytogether.exchange.feign.twelvedata.dto

import java.math.BigDecimal

/**
 * "symbol": "EUR/KRW",
 * "rate": 1589.63,
 * "timestamp": 1742799600
 */

data class ExchangeRateResponse(
    val symbol: String? = null,
    val rate: BigDecimal? = null,
    val timestamp: Long? = null,

    val code: Int? = null,
    val message: String? = null,
    val status: String? = null,
)

data class ExchangeRateSuccess(
    val symbol: String,
    val rate: BigDecimal,
    val timestamp: Long,
)