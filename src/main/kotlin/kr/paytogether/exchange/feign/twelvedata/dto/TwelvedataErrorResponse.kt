package kr.paytogether.exchange.feign.twelvedata.dto

import kr.paytogether.shared.exception.FeignException

/**
 *
 *   "code": 400,
 *   "message": "**symbol** not found: BAM/KRW. Please specify it correctly according to API Documentation.",
 *   "status": "error"
 *
 *   "code":429
 *   "message":"You have run out of API credits for the current minute. 17 API credits were used, with the current limit being 8. Wait for the next minute or consider switching to a higher tier plan at https://twelvedata.com/pricing"
 *   "status":"error"
 *
 */
data class TwelvedataErrorResponse(
    val code: Int,
    val message: String,
    val status: String,
) {
    fun toFeignException(): FeignException =
        FeignException(
            message = message,
            details = "code: $code, status: $status",
        )
}
