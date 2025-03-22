package kr.paytogether.code

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("code")
data class Code(
    @Id val codeId: Long,

    val groupCode: String,

    val parentCode: String,

    val code: String,

    val value: String,

    val description: String?,

    val sort: Int,

    val deletedAt: LocalDateTime?,

    val createdAt: LocalDateTime
)
