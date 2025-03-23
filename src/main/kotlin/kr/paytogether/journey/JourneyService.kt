package kr.paytogether.journey

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kr.paytogether.journey.dto.*
import kr.paytogether.journey.repository.*

@Service
class JourneyService(
    private val journeyRepository: JourneyRepository,
    private val journeyMemberRepository: JourneyMemberRepository,
) {

    @Transactional
    suspend fun createJourney(create: JourneyCreate): JourneyResponse {
        val journey = journeyRepository.save(create.toEntity())
        require(journey.journeyId != null)
        val members = create.members.map { it.toEntity(journey.journeyId) }.let {
            journeyMemberRepository.saveAll(it).map { member ->  JourneyMemberResponse.from(member) }.toList()
        }

        return JourneyResponse.of(journey, members)
    }
}