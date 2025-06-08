package kr.paytogether.journey.dto

import org.hibernate.validator.constraints.Length
import org.springframework.format.annotation.DateTimeFormat
import java.math.BigDecimal
import java.time.LocalDate

data class JourneyExpenseUpdate(
    val payerName: String? = null,

    val category: String? = null,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val expenseDate: LocalDate? = null,

    val amount: BigDecimal? = null,

    @field:Length(max = 25)
    val memo: String? = null,

    val members: List<JourneyLedgerCreate> = emptyList(),
)
