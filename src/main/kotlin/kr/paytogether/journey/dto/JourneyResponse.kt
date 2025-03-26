package kr.paytogether.journey.dto

import kr.paytogether.journey.entity.Journey
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class JourneyResponse(
    val journeyId: String,

    val baseCurrency: String,

    val quoteCurrency: String,

    val exchangeRate: BigDecimal,

    val title: String,

    val startDate: LocalDate,

    val endDate: LocalDate,

    val localeCode: String,

    val journeySettlementId: Long? = null,

    val closedAt: LocalDateTime?,

    val createdAt: LocalDateTime,

    val members: List<JourneyMemberResponse>,
) {
    companion object {
        fun of(journey: Journey, members: List<JourneyMemberResponse>): JourneyResponse {
            require(journey.createdAt != null) { "Journey createdAt cannot be null" }
            return JourneyResponse(
                journeyId = journey.journeyId,
                baseCurrency = journey.baseCurrency,
                quoteCurrency = journey.quoteCurrency,
                exchangeRate = journey.exchangeRate.stripTrailingZeros(),
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
