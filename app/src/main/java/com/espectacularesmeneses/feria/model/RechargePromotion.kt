package com.espectacularesmeneses.feria.model

data class RechargePromotion(
    val id: String,
    val name: String,
    val cashAmount: Long,
    val promotionalAmount: Long,
    val totalCreditAmount: Long
)
