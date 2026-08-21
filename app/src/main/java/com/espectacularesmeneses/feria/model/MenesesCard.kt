package com.espectacularesmeneses.feria.model

data class MenesesCard(

    val version: Int,

    val type: CardType,

    val status: CardStatus,

    val cardId: Long,

    val balance: Long,

    val transactionCounter: Long,

    val referenceId: Long = 0
)