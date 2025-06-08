package kr.paytogether.journey.enums

import com.fasterxml.jackson.annotation.JsonCreator

enum class Category(val value: String, val sort: Int) {
    Other("기타", 1),
    Food("식비", 2),
    Transportation("교통", 3),
    Tourism("관광", 4),
    Shopping("쇼핑", 5),
    Accommodation("숙소", 6),
    Airfare("항공", 7),
    ;

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): Category {
            return when (value) {
                Other.value -> Other
                Food.value -> Food
                Transportation.value -> Transportation
                Tourism.value -> Tourism
                Shopping.value -> Shopping
                Accommodation.value -> Accommodation
                Airfare.value -> Airfare
                else -> throw IllegalArgumentException("Unknown category: $value")
            }
        }
    }
}