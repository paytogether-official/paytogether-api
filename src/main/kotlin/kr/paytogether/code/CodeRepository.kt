package kr.paytogether.code

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CodeRepository : CoroutineCrudRepository<Code, Long> {
    suspend fun findByDeletedAtIsNull(): List<Code>

    suspend fun findByGroupCodeAndDeletedAtIsNull(groupCode: String): List<Code>
}