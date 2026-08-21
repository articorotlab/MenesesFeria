package com.espectacularesmeneses.feria.model

data class GameSession(

    val sessionId: String,

    val deviceCode: String,

    val deviceName: String,

    val gameCardId: Long?,

    val gameCardUid: String?,

    val gameCode: String,

    val gameName: String,

    val price: Long,

    val startedAt: String?
)