package com.espectacularesmeneses.feria.model


data class RechargeCardAuthorization(

    val registrationId: String,

    val cardId: Long,

    val uid: String,

    val cardType: String,

    val rechargePointId: String,

    val rechargePointCode: String,

    val rechargePointName: String,

    val balance: Long,

    val transactionCounter: Long,

    val status: String
)