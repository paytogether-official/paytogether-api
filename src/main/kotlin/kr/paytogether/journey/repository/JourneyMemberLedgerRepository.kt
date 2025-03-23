package kr.paytogether.journey.repository

import kr.paytogether.journey.entity.JourneyMemberLedger
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface JourneyMemberLedgerRepository : CoroutineCrudRepository<JourneyMemberLedger,Long>