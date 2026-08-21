package com.espectacularesmeneses.feria.network

import com.espectacularesmeneses.feria.device.DeviceRuntimeIdentity

import com.espectacularesmeneses.feria.model.AdminDeviceHistory
import com.espectacularesmeneses.feria.model.AdminDeviceReport
import com.espectacularesmeneses.feria.model.AdminDeviceReportHeader
import com.espectacularesmeneses.feria.model.AdminDeviceSessionAdmin
import com.espectacularesmeneses.feria.model.AdminDeviceSessionGame
import com.espectacularesmeneses.feria.model.AdminDeviceSessionMetrics
import com.espectacularesmeneses.feria.model.AdminDeviceSessionRechargePoint
import com.espectacularesmeneses.feria.model.AdminDeviceSessionReport
import com.espectacularesmeneses.feria.model.AdminGameDailyReport
import com.espectacularesmeneses.feria.model.AdminGameReport
import com.espectacularesmeneses.feria.model.AdminRechargePointDailyReport
import com.espectacularesmeneses.feria.model.AdminRechargePointReport
import com.espectacularesmeneses.feria.model.AdminReportSummary
import com.espectacularesmeneses.feria.util.ServerConfig
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object ReportsApiClient {

    /*
     * =====================================================
     * RESUMEN
     * =====================================================
     *
     * GET /admin/reports/summary
     * =====================================================
     */
    fun getSummary(
        from: String,
        to: String
    ): AdminReportSummary {

        validateDateRange(
            from = from,
            to = to
        )

        val json =
            getJson(
                path =
                    "/admin/reports/summary" +
                            reportQuery(
                                from = from,
                                to = to
                            )
            )

        val summary =
            json.getJSONObject(
                "summary"
            )

        val admin =
            summary.getJSONObject(
                "admin"
            )

        return AdminReportSummary(

            from =
                json.getString(
                    "from"
                ),

            to =
                json.getString(
                    "to"
                ),

            timezone =
                json.getString(
                    "timezone"
                ),

            rechargePointAmount =
                summary.getLong(
                    "rechargePointAmount"
                ),

            gameConsumptionAmount =
                summary.getLong(
                    "gameConsumptionAmount"
                ),

            gamePeopleCount =
                summary.getLong(
                    "gamePeopleCount"
                ),

            adminRechargeAmount =
                admin.getLong(
                    "rechargeAmount"
                ),

            adminAdjustmentAmount =
                admin.getLong(
                    "adjustmentAmount"
                )
        )
    }


    /*
     * =====================================================
     * JUEGOS
     * =====================================================
     *
     * GET /admin/reports/games
     * =====================================================
     */
    fun getGames(
        from: String,
        to: String
    ): List<AdminGameReport> {

        validateDateRange(
            from = from,
            to = to
        )

        val json =
            getJson(
                path =
                    "/admin/reports/games" +
                            reportQuery(
                                from = from,
                                to = to
                            )
            )

        val items =
            json.getJSONArray(
                "games"
            )

        val result =
            mutableListOf<AdminGameReport>()

        for (
        index in 0 until items.length()
        ) {

            val item =
                items.getJSONObject(
                    index
                )

            val dailyItems =
                item.optJSONArray(
                    "dailyBreakdown"
                )

            val dailyBreakdown =
                mutableListOf<AdminGameDailyReport>()

            if (
                dailyItems != null
            ) {

                for (
                dailyIndex in
                0 until dailyItems.length()
                ) {

                    val daily =
                        dailyItems
                            .getJSONObject(
                                dailyIndex
                            )

                    dailyBreakdown.add(
                        AdminGameDailyReport(

                            date =
                                daily.getString(
                                    "date"
                                ),

                            consumptionAmount =
                                daily.getLong(
                                    "consumptionAmount"
                                ),

                            peopleCount =
                                daily.getLong(
                                    "peopleCount"
                                )
                        )
                    )
                }
            }

            result.add(
                AdminGameReport(

                    gameId =
                        item.getString(
                            "gameId"
                        ),

                    name =
                        item.getString(
                            "name"
                        ),

                    currentPrice =
                        item.getLong(
                            "currentPrice"
                        ),

                    consumptionAmount =
                        item.getLong(
                            "consumptionAmount"
                        ),

                    peopleCount =
                        item.getLong(
                            "peopleCount"
                        ),

                    dailyBreakdown =
                        dailyBreakdown
                )
            )
        }

        return result
    }


    /*
     * =====================================================
     * TAQUILLAS
     * =====================================================
     *
     * GET /admin/reports/recharge-points
     * =====================================================
     */
    fun getRechargePoints(
        from: String,
        to: String
    ): List<AdminRechargePointReport> {

        validateDateRange(
            from = from,
            to = to
        )

        val json =
            getJson(
                path =
                    "/admin/reports/recharge-points" +
                            reportQuery(
                                from = from,
                                to = to
                            )
            )

        val items =
            json.getJSONArray(
                "rechargePoints"
            )

        val result =
            mutableListOf<AdminRechargePointReport>()

        for (
        index in 0 until items.length()
        ) {

            val item =
                items.getJSONObject(
                    index
                )

            val dailyItems =
                item.optJSONArray(
                    "dailyBreakdown"
                )

            val dailyBreakdown =
                mutableListOf<AdminRechargePointDailyReport>()

            if (
                dailyItems != null
            ) {

                for (
                dailyIndex in
                0 until dailyItems.length()
                ) {

                    val daily =
                        dailyItems
                            .getJSONObject(
                                dailyIndex
                            )

                    dailyBreakdown.add(
                        AdminRechargePointDailyReport(

                            date =
                                daily.getString(
                                    "date"
                                ),

                            rechargedAmount =
                                daily.getLong(
                                    "rechargedAmount"
                                )
                        )
                    )
                }
            }

            result.add(
                AdminRechargePointReport(

                    rechargePointId =
                        item.getString(
                            "rechargePointId"
                        ),

                    name =
                        item.getString(
                            "name"
                        ),

                    rechargedAmount =
                        item.getLong(
                            "rechargedAmount"
                        ),

                    dailyBreakdown =
                        dailyBreakdown
                )
            )
        }

        return result
    }


    /*
     * =====================================================
     * DISPOSITIVOS
     * =====================================================
     *
     * GET /admin/reports/devices
     * =====================================================
     */
    fun getDevices(
        from: String,
        to: String
    ): List<AdminDeviceReport> {

        validateDateRange(
            from = from,
            to = to
        )

        val json =
            getJson(
                path =
                    "/admin/reports/devices" +
                            reportQuery(
                                from = from,
                                to = to
                            )
            )

        val items =
            json.getJSONArray(
                "devices"
            )

        val result =
            mutableListOf<AdminDeviceReport>()

        for (
        index in 0 until items.length()
        ) {

            val item =
                items.getJSONObject(
                    index
                )

            result.add(
                AdminDeviceReport(

                    deviceId =
                        item.getString(
                            "deviceId"
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

                    sessionSeconds =
                        item.getLong(
                            "sessionSeconds"
                        )
                )
            )
        }

        return result
    }


    /*
     * =====================================================
     * HISTORIAL DE UN DISPOSITIVO
     * =====================================================
     *
     * GET /admin/reports/devices/:deviceId/history
     * =====================================================
     */
    fun getDeviceHistory(
        deviceId: String,
        from: String,
        to: String
    ): AdminDeviceHistory {

        if (
            deviceId.isBlank()
        ) {
            throw IllegalArgumentException(
                "El dispositivo no tiene un ID válido."
            )
        }

        validateDateRange(
            from = from,
            to = to
        )

        val encodedDeviceId =
            encode(
                deviceId.trim()
            )

        val json =
            getJson(
                path =
                    "/admin/reports/devices/" +
                            encodedDeviceId +
                            "/history" +
                            reportQuery(
                                from = from,
                                to = to
                            )
            )

        val device =
            json.getJSONObject(
                "device"
            )

        val sessionsJson =
            json.getJSONArray(
                "sessions"
            )

        val sessions =
            mutableListOf<AdminDeviceSessionReport>()

        for (
        index in 0 until sessionsJson.length()
        ) {

            val item =
                sessionsJson.getJSONObject(
                    index
                )

            val game =
                if (
                    item.has("game") &&
                    !item.isNull("game")
                ) {

                    val gameJson =
                        item.getJSONObject(
                            "game"
                        )

                    AdminDeviceSessionGame(

                        gameId =
                            gameJson.getString(
                                "gameId"
                            ),

                        name =
                            gameJson.getString(
                                "name"
                            )
                    )

                } else {

                    null
                }

            val rechargePoint =
                if (
                    item.has(
                        "rechargePoint"
                    ) &&
                    !item.isNull(
                        "rechargePoint"
                    )
                ) {

                    val rechargeJson =
                        item.getJSONObject(
                            "rechargePoint"
                        )

                    AdminDeviceSessionRechargePoint(

                        rechargePointId =
                            rechargeJson.getString(
                                "rechargePointId"
                            ),

                        name =
                            rechargeJson.getString(
                                "name"
                            )
                    )

                } else {

                    null
                }

            val admin =
                if (
                    item.has("admin") &&
                    !item.isNull("admin")
                ) {

                    val adminJson =
                        item.getJSONObject(
                            "admin"
                        )

                    AdminDeviceSessionAdmin(

                        cardId =
                            if (
                                adminJson.has(
                                    "cardId"
                                ) &&
                                !adminJson.isNull(
                                    "cardId"
                                )
                            ) {
                                adminJson.getLong(
                                    "cardId"
                                )
                            } else {
                                null
                            }
                    )

                } else {

                    null
                }

            val metricsJson =
                item.getJSONObject(
                    "metrics"
                )

            sessions.add(
                AdminDeviceSessionReport(

                    sessionId =
                        item.getString(
                            "sessionId"
                        ),

                    mode =
                        item.getString(
                            "mode"
                        ),

                    status =
                        item.getString(
                            "status"
                        ),

                    startedAt =
                        item.getString(
                            "startedAt"
                        ),

                    endedAt =
                        optionalString(
                            item,
                            "endedAt"
                        ),

                    visibleStartedAt =
                        item.getString(
                            "visibleStartedAt"
                        ),

                    visibleEndedAt =
                        item.getString(
                            "visibleEndedAt"
                        ),

                    durationSeconds =
                        item.getLong(
                            "durationSeconds"
                        ),

                    game =
                        game,

                    rechargePoint =
                        rechargePoint,

                    admin =
                        admin,

                    metrics =
                        AdminDeviceSessionMetrics(

                            gameConsumptionAmount =
                                metricsJson.getLong(
                                    "gameConsumptionAmount"
                                ),

                            gamePeopleCount =
                                metricsJson.getLong(
                                    "gamePeopleCount"
                                ),

                            rechargeAmount =
                                metricsJson.getLong(
                                    "rechargeAmount"
                                ),

                            adminRechargeAmount =
                                metricsJson.getLong(
                                    "adminRechargeAmount"
                                ),

                            adminAdjustmentAmount =
                                metricsJson.getLong(
                                    "adminAdjustmentAmount"
                                )
                        )
                )
            )
        }

        return AdminDeviceHistory(

            from =
                json.getString(
                    "from"
                ),

            to =
                json.getString(
                    "to"
                ),

            timezone =
                json.getString(
                    "timezone"
                ),

            device =
                AdminDeviceReportHeader(

                    deviceId =
                        device.getString(
                            "deviceId"
                        ),

                    code =
                        device.getString(
                            "code"
                        ),

                    name =
                        device.getString(
                            "name"
                        ),

                    status =
                        device.getString(
                            "status"
                        )
                ),

            sessions =
                sessions
        )
    }


    /*
     * =====================================================
     * QUERY
     * =====================================================
     */
    private fun reportQuery(
        from: String,
        to: String
    ): String {

        return "?deviceCode=" +
                encode(
                    DeviceRuntimeIdentity.deviceCode()
                ) +
                "&from=" +
                encode(
                    from
                ) +
                "&to=" +
                encode(
                    to
                )
    }


    /*
     * =====================================================
     * VALIDACIÓN DE FECHAS
     * =====================================================
     *
     * No intentamos interpretar la zona horaria aquí.
     * El servidor es quien aplica America/Mexico_City.
     *
     * Android solamente manda YYYY-MM-DD.
     * =====================================================
     */
    private fun validateDateRange(
        from: String,
        to: String
    ) {

        val dateRegex =
            Regex(
                """^\d{4}-\d{2}-\d{2}$"""
            )

        if (
            !dateRegex.matches(
                from
            )
        ) {
            throw IllegalArgumentException(
                "La fecha inicial debe tener formato YYYY-MM-DD."
            )
        }

        if (
            !dateRegex.matches(
                to
            )
        ) {
            throw IllegalArgumentException(
                "La fecha final debe tener formato YYYY-MM-DD."
            )
        }

        if (
            from > to
        ) {
            throw IllegalArgumentException(
                "La fecha inicial no puede ser posterior a la fecha final."
            )
        }
    }


    /*
     * =====================================================
     * HTTP GET
     * =====================================================
     */
    private fun getJson(
        path: String
    ): JSONObject {

        val connection =
            openConnection(
                path =
                    path,

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

            return json

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

        return (
                URL(
                    "${ServerConfig.BASE_URL}$path"
                )
                    .openConnection()
                        as HttpURLConnection
                ).apply {

                requestMethod =
                    method

                /*
                 * Reportes pueden ser un poco más pesados
                 * que las operaciones NFC normales.
                 */
                connectTimeout =
                    5_000

                readTimeout =
                    10_000

                setRequestProperty(
                    "Accept",
                    "application/json"
                )
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


    private fun encode(
        value: String
    ): String {

        return URLEncoder.encode(
            value,
            Charsets.UTF_8.name()
        )
    }


    private fun optionalString(
        json: JSONObject,
        key: String
    ): String? {

        if (
            !json.has(key) ||
            json.isNull(key)
        ) {
            return null
        }

        return json
            .optString(key)
            .takeIf {
                it.isNotBlank() &&
                        it != "null"
            }
    }
}
