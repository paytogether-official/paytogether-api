package kr.paytogether.locale

import kr.paytogether.exchange.enums.ExchangeRateProvider
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface LocaleRepository : CoroutineCrudRepository<Locale, Long> {
    suspend fun findAllByExchangeRateProviderAndCurrencyNotIn(exchangeRateProvider: ExchangeRateProvider, currencies: List<String>): List<Locale>
}