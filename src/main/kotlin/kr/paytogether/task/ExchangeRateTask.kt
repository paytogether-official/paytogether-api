package kr.paytogether.task

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kr.paytogether.exchange.ExchangeRateService
import kr.paytogether.exchange.enums.ExchangeRateProvider
import kr.paytogether.exchange.feign.twelvedata.TwelvedataService
import kr.paytogether.exchange.repository.ExchangeRateRepository
import kr.paytogether.locale.LocaleRepository
import kr.paytogether.shared.slack.Color
import kr.paytogether.shared.slack.SlackEvent
import kr.paytogether.shared.slack.Topic
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ExchangeRateTask(
    private val twelvedataService: TwelvedataService,
    private val exchangeRateService: ExchangeRateService,
    private val exchangeRateRepository: ExchangeRateRepository,
    private val localeRepository: LocaleRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {
    private val logger = KotlinLogging.logger {}


    //     @Scheduled(fixedDelay = 6000000) //  for Test
    // at 10 minute at 0, 6, 12 and 18 hours
    @Scheduled(cron = "0 10 0,6,12,18 * * *")
    suspend fun collectExchangeRate() = runCatching {
        val todayExistsCurrencies = exchangeRateRepository.findByDate(LocalDate.now())
            .map { it.baseCurrency }
            .toList()

        localeRepository.findAllByExchangeRateProviderAndCurrencyNotIn(ExchangeRateProvider.TWELVEDATA, todayExistsCurrencies)
            .distinctBy { it.currency }
            .map { locale -> "${locale.currency}/KRW" }
            .let { twelvedataService.getExchangeRates(it) }
            .let { exchangeRateService.createExchangeRates(it) }
    }
        .onSuccess { logger.info { "Exchange rates collected" } }
        .onFailure {
            logger.error(it) { "Exchange rates collection failed" }
            eventPublisher.publishEvent(
                SlackEvent(
                    topic = Topic.ERROR,
                    title = "Exchange rates collection failed",
                    message = it.message,
                    color = Color.DANGER,
                )
            )
        }
}