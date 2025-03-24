package kr.paytogether.exchange

import kotlinx.coroutines.flow.collect
import kr.paytogether.exchange.dto.ExchangeResponse
import kr.paytogether.exchange.entity.ExchangeRate
import kr.paytogether.exchange.enums.ExchangeRateProvider
import kr.paytogether.exchange.feign.twelvedata.dto.ExchangeRateSuccess
import kr.paytogether.exchange.repository.ExchangeRateRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Service
class ExchangeRateService(
    private val exchangeRepository: ExchangeRepository,
    private val exchangeRateRepository: ExchangeRateRepository,
) {
    suspend fun getExchangeRatesByCurrencies(currencies: List<String>) = exchangeRepository.findLatestByCurrencies(currencies)
        .map {
            ExchangeResponse(
                date = it.date,
                currency = it.currency,
                exchangeRate = it.exchangeRate.replace(",", "").toBigDecimalOrNull()
            )
        }

    suspend fun getExchangeRates() = exchangeRepository.findLatest().map {
        ExchangeResponse(
            date = it.date,
            currency = it.currency,
            exchangeRate = it.exchangeRate.replace(",", "").toBigDecimalOrNull()
        )
    }

    suspend fun getExchangeRate(currency: String): ExchangeResponse {
        val exchange = exchangeRepository.findTopByCurrencyOrderByDateDesc(currency)
        return ExchangeResponse(
            date = exchange?.date,
            currency = currency,
            exchangeRate = exchange
                ?.exchangeRate
                ?.replace(",", "")
                ?.toBigDecimalOrNull()
        )
    }

    @Transactional
    suspend fun createExchangeRates(exchangeRates: List<ExchangeRateSuccess>, date: LocalDate = LocalDate.now()) {
        val exchangeKeySet = exchangeRateRepository.findByDate(date).map { "${it.baseCurrency}/${it.quoteCurrency}" }.toSet()
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
            .let { exchangeRateRepository.saveAll(it).collect() }
    }
}