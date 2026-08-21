package com.espectacularesmeneses.feria.network

import com.espectacularesmeneses.feria.device.DeviceRuntimeIdentity

import com.espectacularesmeneses.feria.model.AdminSession
import com.espectacularesmeneses.feria.model.CardRegistrationAuthorization
import com.espectacularesmeneses.feria.model.ChargeAuthorization
import com.espectacularesmeneses.feria.model.CustomerHistory
import com.espectacularesmeneses.feria.model.CustomerHistoryItem
import com.espectacularesmeneses.feria.model.GameSession
import com.espectacularesmeneses.feria.model.RechargeAuthorization
import com.espectacularesmeneses.feria.model.RechargeSession
import com.espectacularesmeneses.feria.model.TransactionReconciliation
import com.espectacularesmeneses.feria.util.ServerConfig
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

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

            items =
                items
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
