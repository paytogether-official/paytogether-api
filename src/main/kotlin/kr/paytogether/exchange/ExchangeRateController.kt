package kr.paytogether.exchange

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.paytogether.task.ExchangeRateTask
import org.springframework.web.bind.annotation.*

@RestController
class ExchangeRateController(
    private val exchangeService: ExchangeRateService,
    private val task: ExchangeRateTask,
) {
    @GetMapping("/exchange-rate")
    suspend fun getExchangeRate(
        @RequestParam currency: String,
    ) = exchangeService.getExchangeRate(currency)

    @GetMapping("/exchange-rates")
    suspend fun getExchangeRates(
    ) = exchangeService.getExchangeRates()

    @GetMapping("/exchange-rates/{currencies}")
    suspend fun getExchangeRatesByCurrencies(
        @PathVariable currencies: List<String>,
    ) = exchangeService.getExchangeRatesByCurrencies(currencies)

    @PostMapping("/exchange-rates/collect")
    suspend fun collectExchangeRates(): String {
        CoroutineScope(Dispatchers.Default).launch {
            task.collectExchangeRate()
        }

        return "Exchange rates collection started"
    }

}