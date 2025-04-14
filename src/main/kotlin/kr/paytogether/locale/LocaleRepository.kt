package kr.paytogether.locale

import kotlinx.coroutines.flow.Flow
import kr.paytogether.exchange.enums.ExchangeRateProvider
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface LocaleRepository : CoroutineCrudRepository<Locale, Long> {
    suspend fun findAllByExchangeRateProvider(exchangeRateProvider: ExchangeRateProvider): Flow<Locale>
}