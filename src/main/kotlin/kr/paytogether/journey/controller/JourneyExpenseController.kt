package kr.paytogether.journey.controller

import jakarta.validation.Valid
import kr.paytogether.journey.JourneyExpenseService
import kr.paytogether.journey.JourneyService
import kr.paytogether.journey.dto.JourneyExpenseCreate
import kr.paytogether.journey.dto.JourneyExpenseUpdate
import kr.paytogether.journey.dto.JourneyExpensesResponse
import kr.paytogether.shared.exception.BadRequestException
import kr.paytogether.shared.exception.ErrorCode
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
class JourneyExpenseController(
    private val journeyService: JourneyService,
    private val journeyExpenseService: JourneyExpenseService,
) {
    @PostMapping("/journeys/{journeyId:[a-z0-9]+}/expenses")
    suspend fun createExpense(
        @PathVariable journeyId: String,
        @Valid @RequestBody create: JourneyExpenseCreate,
    ) = journeyExpenseService.createExpense(journeyId, create)

    @GetMapping("/journeys/{journeyId:[a-z0-9]+}/expenses")
    suspend fun getJourneyExpenses(
        @PathVariable journeyId: String,
        @RequestParam quoteCurrency: String = "KRW",
        @RequestParam category: String?,
        @RequestParam expenseDate: String?, // LocalDate format (yyyy-MM-dd) | "OTHER"
        @PageableDefault(sort = ["expenseDate"], direction = Sort.Direction.DESC, size = Int.MAX_VALUE) pageable: Pageable,
    ): JourneyExpensesResponse {
        fun validateExpenseDate(date: String?): Boolean {
            return when (date) {
                null, "OTHER" -> true
                else -> try {
                    LocalDate.parse(date)
                    true
                } catch (e: Exception) {
                    false
                }
            }
        }
        if (validateExpenseDate(expenseDate).not()) {
            throw BadRequestException(
                ErrorCode.VALIDATION_ERROR,
                "Invalid expenseDate format. Use 'yyyy-MM-dd' or 'OTHER'."
            )
        }

        return journeyExpenseService.getExpenses(journeyId, quoteCurrency, category, expenseDate, pageable)
    }

    @GetMapping("/journeys/{journeyId:[a-z0-9]+}/expenses/categories")
    suspend fun getJourneyExpenseCategories(
        @PathVariable journeyId: String,
    ) = journeyExpenseService.getExpenseCategories(journeyId)

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