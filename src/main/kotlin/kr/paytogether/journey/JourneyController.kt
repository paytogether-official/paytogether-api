package kr.paytogether.journey

import jakarta.validation.Valid
import kr.paytogether.journey.dto.JourneyCreate
import kr.paytogether.journey.dto.JourneyExpenseCreate
import kr.paytogether.journey.dto.JourneyExpenseUpdate
import org.springframework.http.HttpStatus
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

    @GetMapping("/journeys/{journeyId:[a-z0-9]+}")
    suspend fun getJourney(
        @PathVariable journeyId: String
    ) = journeyService.getJourney(journeyId)

    @GetMapping("/journeys")
    suspend fun getJourneys(
        @RequestParam journeyIds: List<String>
    ) = journeyService.getJourneys(journeyIds)

    @PostMapping("/journeys/{journeyId:[a-z0-9]+}/close")
    suspend fun closeJourney(
        @PathVariable journeyId: String
    ) = journeyService.closeJourney(journeyId)

    @PostMapping("/journeys/{journeyId:[a-z0-9]+}/expenses")
    suspend fun createExpense(
        @PathVariable journeyId: String,
        @Valid @RequestBody create: JourneyExpenseCreate,
    ) = journeyExpenseService.createExpense(journeyId, create)

    @GetMapping("/journeys/{journeyId:[a-z0-9]+}/expenses")
    suspend fun getJourneyExpenses(
        @PathVariable journeyId: String,
    ) = journeyExpenseService.getExpenses(journeyId)

    @GetMapping("/journeys/{journeyId:[a-z0-9]+}/expenses/{expenseId:[0-9]+}")
    suspend fun getJourneyExpense(
        @PathVariable journeyId: String,
        @PathVariable expenseId: Long,
    ) = journeyExpenseService.getExpense(journeyId, expenseId)

    @PatchMapping("/journeys/{journeyId:[a-z0-9]+}/expenses/{expenseId:[0-9]+}")
    suspend fun updateExpense(
        @PathVariable journeyId: String,
        @PathVariable expenseId: Long,
        @Valid @RequestBody update: JourneyExpenseUpdate,
    ) = journeyExpenseService.updateExpense(journeyId, expenseId, update)

    @DeleteMapping("/journeys/{journeyId:[a-z0-9]+}/expenses/{expenseId:[0-9]+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteExpense(
        @PathVariable journeyId: String,
        @PathVariable expenseId: Long,
    ) = journeyExpenseService.deleteExpense(journeyId, expenseId)
}