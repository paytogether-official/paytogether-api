package kr.paytogether.shared.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val code: HttpStatus) {
    UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR),

    BAD_REQUEST(HttpStatus.BAD_REQUEST),

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST),

    DUPLICATE(HttpStatus.CONFLICT),

    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
}