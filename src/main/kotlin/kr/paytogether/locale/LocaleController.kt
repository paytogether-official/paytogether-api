package kr.paytogether.locale

import kotlinx.coroutines.flow.map
import kr.paytogether.locale.dto.LocaleResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LocaleController(
    private val localeRepository: LocaleRepository,
) {
    @GetMapping("/locales")
    suspend fun getLocales() = localeRepository.findAll().map { LocaleResponse.from(it) }
}