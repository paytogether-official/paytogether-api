package kr.paytogether.journey

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kr.paytogether.journey.dto.*
import kr.paytogether.journey.repository.*
import kr.paytogether.shared.exception.NotFoundException

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

    suspend fun getJourney(slug: String): JourneyResponse {
        val journey = journeyRepository.findBySlug(slug) ?: throw NotFoundException("Journey not found by slug: $slug")
        val members = journeyMemberRepository.findByJourneyId(journey.journeyId!!).map { JourneyMemberResponse.from(it) }.toList()
        return JourneyResponse.of(journey, members)
    }

    suspend fun getJourneys(slugs: List<String>): List<JourneyResponse> {
        val journeys = journeyRepository.findBySlugIn(slugs)
        val memberMap = journeyMemberRepository.findByJourneyIdIn(journeys.map { it.journeyId!! }).groupBy { it.journeyId }

        return journeys.map { journey ->
            val members = memberMap[journey.journeyId!!]?.map { JourneyMemberResponse.from(it) } ?: emptyList()
            JourneyResponse.of(journey, members)
        }
    }
}