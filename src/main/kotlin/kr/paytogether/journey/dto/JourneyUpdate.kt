package kr.paytogether.journey.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class JourneyUpdate(
    @field:DateTimeFormat(pattern = "yyyy-MM-dd")
    val startDate: LocalDate,

    @field:DateTimeFormat(pattern = "yyyy-MM-dd")
    val endDate: LocalDate,

    @field:NotBlank(message = "baseCurrency must not be blank")
    @field:Pattern(regexp = "^[A-Z]{3}$", message = "baseCurrency must be a 3-letter currency code")
    val baseCurrency: String,

    @field:Size(min = 0, max = 30, message = "members size must be between 0 and 30")
    val members: List<JourneyMemberCreate>
) {
    init {
        require(!members.hasDuplicateName()) { "중복된 이름이 존재합니다." }
        require(startDate.isBefore(endDate)) { "시작일은 종료일보다 이전이어야 합니다." }
    }

    private fun List<JourneyMemberCreate>.hasDuplicateName() =
        this.groupingBy { it.name }.eachCount().any { it.value > 1 }
}
