package com.espectacularesmeneses.feria.network

import com.espectacularesmeneses.feria.device.DeviceRuntimeIdentity
import com.espectacularesmeneses.feria.util.ServerConfig
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

object RechargeCheckoutApiClient {

    enum class PaymentMethod {
        CASH,
        CARD
    }

    data class CheckoutAmounts(
        val paidRecharge: Long,
        val promotionalCredit: Long,
        val credited: Long,
        val activationFee: Long,
        val totalDue: Long
    )

    data class Checkout(
        val checkoutId: String,
        val idempotencyKey: String,
        val targetUid: String,
        val cardId: Long?,
        val cardPath: String,
        val registrationId: String?,
        val activationTransactionId: String?,
        val rechargeTransactionId: String?,
        val promotionId: String?,
        val amounts: CheckoutAmounts,
        val paymentMethod: String?,
        val status: String,
        val failureReason: String?
    )

    data class Registration(
        val registrationId: String,
        val status: String,
        val reservedCardId: Long
    )

    data class CardState(
        val cardId: Long,
        val uid: String,
        val cardType: String,
        val status: String,
        val balance: Long,
        val transactionCounter: Long
    )

    data class PrepareResult(
        val prepared: Boolean,
        val duplicated: Boolean,
        val checkout: Checkout
    )

    data class AuthorizeResult(
        val authorized: Boolean,
        val duplicated: Boolean,
        val checkout: Checkout,
        val registration: Registration?,
        val transactionId: String?,
        val transactionStatus: String?,
        val beforeCardState: CardState?,
        val finalCardState: CardState
    )

    data class ResumeResult(
        val found: Boolean,
        val state: String,
        val checkout: Checkout?,
        val registration: Registration?,
        val beforeCardState: CardState?,
        val finalCardState: CardState?
    )

    data class ConfirmResult(
        val confirmed: Boolean,
        val duplicated: Boolean,
        val checkout: Checkout,
        val card: CardState
    )

    fun newIdempotencyKey(): String = UUID.randomUUID().toString()

    fun prepare(
        targetUid: String,
        amount: Long,
        paymentMethod: PaymentMethod,
        promotionId: String? = null,
        idempotencyKey: String = newIdempotencyKey()
    ): PrepareResult {

        val body = JSONObject().apply {
            put("idempotencyKey", idempotencyKey)
            put("deviceCode", DeviceRuntimeIdentity.deviceCode())
            put("targetUid", targetUid.trim().uppercase())
            put("amount", amount)
            put("paymentMethod", paymentMethod.name)

            if (!promotionId.isNullOrBlank()) {
                put("promotionId", promotionId.trim())
            }
        }

        val json = postJson(
            path = "/recharge-checkouts/prepare",
            body = body
        )

        return PrepareResult(
            prepared = json.optBoolean("prepared", false),
            duplicated = json.optBoolean("duplicated", false),
            checkout = checkoutFromJson(json.getJSONObject("checkout"))
        )
    }

    fun authorizeNew(
        checkoutId: String,
        targetUid: String
    ): AuthorizeResult {

        return authorizeInternal(
            checkoutId = checkoutId,
            targetUid = targetUid,
            cardBalance = null,
            cardCounter = null
        )
    }

    fun authorizeExisting(
        checkoutId: String,
        targetUid: String,
        cardBalance: Long,
        cardCounter: Long
    ): AuthorizeResult {

        return authorizeInternal(
            checkoutId = checkoutId,
            targetUid = targetUid,
            cardBalance = cardBalance,
            cardCounter = cardCounter
        )
    }

    private fun authorizeInternal(
        checkoutId: String,
        targetUid: String,
        cardBalance: Long?,
        cardCounter: Long?
    ): AuthorizeResult {

        val body = JSONObject().apply {
            put("checkoutId", checkoutId.trim())
            put("deviceCode", DeviceRuntimeIdentity.deviceCode())
            put("targetUid", targetUid.trim().uppercase())

            if (cardBalance != null) {
                put("cardBalance", cardBalance)
            }

            if (cardCounter != null) {
                put("cardCounter", cardCounter)
            }
        }

        val json = postJson(
            path = "/recharge-checkouts/authorize",
            body = body
        )

        val transaction = json.optJSONObject("transaction")

        return AuthorizeResult(
            authorized = json.optBoolean("authorized", false),
            duplicated = json.optBoolean("duplicated", false),
            checkout = checkoutFromJson(json.getJSONObject("checkout")),
            registration = registrationFromJsonOrNull(json.optJSONObject("registration")),
            transactionId = transaction?.optString("transactionId")?.takeIf { it.isNotBlank() },
            transactionStatus = transaction?.optString("status")?.takeIf { it.isNotBlank() },
            beforeCardState = cardStateFromJsonOrNull(json.optJSONObject("beforeCardState")),
            finalCardState = cardStateFromJson(json.getJSONObject("finalCardState"))
        )
    }

    fun resume(
        targetUid: String
    ): ResumeResult {

        val body = JSONObject().apply {
            put("deviceCode", DeviceRuntimeIdentity.deviceCode())
            put("targetUid", targetUid.trim().uppercase())
        }

        val json = postJson(
            path = "/recharge-checkouts/resume",
            body = body
        )

        return ResumeResult(
            found = json.optBoolean("found", false),
            state = json.optString("state", "NONE"),
            checkout = json.optJSONObject("checkout")?.let(::checkoutFromJson),
            registration = registrationFromJsonOrNull(json.optJSONObject("registration")),
            beforeCardState = cardStateFromJsonOrNull(json.optJSONObject("beforeCardState")),
            finalCardState = cardStateFromJsonOrNull(json.optJSONObject("finalCardState"))
        )
    }

    fun confirm(
        checkoutId: String,
        targetUid: String,
        cardId: Long,
        writtenBalance: Long,
        writtenCounter: Long
    ): ConfirmResult {

        val body = JSONObject().apply {
            put("checkoutId", checkoutId.trim())
            put("deviceCode", DeviceRuntimeIdentity.deviceCode())
            put("targetUid", targetUid.trim().uppercase())
            put("cardId", cardId)
            put("writtenBalance", writtenBalance)
            put("writtenCounter", writtenCounter)
        }

        val json = postJson(
            path = "/recharge-checkouts/confirm",
            body = body
        )

        return ConfirmResult(
            confirmed = json.optBoolean("confirmed", false),
            duplicated = json.optBoolean("duplicated", false),
            checkout = checkoutFromJson(json.getJSONObject("checkout")),
            card = cardStateFromJson(json.getJSONObject("card"))
        )
    }

    private fun checkoutFromJson(json: JSONObject): Checkout {
        val amountsJson = json.getJSONObject("amounts")

        return Checkout(
            checkoutId = json.getString("checkoutId"),
            idempotencyKey = json.getString("idempotencyKey"),
            targetUid = json.getString("targetUid"),
            cardId = json.optLongOrNull("cardId"),
            cardPath = json.getString("cardPath"),
            registrationId = json.optStringOrNull("registrationId"),
            activationTransactionId = json.optStringOrNull("activationTransactionId"),
            rechargeTransactionId = json.optStringOrNull("rechargeTransactionId"),
            promotionId = json.optStringOrNull("promotionId"),
            amounts = CheckoutAmounts(
                paidRecharge = amountsJson.getLong("paidRecharge"),
                promotionalCredit = amountsJson.getLong("promotionalCredit"),
                credited = amountsJson.getLong("credited"),
                activationFee = amountsJson.getLong("activationFee"),
                totalDue = amountsJson.getLong("totalDue")
            ),
            paymentMethod = json.optStringOrNull("paymentMethod"),
            status = json.getString("status"),
            failureReason = json.optStringOrNull("failureReason")
        )
    }

    private fun registrationFromJsonOrNull(json: JSONObject?): Registration? {
        if (json == null) return null

        return Registration(
            registrationId = json.getString("registrationId"),
            status = json.getString("status"),
            reservedCardId = json.getLong("reservedCardId")
        )
    }

    private fun cardStateFromJson(json: JSONObject): CardState {
        return CardState(
            cardId = json.getLong("cardId"),
            uid = json.getString("uid"),
            cardType = json.getString("cardType"),
            status = json.getString("status"),
            balance = json.getLong("balance"),
            transactionCounter = json.getLong("transactionCounter")
        )
    }

    private fun cardStateFromJsonOrNull(json: JSONObject?): CardState? {
        return json?.let(::cardStateFromJson)
    }

    private fun JSONObject.optStringOrNull(name: String): String? {
        if (!has(name) || isNull(name)) return null

        return optString(name)
            .takeIf { it.isNotBlank() }
    }

    private fun JSONObject.optLongOrNull(name: String): Long? {
        if (!has(name) || isNull(name)) return null
        return getLong(name)
    }

    private fun postJson(
        path: String,
        body: JSONObject
    ): JSONObject {

        val connection =
            (URL("${ServerConfig.BASE_URL}$path")
                .openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 5_000
                readTimeout = 8_000
                doOutput = true
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
            }

        try {
            connection.outputStream
                .bufferedWriter(Charsets.UTF_8)
                .use { writer ->
                    writer.write(body.toString())
                }

            val responseCode = connection.responseCode
            val json = readJsonResponse(connection, responseCode)

            if (responseCode !in 200..299) {
                throw serverException(json, responseCode)
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
            if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream ?: connection.inputStream
            }

        val text =
            stream.bufferedReader(Charsets.UTF_8)
                .use { reader -> reader.readText() }

        return if (text.isBlank()) {
            JSONObject()
        } else {
            JSONObject(text)
        }
    }

    private fun serverException(
        json: JSONObject,
        responseCode: Int
    ): IllegalStateException {

        val code =
            json.optString("error")
                .takeIf { it.isNotBlank() }
                ?: "HTTP_$responseCode"

        val message =
            json.optString("message")
                .takeIf { it.isNotBlank() }

        return IllegalStateException(
            if (message != null) {
                "$code: $message"
            } else {
                code
            }
        )
    }
}
