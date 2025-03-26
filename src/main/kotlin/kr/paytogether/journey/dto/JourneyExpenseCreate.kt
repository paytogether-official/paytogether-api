package kr.paytogether.journey.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import kr.paytogether.journey.entity.JourneyExpense
import org.hibernate.validator.constraints.*
import org.springframework.format.annotation.DateTimeFormat
import java.math.BigDecimal
import java.time.LocalDate

data class JourneyExpenseCreate(
    @field:NotNull
    val payerName: String,

    @field:NotBlank
    val category: String,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val expenseDate: LocalDate,

    @field:NotBlank
    val currency: String,

    @field:NotNull
    @field:PositiveOrZero
    @field:Max(1_000_000_000)
    val amount: BigDecimal,

    @field:Length(max = 25)
    val memo: String = "",

    val members: List<JourneyLedgerCreate> = emptyList(),
) {
    fun toEntity(journeyId: String, expensePayerId: Long): JourneyExpense {
        return JourneyExpense(
            journeyId = journeyId,
            expensePayerId = expensePayerId,
            category = category,
            expenseDate = expenseDate,
            currency = currency,
            amount = amount,
            memo = memo,
        )
    }
}
