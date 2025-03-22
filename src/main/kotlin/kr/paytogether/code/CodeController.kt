package kr.paytogether.code

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class CodeController(
    private val codeService: CodeService,
) {

    @GetMapping("/codes")
    suspend fun getCodes() = codeService.getCodes()

    @GetMapping("/codes/{groupCode}")
    suspend fun getCodesByGroupCode(
        @PathVariable groupCode: String
    ) = codeService.getCodesByGroupCode(groupCode)
}