package com.espectacularesmeneses.feria.model

data class RechargeSession(

    val sessionId: String,

    val deviceCode: String,

    val deviceName: String,

    val rechargeCardId: Long?,

    val rechargeCardUid: String?,

    val rechargePointCode: String,

    val rechargePointName: String,

    val startedAt: String?
)