package kr.paytogether.shared.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun Instant.toLocalDateTime(zoneId: ZoneId = ZoneId.of("Asia/Seoul")): LocalDateTime =
    LocalDateTime.ofInstant(this, zoneId)

fun Instant?.toLocalDateTimeOrNull(zoneId: ZoneId = ZoneId.of("Asia/Seoul")): LocalDateTime? =
    this?.let { LocalDateTime.ofInstant(it, zoneId) }