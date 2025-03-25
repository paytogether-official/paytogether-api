package kr.paytogether.journey.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Table(name = "journey_expense")
data class JourneyExpense(
    @Id val journeyExpenseId: Long? = null,

    val journeyId: String,

    val expensePayerId: Long,

    val category: String,

    val expenseDate: LocalDate,

    val currency: String,

    val amount: BigDecimal,

    val memo: String,

    @CreatedDate
    val createdAt: LocalDateTime? = null,
)
