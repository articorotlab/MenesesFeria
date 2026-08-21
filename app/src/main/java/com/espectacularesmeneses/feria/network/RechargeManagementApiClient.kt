package com.espectacularesmeneses.feria.network

import com.espectacularesmeneses.feria.device.DeviceRuntimeIdentity


import com.espectacularesmeneses.feria.model.AdminRechargePoint
import com.espectacularesmeneses.feria.model.AdminRechargePointCard
import com.espectacularesmeneses.feria.model.RechargeCardAuthorization
import com.espectacularesmeneses.feria.util.ServerConfig
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID


object RechargeManagementApiClient {


    /*
     * =====================================================
     * CREAR TAQUILLA
     * =====================================================
     *
     * El administrador solamente captura:
     *
     * - nombre
     *
     * recharge_code se genera en el servidor.
     * =====================================================
     */

    fun createRechargePoint(
        name: String
    ): AdminRechargePoint {

        val normalizedName =
            name.trim()


        if (
            normalizedName.length < 2
        ) {

            throw IllegalArgumentException(
                "Escribe un nombre válido para la taquilla."
            )
        }


        val body =
            JSONObject().apply {

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )

                put(
                    "name",
                    normalizedName
                )
            }


        val response =
            postJson(

                path =
                    "/admin/recharge-points",

                body =
                    body
            )


        if (
            !response.optBoolean(
                "created",
                false
            )
        ) {

            throw IllegalStateException(
                "El servidor no creó la taquilla."
            )
        }


        val rechargePoint =
            response.getJSONObject(
                "rechargePoint"
            )


        return AdminRechargePoint(

            id =
                rechargePoint.getString(
                    "id"
                ),

            code =
                rechargePoint.getString(
                    "code"
                ),

            name =
                rechargePoint.getString(
                    "name"
                ),

            status =
                rechargePoint.getString(
                    "status"
                ),

            card =
                null,

            createdAt =
                rechargePoint
                    .optString(
                        "createdAt"
                    )
                    .takeIf {
                        it.isNotBlank() &&
                                it != "null"
                    }
        )
    }


    /*
     * =====================================================
     * LISTAR TAQUILLAS
     * =====================================================
     */

    fun getRechargePoints():
            List<AdminRechargePoint> {


        val connection =
            openConnection(

                path =
                    "/admin/recharge-points?deviceCode=" +
                            DeviceRuntimeIdentity.deviceCode(),

                method =
                    "GET"
            )


        try {

            val responseCode =
                connection.responseCode


            val json =
                readJsonResponse(

                    connection =
                        connection,

                    responseCode =
                        responseCode
                )


            if (
                responseCode !in 200..299
            ) {

                throw serverException(
                    json
                )
            }


            val rechargePointsJson =
                json.getJSONArray(
                    "rechargePoints"
                )


            val rechargePoints =
                mutableListOf<AdminRechargePoint>()


            for (
            index in
            0 until rechargePointsJson.length()
            ) {

                val item =
                    rechargePointsJson
                        .getJSONObject(
                            index
                        )


                val cardJson =
                    if (
                        item.has(
                            "card"
                        ) &&
                        !item.isNull(
                            "card"
                        )
                    ) {

                        item.getJSONObject(
                            "card"
                        )

                    } else {

                        null
                    }


                val card =
                    cardJson?.let {

                        AdminRechargePointCard(

                            cardId =
                                it.getLong(
                                    "cardId"
                                ),

                            uid =
                                it.getString(
                                    "uid"
                                ),

                            status =
                                it.getString(
                                    "status"
                                )
                        )
                    }


                rechargePoints.add(

                    AdminRechargePoint(

                        id =
                            item.getString(
                                "id"
                            ),

                        code =
                            item.getString(
                                "code"
                            ),

                        name =
                            item.getString(
                                "name"
                            ),

                        status =
                            item.getString(
                                "status"
                            ),

                        card =
                            card,

                        createdAt =
                            item
                                .optString(
                                    "createdAt"
                                )
                                .takeIf {
                                    it.isNotBlank() &&
                                            it != "null"
                                }
                    )
                )
            }


            return rechargePoints


        } finally {

            connection.disconnect()
        }
    }



    /*
     * =====================================================
     * EDITAR TAQUILLA
     * =====================================================
     */
    fun updateRechargePoint(
        rechargePointId: String,
        name: String
    ) {

        val normalizedName =
            name.trim()

        if (
            rechargePointId.isBlank()
        ) {
            throw IllegalArgumentException(
                "La taquilla no tiene un ID válido."
            )
        }

        if (
            normalizedName.length < 2
        ) {
            throw IllegalArgumentException(
                "Escribe un nombre válido para la taquilla."
            )
        }

        val body =
            JSONObject().apply {

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )

                put(
                    "name",
                    normalizedName
                )
            }

        val response =
            sendJson(
                path =
                    "/admin/recharge-points/$rechargePointId",

                method =
                    "PUT",

                body =
                    body
            )

        if (
            !response.optBoolean(
                "updated",
                false
            )
        ) {
            throw IllegalStateException(
                "El servidor no actualizó la taquilla."
            )
        }
    }


    /*
     * =====================================================
     * AUTORIZAR TARJETA RECHARGE
     * =====================================================
     */

    fun authorizeRechargeCard(

        rechargePointId: String,

        targetUid: String

    ): RechargeCardAuthorization {


        if (
            rechargePointId.isBlank()
        ) {

            throw IllegalArgumentException(
                "La taquilla no tiene un ID válido."
            )
        }


        if (
            targetUid.isBlank()
        ) {

            throw IllegalArgumentException(
                "El UID de la tarjeta no es válido."
            )
        }


        val body =
            JSONObject().apply {

                put(
                    "idempotencyKey",
                    UUID
                        .randomUUID()
                        .toString()
                )

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )

                put(
                    "rechargePointId",
                    rechargePointId
                )

                put(
                    "targetUid",
                    targetUid
                )
            }


        val response =
            postJson(

                path =
                    "/admin/recharge-points/card/authorize",

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
                "El servidor no autorizó la tarjeta TAQUILLA."
            )
        }


        val rechargePoint =
            response.getJSONObject(
                "rechargePoint"
            )


        val initialState =
            response.getJSONObject(
                "initialState"
            )


        return RechargeCardAuthorization(

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

            rechargePointId =
                rechargePoint.getString(
                    "id"
                ),

            rechargePointCode =
                rechargePoint.getString(
                    "code"
                ),

            rechargePointName =
                rechargePoint.getString(
                    "name"
                ),

            balance =
                initialState.getLong(
                    "balance"
                ),

            transactionCounter =
                initialState.getLong(
                    "transactionCounter"
                ),

            status =
                initialState.getString(
                    "status"
                )
        )
    }


    /*
     * =====================================================
     * CONFIRMAR TARJETA RECHARGE
     * =====================================================
     */

    fun confirmRechargeCard(

        registrationId: String,

        targetUid: String,

        writtenCardId: Long
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
            }


        val response =
            postJson(

                path =
                    "/admin/recharge-points/card/confirm",

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
                "El servidor no confirmó la tarjeta TAQUILLA."
            )
        }
    }


    /*
     * =====================================================
     * FALLAR REGISTRO DE TARJETA RECHARGE
     * =====================================================
     */

    fun failRechargeCard(

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
                "/admin/recharge-points/card/fail",

            body =
                body
        )
    }


    /*
     * =====================================================
     * ACTIVAR / DESACTIVAR TARJETA TAQUILLA
     * =====================================================
     */

    fun updateRechargeCardStatus(
        cardId: Long,
        status: String
    ) {

        val normalizedStatus =
            status
                .trim()
                .uppercase()

        if (
            normalizedStatus != "ACTIVE" &&
            normalizedStatus != "INACTIVE"
        ) {
            throw IllegalArgumentException(
                "Estado de tarjeta TAQUILLA no válido."
            )
        }

        val body =
            JSONObject().apply {
                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )
                put(
                    "status",
                    normalizedStatus
                )
            }

        val response =
            sendJson(
                path =
                    "/admin/managed-cards/$cardId/status",
                method =
                    "PATCH",
                body =
                    body
            )

        if (
            !response.optBoolean(
                "updated",
                false
            )
        ) {
            throw IllegalStateException(
                "El servidor no actualizó la tarjeta TAQUILLA."
            )
        }
    }


    /*
     * =====================================================
     * DESVINCULAR TARJETA TAQUILLA
     * =====================================================
     */

    fun unlinkRechargeCard(
        cardId: Long
    ) {

        if (
            cardId <= 0
        ) {
            throw IllegalArgumentException(
                "La tarjeta TAQUILLA no tiene un ID válido."
            )
        }

        val body =
            JSONObject().apply {

                put(
                    "deviceCode",
                    DeviceRuntimeIdentity.deviceCode()
                )
            }

        val response =
            sendJson(
                path =
                    "/admin/managed-cards/$cardId/unlink",

                method =
                    "POST",

                body =
                    body
            )

        if (
            !response.optBoolean(
                "unlinked",
                false
            )
        ) {
            throw IllegalStateException(
                "El servidor no desvinculó la tarjeta TAQUILLA."
            )
        }

        val type =
            response.optString(
                "type",
                ""
            )

        if (
            type.isNotBlank() &&
            type != "RECHARGE"
        ) {
            throw IllegalStateException(
                "El servidor devolvió un tipo de tarjeta inesperado."
            )
        }
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

        return sendJson(
            path = path,
            method = "POST",
            body = body
        )
    }

    private fun sendJson(
        path: String,
        method: String,
        body: JSONObject
    ): JSONObject {

        val connection =
            openConnection(
                path =
                    path,

                method =
                    method
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
                    connection =
                        connection,

                    responseCode =
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

            JSONObject(
                text
            )
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
