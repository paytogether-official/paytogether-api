package kr.paytogether.shared.utils

import java.math.BigDecimal

fun BigDecimal.isZero(): Boolean = this.compareTo(BigDecimal.ZERO) == 0

infix fun BigDecimal.eqIgnoreScale(other: BigDecimal): Boolean = this.compareTo(other) == 0

infix fun BigDecimal.notEqIgnoreScale(other: BigDecimal): Boolean = this.compareTo(other) != 0