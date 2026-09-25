package com.espectacularesmeneses.feria.model

data class AdminPromotionRechargePoint(
    val id: String,
    val code: String,
    val name: String,
    val status: String
)

data class AdminPromotion(
    val id: String,
    val name: String,
    val cashAmount: Long,
    val promotionalAmount: Long,
    val totalCreditAmount: Long,
    val active: Boolean,
    val createdByAdminCardId: Long?,
    val scope: String,
    val rechargePoints: List<AdminPromotionRechargePoint>,
    val createdAt: String,
    val updatedAt: String
)
