package com.espectacularesmeneses.feria.model

data class AdminPromotion(
    val id: String,
    val name: String,
    val cashAmount: Long,
    val promotionalAmount: Long,
    val totalCreditAmount: Long,
    val active: Boolean,
    val createdByAdminCardId: Long?,
    val createdAt: String,
    val updatedAt: String
)
