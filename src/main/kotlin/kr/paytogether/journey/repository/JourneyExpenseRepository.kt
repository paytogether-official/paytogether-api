package kr.paytogether.journey.repository

import kotlinx.coroutines.flow.Flow
import kr.paytogether.journey.entity.JourneyExpense
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface JourneyExpenseRepository : CoroutineCrudRepository<JourneyExpense, Long> {
    suspend fun findByJourneyId(journeyId: String): Flow<JourneyExpense>

    suspend fun findByJourneyIdAndJourneyExpenseId(journeyId: String, journeyExpenseId: Long): JourneyExpense?
}