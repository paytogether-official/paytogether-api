package kr.paytogether.journey.dto

import jakarta.validation.constraints.*
import kr.paytogether.journey.entity.Journey
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.format.annotation.DateTimeFormat.*
import java.math.BigDecimal
import java.security.MessageDigest
import java.time.Instant
import java.time.LocalDate

data class JourneyCreate(
    @field:NotBlank(message = "baseCurrency must not be blank")
    val baseCurrency: String,

    val quoteCurrency: String = "KRW",

    @field:DecimalMin(value = "0.0", message = "exchangeRate must be greater than or equal to 0")
    val exchangeRate: BigDecimal,

    @field:NotBlank(message = "title must not be blank")
    val title: String,

    @field:DateTimeFormat(pattern = "yyyy-MM-dd")
    val startDate: LocalDate,

    @field:DateTimeFormat(pattern = "yyyy-MM-dd")
    val endDate: LocalDate,

    @field:NotBlank(message = "localeCode must not be blank")
    val localeCode: String,

    @field:Size(min = 0, max = 30, message = "members size must be between 0 and 30")
    val members: List<JourneyMemberCreate>
) {
    init {
        require(!members.hasDuplicateName()) { "중복된 이름이 존재합니다." }
        require(startDate.isBefore(endDate)) { "시작일은 종료일보다 이전이어야 합니다." }
    }

    fun toEntity() = Journey(
        baseCurrency = baseCurrency,
        quoteCurrency = quoteCurrency,
        exchangeRate = exchangeRate,
        title = title,
        startDate = startDate,
        endDate = endDate,
        localeCode = localeCode,
        slug = generateSlug(),
    )

    private fun generateSlug(): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hashBytes = md.digest(Instant.now().toEpochMilli().toString().toByteArray())
        val hashHex = hashBytes.joinToString("") { "%02x".format(it) }
        return hashHex.substring(0, 8)
    }

    private fun List<JourneyMemberCreate>.hasDuplicateName() =
        this.groupingBy { it.name }.eachCount().any { it.value > 1 }
}
