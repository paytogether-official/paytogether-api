package kr.paytogether.journey.repository

import kr.paytogether.journey.entity.JourneyMember
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface JourneyMemberRepository : CoroutineCrudRepository<JourneyMember, Long> {
    suspend fun findByJourneyId(journeyId: String): List<JourneyMember>

    suspend fun findByJourneyIdIn(journeyIds: List<String>): List<JourneyMember>
}