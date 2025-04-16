package kr.paytogether.exchange.feign.twelvedata

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kr.paytogether.exchange.feign.twelvedata.dto.ExchangeRateQuery
import kr.paytogether.exchange.feign.twelvedata.dto.ExchangeRateSuccess
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class TwelvedataService(
    private val twelvedataRest: TwelvedataRest,
) {
    private val logger = KotlinLogging.logger {}

    // 1분에 8개씩 요청 가능
//    private val rateLimiter = RateLimiterConfig.custom()
//        .limitForPeriod(1) // 분당 최대 8개의 요청 허용
//        .limitRefreshPeriod(Duration.ofMinutes(1)) //  1분마다 제한 초기화
//        .timeoutDuration(Duration.ofMinutes(5)) // 요청 대기 시간 설정
//        .build().let { RateLimiter.of("twelvedata", it) }


    suspend fun getExchangeRates(
        symbols: String,
        date: LocalDate = LocalDate.now(),
    ): List<ExchangeRateSuccess> {
        val query = ExchangeRateQuery(
            symbol = symbols,
            date = date,
        )
        return twelvedataRest.getExchangeRate(query)
            .values
            .mapNotNull {
                if (it.symbol == null) null
                else ExchangeRateSuccess(
                    symbol = it.symbol,
                    rate = it.rate!!,
                    timestamp = it.timestamp!!,
                )
            }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getExchangeRates(
        symbols: List<String>,
        date: LocalDate = LocalDate.now(),
    ) =
        symbols
            .windowed(8, 8, true)
            .onEach {
                logger.info { "Requesting exchange rates for ${it.joinToString(", ")}" }
            }
            .asFlow()
            .map { chunk -> getExchangeRates(chunk.joinToString(","), date) }
            .onEach { delay(1_000 * 60) } // 1분에 8개씩 요청 가능
            .flatMapConcat { it.asFlow() }
}
