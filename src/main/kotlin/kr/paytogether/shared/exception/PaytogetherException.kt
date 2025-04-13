package kr.paytogether.shared.exception

import org.springframework.http.HttpStatus

open class PaytogetherException(
    open val errorCode: ErrorCode,
    override val message: String,
    val status: HttpStatus,
    open val details: String? = null,
    open val data: Any? = null,
): RuntimeException(message) {
    constructor(errorCode: ErrorCode, message: String): this(errorCode, message, errorCode.code)

    override fun toString(): String {
        return "PaytogetherException(message='$message', errorCode='$errorCode', status=$status, details=$details, data=$data)"
    }
}

data class BadRequestException(
    override val errorCode: ErrorCode,
    override val message: String,
    override val details: String? = null,
    override val data: Any? = null,
): PaytogetherException(
    errorCode = errorCode,
    message = message,
    status = HttpStatus.BAD_REQUEST,
    details = details,
    data = data
)

data class NotFoundException(
    override val message: String,
    override val details: String? = null,
    override val data: Any? = null,
): PaytogetherException(
    errorCode = ErrorCode.BAD_REQUEST,
    message = message,
    status = HttpStatus.NOT_FOUND,
    details = details,
    data = data
)

data class ConflictException(
    override val message: String,
    override val details: String? = null,
    override val data: Any? = null,
): PaytogetherException(
    errorCode = ErrorCode.BAD_REQUEST,
    message = message,
    status = HttpStatus.CONFLICT,
    details = details,
    data = data
)

data class FeignException(
    override val message: String,
    override val details: String? = null,
    override val data: Any? = null,
): PaytogetherException(
    errorCode = ErrorCode.FEIGN_ERROR,
    message = message,
    status = HttpStatus.INTERNAL_SERVER_ERROR,
    details = details,
    data = data
)