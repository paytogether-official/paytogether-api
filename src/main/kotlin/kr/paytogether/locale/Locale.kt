package kr.paytogether.locale

import kr.paytogether.exchange.enums.ExchangeRateProvider
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("locale")
data class Locale(
    @Id val id: Long,
    val imageUrl: String,
    val continent: String,
    val currency: String,
    val countryKoreanName: String,
    val countryEnglishName: String,
    val localeCode: String,
    val exchangeRateProvider: ExchangeRateProvider,
    val sort: Int,
    val createdAt: Instant,
)
