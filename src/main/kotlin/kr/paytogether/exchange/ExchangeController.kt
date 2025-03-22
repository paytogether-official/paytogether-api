package kr.paytogether.exchange

import org.springframework.web.bind.annotation.*

@RestController
class ExchangeController(
    private val exchangeService: ExchangeService,
) {
    @GetMapping("/exchange-rate")
    suspend fun getExchangeRate(
        @RequestParam currency: String,
    ) = exchangeService.getExchangeRate(currency)
}