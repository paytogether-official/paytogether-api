package kr.paytogether.journey.enums

import com.fasterxml.jackson.annotation.JsonCreator

enum class Category(val value: String) {
    OTHER("기타"),
    FOOD("식비"),
    TRANSPORTATION("교통"),
    TOURISM("관광"),
    SHOPPING("쇼핑"),
    ACCOMMODATION("숙소");

    companion object {

        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): Category {
            return Category.entries.find { it.value == value }
                ?: throw IllegalArgumentException("Unknown category: $value")
        }
    }
}