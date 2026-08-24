package com.espectacularesmeneses.feria.network

import com.espectacularesmeneses.feria.device.DeviceRuntimeIdentity
import com.espectacularesmeneses.feria.model.RechargeAuthorization
import com.espectacularesmeneses.feria.model.RechargePromotion
import com.espectacularesmeneses.feria.util.ServerConfig

import org.json.JSONObject

import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

object PromotionApiClient {

    fun getActivePromotions():
            List<RechargePromotion> {

        val identity =
            DeviceRuntimeIdentity
                .requireIdentity()

        val connection =
            openConnection(
                path =
                    "/promotions/active",

                method =
                    "GET"
            )

        connection.setRequestProperty(
            "x-device-code",
            identity.deviceCode
        )

        connection.setRequestProperty(
            "x-device-token",
            identity.deviceToken
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
                responseCode !in
                200..299
            ) {

                throw serverException(
                    json,
                    responseCode
                )
            }

            val promotionsJson =
                json.getJSONArray(
                    "promotions"
                )

            val promotions =
                mutableListOf<
                        RechargePromotion
                        >()

            for (
            index in
            0 until promotionsJson.length()
            ) {

                val item =
                    promotionsJson
                        .getJSONObject(
                            index
                        )

                promotions +=
                    RechargePromotion(
                        id =
                            item.getString(
                                "id"
                            ),

                        name =
                            item.getString(
                                "name"
                            ),

                        cashAmount =
                            item.getLong(
                                "cashAmount"
                            ),

                        promotionalAmount =
                            item.getLong(
                                "promotionalAmount"
                            ),

                        totalCreditAmount =
                            item.getLong(
                                "totalCreditAmount"
                            )
                    )
            }

            return promotions

        } finally {

            connection.disconnect()
        }
    }

    fun authorizePromotionalRecharge(
        cardId: Long,
        uid: String,
        promotionId: String,
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
                put("promotionId", promotionId)
                put("cardBalance", cardBalance)
                put("cardCounter", cardCounter)

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity
                        .deviceCode()
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
                    5_000

                readTimeout =
                    5_000

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
                "application/json; charset=utf-8"
            )

            connection
                .outputStream
                .bufferedWriter(
                    Charsets.UTF_8
                )
                .use {
                        writer ->

                    writer.write(
                        body.toString()
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
                responseCode !in
                200..299
            ) {

                throw serverException(
                    json,
                    responseCode
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
                responseCode in
                200..299
            ) {

                connection.inputStream

            } else {

                connection.errorStream
                    ?: connection.inputStream
            }

        val text =
            stream
                .bufferedReader(
                    Charsets.UTF_8
                )
                .use {
                        reader ->

                    reader.readText()
                }

        if (
            text.isBlank()
        ) {

            return JSONObject()
        }

        return JSONObject(
            text
        )
    }

    private fun serverException(
        json: JSONObject,
        responseCode: Int
    ): IllegalStateException {

        val code =
            json.optString(
                "error"
            )
                .takeIf {
                    it.isNotBlank()
                }
                ?: "HTTP_$responseCode"

        val message =
            json.optString(
                "message"
            )
                .takeIf {
                    it.isNotBlank()
                }

        return IllegalStateException(
            if (
                message != null
            ) {
                "$code: $message"
            } else {
                code
            }
        )
    }
}
