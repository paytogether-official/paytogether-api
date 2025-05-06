package kr.paytogether.journey.repository

import kr.paytogether.journey.entity.JourneyExpense
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface JourneyExpenseRepository : CoroutineCrudRepository<JourneyExpense, Long> {
    suspend fun findByJourneyIdAndDeletedAtIsNull(journeyId: String): List<JourneyExpense>

    suspend fun findByJourneyIdInAndDeletedAtIsNull(journeyIds: List<String>): List<JourneyExpense>

    suspend fun findByJourneyIdAndJourneyExpenseIdAndDeletedAtIsNull(journeyId: String, journeyExpenseId: Long): JourneyExpense?
}