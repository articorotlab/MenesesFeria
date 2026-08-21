package com.espectacularesmeneses.feria.model

data class CardRegistrationAuthorization(

    val registrationId: String,

    val cardId: Long,

    val uid: String,

    val cardType: String,

    val balance: Long,

    val transactionCounter: Long,

    val status: String
)