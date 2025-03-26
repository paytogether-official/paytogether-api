package kr.paytogether.journey.repository

import kr.paytogether.journey.entity.JourneyMemberLedger
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface JourneyMemberLedgerRepository : CoroutineCrudRepository<JourneyMemberLedger,Long> {
    suspend fun findByJourneyExpenseId(journeyExpenseId: Long): List<JourneyMemberLedger>

    @Modifying
    @Query("""
        UPDATE journey_member_ledger
        SET deleted_at = NOW()
        WHERE journey_expense_id = :journeyExpenseId AND deleted_at IS NULL
    """)
    suspend fun deleteByJourneyExpenseId(journeyExpenseId: Long)
}