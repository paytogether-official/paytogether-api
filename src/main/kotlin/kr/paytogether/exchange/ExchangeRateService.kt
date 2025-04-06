package kr.paytogether.exchange

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import kr.paytogether.exchange.dto.ExchangeResponse
import kr.paytogether.exchange.entity.ExchangeRate
import kr.paytogether.exchange.enums.ExchangeRateProvider
import kr.paytogether.exchange.feign.twelvedata.dto.ExchangeRateSuccess
import kr.paytogether.exchange.repository.ExchangeRateRepository
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
        val exchange = exchangeRateRepository.findTopByBaseCurrencyOrderByDateDesc(currency)
        return if (exchange == null) ExchangeResponse.empty(currency) else ExchangeResponse.from(exchange)
    }

    @Transactional
    suspend fun createExchangeRates(exchangeRates: List<ExchangeRateSuccess>, date: LocalDate = LocalDate.now()) {
        val exchangeKeySet = exchangeRateRepository.findByDate(date)
            .map { "${it.baseCurrency}/${it.quoteCurrency}" }.toSet()
        exchangeRates.filterNot { exchangeKeySet.contains(it.symbol) }
            .map {
                ExchangeRate(
                    date = date,
                    provider = ExchangeRateProvider.TWELVEDATA,
                    baseCurrency = it.symbol.substringBefore("/"),
                    quoteCurrency = it.symbol.substringAfter("/"),
                    rate = it.rate
                )
            }
            .filterNot { it.baseCurrency == "KRW" }
            .let { exchangeRateRepository.saveAll(it).collect() }
    }
}