package kr.paytogether.journey.dto

import kr.paytogether.journey.entity.Journey
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class JourneyResponse(
    val slug: String,

    val baseCurrency: String,

    val quoteCurrency: String,

    val exchangeRate: BigDecimal,

    val title: String,

    val startDate: LocalDate,

    val endDate: LocalDate,

    val localeCode: String,

    val closedAt: LocalDateTime?,

    val createdAt: LocalDateTime,

    val members: List<JourneyMemberResponse>,
) {
    companion object {
        fun of(journey: Journey, members: List<JourneyMemberResponse>): JourneyResponse {
            require(journey.slug.isNotBlank()) { "Journey slug cannot be blank" }
            require(journey.createdAt != null) { "Journey createdAt cannot be null" }
            return JourneyResponse(
                slug = journey.slug,
                baseCurrency = journey.baseCurrency,
                quoteCurrency = journey.quoteCurrency,
                exchangeRate = journey.exchangeRate,
                title = journey.title,
                startDate = journey.startDate,
                endDate = journey.endDate,
                localeCode = journey.localeCode,
                closedAt = journey.closedAt,
                createdAt = journey.createdAt,
                members = members
            )
        }
    }

}
