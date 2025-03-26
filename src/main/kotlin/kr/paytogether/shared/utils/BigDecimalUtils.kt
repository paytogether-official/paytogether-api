package kr.paytogether.shared.utils

import java.math.BigDecimal

fun BigDecimal.isZero(): Boolean = this.compareTo(BigDecimal.ZERO) == 0