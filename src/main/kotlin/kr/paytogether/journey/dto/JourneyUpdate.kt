package kr.paytogether.journey.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class JourneyUpdate(
    @field:DateTimeFormat(pattern = "yyyy-MM-dd")
    val startDate: LocalDate? = null,

    @field:DateTimeFormat(pattern = "yyyy-MM-dd")
    val endDate: LocalDate? = null,

    val baseCurrency: String? = null,

    @field:Size(min = 0, max = 30, message = "members size must be between 0 and 30")
    val members: List<JourneyMemberCreate>? = null,
) {
    init {
        require(members == null || !members.hasDuplicateName()) { "중복된 이름이 존재합니다." }
        require(startDate == null || endDate == null || startDate.isBefore(endDate)) { "시작일은 종료일보다 이전이어야 합니다." }
        require(baseCurrency == null || "^[A-Z]{3}$".toRegex().matches(baseCurrency)) { "baseCurrency must be a 3-letter currency code" }
    }

    private fun List<JourneyMemberCreate>.hasDuplicateName() =
        this.groupingBy { it.name }.eachCount().any { it.value > 1 }
}
