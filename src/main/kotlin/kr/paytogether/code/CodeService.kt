package kr.paytogether.code

import kr.paytogether.code.dto.CodeResponse
import org.springframework.stereotype.Service

@Service
class CodeService(
    private val codeRepository: CodeRepository,
) {
    suspend fun getCodes(): List<CodeResponse> {
        val codes = codeRepository.findByDeletedAtIsNull()

        return codes.map {
            CodeResponse.of(it, buildTree(it.code, codes))
        }
    }

    suspend fun getCodesByGroupCode(groupCode: String): List<CodeResponse> {
        val codes = codeRepository.findByGroupCodeAndDeletedAtIsNull(groupCode)
        return codes.map {
            CodeResponse.of(it, buildTree(it.code, codes))
        }
    }

    private fun buildTree(parent: String, codes: List<Code>): List<CodeResponse> {
        if (codes.isEmpty()) {
            return emptyList()
        }

        val children = codes.filter { it.parentCode == parent }
        return children.map {
            CodeResponse.of(it, buildTree(it.code, codes))
        }
    }
}