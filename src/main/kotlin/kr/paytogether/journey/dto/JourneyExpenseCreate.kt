package kr.paytogether.journey.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import kr.paytogether.journey.entity.JourneyExpense
import kr.paytogether.journey.enums.Category
import kr.paytogether.shared.exception.BadRequestException
import kr.paytogether.shared.exception.ErrorCode
import kr.paytogether.shared.utils.notEqIgnoreScale
import org.hibernate.validator.constraints.Length
import org.springframework.format.annotation.DateTimeFormat
import java.math.BigDecimal
import java.time.LocalDate

data class JourneyExpenseCreate(
    @field:NotNull
    val payerName: String,

    val category: Category,

    @field:Length(max = 255)
    var categoryDescription: String = category.value,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val expenseDate: LocalDate,

    @field:NotBlank
    val currency: String,

    @field:NotNull
    @field:PositiveOrZero
    @field:Max(1_000_000_000)
    val amount: BigDecimal,

    @field:NotNull
    @field:PositiveOrZero
    @field:Max(1_000_000_000)
    val remainingAmount: BigDecimal,

    @field:Length(max = 25)
    val memo: String = "",

    val members: List<JourneyLedgerCreate> = emptyList(),
) {
    init {
        // 요청 받은 금액과 멤버 금액 합계가 다름
        if (amount notEqIgnoreScale (members.sumOf { it.amount } + remainingAmount)) {
            throw BadRequestException(
                ErrorCode.VALIDATION_ERROR,
                "Amount is not matched, expected: ${amount}, actual: ${members.sumOf { it.amount } + remainingAmount}"
            )
        }

        // 남은 금액이 0 이상이면 모든 금액은 동일해야함
        if (remainingAmount > BigDecimal.ZERO && members.any { it.amount notEqIgnoreScale members.first().amount }) {
            throw BadRequestException(
                ErrorCode.VALIDATION_ERROR,
                "Remaining amount is not matched, expected: ${members.first().amount}, actual: ${members.joinToString { it.amount.toString() }}"
            )
        }

        if (categoryDescription.isBlank()) {
            categoryDescription = category.value
        }
    }

    fun toEntity(journeyId: String, expensePayerId: Long): JourneyExpense {
        return JourneyExpense(
            journeyId = journeyId,
            expensePayerId = expensePayerId,
            category = category.value,
            categoryDescription = categoryDescription,
            expenseDate = expenseDate,
            currency = currency,
            amount = amount,
            remainingAmount = remainingAmount,
            memo = memo,
        )
    }
}
