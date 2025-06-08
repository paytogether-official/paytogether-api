package kr.paytogether.journey.controller

import jakarta.validation.Valid
import kr.paytogether.journey.JourneyExpenseService
import kr.paytogether.journey.JourneyService
import kr.paytogether.journey.dto.JourneyCreate
import kr.paytogether.journey.dto.JourneyUpdate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class JourneyController(
    private val journeyService: JourneyService,
    private val journeyExpenseService: JourneyExpenseService,
) {
    @PostMapping("/journeys")
    suspend fun createJourney(
        @RequestBody @Valid create: JourneyCreate,
    ) = journeyService.createJourney(create)

    @GetMapping("/journeys/{journeyId:[a-z0-9]+}")
    suspend fun getJourney(
        @PathVariable journeyId: String,
        @RequestParam quoteCurrency: String = "KRW",
    ) = journeyService.getJourney(journeyId, quoteCurrency)

    @GetMapping("/journeys")
    suspend fun getJourneys(
        @RequestParam journeyIds: List<String>,
    ) = journeyService.getJourneys(journeyIds)

    @PatchMapping("/journeys/{journeyId:[a-z0-9]+}")
    suspend fun updateJourney(
        @PathVariable journeyId: String,
        @Valid @RequestBody update: JourneyUpdate,
    ) = journeyService.updateJourney(journeyId, update)

    @PostMapping("/journeys/{journeyId:[a-z0-9]+}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun closeJourney(
        @PathVariable journeyId: String,
    ) = journeyService.closeJourney(journeyId)

    @PostMapping("/journeys/{journeyId:[a-z0-9]+}/reopen")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun reopenJourney(
        @PathVariable journeyId: String,
    ) = journeyService.reopen(journeyId)

    @GetMapping("/journeys/{journeyId:[a-z0-9]+}/settlement")
    suspend fun getJourneySettlement(
        @PathVariable journeyId: String,
        @RequestParam quoteCurrency: String = "KRW",
    ) = journeyService.getSettlement(journeyId, quoteCurrency)
}