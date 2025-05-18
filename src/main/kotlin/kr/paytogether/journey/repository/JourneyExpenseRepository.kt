package kr.paytogether.journey.repository

import kotlinx.coroutines.flow.Flow
import kr.paytogether.journey.entity.JourneyExpense
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface JourneyExpenseRepository : CoroutineCrudRepository<JourneyExpense, Long> {
    suspend fun findByJourneyIdAndDeletedAtIsNull(journeyId: String, pageable: Pageable? = null): Flow<JourneyExpense>

    suspend fun findByJourneyIdInAndDeletedAtIsNull(journeyIds: List<String>): List<JourneyExpense>

    suspend fun findByJourneyIdAndJourneyExpenseIdAndDeletedAtIsNull(journeyId: String, journeyExpenseId: Long): JourneyExpense?
}