package kr.paytogether.journey.repository

import kotlinx.coroutines.flow.Flow
import kr.paytogether.journey.entity.JourneyExpense
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface JourneyExpenseRepository : CoroutineCrudRepository<JourneyExpense, Long> {
    suspend fun findByJourneyIdAndDeletedAtIsNull(journeyId: String, category: String? = null, pageable: Pageable? = null): Flow<JourneyExpense>

    suspend fun findByJourneyIdInAndDeletedAtIsNull(journeyIds: List<String>): List<JourneyExpense>

    suspend fun findByJourneyIdAndJourneyExpenseIdAndDeletedAtIsNull(journeyId: String, journeyExpenseId: Long): JourneyExpense?

    @Query("""
        SELECT DISTINCT category 
        FROM journey_expense
        WHERE journey_id = :journey AND deleted_at IS NULL
    """)
    suspend fun findDistinctByJourneyIdAndDeletedAtIsNull(journeyId: String): List<String>
}