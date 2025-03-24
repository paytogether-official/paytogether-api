package kr.paytogether.exchange.repository

import kr.paytogether.exchange.entity.ExchangeRate
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.LocalDate

interface ExchangeRateRepository: CoroutineCrudRepository<ExchangeRate, Long> {
    suspend fun findByDate(date: LocalDate): List<ExchangeRate>
}