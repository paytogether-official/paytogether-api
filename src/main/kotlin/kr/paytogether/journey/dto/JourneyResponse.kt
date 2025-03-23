package kr.paytogether.journey.dto

import kr.paytogether.journey.entity.Journey
import kr.paytogether.journey.entity.JourneyMember
import java.math.BigDecimal
import java.time.LocalDate

data class JourneyResponse(
    val slug: String,

    val baseCurrency: String,

    val quoteCurrency: String,

    val exchangeRate: BigDecimal,

    val title: String,

    val startDate: LocalDate,

    val endDate: LocalDate,

    val localeCode: String,

    val members: List<JourneyMemberResponse>,
) {
    companion object {
        fun of(journey: Journey, members: List<JourneyMemberResponse>) = JourneyResponse(
            slug = journey.slug,
            baseCurrency = journey.baseCurrency,
            quoteCurrency = journey.quoteCurrency,
            exchangeRate = journey.exchangeRate,
            title = journey.title,
            startDate = journey.startDate,
            endDate = journey.endDate,
            localeCode = journey.localeCode,
            members = members
        )
    }

}
