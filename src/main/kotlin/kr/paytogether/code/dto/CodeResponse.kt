package kr.paytogether.code.dto

import kr.paytogether.code.Code

data class CodeResponse(
    val groupCode: String,
    val parentCode: String,
    val code: String,
    val value: String,
    val description: String?,
    val sort: Int,
    val children: List<CodeResponse>
) {
    companion object {
        fun of(code: Code, children: List<CodeResponse> = emptyList()): CodeResponse {
            return CodeResponse(
                groupCode = code.groupCode,
                parentCode = code.parentCode,
                code = code.code,
                value = code.value,
                description = code.description,
                sort = code.sort,
                children = children
            )
        }
    }
}
