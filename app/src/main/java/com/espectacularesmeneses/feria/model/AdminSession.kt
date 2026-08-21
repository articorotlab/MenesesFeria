package com.espectacularesmeneses.feria.model

data class AdminSession(

    val sessionId: String,

    val deviceCode: String,

    val deviceName: String,

    val adminCardId: Long,

    val adminCardUid: String,

    val startedAt: String?
)