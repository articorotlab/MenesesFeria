package com.espectacularesmeneses.feria.model

enum class CardStatus(val code: Int) {

    INACTIVE(0),
    ACTIVE(1),
    BLOCKED(2);

    companion object {

        fun fromCode(code: Int): CardStatus? {
            return entries.find { it.code == code }
        }
    }
}