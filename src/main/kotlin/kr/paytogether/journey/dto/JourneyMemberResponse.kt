package kr.paytogether.journey.dto

import kr.paytogether.journey.entity.JourneyMember

data class JourneyMemberResponse(
    val journeyMemberId: Long,
    val name: String,
) {
    companion object {
        fun from(member: JourneyMember) = JourneyMemberResponse(
            journeyMemberId = member.journeyMemberId!!,
            name = member.name,
        )
    }
}
