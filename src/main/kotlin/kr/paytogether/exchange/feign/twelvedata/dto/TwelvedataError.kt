package kr.paytogether.exchange.feign.twelvedata.dto

/**
 *   "code": 400,
 *   "message": "**symbol** not found: BAM/KRW. Please specify it correctly according to API Documentation.",
 *   "status": "error"
 */
class TwelvedataError(
    val code: Int,
    val message: String,
    val status: String,
)
