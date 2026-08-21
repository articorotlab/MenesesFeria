package com.espectacularesmeneses.feria.network

import com.espectacularesmeneses.feria.device.DeviceIdentity
import com.espectacularesmeneses.feria.util.ServerConfig
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object DeviceProvisioningApiClient {

    fun provision(
        provisioningCode: String
    ): DeviceIdentity {

        val body =
            JSONObject().apply {
                put(
                    "code",
                    provisioningCode
                        .trim()
                        .uppercase()
                )
            }

        val response =
            postJson(
                path = "/devices/provision",
                body = body
            )

        if (
            !response.optBoolean(
                "provisioned",
                false
            )
        ) {
            throw IllegalStateException(
                "El servidor no confirmó la activación."
            )
        }

        val device =
            response.getJSONObject(
                "device"
            )

        val credential =
            response.getJSONObject(
                "credential"
            )

        return DeviceIdentity(
            deviceId =
                device.getString(
                    "deviceId"
                ),

            deviceCode =
                device.getString(
                    "code"
                ),

            deviceName =
                device.getString(
                    "name"
                ),

            deviceToken =
                credential.getString(
                    "deviceToken"
                )
        )
    }

    fun verify(
        identity: DeviceIdentity
    ): Boolean {

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
                path = "/devices/verify",
                method = "POST"
            )

        try {

            connection.doOutput = true

            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            connection
                .outputStream
                .use { stream ->

                    stream.write(
                        body
                            .toString()
                            .toByteArray(
                                Charsets.UTF_8
                            )
                    )
                }

            val responseCode =
                connection.responseCode

            return responseCode in 200..299

        } finally {

            connection.disconnect()
        }
    }

    private fun postJson(
        path: String,
        body: JSONObject
    ): JSONObject {

        val connection =
            openConnection(
                path = path,
                method = "POST"
            )

        try {

            connection.doOutput = true

            connection.setRequestProperty(
                "Content-Type",
                "application/json"
            )

            connection
                .outputStream
                .use { stream ->

                    stream.write(
                        body
                            .toString()
                            .toByteArray(
                                Charsets.UTF_8
                            )
                    )
                }

            val responseCode =
                connection.responseCode

            val stream =
                if (
                    responseCode in 200..299
                ) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

            val responseText =
                stream
                    ?.bufferedReader()
                    ?.use {
                        it.readText()
                    }
                    ?: ""

            val json =
                if (
                    responseText.isBlank()
                ) {
                    JSONObject()
                } else {
                    JSONObject(
                        responseText
                    )
                }

            if (
                responseCode !in 200..299
            ) {

                val error =
                    json.optString(
                        "error",
                        "UNKNOWN_ERROR"
                    )

                throw IllegalStateException(
                    error
                )
            }

            return json

        } finally {

            connection.disconnect()
        }
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
}