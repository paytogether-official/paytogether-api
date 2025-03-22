package kr.paytogether.exchange

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.LocalDate

interface ExchangeRepository : CoroutineCrudRepository<Exchange, Long> {
    suspend fun findTopByCurrencyOrderByDateDesc(currency: String): Exchange?
}