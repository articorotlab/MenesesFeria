package com.espectacularesmeneses.feria.network

import com.espectacularesmeneses.feria.device.DeviceRuntimeIdentity
import com.espectacularesmeneses.feria.util.ServerConfig

import org.json.JSONObject

import java.net.HttpURLConnection
import java.net.URL


object DeviceHeartbeatApiClient {

    /*
     * =====================================================
     * HEARTBEAT
     * =====================================================
     *
     * Envía al servidor una confirmación de que este
     * Ulefone sigue activo y conectado.
     *
     * El servidor valida:
     *
     * - deviceCode;
     * - deviceToken;
     * - status ACTIVE.
     *
     * Si todo es correcto actualiza:
     *
     * devices.last_seen_at
     * =====================================================
     */

    fun sendHeartbeat(): Boolean {

        val identity =
            DeviceRuntimeIdentity
                .requireIdentity()


        val body =
            JSONObject().apply {

                put(
                    "deviceCode",
                    identity.deviceCode
                )

                put(
                    "deviceToken",
                    identity.deviceToken
                )
            }


        val connection =
            openConnection(
                path =
                    "/devices/heartbeat",

                method =
                    "POST"
            )


        try {

            connection.doOutput =
                true


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
                connection
                    .responseCode


            if (
                responseCode !in
                200..299
            ) {

                return false
            }


            val responseText =
                connection
                    .inputStream
                    .bufferedReader(
                        Charsets.UTF_8
                    )
                    .use {
                            reader ->

                        reader.readText()
                    }


            if (
                responseText.isBlank()
            ) {

                return false
            }


            val response =
                JSONObject(
                    responseText
                )


            return response
                .optBoolean(
                    "alive",
                    false
                )


        } catch (
            _error: Exception
        ) {

            /*
             * El heartbeat nunca debe tumbar la aplicación.
             *
             * Si se pierde Wi-Fi o el servidor no responde,
             * simplemente dejamos de actualizar last_seen_at.
             *
             * Eso permitirá que la web marque posteriormente
             * el Ulefone como desconectado.
             */

            return false


        } finally {

            connection.disconnect()
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

        val connection =
            URL(
                "${ServerConfig.BASE_URL}$path"
            )
                .openConnection()
                    as HttpURLConnection


        connection.requestMethod =
            method


        connection.connectTimeout =
            5_000


        connection.readTimeout =
            5_000


        connection.setRequestProperty(
            "Content-Type",
            "application/json; charset=utf-8"
        )


        connection.setRequestProperty(
            "Accept",
            "application/json"
        )


        return connection
    }
}