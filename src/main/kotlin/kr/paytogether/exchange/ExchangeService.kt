package kr.paytogether.exchange

import kr.paytogether.exchange.dto.ExchangeResponse
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ExchangeService(
    private val exchangeRepository: ExchangeRepository,
) {
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
}