package kr.paytogether.exchange.repository

import kotlinx.coroutines.flow.Flow
import kr.paytogether.exchange.entity.ExchangeRate
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.LocalDate

interface ExchangeRateRepository: CoroutineCrudRepository<ExchangeRate, Long> {
    suspend fun findTopByBaseCurrencyOrderByDateDesc(baseCurrency: String): ExchangeRate?

    suspend fun findByDate(date: LocalDate): Flow<ExchangeRate>

    @Query("""
        SELECT b.*
        FROM (SELECT base_currency, MAX(date) max_date
              FROM exchange_rate
              GROUP BY base_currency) a
                 INNER JOIN exchange_rate b
                            ON a.base_currency = b.base_currency AND a.max_date = b.date
    """)
    suspend fun findLatest(): Flow<ExchangeRate>

    @Query("""
        SELECT b.*
        FROM (SELECT base_currency, MAX(date) max_date
              FROM exchange_rate
              GROUP BY base_currency) a
                 INNER JOIN exchange_rate b
                            ON a.base_currency = b.base_currency AND a.max_date = b.date
        WHERE b.base_currency IN (:currencies)
    """)
    suspend fun findLatestByCurrencies(currencies: List<String>): Flow<ExchangeRate>
}