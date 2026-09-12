package com.espectacularesmeneses.feria.model

data class CustomerCardReturnAuthorization(
    val operationId: String,
    val cardId: Long,
    val uid: String,
    val activationId: String,
    val balanceBefore: Long,
    val transactionCounterBefore: Long,

    val activatedByRole: String,
    val activationFee: Long,
    val activationFeeKnown: Boolean,

    val refundAmount: Long,
    val shouldRefundMoney: Boolean,
    val refundPolicyReason: String,
    val refundPolicyMessage: String,

    val discardedCash: Long,
    val discardedPromotional: Long,
    val discardedAdminCredit: Long,
    val discardedLegacy: Long,
    val discardedTotal: Long,

    val targetStatus: String,
    val targetBalance: Long,
    val targetTransactionCounter: Long
)
