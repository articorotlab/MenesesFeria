package com.espectacularesmeneses.feria.model

data class RechargeAuthorization(

    val transactionId: String,

    val cardId: Long,

    val balanceBefore: Long,

    val balanceAfter: Long,

    val counterBefore: Long,

    val counterAfter: Long
)