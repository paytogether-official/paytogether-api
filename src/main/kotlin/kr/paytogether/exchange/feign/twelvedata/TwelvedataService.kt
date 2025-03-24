package kr.paytogether.exchange.feign.twelvedata

import io.github.resilience4j.kotlin.ratelimiter.rateLimiter
import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import kr.paytogether.exchange.feign.twelvedata.dto.ExchangeRateQuery
import kr.paytogether.exchange.feign.twelvedata.dto.ExchangeRateSuccess
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import kotlinx.coroutines.flow.*

@Service
class TwelvedataService(
    private val twelvedataRest: TwelvedataRest,
) {
    // 1분에 8개씩 요청 가능
    private val rateLimiter = RateLimiterConfig.custom()
        .limitForPeriod(8)
        .limitRefreshPeriod(Duration.ofMinutes(1))
        .timeoutDuration(Duration.ofMinutes(5))
        .build().let { RateLimiter.of("twelvedata", it) }

    suspend fun getExchangeRates(
        symbols: List<String>,
        date: LocalDate = LocalDate.now(),
    ): List<ExchangeRateSuccess> {
        return symbols
            .chunked(8)
            .asFlow()
            .rateLimiter(rateLimiter)
            .map { chunk ->
                val query = ExchangeRateQuery(
                    symbols = chunk,
                    date = date,
                )
                twelvedataRest.getExchangeRate(query).values
                    .filterIsInstance<ExchangeRateSuccess>()
            }
            .toList()
            .flatten()
    }

}