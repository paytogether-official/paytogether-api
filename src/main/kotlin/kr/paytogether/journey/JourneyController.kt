package kr.paytogether.journey

import jakarta.validation.Valid
import kr.paytogether.journey.dto.JourneyCreate
import kr.paytogether.journey.dto.JourneyExpenseCreate
import org.springframework.web.bind.annotation.*

@RestController
class JourneyController(
    private val journeyService: JourneyService,
    private val journeyExpenseService: JourneyExpenseService,
) {
    @PostMapping("/journeys")
    suspend fun createJourney(
        @RequestBody @Valid create: JourneyCreate
    ) = journeyService.createJourney(create)

    @GetMapping("/journeys/{slug:[a-z0-9-]+}")
    suspend fun getJourney(
        @PathVariable slug: String
    ) = journeyService.getJourney(slug)

    @GetMapping("/journeys/{slugs}")
    suspend fun getJourneys(
        @PathVariable slugs: List<String>
    ) = journeyService.getJourneys(slugs)

    @PostMapping("/journeys/{slug:[a-z0-9-]+}/expenses")
    suspend fun createExpense(
        @PathVariable slug: String,
        @RequestBody @Valid create: JourneyExpenseCreate,
    ) = journeyExpenseService.createExpense(slug, create)
}