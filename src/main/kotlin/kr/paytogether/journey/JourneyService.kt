package kr.paytogether.journey

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kr.paytogether.journey.dto.*
import kr.paytogether.journey.repository.*
import kr.paytogether.shared.exception.NotFoundException
import java.security.MessageDigest
import java.time.Instant

@Service
class JourneyService(
    private val journeyRepository: JourneyRepository,
    private val journeyMemberRepository: JourneyMemberRepository,
) {

    @Transactional
    suspend fun createJourney(create: JourneyCreate): JourneyResponse {
        val journeyId = generateSlug()
        journeyRepository.create(create.toEntity(journeyId))
        val journey = journeyRepository.findByJourneyId(journeyId) ?: throw NotFoundException("Journey not found by journeyId: $journeyId")
        val members = create.members.map { it.toEntity(journey.journeyId) }.let {
            journeyMemberRepository.saveAll(it).map { member -> JourneyMemberResponse.from(member) }.toList()
        }

        return JourneyResponse.of(journey, members)
    }

    @Transactional(readOnly = true)
    suspend fun getJourney(journeyId: String): JourneyResponse {
        val journey = journeyRepository.findByJourneyId(journeyId) ?: throw NotFoundException("Journey not found by journeyId: $journeyId")
        val members = journeyMemberRepository.findByJourneyId(journey.journeyId).map { JourneyMemberResponse.from(it) }.toList()
        return JourneyResponse.of(journey, members)
    }

    @Transactional(readOnly = true)
    suspend fun getJourneys(journeyIds: List<String>): List<JourneyResponse> {
        val journeys = journeyRepository.findByJourneyIdIn(journeyIds)
        val memberMap = journeyMemberRepository.findByJourneyIdIn(journeyIds).groupBy { it.journeyId }

        return journeys.map { journey ->
            val members = memberMap[journey.journeyId]?.map { JourneyMemberResponse.from(it) } ?: emptyList()
            JourneyResponse.of(journey, members)
        }
    }

    private suspend fun generateSlug(): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hashBytes = md.digest(Instant.now().toEpochMilli().toString().toByteArray())
        val hashHex = hashBytes.joinToString("") { "%02x".format(it) }
        val slug = hashHex.substring(0, 8)
        return if (journeyRepository.existsByJourneyId(slug)) {
            generateSlug()
        } else {
            slug
        }
    }
}