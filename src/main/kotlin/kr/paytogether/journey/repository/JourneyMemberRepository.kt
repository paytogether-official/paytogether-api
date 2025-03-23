package kr.paytogether.journey.repository

import kr.paytogether.journey.entity.JourneyMember
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface JourneyMemberRepository : CoroutineCrudRepository<JourneyMember, Long>