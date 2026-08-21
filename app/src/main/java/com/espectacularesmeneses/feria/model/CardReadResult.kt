package com.espectacularesmeneses.feria.model

sealed class CardReadResult {

    data object Waiting :
        CardReadResult()

    data class WaitingForDevCard(
        val title: String,
        val message: String
    ) :
        CardReadResult()

    data class WaitingForRecharge(
        val amount: Long,
        val rechargePointName: String
    ) :
        CardReadResult()

    data class WaitingForCharge(
        val peopleCount: Int,
        val gameName: String,
        val unitPrice: Long,
        val estimatedTotal: Long
    ) :
        CardReadResult()

    data object WaitingForBalance :
        CardReadResult()

    data object WaitingForHistory :
        CardReadResult()

    data class Unregistered(
        val uid: String,
        val rawData: String
    ) :
        CardReadResult()

    data class Registered(
        val uid: String,
        val card: MenesesCard
    ) :
        CardReadResult()

    data class HistoryLoaded(
        val history: CustomerHistory
    ) :
        CardReadResult()

    data class Success(
        val title: String,
        val message: String
    ) :
        CardReadResult()

    data class Error(
        val message: String
    ) :
        CardReadResult()
}