package kr.paytogether.journey.repository

import kr.paytogether.journey.entity.JourneyMemberLedger
import kr.paytogether.journey.projection.JourneyLedgerSumProjection
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface JourneyMemberLedgerRepository : CoroutineCrudRepository<JourneyMemberLedger,Long> {
    suspend fun findByJourneyExpenseIdAndDeletedAtIsNull(journeyExpenseId: Long): List<JourneyMemberLedger>

    @Query("""
        SELECT journey_member_id, SUM(amount) AS amount
        FROM journey_member_ledger
        WHERE journey_id = :journeyId AND deleted_at IS NULL
        GROUP BY journey_member_id
    """)
    suspend fun findJourneyLedgerSum(journeyId: String): List<JourneyLedgerSumProjection>

    @Modifying
    @Query("""
        UPDATE journey_member_ledger
        SET deleted_at = NOW()
        WHERE journey_expense_id = :journeyExpenseId AND deleted_at IS NULL
    """)
    suspend fun deleteByJourneyExpenseId(journeyExpenseId: Long)
}