package com.espectacularesmeneses.feria.model

enum class CardType(val code: Int) {

    CUSTOMER(1),
    ADMIN(2),
    GAME(3),
    RECHARGE(4);

    companion object {

        fun fromCode(code: Int): CardType? {
            return entries.find { it.code == code }
        }
    }
}