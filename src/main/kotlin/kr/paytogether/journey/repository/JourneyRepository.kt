package kr.paytogether.journey.repository

import kr.paytogether.journey.entity.Journey
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface JourneyRepository : CoroutineCrudRepository<Journey, Long> {
    suspend fun findBySlug(slug: String): Journey?

    suspend fun findBySlugIn(slugs: List<String>): List<Journey>
}