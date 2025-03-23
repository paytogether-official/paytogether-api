package kr.paytogether.journey.dto

import kr.paytogether.journey.entity.JourneyMember

data class JourneyMemberResponse(
    val name: String,
) {
    companion object {
        fun from(member: JourneyMember) = JourneyMemberResponse(
            name = member.name,
        )
    }
}
