package com.espectacularesmeneses.feria.network

import com.espectacularesmeneses.feria.device.DeviceRuntimeIdentity

import com.espectacularesmeneses.feria.model.AdminGame
import com.espectacularesmeneses.feria.model.AdminGameCard
import com.espectacularesmeneses.feria.model.GameCardAuthorization
import com.espectacularesmeneses.feria.util.ServerConfig
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

object GameManagementApiClient {


    /*
     * =====================================================
     * CREAR JUEGO
     * =====================================================
     *
     * El administrador solamente captura:
     * - nombre
     * - precio
     *
     * El servidor genera game_code internamente.
     */

    fun createGame(
        name: String,
        price: Long
    ): AdminGame {

        val normalizedName =
            name.trim()

        if (
            normalizedName.length < 2
        ) {

            throw IllegalArgumentException(
                "Escribe un nombre válido para el juego."
            )
        }

        if (
            price <= 0
        ) {

            throw IllegalArgumentException(
                "El precio debe ser mayor a \$0."
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

                put(
                    "price",
                    price
                )
            }

        val response =
            postJson(
                path =
                    "/admin/games",

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
                "El servidor no creó el juego."
            )
        }

        val game =
            response.getJSONObject(
                "game"
            )

        return AdminGame(

            id =
                game.getString(
                    "id"
                ),

            code =
                game.getString(
                    "code"
                ),

            name =
                game.getString(
                    "name"
                ),

            price =
                game.getLong(
                    "price"
                ),

            status =
                game.getString(
                    "status"
                ),

            card =
                null,

            createdAt =
                game
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
     * LISTAR JUEGOS
     * =====================================================
     */

    fun getGames(): List<AdminGame> {

        val connection =
            openConnection(
                path =
                    "/admin/games?deviceCode=" +
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

            val gamesJson =
                json.getJSONArray(
                    "games"
                )

            val games =
                mutableListOf<AdminGame>()

            for (
            index in 0 until gamesJson.length()
            ) {

                val item =
                    gamesJson.getJSONObject(
                        index
                    )

                val cardJson =
                    if (
                        item.has("card") &&
                        !item.isNull("card")
                    ) {

                        item.getJSONObject(
                            "card"
                        )

                    } else {

                        null
                    }

                val card =
                    cardJson?.let {

                        AdminGameCard(

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

                games.add(

                    AdminGame(

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

                        price =
                            item.getLong(
                                "price"
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

            return games

        } finally {

            connection.disconnect()
        }
    }


    /*
     * =====================================================
     * EDITAR JUEGO
     * =====================================================
     */
    fun updateGame(
        gameId: String,
        name: String,
        price: Long
    ) {

        val normalizedName =
            name.trim()

        if (
            gameId.isBlank()
        ) {
            throw IllegalArgumentException(
                "El juego no tiene un ID válido."
            )
        }

        if (
            normalizedName.length < 2
        ) {
            throw IllegalArgumentException(
                "Escribe un nombre válido para el juego."
            )
        }

        if (
            price <= 0
        ) {
            throw IllegalArgumentException(
                "El precio debe ser mayor a \$0."
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

                put(
                    "price",
                    price
                )
            }

        val response =
            sendJson(
                path =
                    "/admin/games/$gameId",

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
                "El servidor no actualizó el juego."
            )
        }
    }


    /*
     * =====================================================
     * AUTORIZAR TARJETA GAME
     * =====================================================
     */

    fun authorizeGameCard(
        gameId: String,
        targetUid: String
    ): GameCardAuthorization {

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
                    "gameId",
                    gameId
                )

                put(
                    "targetUid",
                    targetUid
                )
            }

        val response =
            postJson(
                path =
                    "/admin/games/card/authorize",

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
                "El servidor no autorizó la tarjeta GAME."
            )
        }

        val game =
            response.getJSONObject(
                "game"
            )

        val initialState =
            response.getJSONObject(
                "initialState"
            )

        return GameCardAuthorization(

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

            gameId =
                game.getString(
                    "id"
                ),

            gameCode =
                game.getString(
                    "code"
                ),

            gameName =
                game.getString(
                    "name"
                ),

            gamePrice =
                game.getLong(
                    "price"
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
     * CONFIRMAR TARJETA GAME
     * =====================================================
     */

    fun confirmGameCard(
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
                    "/admin/games/card/confirm",

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
                "El servidor no confirmó la tarjeta GAME."
            )
        }
    }

    /*
     * =====================================================
     * FALLAR REGISTRO
     * =====================================================
     */

    fun failGameCard(
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
                "/admin/games/card/fail",

            body =
                body
        )
    }

    /*
     * =====================================================
     * ACTIVAR / DESACTIVAR TARJETA GAME
     * =====================================================
     */

    fun updateGameCardStatus(
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
                "Estado de tarjeta GAME no válido."
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
                "El servidor no actualizó la tarjeta GAME."
            )
        }
    }


    /*
     * =====================================================
     * DESVINCULAR TARJETA GAME
     * =====================================================
     */

    fun unlinkGameCard(
        cardId: Long
    ) {

        if (
            cardId <= 0
        ) {
            throw IllegalArgumentException(
                "La tarjeta GAME no tiene un ID válido."
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
                "El servidor no desvinculó la tarjeta GAME."
            )
        }

        val type =
            response.optString(
                "type",
                ""
            )

        if (
            type.isNotBlank() &&
            type != "GAME"
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
