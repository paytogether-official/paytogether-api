package kr.paytogether.exchange.feign.twelvedata

import feign.QueryMap
import feign.RequestLine
import kr.paytogether.exchange.feign.twelvedata.dto.ExchangeRateQuery
import kr.paytogether.exchange.feign.twelvedata.dto.ExchangeRateResponse
import java.math.BigDecimal


/**
 * 분랑 8개 요청 가능.
 * 8 (800 a day)
 */
interface TwelvedataRest {

    @RequestLine("GET /exchange_rate")
    suspend fun getExchangeRate(
        @QueryMap query: ExchangeRateQuery
    ): Map<String, ExchangeRateResponse>
    // Map<String, ExchangeRateResponse | TwelveDataError>
}