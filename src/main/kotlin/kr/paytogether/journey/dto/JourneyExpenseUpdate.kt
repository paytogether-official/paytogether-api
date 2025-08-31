package kr.paytogether.journey.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import org.hibernate.validator.constraints.Length
import org.springframework.format.annotation.DateTimeFormat
import java.math.BigDecimal
import java.time.LocalDate

data class JourneyExpenseUpdate(
    val payerName: String? = null,

    val category: String? = null,

    @field:Length(max = 255)
    val categoryDescription: String? = null,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val expenseDate: LocalDate? = null,

    val amount: BigDecimal? = null,

    @field:NotNull
    @field:PositiveOrZero
    @field:Max(1_000_000_000)
    val remainingAmount: BigDecimal,

    @field:Length(max = 25)
    val memo: String? = null,

    val members: List<JourneyLedgerCreate> = emptyList(),
)
