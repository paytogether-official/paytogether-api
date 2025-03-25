package kr.paytogether.journey.dto

import jakarta.validation.constraints.NotBlank
import kr.paytogether.journey.entity.JourneyMember

data class JourneyMemberCreate(
    @field:NotBlank(message = "name must not be blank")
    val name: String
) {
    fun toEntity(journeyId: String) = JourneyMember(
        journeyId = journeyId,
        name = name
    )
}
