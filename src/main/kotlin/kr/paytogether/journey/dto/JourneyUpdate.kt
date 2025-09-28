package kr.paytogether.journey.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.math.BigDecimal
import java.time.LocalDate

data class JourneyUpdate(
    @field:DateTimeFormat(pattern = "yyyy-MM-dd")
    val startDate: LocalDate? = null,

    @field:DateTimeFormat(pattern = "yyyy-MM-dd")
    val endDate: LocalDate? = null,

    @field:DecimalMin(value = "0.0", message = "exchangeRate must be greater than or equal to 0")
    val exchangeRate: BigDecimal? = null,

    @field:Size(min = 0, max = 30, message = "members size must be between 0 and 30")
    val members: List<JourneyMemberCreate>? = null,
) {
    init {
        require(members == null || !members.hasDuplicateName()) { "중복된 이름이 존재합니다." }
        require(startDate == null || endDate == null || startDate <= endDate) { "시작일은 종료일보다 이전이거나 같아야 합니다. startDate: $startDate, endDate: $endDate" }
    }

    private fun List<JourneyMemberCreate>.hasDuplicateName() =
        this.groupingBy { it.name }.eachCount().any { it.value > 1 }
}
