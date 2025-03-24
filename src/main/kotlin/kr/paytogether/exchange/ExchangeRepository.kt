package kr.paytogether.exchange

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ExchangeRepository : CoroutineCrudRepository<Exchange, Long> {
    suspend fun findTopByCurrencyOrderByDateDesc(currency: String): Exchange?

    @Query("""
        select b.*
        from (select cur_unit, max(date) max_date from koreaexim_exchange group by cur_unit) a
            INNER JOIN koreaexim_exchange b
        ON a.cur_unit = b.cur_unit AND a.max_date = b.date
    """)
    suspend fun findLatest(): List<Exchange>

    @Query("""
        select b.*
        from (select cur_unit, max(date) max_date from koreaexim_exchange group by cur_unit) a
            INNER JOIN koreaexim_exchange b
        ON a.cur_unit = b.cur_unit AND a.max_date = b.date
        WHERE b.cur_unit in (:currencies)
    """)
    suspend fun findLatestByCurrencies(currencies: List<String>): List<Exchange>
}