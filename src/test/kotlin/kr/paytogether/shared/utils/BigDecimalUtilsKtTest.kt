package kr.paytogether.shared.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class BigDecimalUtilsKtTest: StringSpec({
    "isZero returns true for zero" {
        val zero = BigDecimal.ZERO
        zero.isZero() shouldBe true
    }

    "isZero returns false for non-zero" {
        val nonZero = BigDecimal("1.0")
        nonZero.isZero() shouldBe false
    }

    "eqIgnoreScale returns true for equal values with different scales" {
        val a = BigDecimal("1.0")
        val b = BigDecimal("1.00")
        a eqIgnoreScale b shouldBe true
    }

    "eqIgnoreScale returns false for different values" {
        val a = BigDecimal("1.0")
        val b = BigDecimal("2.0")
        a eqIgnoreScale b shouldBe false
    }})