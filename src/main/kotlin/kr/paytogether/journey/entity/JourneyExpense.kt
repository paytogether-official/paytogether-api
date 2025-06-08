package kr.paytogether.journey.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@Table(name = "journey_expense")
data class JourneyExpense(
    @Id val journeyExpenseId: Long? = null,

    val journeyId: String,

    val expensePayerId: Long,

    val category: String,

    val categoryDescription: String,

    val expenseDate: LocalDate,

    val baseCurrency: String,

    val amount: BigDecimal,

    val remainingAmount: BigDecimal,

    val memo: String,

    val deletedAt: Instant? = null,

    @CreatedDate
    @LastModifiedDate
    val updatedAt: Instant? = null,

    @CreatedDate
    val createdAt: Instant? = null,
)
