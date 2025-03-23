package kr.paytogether.shared.exception

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.server.ServerWebInputException

@Component
class GlobalErrorAttributes: DefaultErrorAttributes() {
    override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): MutableMap<String, out Any?> {
        val errorAttributes = super.getErrorAttributes(request, options)
        val error: Throwable = super.getError(request)

        return when (error) {
            is PaytogetherException -> mutableMapOf(
                "message" to error.message,
                "errorCode" to error.errorCode,
                "status" to error.status.value(),
                "details" to error.details,
                "data" to error.data,
            )
            is WebExchangeBindException -> mutableMapOf(
                "message" to error.bindingResult.fieldErrors.joinToString { it.defaultMessage.orEmpty() },
                "errorCode" to ErrorCode.VALIDATION_ERROR,
                "status" to errorAttributes["status"] as Int,
                "details" to error.bindingResult.fieldErrors,
                "data" to null,
            )
            is ServerWebInputException -> mutableMapOf(
                "message" to error.reason,
                "errorCode" to ErrorCode.VALIDATION_ERROR,
                "status" to errorAttributes["status"] as Int,
                "details" to error.cause?.localizedMessage,
                "data" to null,
            )
            else -> mutableMapOf(
                "message" to error.localizedMessage,
                "errorCode" to ErrorCode.UNKNOWN,
                "status" to errorAttributes["status"] as Int,
                "details" to null,
                "data" to null,
            )
        }
    }
}