package kr.paytogether.journey

import jakarta.validation.Valid
import kr.paytogether.journey.dto.JourneyCreate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class JourneyController(
    private val journeyService: JourneyService,
) {

    @PostMapping("/journeys")
    suspend fun createJourney(
        @RequestBody @Valid create: JourneyCreate
    ) = journeyService.createJourney(create)
}