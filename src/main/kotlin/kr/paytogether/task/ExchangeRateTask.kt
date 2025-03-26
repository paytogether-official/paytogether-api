package kr.paytogether.task

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kr.paytogether.exchange.ExchangeRateService
import kr.paytogether.exchange.feign.twelvedata.TwelvedataService
import kr.paytogether.exchange.repository.ExchangeRateRepository
import kr.paytogether.locale.LocaleRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ExchangeRateTask(
    private val twelvedataService: TwelvedataService,
    private val exchangeRateService: ExchangeRateService,
    private val exchangeRateRepository: ExchangeRateRepository,
    private val localeRepository: LocaleRepository,
) {
    private val logger = KotlinLogging.logger {}


//     @Scheduled(fixedDelay = 6000000) //  for Test
    // at 10 minute at 0, 6, 12 and 18 hours
    @Scheduled(cron = "0 10 0,6,12,18 * * *")
    suspend fun collectExchangeRate() = runCatching {
        val todayExistsCurrencySet = exchangeRateRepository.findByDate(LocalDate.now())
            .map { it.baseCurrency }
            .toSet()

        localeRepository.findAll()
            .filterNot { todayExistsCurrencySet.contains(it.currency) }
            .toList()
            .distinctBy { it.currency }
            .map { locale -> "${locale.currency}/KRW" }
            .let { twelvedataService.getExchangeRates(it) }
            .let { exchangeRateService.createExchangeRates(it) }
    }
        .onSuccess { logger.info { "Exchange rates collected" } }
        .onFailure { logger.error(it) { "Exchange rates collection failed" } }
}