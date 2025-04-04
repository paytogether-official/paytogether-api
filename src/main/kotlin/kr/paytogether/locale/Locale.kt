package kr.paytogether.locale

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("locale")
data class Locale(
    @Id val id: Long,
    val imageUrl: String,
    val continent: String,
    val currency: String,
    val countryKoreanName: String,
    val countryEnglishName: String,
    val localeCode: String,
    val sort: Int,
    val createdAt: LocalDateTime,
)
