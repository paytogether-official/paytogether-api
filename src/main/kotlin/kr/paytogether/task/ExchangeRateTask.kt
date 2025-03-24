package kr.paytogether.task

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kr.paytogether.exchange.ExchangeRateService
import kr.paytogether.exchange.feign.twelvedata.TwelvedataService
import kr.paytogether.locale.LocaleRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ExchangeRateTask(
    private val twelvedataService: TwelvedataService,
    private val exchangeRateService: ExchangeRateService,
    private val localeRepository: LocaleRepository,
) {
    private val logger = KotlinLogging.logger {}

    // 0시 10분, 6시 10분, 12시 10분, 18시 10분에 실행
//    @Scheduled(cron = "0 10 0,6,12,18 * * *")

    // 10분 주기로 실행
//    @Scheduled(fixedDelay = 600000)
    suspend fun collectExchangeRate() = runBlocking {
        localeRepository.findAll().map { locale -> "${locale.currency}/KRW" }.toList()
            .let { twelvedataService.getExchangeRates(it) }
            .let { exchangeRateService.createExchangeRates(it) }
    }
}