package kr.paytogether.exchange

import kotlinx.coroutines.flow.*
import kr.paytogether.exchange.dto.ExchangeResponse
import kr.paytogether.exchange.entity.ExchangeRate
import kr.paytogether.exchange.enums.ExchangeRateProvider
import kr.paytogether.exchange.feign.twelvedata.dto.ExchangeRateSuccess
import kr.paytogether.exchange.repository.ExchangeRateRepository
import kr.paytogether.shared.exception.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ExchangeRateService(
    private val exchangeRateRepository: ExchangeRateRepository,
) {
    suspend fun getExchangeRatesByCurrencies(currencies: List<String>) = exchangeRateRepository.findLatestByCurrencies(currencies)
        .map { ExchangeResponse.from(it) }

    suspend fun getExchangeRates() = exchangeRateRepository.findLatest()
        .map { ExchangeResponse.from(it) }

    suspend fun getExchangeRate(currency: String): ExchangeResponse {
        val exchange =
            exchangeRateRepository.findTopByBaseCurrencyOrderByDateDesc(currency) ?: throw NotFoundException("Currency $currency not found")
        return ExchangeResponse.from(exchange)
    }

    suspend fun getExchangeRate(baseCurrency: String, quoteCurrency: String): ExchangeRate =
        exchangeRateRepository.findTopByBaseCurrencyAndQuoteCurrencyOrderByDateDesc(baseCurrency, quoteCurrency)
            ?: throw NotFoundException("Exchange rate for $baseCurrency/$quoteCurrency not found")


    @Transactional
    suspend fun createExchangeRates(exchangeRates: Flow<ExchangeRateSuccess>, date: LocalDate = LocalDate.now()) {
        val exchangeKeySet = exchangeRateRepository.findByDate(date)
            .map { "${it.baseCurrency}/${it.quoteCurrency}" }.toSet()
        exchangeRates
            .filterNot { exchangeKeySet.contains(it.symbol) }
            .map {
                ExchangeRate(
                    date = date,
                    provider = ExchangeRateProvider.TWELVEDATA,
                    baseCurrency = it.symbol.substringBefore("/"),
                    quoteCurrency = it.symbol.substringAfter("/"),
                    rate = it.rate
                )
            }
            .let { exchangeRateRepository.saveAll(it).collect() }
    }
}