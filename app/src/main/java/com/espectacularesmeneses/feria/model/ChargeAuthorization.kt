package com.espectacularesmeneses.feria.model

data class ChargeAuthorization(

    val transactionId: String,

    val cardId: Long,

    val gameCode: String,

    val gameName: String,

    val unitPrice: Long,

    val peopleCount: Int,

    val total: Long,

    val balanceBefore: Long,

    val balanceAfter: Long,

    val counterBefore: Long,

    val counterAfter: Long
)