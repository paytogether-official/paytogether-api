package kr.paytogether.journey.dto

import kr.paytogether.journey.entity.JourneySettlement
import java.math.BigDecimal
import java.math.RoundingMode

data class JourneySettlementResultResponse(
    val journeyId: String,
    val settlements: List<JourneySettlementResponse>,
    val expenseCategories: List<ExpenseCategoryResponse>,
    val memberExpenses: List<MemberExpenseResponse>,
) {
    companion object {
        fun of(
            journeyId: String,
            settlements: List<JourneySettlementResponse>,
            expenseCategories: List<ExpenseCategoryResponse>,
            memberExpenses: List<MemberExpenseResponse>,
        ): JourneySettlementResultResponse =
            JourneySettlementResultResponse(
                journeyId = journeyId,
                settlements = settlements,
                expenseCategories = expenseCategories,
                memberExpenses = memberExpenses,
            )
    }
}

data class JourneySettlementResponse(
    val fromMemberId: Long,
    val fromMemberName: String,
    val toMemberId: Long,
    val toMemberName: String,
    val amount: BigDecimal,
) {

    companion object {
        fun of(
            settlement: JourneySettlement,
            fromMemberName: String,
            toMemberName: String,
        ): JourneySettlementResponse =
            JourneySettlementResponse(
                fromMemberId = settlement.fromMemberId,
                fromMemberName = fromMemberName,
                toMemberId = settlement.toMemberId,
                toMemberName = toMemberName,
                amount = settlement.amount
            )
    }
}

data class ExpenseCategoryResponse(
    val name: String,
    val amount: BigDecimal,
    val percentage: BigDecimal,
) {
    companion object {
        fun of(
            name: String,
            amount: BigDecimal,
            percentage: BigDecimal,
        ): ExpenseCategoryResponse =
            ExpenseCategoryResponse(
                name = name,
                amount = amount,
                percentage = percentage.setScale(2, RoundingMode.HALF_UP),
            )
    }
}

data class MemberExpenseResponse(
    val name: String,
    val amount: BigDecimal,
) {
    companion object {
        fun of(
            name: String,
            amount: BigDecimal,
        ) = MemberExpenseResponse(
            name = name,
            amount = amount.setScale(2, RoundingMode.HALF_UP),
        )
    }
}