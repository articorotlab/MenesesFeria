package com.espectacularesmeneses.feria.network

import com.espectacularesmeneses.feria.device.DeviceRuntimeIdentity

import com.espectacularesmeneses.feria.model.AdminSession
import com.espectacularesmeneses.feria.model.CardRegistrationAuthorization
import com.espectacularesmeneses.feria.model.ChargeAuthorization
import com.espectacularesmeneses.feria.model.CustomerHistory
import com.espectacularesmeneses.feria.model.CustomerHistoryActivation
import com.espectacularesmeneses.feria.model.CustomerHistoryItem
import com.espectacularesmeneses.feria.model.CustomerFinancialDifferences
import com.espectacularesmeneses.feria.model.CustomerFinancialHold
import com.espectacularesmeneses.feria.model.CustomerFinancialIncident
import com.espectacularesmeneses.feria.model.CustomerFinancialIncidentTransaction
import com.espectacularesmeneses.feria.model.CustomerFinancialState
import com.espectacularesmeneses.feria.model.CustomerLedgerState
import com.espectacularesmeneses.feria.model.CustomerCardReturnAuthorization
import com.espectacularesmeneses.feria.model.GameSession
import com.espectacularesmeneses.feria.model.RechargeAuthorization
import com.espectacularesmeneses.feria.model.RechargeSession
import com.espectacularesmeneses.feria.model.TransactionReconciliation
import com.espectacularesmeneses.feria.util.ServerConfig
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

data class CustomerCardReturnResume(
    val found: Boolean,
    val authorization: CustomerCardReturnAuthorization?
)


data class CustomerCardReturnReconciliation(
    val action: String,
    val operationId: String,
    val status: String,
    val duplicated: Boolean,
    val returnId: String?,
    val refundAmount: Long?,
    val discardedCash: Long?,
    val discardedPromotional: Long?,
    val discardedAdminCredit: Long?,
    val discardedLegacy: Long?,
    val message: String?
)


object MenesesApiClient {

    /*
     * =====================================================
     * HEALTH
     * =====================================================
     */

    fun healthCheck(): Boolean {

        val connection =
            openConnection(
                path = "/health",
                method = "GET"
            )

        try {

            return connection.responseCode == 200

        } finally {

            connection.disconnect()
        }
    }

    /*
     * =====================================================
     * GAME
     * =====================================================
     */

    fun getCurrentGameSession(): GameSession? {

        val connection =
            openConnection(
                path =
                    "/device-sessions/game/current/" +
                            DeviceRuntimeIdentity.deviceCode(),
                method =
                    "GET"
            )

        try {

            val responseCode =
                connection.responseCode

            if (responseCode == 404) {
                return null
            }

            val json =
                readJsonResponse(
                    connection,
                    responseCode
                )

            if (responseCode !in 200..299) {
                throw serverException(json)
            }

            return parseGameSession(json)

        } finally {

            connection.disconnect()
        }
    }

    fun loginGameSession(
        cardId: Long,
        uid: String
    ): GameSession {

        val body =
            JSONObject().apply {

                put("cardId", cardId)
                put("uid", uid)

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )
            }

        return parseGameSession(
            postJson(
                path =
                    "/device-sessions/game/login",
                body =
                    body
            )
        )
    }

    fun logoutGameSession() {

        val body =
            JSONObject().apply {

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )
            }

        val response =
            postJson(
                path =
                    "/device-sessions/game/logout",
                body =
                    body
            )

        if (
            !response.optBoolean(
                "closed",
                false
            )
        ) {

            throw IllegalStateException(
                "No fue posible cerrar la sesión GAME."
            )
        }
    }

    /*
     * =====================================================
     * RECHARGE
     * =====================================================
     */

    fun getCurrentRechargeSession(): RechargeSession? {

        val connection =
            openConnection(
                path =
                    "/device-sessions/recharge/current/" +
                            DeviceRuntimeIdentity.deviceCode(),
                method =
                    "GET"
            )

        try {

            val responseCode =
                connection.responseCode

            if (responseCode == 404) {
                return null
            }

            val json =
                readJsonResponse(
                    connection,
                    responseCode
                )

            if (responseCode !in 200..299) {
                throw serverException(json)
            }

            return parseRechargeSession(json)

        } finally {

            connection.disconnect()
        }
    }

    fun loginRechargeSession(
        cardId: Long,
        uid: String
    ): RechargeSession {

        val body =
            JSONObject().apply {

                put("cardId", cardId)
                put("uid", uid)

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )
            }

        return parseRechargeSession(
            postJson(
                path =
                    "/device-sessions/recharge/login",
                body =
                    body
            )
        )
    }

    fun logoutRechargeSession() {

        val body =
            JSONObject().apply {

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )
            }

        val response =
            postJson(
                path =
                    "/device-sessions/recharge/logout",
                body =
                    body
            )

        if (
            !response.optBoolean(
                "closed",
                false
            )
        ) {

            throw IllegalStateException(
                "No fue posible cerrar la sesión RECHARGE."
            )
        }
    }

    /*
     * =====================================================
     * ADMIN
     * =====================================================
     */

    fun getCurrentAdminSession(): AdminSession? {

        val connection =
            openConnection(
                path =
                    "/admin/current/" +
                            DeviceRuntimeIdentity.deviceCode(),
                method =
                    "GET"
            )

        try {

            val responseCode =
                connection.responseCode

            if (responseCode == 404) {
                return null
            }

            val json =
                readJsonResponse(
                    connection,
                    responseCode
                )

            if (responseCode !in 200..299) {
                throw serverException(json)
            }

            return parseAdminSession(json)

        } finally {

            connection.disconnect()
        }
    }

    fun loginAdminSession(
        cardId: Long,
        uid: String
    ): AdminSession {

        val body =
            JSONObject().apply {

                put("cardId", cardId)
                put("uid", uid)

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )
            }

        return parseAdminSession(
            postJson(
                path =
                    "/admin/login",
                body =
                    body
            )
        )
    }

    fun logoutAdminSession() {

        val body =
            JSONObject().apply {

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )
            }

        val response =
            postJson(
                path =
                    "/admin/logout",
                body =
                    body
            )

        if (
            !response.optBoolean(
                "closed",
                false
            )
        ) {

            throw IllegalStateException(
                "No fue posible cerrar la sesión ADMIN."
            )
        }
    }

    /*
     * =====================================================
     * CARD REGISTRATIONS
     * =====================================================
     *
     * Ahora usa las rutas GENERALES.
     *
     * ADMIN + RECHARGE pueden crear CUSTOMER.
     * =====================================================
     */

    fun authorizeCustomerRegistration(
        targetUid: String
    ): CardRegistrationAuthorization {

        val body =
            JSONObject().apply {

                put(
                    "idempotencyKey",
                    UUID.randomUUID()
                        .toString()
                )

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )

                put(
                    "targetUid",
                    targetUid
                )

                put(
                    "targetCardType",
                    "CUSTOMER"
                )
            }

        val response =
            postJson(
                path =
                    "/card-registrations/authorize",
                body =
                    body
            )

        if (
            !response.optBoolean(
                "authorized",
                false
            )
        ) {

            throw IllegalStateException(
                "El servidor no autorizó el registro."
            )
        }

        val initialState =
            response.optJSONObject(
                "initialState"
            )

        return CardRegistrationAuthorization(

            registrationId =
                response.getString(
                    "registrationId"
                ),

            cardId =
                response.getLong(
                    "cardId"
                ),

            uid =
                response.getString(
                    "uid"
                ),

            cardType =
                response.getString(
                    "cardType"
                ),

            balance =
                initialState
                    ?.optLong(
                        "balance",
                        0
                    )
                    ?: 0,

            transactionCounter =
                initialState
                    ?.optLong(
                        "transactionCounter",
                        0
                    )
                    ?: 0,

            status =
                initialState
                    ?.optString(
                        "status",
                        "ACTIVE"
                    )
                    ?: "ACTIVE"
        )
    }

    fun confirmCardRegistration(
        registrationId: String,
        targetUid: String,
        writtenCardId: Long,
        writtenCardType: String
    ) {

        val body =
            JSONObject().apply {

                put(
                    "registrationId",
                    registrationId
                )

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )

                put(
                    "targetUid",
                    targetUid
                )

                put(
                    "writtenCardId",
                    writtenCardId
                )

                put(
                    "writtenCardType",
                    writtenCardType
                )
            }

        val response =
            postJson(
                path =
                    "/card-registrations/confirm",
                body =
                    body
            )

        if (
            !response.optBoolean(
                "confirmed",
                false
            )
        ) {

            throw IllegalStateException(
                "El servidor no confirmó la tarjeta."
            )
        }
    }

    fun failCardRegistration(
        registrationId: String,
        reason: String
    ) {

        val body =
            JSONObject().apply {

                put(
                    "registrationId",
                    registrationId
                )

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )

                put(
                    "reason",
                    reason
                )
            }

        postJson(
            path =
                "/card-registrations/fail",
            body =
                body
        )
    }

    /*
     * =====================================================
     * CUSTOMER HISTORY
     * =====================================================
     */

    fun getCustomerHistory(
        cardId: Long,
        uid: String
    ): CustomerHistory {

        val body =
            JSONObject().apply {

                put(
                    "cardId",
                    cardId
                )

                put(
                    "uid",
                    uid
                )

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )
            }

        val response =
            postJson(
                path =
                    "/customer-support/card-history",
                body =
                    body
            )

        val requester =
            response.getJSONObject(
                "requester"
            )

        val card =
            response.getJSONObject(
                "card"
            )

        val financialHoldJson =
            card.optJSONObject(
                "financialHold"
            )

        val financialHold =
            CustomerFinancialHold(
                active =
                    financialHoldJson
                        ?.optBoolean(
                            "active",
                            false
                        )
                        ?: false,
                reason =
                    financialHoldJson
                        ?.optString(
                            "reason"
                        )
                        ?.takeIf {
                            it.isNotBlank() &&
                                    it != "null"
                        },
                heldAt =
                    financialHoldJson
                        ?.optString(
                            "heldAt"
                        )
                        ?.takeIf {
                            it.isNotBlank() &&
                                    it != "null"
                        }
            )

        val financialIncidents =
            mutableListOf<CustomerFinancialIncident>()

        val incidentsJson =
            response.optJSONArray(
                "financialIncidents"
            )

        if (
            incidentsJson != null
        ) {
            for (
            index in 0 until incidentsJson.length()
            ) {
                val incident =
                    incidentsJson.getJSONObject(
                        index
                    )

                val transaction =
                    incident.optJSONObject(
                        "transaction"
                    )

                val nfc =
                    incident.getJSONObject(
                        "nfc"
                    )

                val postgreSQL =
                    incident.getJSONObject(
                        "postgreSQL"
                    )

                val ledger =
                    incident.getJSONObject(
                        "ledger"
                    )

                val expectedBefore =
                    incident.getJSONObject(
                        "expectedBefore"
                    )

                val expectedAfter =
                    incident.getJSONObject(
                        "expectedAfter"
                    )

                val differences =
                    incident.getJSONObject(
                        "differences"
                    )

                financialIncidents.add(
                    CustomerFinancialIncident(
                        incidentId =
                            incident.getString(
                                "incidentId"
                            ),
                        type =
                            incident.getString(
                                "type"
                            ),
                        detectedAt =
                            incident.getString(
                                "detectedAt"
                            ),
                        transactionId =
                            incident.getString(
                                "transactionId"
                            ),
                        deviceCode =
                            incident
                                .optString(
                                    "deviceCode"
                                )
                                .takeIf {
                                    it.isNotBlank() &&
                                            it != "null"
                                },
                        transaction =
                            CustomerFinancialIncidentTransaction(
                                type =
                                    transaction
                                        ?.optString(
                                            "type"
                                        )
                                        ?.takeIf {
                                            it.isNotBlank() &&
                                                    it != "null"
                                        },
                                amount =
                                    transaction
                                        ?.takeIf {
                                            it.has("amount") &&
                                                    !it.isNull("amount")
                                        }
                                        ?.getLong(
                                            "amount"
                                        ),
                                promotionId =
                                    transaction
                                        ?.optString(
                                            "promotionId"
                                        )
                                        ?.takeIf {
                                            it.isNotBlank() &&
                                                    it != "null"
                                        },
                                statusBefore =
                                    transaction
                                        ?.optString(
                                            "statusBefore"
                                        )
                                        ?.takeIf {
                                            it.isNotBlank() &&
                                                    it != "null"
                                        }
                            ),
                        nfc =
                            CustomerFinancialState(
                                balance =
                                    nfc.getLong(
                                        "balance"
                                    ),
                                transactionCounter =
                                    nfc.getLong(
                                        "transactionCounter"
                                    )
                            ),
                        postgreSQL =
                            CustomerFinancialState(
                                balance =
                                    postgreSQL.getLong(
                                        "balance"
                                    ),
                                transactionCounter =
                                    postgreSQL.getLong(
                                        "transactionCounter"
                                    )
                            ),
                        ledger =
                            CustomerLedgerState(
                                balance =
                                    ledger.getLong(
                                        "balance"
                                    )
                            ),
                        expectedBefore =
                            CustomerFinancialState(
                                balance =
                                    expectedBefore.getLong(
                                        "balance"
                                    ),
                                transactionCounter =
                                    expectedBefore.getLong(
                                        "transactionCounter"
                                    )
                            ),
                        expectedAfter =
                            CustomerFinancialState(
                                balance =
                                    expectedAfter.getLong(
                                        "balance"
                                    ),
                                transactionCounter =
                                    expectedAfter.getLong(
                                        "transactionCounter"
                                    )
                            ),
                        differences =
                            CustomerFinancialDifferences(
                                nfcVsPostgreSQL =
                                    differences.getLong(
                                        "nfcVsPostgreSQL"
                                    ),
                                nfcVsLedger =
                                    differences.getLong(
                                        "nfcVsLedger"
                                    ),
                                postgreSQLVsLedger =
                                    differences.getLong(
                                        "postgreSQLVsLedger"
                                    )
                            ),
                        failureReason =
                            incident.getString(
                                "failureReason"
                            )
                    )
                )
            }
        }

        val activationJson =
            response.getJSONObject(
                "activation"
            )

        val activation =
            CustomerHistoryActivation(
                activationId =
                    activationJson.getString(
                        "activationId"
                    ),
                activationNumber =
                    activationJson.getInt(
                        "activationNumber"
                    ),
                activationFee =
                    activationJson.getLong(
                        "activationFee"
                    ),
                activationFeeKnown =
                    activationJson.getBoolean(
                        "activationFeeKnown"
                    ),
                startedAt =
                    activationJson.getString(
                        "startedAt"
                    )
            )

        val historyJson =
            response.getJSONArray(
                "history"
            )

        val items =
            mutableListOf<CustomerHistoryItem>()

        for (
        index in 0 until historyJson.length()
        ) {

            val item =
                historyJson.getJSONObject(
                    index
                )

            val game =
                item.optJSONObject(
                    "game"
                )

            val rechargePoint =
                item.optJSONObject(
                    "rechargePoint"
                )

            items.add(

                CustomerHistoryItem(

                    transactionId =
                        item.getString(
                            "transactionId"
                        ),

                    type =
                        item.getString(
                            "type"
                        ),

                    direction =
                        item.getString(
                            "direction"
                        ),

                    amount =
                        item.getLong(
                            "amount"
                        ),

                    balanceBefore =
                        item.getLong(
                            "balanceBefore"
                        ),

                    balanceAfter =
                        item.getLong(
                            "balanceAfter"
                        ),

                    counterBefore =
                        item.getLong(
                            "counterBefore"
                        ),

                    counterAfter =
                        item.getLong(
                            "counterAfter"
                        ),

                    status =
                        item.getString(
                            "status"
                        ),

                    gameName =
                        game
                            ?.optString(
                                "name"
                            )
                            ?.takeIf {
                                it.isNotBlank()
                            },

                    unitPrice =
                        game
                            ?.takeIf {
                                it.has("unitPrice") &&
                                        !it.isNull("unitPrice")
                            }
                            ?.getLong(
                                "unitPrice"
                            ),

                    quantity =
                        game
                            ?.takeIf {
                                it.has("quantity") &&
                                        !it.isNull("quantity")
                            }
                            ?.getInt(
                                "quantity"
                            ),

                    rechargePointName =
                        rechargePoint
                            ?.optString(
                                "name"
                            )
                            ?.takeIf {
                                it.isNotBlank()
                            },

                    checkoutId =
                        item
                            .optString(
                                "checkoutId"
                            )
                            .takeIf {
                                it.isNotBlank() &&
                                        it != "null"
                            },

                    promotionId =
                        item
                            .optString(
                                "promotionId"
                            )
                            .takeIf {
                                it.isNotBlank() &&
                                        it != "null"
                            },

                    promotionName =
                        item
                            .optString(
                                "promotionName"
                            )
                            .takeIf {
                                it.isNotBlank() &&
                                        it != "null"
                            },

                    paidRechargeAmount =
                        item
                            .takeIf {
                                it.has(
                                    "paidRechargeAmount"
                                ) &&
                                        !it.isNull(
                                            "paidRechargeAmount"
                                        )
                            }
                            ?.getLong(
                                "paidRechargeAmount"
                            ),

                    promotionalCreditAmount =
                        item
                            .takeIf {
                                it.has(
                                    "promotionalCreditAmount"
                                ) &&
                                        !it.isNull(
                                            "promotionalCreditAmount"
                                        )
                            }
                            ?.getLong(
                                "promotionalCreditAmount"
                            ),

                    creditedAmount =
                        item
                            .takeIf {
                                it.has(
                                    "creditedAmount"
                                ) &&
                                        !it.isNull(
                                            "creditedAmount"
                                        )
                            }
                            ?.getLong(
                                "creditedAmount"
                            ),

                    paymentMethod =
                        item
                            .optString(
                                "paymentMethod"
                            )
                            .takeIf {
                                it.isNotBlank() &&
                                        it != "null"
                            },

                    paymentMethodEditable =
                        item.optBoolean(
                            "paymentMethodEditable",
                            false
                        ),

                    createdAt =
                        item.getString(
                            "createdAt"
                        ),

                    confirmedAt =
                        item
                            .optString(
                                "confirmedAt"
                            )
                            .takeIf {
                                it.isNotBlank() &&
                                        it != "null"
                            },

                    failureReason =
                        item
                            .optString(
                                "failureReason"
                            )
                            .takeIf {
                                it.isNotBlank() &&
                                        it != "null"
                            }
                )
            )
        }

        return CustomerHistory(

            requesterRole =
                requester.getString(
                    "role"
                ),

            cardId =
                card.getLong(
                    "cardId"
                ),

            uid =
                card.getString(
                    "uid"
                ),

            cardStatus =
                card.getString(
                    "status"
                ),

            balance =
                card.getLong(
                    "balance"
                ),

            transactionCounter =
                card.getLong(
                    "transactionCounter"
                ),

            activation =
                activation,

            financialHold =
                financialHold,

            financialIncidentsCount =
                response.optInt(
                    "financialIncidentsCount",
                    financialIncidents.size
                ),

            financialIncidents =
                financialIncidents,

            items =
                items
        )
    }

    /*
     * =====================================================
     * CUSTOMER HISTORY PAYMENT METHOD CORRECTION
     * =====================================================
     */

    fun changeCustomerHistoryPaymentMethod(
        checkoutId: String,
        cardId: Long,
        uid: String,
        newPaymentMethod: String,
        reason: String =
            "Corrección desde historial CUSTOMER"
    ) {

        val body =
            JSONObject().apply {

                put(
                    "checkoutId",
                    checkoutId
                )

                put(
                    "cardId",
                    cardId
                )

                put(
                    "uid",
                    uid
                )

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )

                put(
                    "newPaymentMethod",
                    newPaymentMethod
                )

                put(
                    "reason",
                    reason
                )
            }


        postJson(
            path =
                "/customer-support/card-history/payment-method",

            body =
                body
        )
    }


    /*
     * =====================================================
     * CUSTOMER CARD RETURN
     * =====================================================
     *
     * Flujo:
     *
     * AUTHORIZE
     * -> Android escribe y verifica NFC
     * -> CONFIRM
     *
     * Si falla antes de comenzar la escritura NFC:
     * -> FAIL
     * =====================================================
     */

    fun resumeCustomerCardReturn(
        cardId: Long,
        uid: String
    ): CustomerCardReturnResume {

        val body =
            JSONObject().apply {

                put(
                    "cardId",
                    cardId
                )

                put(
                    "uid",
                    uid
                )

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )
            }


        val response =
            postJson(
                path =
                    "/customer-support/card-return/resume",

                body =
                    body
            )


        if (
            !response.optBoolean(
                "found",
                false
            )
        ) {

            return CustomerCardReturnResume(
                found = false,
                authorization = null
            )
        }


        val card =
            response.getJSONObject(
                "card"
            )

        val activation =
            response.getJSONObject(
                "activation"
            )

        val refundPolicy =
            response.getJSONObject(
                "refundPolicy"
            )

        val discarded =
            response.getJSONObject(
                "discarded"
            )

        val targetState =
            response.getJSONObject(
                "targetState"
            )


        return CustomerCardReturnResume(
            found = true,

            authorization =
                CustomerCardReturnAuthorization(

                    operationId =
                        response.getString(
                            "operationId"
                        ),

                    cardId =
                        card.getLong(
                            "cardId"
                        ),

                    uid =
                        card.getString(
                            "uid"
                        ),

                    activationId =
                        card.getString(
                            "activationId"
                        ),

                    balanceBefore =
                        card.getLong(
                            "balanceBefore"
                        ),

                    transactionCounterBefore =
                        card.getLong(
                            "transactionCounterBefore"
                        ),

                    activatedByRole =
                        activation.getString(
                            "activatedByRole"
                        ),

                    activationFee =
                        activation.getLong(
                            "activationFee"
                        ),

                    activationFeeKnown =
                        activation.getBoolean(
                            "activationFeeKnown"
                        ),

                    refundAmount =
                        response.getLong(
                            "refundAmount"
                        ),

                    shouldRefundMoney =
                        refundPolicy.getBoolean(
                            "shouldRefundMoney"
                        ),

                    refundPolicyReason =
                        refundPolicy.getString(
                            "reason"
                        ),

                    refundPolicyMessage =
                        refundPolicy.getString(
                            "message"
                        ),

                    discardedCash =
                        discarded.getLong(
                            "cash"
                        ),

                    discardedPromotional =
                        discarded.getLong(
                            "promotional"
                        ),

                    discardedAdminCredit =
                        discarded.getLong(
                            "adminCredit"
                        ),

                    discardedLegacy =
                        discarded.getLong(
                            "legacy"
                        ),

                    discardedTotal =
                        discarded.getLong(
                            "total"
                        ),

                    targetStatus =
                        targetState.getString(
                            "status"
                        ),

                    targetBalance =
                        targetState.getLong(
                            "balance"
                        ),

                    targetTransactionCounter =
                        targetState.getLong(
                            "transactionCounter"
                        )
                )
        )
    }


    fun reconcileCustomerCardReturn(
        operationId: String,
        cardId: Long,
        uid: String,
        physicalCardId: Long,
        physicalCardType: String,
        physicalStatus: String,
        physicalBalance: Long,
        physicalTransactionCounter: Long
    ): CustomerCardReturnReconciliation {

        val body =
            JSONObject().apply {

                put(
                    "operationId",
                    operationId
                )

                put(
                    "cardId",
                    cardId
                )

                put(
                    "uid",
                    uid
                )

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )

                put(
                    "physicalCardId",
                    physicalCardId
                )

                put(
                    "physicalCardType",
                    physicalCardType
                )

                put(
                    "physicalStatus",
                    physicalStatus
                )

                put(
                    "physicalBalance",
                    physicalBalance
                )

                put(
                    "physicalTransactionCounter",
                    physicalTransactionCounter
                )
            }


        val response =
            postJson(
                path =
                    "/customer-support/card-return/reconcile",

                body =
                    body
            )


        val result =
            response.optJSONObject(
                "result"
            )


        return CustomerCardReturnReconciliation(

            action =
                response.getString(
                    "action"
                ),

            operationId =
                response.getString(
                    "operationId"
                ),

            status =
                response.getString(
                    "status"
                ),

            duplicated =
                response.optBoolean(
                    "duplicated",
                    false
                ),

            returnId =
                result
                    ?.optString(
                        "returnId"
                    )
                    ?.takeIf {
                        it.isNotBlank() &&
                                it != "null"
                    },

            refundAmount =
                result
                    ?.takeIf {
                        it.has(
                            "refundAmount"
                        ) &&
                                !it.isNull(
                                    "refundAmount"
                                )
                    }
                    ?.getLong(
                        "refundAmount"
                    ),

            discardedCash =
                result
                    ?.takeIf {
                        it.has(
                            "discardedCash"
                        ) &&
                                !it.isNull(
                                    "discardedCash"
                                )
                    }
                    ?.getLong(
                        "discardedCash"
                    ),

            discardedPromotional =
                result
                    ?.takeIf {
                        it.has(
                            "discardedPromotional"
                        ) &&
                                !it.isNull(
                                    "discardedPromotional"
                                )
                    }
                    ?.getLong(
                        "discardedPromotional"
                    ),

            discardedAdminCredit =
                result
                    ?.takeIf {
                        it.has(
                            "discardedAdminCredit"
                        ) &&
                                !it.isNull(
                                    "discardedAdminCredit"
                                )
                    }
                    ?.getLong(
                        "discardedAdminCredit"
                    ),

            discardedLegacy =
                result
                    ?.takeIf {
                        it.has(
                            "discardedLegacy"
                        ) &&
                                !it.isNull(
                                    "discardedLegacy"
                                )
                    }
                    ?.getLong(
                        "discardedLegacy"
                    ),

            message =
                response
                    .optString(
                        "message"
                    )
                    .takeIf {
                        it.isNotBlank() &&
                                it != "null"
                    }
        )
    }


    fun authorizeCustomerCardReturn(
        cardId: Long,
        uid: String
    ): CustomerCardReturnAuthorization {

        val body =
            JSONObject().apply {

                put(
                    "idempotencyKey",
                    UUID.randomUUID()
                        .toString()
                )

                put(
                    "cardId",
                    cardId
                )

                put(
                    "uid",
                    uid
                )

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )
            }


        val response =
            postJson(
                path =
                    "/customer-support/card-return/authorize",

                body =
                    body
            )


        if (
            !response.optBoolean(
                "authorized",
                false
            )
        ) {

            throw IllegalStateException(
                "El servidor no autorizó la devolución."
            )
        }


        val card =
            response.getJSONObject(
                "card"
            )


        val activation =
            response.getJSONObject(
                "activation"
            )


        val refundPolicy =
            response.getJSONObject(
                "refundPolicy"
            )


        val discarded =
            response.getJSONObject(
                "discarded"
            )


        val targetState =
            response.getJSONObject(
                "targetState"
            )


        return CustomerCardReturnAuthorization(

            operationId =
                response.getString(
                    "operationId"
                ),

            cardId =
                card.getLong(
                    "cardId"
                ),

            uid =
                card.getString(
                    "uid"
                ),

            activationId =
                card.getString(
                    "activationId"
                ),

            balanceBefore =
                card.getLong(
                    "balanceBefore"
                ),

            transactionCounterBefore =
                card.getLong(
                    "transactionCounterBefore"
                ),

            activatedByRole =
                activation.getString(
                    "activatedByRole"
                ),

            activationFee =
                activation.getLong(
                    "activationFee"
                ),

            activationFeeKnown =
                activation.getBoolean(
                    "activationFeeKnown"
                ),

            refundAmount =
                response.getLong(
                    "refundAmount"
                ),

            shouldRefundMoney =
                refundPolicy.getBoolean(
                    "shouldRefundMoney"
                ),

            refundPolicyReason =
                refundPolicy.getString(
                    "reason"
                ),

            refundPolicyMessage =
                refundPolicy.getString(
                    "message"
                ),

            discardedCash =
                discarded.getLong(
                    "cash"
                ),

            discardedPromotional =
                discarded.getLong(
                    "promotional"
                ),

            discardedAdminCredit =
                discarded.getLong(
                    "adminCredit"
                ),

            discardedLegacy =
                discarded.getLong(
                    "legacy"
                ),

            discardedTotal =
                discarded.getLong(
                    "total"
                ),

            targetStatus =
                targetState.getString(
                    "status"
                ),

            targetBalance =
                targetState.getLong(
                    "balance"
                ),

            targetTransactionCounter =
                targetState.getLong(
                    "transactionCounter"
                )
        )
    }


    fun confirmCustomerCardReturn(
        operationId: String,
        cardId: Long,
        uid: String,
        writtenCardId: Long,
        writtenCardType: String,
        writtenStatus: String,
        writtenBalance: Long,
        writtenTransactionCounter: Long
    ) {

        val body =
            JSONObject().apply {

                put(
                    "operationId",
                    operationId
                )

                put(
                    "cardId",
                    cardId
                )

                put(
                    "uid",
                    uid
                )

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )

                put(
                    "writtenCardId",
                    writtenCardId
                )

                put(
                    "writtenCardType",
                    writtenCardType
                )

                put(
                    "writtenStatus",
                    writtenStatus
                )

                put(
                    "writtenBalance",
                    writtenBalance
                )

                put(
                    "writtenTransactionCounter",
                    writtenTransactionCounter
                )
            }


        val response =
            postJson(
                path =
                    "/customer-support/card-return/confirm",

                body =
                    body
            )


        if (
            !response.optBoolean(
                "confirmed",
                false
            )
        ) {

            throw IllegalStateException(
                "El servidor no confirmó la devolución."
            )
        }
    }


    fun failCustomerCardReturn(
        operationId: String,
        reason: String
    ) {

        val body =
            JSONObject().apply {

                put(
                    "operationId",
                    operationId
                )

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )

                put(
                    "reason",
                    reason
                )
            }


        postJson(
            path =
                "/customer-support/card-return/fail",

            body =
                body
        )
    }


    /*
     * =====================================================
     * RECHARGE TRANSACTION
     * =====================================================
     */

    fun authorizeRecharge(
        cardId: Long,
        uid: String,
        amount: Long,
        cardBalance: Long,
        cardCounter: Long
    ): RechargeAuthorization {

        val body =
            JSONObject().apply {

                put(
                    "idempotencyKey",
                    UUID.randomUUID()
                        .toString()
                )

                put("cardId", cardId)
                put("uid", uid)
                put("amount", amount)
                put("cardBalance", cardBalance)
                put("cardCounter", cardCounter)

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )
            }

        val response =
            postJson(
                path =
                    "/transactions/recharge/authorize",
                body =
                    body
            )

        return RechargeAuthorization(

            transactionId =
                response.getString(
                    "transactionId"
                ),

            cardId =
                response.getLong(
                    "cardId"
                ),

            balanceBefore =
                response.getLong(
                    "balanceBefore"
                ),

            balanceAfter =
                response.getLong(
                    "balanceAfter"
                ),

            counterBefore =
                response.getLong(
                    "counterBefore"
                ),

            counterAfter =
                response.getLong(
                    "counterAfter"
                )
        )
    }

    /*
     * =====================================================
     * ADMIN RECHARGE
     * =====================================================
     */

    fun authorizeAdminRecharge(
        cardId: Long,
        uid: String,
        amount: Long,
        cardBalance: Long,
        cardCounter: Long
    ): RechargeAuthorization {

        val body =
            JSONObject().apply {

                put(
                    "idempotencyKey",
                    UUID.randomUUID()
                        .toString()
                )

                put("cardId", cardId)
                put("uid", uid)
                put("amount", amount)
                put("cardBalance", cardBalance)
                put("cardCounter", cardCounter)

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )
            }

        val response =
            postJson(
                path =
                    "/transactions/admin/recharge/authorize",
                body =
                    body
            )

        return RechargeAuthorization(
            transactionId =
                response.getString(
                    "transactionId"
                ),
            cardId =
                response.getLong(
                    "cardId"
                ),
            balanceBefore =
                response.getLong(
                    "balanceBefore"
                ),
            balanceAfter =
                response.getLong(
                    "balanceAfter"
                ),
            counterBefore =
                response.getLong(
                    "counterBefore"
                ),
            counterAfter =
                response.getLong(
                    "counterAfter"
                )
        )
    }

    /*
     * =====================================================
     * ADMIN ADJUSTMENT
     * =====================================================
     */

    fun authorizeAdminAdjustment(
        cardId: Long,
        uid: String,
        amount: Long,
        cardBalance: Long,
        cardCounter: Long
    ): RechargeAuthorization {

        val body =
            JSONObject().apply {

                put(
                    "idempotencyKey",
                    UUID.randomUUID()
                        .toString()
                )

                put("cardId", cardId)
                put("uid", uid)
                put("amount", amount)
                put("cardBalance", cardBalance)
                put("cardCounter", cardCounter)

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )
            }

        val response =
            postJson(
                path =
                    "/transactions/admin/adjustment/authorize",
                body =
                    body
            )

        return RechargeAuthorization(
            transactionId =
                response.getString(
                    "transactionId"
                ),
            cardId =
                response.getLong(
                    "cardId"
                ),
            balanceBefore =
                response.getLong(
                    "balanceBefore"
                ),
            balanceAfter =
                response.getLong(
                    "balanceAfter"
                ),
            counterBefore =
                response.getLong(
                    "counterBefore"
                ),
            counterAfter =
                response.getLong(
                    "counterAfter"
                )
        )
    }

    /*
     * =====================================================
     * OPERATIONAL SETTINGS
     * =====================================================
     */

    fun getCustomerCardActivationFee(): Long {

        val connection =
            openConnection(
                path =
                    "/admin/operational-settings?deviceCode=" +
                            DeviceRuntimeIdentity.deviceCode(),
                method =
                    "GET"
            )

        try {

            val responseCode =
                connection.responseCode

            val json =
                readJsonResponse(
                    connection,
                    responseCode
                )

            if (
                responseCode !in 200..299
            ) {
                throw serverException(
                    json
                )
            }

            return json
                .getJSONObject(
                    "settings"
                )
                .getLong(
                    "customerCardActivationFee"
                )

        } finally {
            connection.disconnect()
        }
    }


    fun updateCustomerCardActivationFee(
        amount: Long
    ): Long {

        require(
            amount >= 0
        ) {
            "El precio de activación no puede ser negativo."
        }

        val body =
            JSONObject().apply {

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )

                put(
                    "amount",
                    amount
                )
            }

        val response =
            putJson(
                path =
                    "/admin/operational-settings/card-activation-fee",
                body =
                    body
            )

        return response
            .getJSONObject(
                "settings"
            )
            .getLong(
                "customerCardActivationFee"
            )
    }


    /*
     * =====================================================
     * ADMIN CASH TODAY
     * =====================================================
     */

    fun getAdminCashToday(): Long {

        val connection =
            openConnection(
                path =
                    "/transactions/admin/cash-today?deviceCode=" +
                            DeviceRuntimeIdentity.deviceCode(),
                method =
                    "GET"
            )

        try {

            val responseCode =
                connection.responseCode

            val json =
                readJsonResponse(
                    connection,
                    responseCode
                )

            if (
                responseCode !in 200..299
            ) {

                throw serverException(
                    json
                )
            }

            return json.getLong(
                "total"
            )

        } finally {

            connection.disconnect()
        }
    }

    /*
     * =====================================================
     * CHARGE
     * =====================================================
     */

    fun authorizeCharge(
        cardId: Long,
        uid: String,
        peopleCount: Int,
        cardBalance: Long,
        cardCounter: Long
    ): ChargeAuthorization {

        val body =
            JSONObject().apply {

                put(
                    "idempotencyKey",
                    UUID.randomUUID()
                        .toString()
                )

                put("cardId", cardId)
                put("uid", uid)
                put("peopleCount", peopleCount)
                put("cardBalance", cardBalance)
                put("cardCounter", cardCounter)

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )
            }

        val response =
            postJson(
                path =
                    "/transactions/charge/authorize",
                body =
                    body
            )

        val game =
            response.getJSONObject(
                "game"
            )

        return ChargeAuthorization(

            transactionId =
                response.getString(
                    "transactionId"
                ),

            cardId =
                response.getLong(
                    "cardId"
                ),

            gameCode =
                game.getString(
                    "code"
                ),

            gameName =
                game.getString(
                    "name"
                ),

            unitPrice =
                game.getLong(
                    "unitPrice"
                ),

            peopleCount =
                response.getInt(
                    "peopleCount"
                ),

            total =
                response.getLong(
                    "total"
                ),

            balanceBefore =
                response.getLong(
                    "balanceBefore"
                ),

            balanceAfter =
                response.getLong(
                    "balanceAfter"
                ),

            counterBefore =
                response.getLong(
                    "counterBefore"
                ),

            counterAfter =
                response.getLong(
                    "counterAfter"
                )
        )
    }

    /*
     * =====================================================
     * TRANSACTION RECONCILIATION
     * =====================================================
     *
     * Permite recuperar automáticamente una CUSTOMER cuando
     * PostgreSQL y la NFC quedaron en estados distintos por
     * una transacción interrumpida.
     * =====================================================
     */

    fun reconcileTransaction(
        cardId: Long,
        uid: String,
        cardBalance: Long,
        cardCounter: Long
    ): TransactionReconciliation {

        val body =
            JSONObject().apply {

                put(
                    "cardId",
                    cardId
                )

                put(
                    "uid",
                    uid
                )

                put(
                    "cardBalance",
                    cardBalance
                )

                put(
                    "cardCounter",
                    cardCounter
                )
            }

        val response =
            postJson(
                path =
                    "/transactions/reconcile",
                body =
                    body
            )

        return TransactionReconciliation(

            reconciled =
                response.optBoolean(
                    "reconciled",
                    false
                ),

            action =
                response.optString(
                    "action",
                    ""
                ),

            transactionId =
                response
                    .optString(
                        "transactionId",
                        ""
                    )
                    .takeIf {
                        it.isNotBlank() &&
                                it != "null"
                    },

            cardId =
                response.getLong(
                    "cardId"
                ),

            balance =
                response.getLong(
                    "balance"
                ),

            transactionCounter =
                response.getLong(
                    "transactionCounter"
                )
        )
    }


    /*
     * =====================================================
     * TRANSACTION CONFIRM / FAIL
     * =====================================================
     */

    fun confirmTransaction(
        transactionId: String,
        cardId: Long,
        uid: String,
        writtenBalance: Long,
        writtenCounter: Long
    ) {

        val body =
            JSONObject().apply {

                put("transactionId", transactionId)
                put("cardId", cardId)
                put("uid", uid)
                put("writtenBalance", writtenBalance)
                put("writtenCounter", writtenCounter)
            }

        val response =
            postJson(
                path =
                    "/transactions/confirm",
                body =
                    body
            )

        if (
            !response.optBoolean(
                "confirmed",
                false
            )
        ) {

            throw IllegalStateException(
                "El servidor no confirmó la operación."
            )
        }
    }

    fun failTransaction(
        transactionId: String,
        reason: String
    ) {

        val body =
            JSONObject().apply {

                put(
                    "transactionId",
                    transactionId
                )

                put(
                    "reason",
                    reason
                )
            }

        postJson(
            path =
                "/transactions/fail",
            body =
                body
        )
    }

    /*
     * =====================================================
     * PARSERS
     * =====================================================
     */

    private fun parseAdminSession(
        json: JSONObject
    ): AdminSession {

        val device =
            json.getJSONObject(
                "device"
            )

        val adminCard =
            json.getJSONObject(
                "adminCard"
            )

        return AdminSession(

            sessionId =
                json.getString(
                    "sessionId"
                ),

            deviceCode =
                device.getString(
                    "code"
                ),

            deviceName =
                device.getString(
                    "name"
                ),

            adminCardId =
                adminCard.getLong(
                    "cardId"
                ),

            adminCardUid =
                adminCard.getString(
                    "uid"
                ),

            startedAt =
                json
                    .optString(
                        "startedAt"
                    )
                    .takeIf {
                        it.isNotBlank() &&
                                it != "null"
                    }
        )
    }

    private fun parseGameSession(
        json: JSONObject
    ): GameSession {

        val device =
            json.getJSONObject(
                "device"
            )

        val game =
            json.getJSONObject(
                "game"
            )

        val gameCard =
            json.optJSONObject(
                "gameCard"
            )

        return GameSession(

            sessionId =
                json.getString(
                    "sessionId"
                ),

            deviceCode =
                device.getString(
                    "code"
                ),

            deviceName =
                device.getString(
                    "name"
                ),

            gameCardId =
                gameCard
                    ?.optLong(
                        "cardId"
                    ),

            gameCardUid =
                gameCard
                    ?.optString(
                        "uid"
                    ),

            gameCode =
                game.getString(
                    "code"
                ),

            gameName =
                game.getString(
                    "name"
                ),

            price =
                game.getLong(
                    "price"
                ),

            startedAt =
                json
                    .optString(
                        "startedAt"
                    )
                    .takeIf {
                        it.isNotBlank() &&
                                it != "null"
                    }
        )
    }

    private fun parseRechargeSession(
        json: JSONObject
    ): RechargeSession {

        val device =
            json.getJSONObject(
                "device"
            )

        val rechargePoint =
            json.getJSONObject(
                "rechargePoint"
            )

        val rechargeCard =
            json.optJSONObject(
                "rechargeCard"
            )

        return RechargeSession(

            sessionId =
                json.getString(
                    "sessionId"
                ),

            deviceCode =
                device.getString(
                    "code"
                ),

            deviceName =
                device.getString(
                    "name"
                ),

            rechargeCardId =
                rechargeCard
                    ?.optLong(
                        "cardId"
                    ),

            rechargeCardUid =
                rechargeCard
                    ?.optString(
                        "uid"
                    ),

            rechargePointCode =
                rechargePoint.getString(
                    "code"
                ),

            rechargePointName =
                rechargePoint.getString(
                    "name"
                ),

            startedAt =
                json
                    .optString(
                        "startedAt"
                    )
                    .takeIf {
                        it.isNotBlank() &&
                                it != "null"
                    }
        )
    }

    /*
     * =====================================================
     * HTTP
     * =====================================================
     */

    private fun openConnection(
        path: String,
        method: String
    ): HttpURLConnection {

        return (
                URL(
                    "${ServerConfig.BASE_URL}$path"
                )
                    .openConnection()
                        as HttpURLConnection
                ).apply {

                requestMethod =
                    method

                connectTimeout =
                    3_000

                readTimeout =
                    3_000

                setRequestProperty(
                    "Accept",
                    "application/json"
                )
            }
    }

    private fun postJson(
        path: String,
        body: JSONObject
    ): JSONObject {

        val connection =
            openConnection(
                path =
                    path,
                method =
                    "POST"
            )

        try {

            connection.doOutput =
                true

            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            connection
                .outputStream
                .use {

                    it.write(
                        body
                            .toString()
                            .toByteArray(
                                Charsets.UTF_8
                            )
                    )
                }

            val responseCode =
                connection.responseCode

            val json =
                readJsonResponse(
                    connection,
                    responseCode
                )

            if (
                responseCode !in 200..299
            ) {

                throw serverException(
                    json
                )
            }

            return json

        } finally {

            connection.disconnect()
        }
    }

    private fun putJson(
        path: String,
        body: JSONObject
    ): JSONObject {

        val connection =
            openConnection(
                path =
                    path,
                method =
                    "PUT"
            )

        try {

            connection.doOutput =
                true

            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            connection
                .outputStream
                .use {

                    it.write(
                        body
                            .toString()
                            .toByteArray(
                                Charsets.UTF_8
                            )
                    )
                }

            val responseCode =
                connection.responseCode

            val json =
                readJsonResponse(
                    connection,
                    responseCode
                )

            if (
                responseCode !in 200..299
            ) {
                throw serverException(
                    json
                )
            }

            return json

        } finally {
            connection.disconnect()
        }
    }


    private fun readJsonResponse(
        connection: HttpURLConnection,
        responseCode: Int
    ): JSONObject {

        val stream =
            if (
                responseCode in 200..299
            ) {

                connection.inputStream

            } else {

                connection.errorStream
            }

        val text =
            stream
                ?.bufferedReader()
                ?.use {
                    it.readText()
                }
                ?: ""

        return if (
            text.isBlank()
        ) {

            JSONObject()

        } else {

            JSONObject(text)
        }
    }

    private fun serverException(
        json: JSONObject
    ): IllegalStateException {

        val error =
            json.optString(
                "error",
                "SERVER_ERROR"
            )

        val message =
            json.optString(
                "message",
                error
            )

        return IllegalStateException(
            "$error: $message"
        )
    }
}
