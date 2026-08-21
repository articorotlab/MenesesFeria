package com.espectacularesmeneses.feria.model


data class AdminRechargePoint(

    val id: String,

    val code: String,

    val name: String,

    val status: String,

    val card: AdminRechargePointCard?,

    val createdAt: String?
)


data class AdminRechargePointCard(

    val cardId: Long,

    val uid: String,

    val status: String
)