package kr.paytogether.journey.dto

import kr.paytogether.journey.entity.Journey
import kr.paytogether.shared.utils.toLocalDateTime
import kr.paytogether.shared.utils.toLocalDateTimeOrNull
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime

data class JourneyResponseWithExpenseSum(
    val journeyId: String,

    val baseCurrency: String,

    val exchangeRate: BigDecimal,

    val title: String,

    val startDate: LocalDate,

    val endDate: LocalDate,

    val localeCode: String,

    val totalExpenseAmount: BigDecimal,

    val totalExpenseCount: Int,

    val closedAt: LocalDateTime?,

    val createdAt: LocalDateTime,

    val members: List<JourneyMemberResponse>,

    val dailyExpenseSumByDate: List<DailyExpenseSum>,
) {
    companion object {
        fun of(
            journey: Journey,
            members: List<JourneyMemberResponse>,
            totalExpenseAmount: BigDecimal = BigDecimal.ZERO,
            totalExpenseCount: Int = 0,
            dailyExpenseSumByDate: List<DailyExpenseSum>,
        ): JourneyResponseWithExpenseSum {
            require(journey.createdAt != null) { "Journey createdAt cannot be null" }
            return JourneyResponseWithExpenseSum(
                journeyId = journey.journeyId,
                baseCurrency = journey.baseCurrency,
                exchangeRate = journey.exchangeRate.setScale(2, RoundingMode.FLOOR),
                title = journey.title,
                startDate = journey.startDate,
                endDate = journey.endDate,
                localeCode = journey.localeCode,
                closedAt = journey.closedAt.toLocalDateTimeOrNull(),
                createdAt = journey.createdAt.toLocalDateTime(),
                members = members,
                totalExpenseAmount = totalExpenseAmount.setScale(2, RoundingMode.FLOOR),
                totalExpenseCount = totalExpenseCount,
                dailyExpenseSumByDate = dailyExpenseSumByDate
            )
        }
    }
}
