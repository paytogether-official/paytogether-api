package kr.paytogether.locale.dto

import kr.paytogether.locale.Locale

data class LocaleResponse(
    val imageUrl: String,
    val continent: String,
    val currency: String,
    val countryKoreanName: String,
    val countryEnglishName: String,
    val localeCode: String,
    val sort: Int,
) {
    companion object {
        fun from(locale: Locale) = LocaleResponse(
            imageUrl = locale.imageUrl,
            continent = locale.continent,
            currency = locale.currency,
            countryKoreanName = locale.countryKoreanName,
            countryEnglishName = locale.countryEnglishName,
            localeCode = locale.localeCode,
            sort = locale.sort,
        )
    }
}
