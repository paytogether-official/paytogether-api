package kr.paytogether.exchange.feign.twelvedata

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.resilience4j.kotlin.ratelimiter.executeSuspendFunction
import io.github.resilience4j.kotlin.ratelimiter.rateLimiter
import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import kr.paytogether.exchange.feign.twelvedata.dto.ExchangeRateQuery
import kr.paytogether.exchange.feign.twelvedata.dto.ExchangeRateSuccess
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import kotlinx.coroutines.flow.*
import kr.paytogether.exchange.feign.twelvedata.dto.ExchangeRateResponse

@Service
class TwelvedataService(
    private val twelvedataRest: TwelvedataRest,
) {
    private val logger = KotlinLogging.logger {}

    // 1분에 8개씩 요청 가능
    private val rateLimiter = RateLimiterConfig.custom()
        .limitForPeriod(1)
        .limitRefreshPeriod(Duration.ofMinutes(2))
        .timeoutDuration(Duration.ofMinutes(5))
        .build().let { RateLimiter.of("twelvedata", it) }

    suspend fun getExchangeRates(
        symbols: List<String>,
        date: LocalDate = LocalDate.now(),
    ) =
        symbols
            .windowed(8, 8, true)
            .onEach { logger.info { "Requesting exchange rates for ${it.joinToString(", ")}" } }
            .map { chunk ->
                val query = ExchangeRateQuery(
                    symbol = chunk.joinToString(","),
                    date = date,
                )
                rateLimiter.executeSuspendFunction {
                    val res = twelvedataRest.getExchangeRate(query)
                    return@executeSuspendFunction res
                }.values
                    .mapNotNull {
                        if (it.symbol != null) ExchangeRateSuccess(
                            symbol = it.symbol,
                            rate = it.rate!!,
                            timestamp = it.timestamp!!
                        ) else null
                    }
            }
            .flatten()
}