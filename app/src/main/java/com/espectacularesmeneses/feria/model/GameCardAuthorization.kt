package com.espectacularesmeneses.feria.model

data class GameCardAuthorization(

    val registrationId: String,

    val cardId: Long,

    val uid: String,

    val cardType: String,

    val gameId: String,

    val gameCode: String,

    val gameName: String,

    val gamePrice: Long,

    val balance: Long,

    val transactionCounter: Long,

    val status: String
)