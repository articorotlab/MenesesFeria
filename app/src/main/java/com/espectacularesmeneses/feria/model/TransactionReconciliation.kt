package com.espectacularesmeneses.feria.model


data class TransactionReconciliation(

    val reconciled: Boolean,

    val action: String,

    val transactionId: String?,

    val cardId: Long,

    val balance: Long,

    val transactionCounter: Long
)
