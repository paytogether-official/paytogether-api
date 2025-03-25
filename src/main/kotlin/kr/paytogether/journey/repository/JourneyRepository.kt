package kr.paytogether.journey.repository

import kr.paytogether.journey.entity.Journey
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface JourneyRepository : CoroutineCrudRepository<Journey, String> {


    @Query("""
        INSERT INTO journey (
            journey_id,
            base_currency,
            quote_currency,
            exchange_rate,
            title,
            start_date,
            end_date,
            locale_code
        ) VALUES (
            :#{#journey.journeyId},
            :#{#journey.baseCurrency},
            :#{#journey.quoteCurrency},
            :#{#journey.exchangeRate},
            :#{#journey.title},
            :#{#journey.startDate},
            :#{#journey.endDate},
            :#{#journey.localeCode}
        )
    """)
    suspend fun create(journey: Journey): Journey

    suspend fun existsByJourneyId(journeyId: String): Boolean

    suspend fun findByJourneyId(journeyId: String): Journey?

    suspend fun findByJourneyIdIn(slugs: List<String>): List<Journey>
}