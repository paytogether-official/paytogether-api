package kr.paytogether.journey.repository

import kr.paytogether.journey.entity.JourneySettlement
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface JourneySettlementRepository: CoroutineCrudRepository<JourneySettlement, Long> {
    suspend fun findByJourneyId(journeyId: String): List<JourneySettlement>

    suspend fun existsByJourneyId(journeyId: String): Boolean

    suspend fun deleteByJourneyId(journeyId: String): Int
}