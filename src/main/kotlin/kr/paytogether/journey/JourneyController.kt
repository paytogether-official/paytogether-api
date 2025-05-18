package kr.paytogether.journey

import jakarta.validation.Valid
import kr.paytogether.journey.dto.JourneyCreate
import kr.paytogether.journey.dto.JourneyExpenseCreate
import kr.paytogether.journey.dto.JourneyExpenseUpdate
import kr.paytogether.journey.dto.JourneyUpdate
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
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
    ) = journeyService.getSettlement(journeyId)

    @PostMapping("/journeys/{journeyId:[a-z0-9]+}/expenses")
    suspend fun createExpense(
        @PathVariable journeyId: String,
        @Valid @RequestBody create: JourneyExpenseCreate,
    ) = journeyExpenseService.createExpense(journeyId, create)

    @GetMapping("/journeys/{journeyId:[a-z0-9]+}/expenses")
    suspend fun getJourneyExpenses(
        @PathVariable journeyId: String,
        @RequestParam quoteCurrency: String = "KRW",
        @PageableDefault(sort = ["expenseDate"], direction = Sort.Direction.DESC, size = Int.MAX_VALUE) pageable: Pageable,
    ) = journeyExpenseService.getExpenses(journeyId, quoteCurrency, pageable)

    @GetMapping("/journeys/{journeyId:[a-z0-9]+}/expenses/{expenseId:[0-9]+}")
    suspend fun getJourneyExpense(
        @PathVariable journeyId: String,
        @PathVariable expenseId: Long,
        @RequestParam quoteCurrency: String = "KRW",
    ) = journeyExpenseService.getExpense(journeyId, expenseId, quoteCurrency)

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