package com.espectacularesmeneses.feria.model

data class CustomerHistory(

    val requesterRole: String,

    val cardId: Long,

    val uid: String,

    val cardStatus: String,

    val balance: Long,

    val transactionCounter: Long,

    val items: List<CustomerHistoryItem>
)


data class CustomerHistoryItem(

    val transactionId: String,

    val type: String,

    val direction: String,

    val amount: Long,

    val balanceBefore: Long,

    val balanceAfter: Long,

    val counterBefore: Long,

    val counterAfter: Long,

    val status: String,

    val gameName: String?,

    val unitPrice: Long?,

    val quantity: Int?,

    val rechargePointName: String?,

    val createdAt: String,

    val confirmedAt: String?,

    val failureReason: String?
)