package com.espectacularesmeneses.feria.model

data class CustomerHistory(
    val requesterRole: String,
    val cardId: Long,
    val uid: String,
    val cardStatus: String,
    val balance: Long,
    val transactionCounter: Long,
    val items: List<CustomerHistoryItem>,
    val financialHold: CustomerFinancialHold = CustomerFinancialHold(),
    val financialIncidentsCount: Int = 0,
    val financialIncidents: List<CustomerFinancialIncident> = emptyList(),
    val nfcBalance: Long? = null,
    val nfcTransactionCounter: Long? = null
)

data class CustomerFinancialHold(
    val active: Boolean = false,
    val reason: String? = null,
    val heldAt: String? = null
)

data class CustomerFinancialState(
    val balance: Long,
    val transactionCounter: Long
)

data class CustomerLedgerState(
    val balance: Long
)

data class CustomerFinancialIncidentTransaction(
    val type: String?,
    val amount: Long?,
    val promotionId: String?,
    val statusBefore: String?
)

data class CustomerFinancialDifferences(
    val nfcVsPostgreSQL: Long,
    val nfcVsLedger: Long,
    val postgreSQLVsLedger: Long
)

data class CustomerFinancialIncident(
    val incidentId: String,
    val type: String,
    val detectedAt: String,
    val transactionId: String,
    val deviceCode: String?,
    val transaction: CustomerFinancialIncidentTransaction,
    val nfc: CustomerFinancialState,
    val postgreSQL: CustomerFinancialState,
    val ledger: CustomerLedgerState,
    val expectedBefore: CustomerFinancialState,
    val expectedAfter: CustomerFinancialState,
    val differences: CustomerFinancialDifferences,
    val failureReason: String
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
