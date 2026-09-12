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
import com.espectacularesmeneses.feria.model.AdminGameDetailHeader
import com.espectacularesmeneses.feria.model.AdminGameDetailReport
import com.espectacularesmeneses.feria.model.AdminGameDetailSummary
import com.espectacularesmeneses.feria.model.AdminGameReport
import com.espectacularesmeneses.feria.model.AdminGameTransactionFundBreakdown
import com.espectacularesmeneses.feria.model.AdminGameTransactionReport
import com.espectacularesmeneses.feria.model.AdminRechargePointDailyReport
import com.espectacularesmeneses.feria.model.AdminRechargePointDetailHeader
import com.espectacularesmeneses.feria.model.AdminRechargePointDetailReport
import com.espectacularesmeneses.feria.model.AdminRechargePointDetailSummary
import com.espectacularesmeneses.feria.model.AdminRechargePointReport
import com.espectacularesmeneses.feria.model.AdminRechargePointTransactionPromotion
import com.espectacularesmeneses.feria.model.AdminRechargePointTransactionReport
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

        /*
         * Financial Ledger V2 devuelve el resumen
         * agrupado por dominio:
         *
         * summary.rechargePoints
         * summary.games
         * summary.admin
         *
         * AdminReportSummary conserva por ahora los
         * campos que ya consume la UI existente.
         */
        val rechargePoints =
            summary.getJSONObject(
                "rechargePoints"
            )

        val games =
            summary.getJSONObject(
                "games"
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

            /*
             * La UI histórica llama a este campo
             * rechargePointAmount.
             *
             * En Financial Ledger V2 corresponde al
             * crédito TOTAL entregado por taquillas.
             *
             * Más adelante la UI puede mostrar además
             * CASH y PROMOTIONAL por separado.
             */
            rechargePointAmount =
                rechargePoints.getLong(
                    "creditedAmount"
                ),

            gameConsumptionAmount =
                games.getLong(
                    "consumptionAmount"
                ),

            gamePeopleCount =
                games.getLong(
                    "peopleCount"
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
     * JUEGOS — LISTADO
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

                            cashConsumed =
                                daily.optLong(
                                    "cashConsumed",
                                    0L
                                ),

                            promotionalConsumed =
                                daily.optLong(
                                    "promotionalConsumed",
                                    0L
                                ),

                            adminCreditConsumed =
                                daily.optLong(
                                    "adminCreditConsumed",
                                    0L
                                ),

                            legacyConsumed =
                                daily.optLong(
                                    "legacyConsumed",
                                    0L
                                ),

                            peopleCount =
                                daily.getLong(
                                    "peopleCount"
                                ),

                            operationsCount =
                                daily.optLong(
                                    "operationsCount",
                                    0L
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

                    cashConsumed =
                        item.optLong(
                            "cashConsumed",
                            0L
                        ),

                    promotionalConsumed =
                        item.optLong(
                            "promotionalConsumed",
                            0L
                        ),

                    adminCreditConsumed =
                        item.optLong(
                            "adminCreditConsumed",
                            0L
                        ),

                    legacyConsumed =
                        item.optLong(
                            "legacyConsumed",
                            0L
                        ),

                    peopleCount =
                        item.getLong(
                            "peopleCount"
                        ),

                    operationsCount =
                        item.optLong(
                            "operationsCount",
                            0L
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
     * JUEGO — DETALLE / AUDITORÍA
     * =====================================================
     *
     * GET /admin/reports/games/:gameId
     * =====================================================
     */
    fun getGameDetail(
        gameId: String,
        from: String,
        to: String
    ): AdminGameDetailReport {

        if (
            gameId.isBlank()
        ) {
            throw IllegalArgumentException(
                "El juego no tiene un ID válido."
            )
        }

        validateDateRange(
            from = from,
            to = to
        )

        val json =
            getJson(
                path =
                    "/admin/reports/games/" +
                            encode(
                                gameId.trim()
                            ) +
                            reportQuery(
                                from = from,
                                to = to
                            )
            )

        val gameJson =
            json.getJSONObject(
                "game"
            )

        val summaryJson =
            json.getJSONObject(
                "summary"
            )

        val dailyItems =
            json.getJSONArray(
                "dailyBreakdown"
            )

        val dailyBreakdown =
            mutableListOf<AdminGameDailyReport>()

        for (
        index in 0 until dailyItems.length()
        ) {

            val daily =
                dailyItems.getJSONObject(
                    index
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

                    cashConsumed =
                        daily.getLong(
                            "cashConsumed"
                        ),

                    promotionalConsumed =
                        daily.getLong(
                            "promotionalConsumed"
                        ),

                    adminCreditConsumed =
                        daily.getLong(
                            "adminCreditConsumed"
                        ),

                    legacyConsumed =
                        daily.getLong(
                            "legacyConsumed"
                        ),

                    peopleCount =
                        daily.getLong(
                            "peopleCount"
                        ),

                    operationsCount =
                        daily.getLong(
                            "operationsCount"
                        )
                )
            )
        }

        val transactionItems =
            json.getJSONArray(
                "transactions"
            )

        val transactions =
            mutableListOf<AdminGameTransactionReport>()

        for (
        index in 0 until transactionItems.length()
        ) {

            val item =
                transactionItems.getJSONObject(
                    index
                )

            val funds =
                item.getJSONObject(
                    "fundBreakdown"
                )

            transactions.add(
                AdminGameTransactionReport(
                    transactionId =
                        item.getString(
                            "transactionId"
                        ),

                    cardId =
                        item.getLong(
                            "cardId"
                        ),

                    deviceId =
                        optionalString(
                            item,
                            "deviceId"
                        ),

                    amount =
                        item.getLong(
                            "amount"
                        ),

                    quantity =
                        item.getLong(
                            "quantity"
                        ),

                    unitPrice =
                        optionalLong(
                            item,
                            "unitPrice"
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

                    createdAt =
                        item.getString(
                            "createdAt"
                        ),

                    confirmedAt =
                        optionalString(
                            item,
                            "confirmedAt"
                        ),

                    fundBreakdown =
                        AdminGameTransactionFundBreakdown(
                            cash =
                                funds.getLong(
                                    "cash"
                                ),

                            promotional =
                                funds.getLong(
                                    "promotional"
                                ),

                            adminCredit =
                                funds.getLong(
                                    "adminCredit"
                                ),

                            legacy =
                                funds.getLong(
                                    "legacy"
                                )
                        )
                )
            )
        }

        return AdminGameDetailReport(
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

            game =
                AdminGameDetailHeader(
                    gameId =
                        gameJson.getString(
                            "gameId"
                        ),

                    name =
                        gameJson.getString(
                            "name"
                        ),

                    currentPrice =
                        gameJson.getLong(
                            "currentPrice"
                        )
                ),

            summary =
                AdminGameDetailSummary(
                    consumptionAmount =
                        summaryJson.getLong(
                            "consumptionAmount"
                        ),

                    cashConsumed =
                        summaryJson.getLong(
                            "cashConsumed"
                        ),

                    promotionalConsumed =
                        summaryJson.getLong(
                            "promotionalConsumed"
                        ),

                    adminCreditConsumed =
                        summaryJson.getLong(
                            "adminCreditConsumed"
                        ),

                    legacyConsumed =
                        summaryJson.getLong(
                            "legacyConsumed"
                        ),

                    peopleCount =
                        summaryJson.getLong(
                            "peopleCount"
                        ),

                    operationsCount =
                        summaryJson.getLong(
                            "operationsCount"
                        )
                ),

            dailyBreakdown =
                dailyBreakdown,

            transactions =
                transactions
        )
    }


    /*
     * =====================================================
     * TAQUILLAS — LISTADO
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

                    val dailyCreditedAmount =
                        daily.optLong(
                            "creditedAmount",
                            daily.optLong(
                                "rechargedAmount",
                                0L
                            )
                        )

                    dailyBreakdown.add(
                        AdminRechargePointDailyReport(
                            date =
                                daily.getString(
                                    "date"
                                ),

                            cashReceived =
                                daily.optLong(
                                    "cashReceived",
                                    dailyCreditedAmount
                                ),

                            promotionalGiven =
                                daily.optLong(
                                    "promotionalGiven",
                                    0L
                                ),

                            creditedAmount =
                                dailyCreditedAmount,

                            rechargedAmount =
                                dailyCreditedAmount,

                            operationsCount =
                                daily.optLong(
                                    "operationsCount",
                                    0L
                                )
                        )
                    )
                }
            }

            val creditedAmount =
                item.optLong(
                    "creditedAmount",
                    item.optLong(
                        "rechargedAmount",
                        0L
                    )
                )

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

                    cashReceived =
                        item.optLong(
                            "cashReceived",
                            creditedAmount
                        ),

                    promotionalGiven =
                        item.optLong(
                            "promotionalGiven",
                            0L
                        ),

                    creditedAmount =
                        creditedAmount,

                    rechargedAmount =
                        creditedAmount,

                    operationsCount =
                        item.optLong(
                            "operationsCount",
                            0L
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
     * TAQUILLA — DETALLE / AUDITORÍA
     * =====================================================
     *
     * GET /admin/reports/recharge-points/:rechargePointId
     * =====================================================
     */
    fun getRechargePointDetail(
        rechargePointId: String,
        from: String,
        to: String
    ): AdminRechargePointDetailReport {

        if (
            rechargePointId.isBlank()
        ) {
            throw IllegalArgumentException(
                "La taquilla no tiene un ID válido."
            )
        }

        validateDateRange(
            from = from,
            to = to
        )

        val json =
            getJson(
                path =
                    "/admin/reports/recharge-points/" +
                            encode(
                                rechargePointId.trim()
                            ) +
                            reportQuery(
                                from = from,
                                to = to
                            )
            )

        val rechargePointJson =
            json.getJSONObject(
                "rechargePoint"
            )

        val summaryJson =
            json.getJSONObject(
                "summary"
            )

        val dailyItems =
            json.getJSONArray(
                "dailyBreakdown"
            )

        val dailyBreakdown =
            mutableListOf<AdminRechargePointDailyReport>()

        for (
        index in 0 until dailyItems.length()
        ) {

            val daily =
                dailyItems.getJSONObject(
                    index
                )

            val credited =
                daily.getLong(
                    "creditedAmount"
                )

            dailyBreakdown.add(
                AdminRechargePointDailyReport(
                    date =
                        daily.getString(
                            "date"
                        ),

                    cashReceived =
                        daily.getLong(
                            "cashReceived"
                        ),

                    promotionalGiven =
                        daily.getLong(
                            "promotionalGiven"
                        ),

                    creditedAmount =
                        credited,

                    rechargedAmount =
                        credited,

                    operationsCount =
                        daily.getLong(
                            "operationsCount"
                        )
                )
            )
        }

        val transactionItems =
            json.getJSONArray(
                "transactions"
            )

        val transactions =
            mutableListOf<AdminRechargePointTransactionReport>()

        for (
        index in 0 until transactionItems.length()
        ) {

            val item =
                transactionItems.getJSONObject(
                    index
                )

            val promotion =
                if (
                    item.has(
                        "promotion"
                    ) &&
                    !item.isNull(
                        "promotion"
                    )
                ) {

                    val promotionJson =
                        item.getJSONObject(
                            "promotion"
                        )

                    AdminRechargePointTransactionPromotion(
                        promotionId =
                            promotionJson.getString(
                                "promotionId"
                            ),

                        name =
                            promotionJson.getString(
                                "name"
                            ),

                        cashAmount =
                            promotionJson.getLong(
                                "cashAmount"
                            ),

                        promotionalAmount =
                            promotionJson.getLong(
                                "promotionalAmount"
                            ),

                        creditedAmount =
                            promotionJson.getLong(
                                "creditedAmount"
                            )
                    )

                } else {

                    null
                }

            transactions.add(
                AdminRechargePointTransactionReport(
                    transactionId =
                        item.getString(
                            "transactionId"
                        ),

                    cardId =
                        item.getLong(
                            "cardId"
                        ),

                    deviceId =
                        optionalString(
                            item,
                            "deviceId"
                        ),

                    creditedAmount =
                        item.getLong(
                            "creditedAmount"
                        ),

                    cashReceived =
                        item.getLong(
                            "cashReceived"
                        ),

                    promotionalGiven =
                        item.getLong(
                            "promotionalGiven"
                        ),

                    adminCreditAmount =
                        item.getLong(
                            "adminCreditAmount"
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

                    promotion =
                        promotion,

                    createdAt =
                        item.getString(
                            "createdAt"
                        ),

                    confirmedAt =
                        optionalString(
                            item,
                            "confirmedAt"
                        )
                )
            )
        }

        return AdminRechargePointDetailReport(
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

            rechargePoint =
                AdminRechargePointDetailHeader(
                    rechargePointId =
                        rechargePointJson.getString(
                            "rechargePointId"
                        ),

                    name =
                        rechargePointJson.getString(
                            "name"
                        )
                ),

            summary =
                AdminRechargePointDetailSummary(
                    cashReceived =
                        summaryJson.getLong(
                            "cashReceived"
                        ),

                    promotionalGiven =
                        summaryJson.getLong(
                            "promotionalGiven"
                        ),

                    creditedAmount =
                        summaryJson.getLong(
                            "creditedAmount"
                        ),

                    operationsCount =
                        summaryJson.getLong(
                            "operationsCount"
                        )
                ),

            dailyBreakdown =
                dailyBreakdown,

            transactions =
                transactions
        )
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


    private fun optionalLong(
        json: JSONObject,
        key: String
    ): Long? {

        if (
            !json.has(key) ||
            json.isNull(key)
        ) {
            return null
        }

        return json.getLong(
            key
        )
    }
}
