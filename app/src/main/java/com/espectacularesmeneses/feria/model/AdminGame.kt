package com.espectacularesmeneses.feria.model

data class AdminGame(

    val id: String,

    val code: String,

    val name: String,

    val price: Long,

    val status: String,

    val card: AdminGameCard?,

    val createdAt: String?
)


data class AdminGameCard(

    val cardId: Long,

    val uid: String,

    val status: String
)