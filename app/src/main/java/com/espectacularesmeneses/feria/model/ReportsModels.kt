package com.espectacularesmeneses.feria.model

/*
 * =========================================================
 * REPORTS MODELS
 * =========================================================
 *
 * Modelos usados por el panel ADMIN para:
 *
 * - resumen general;
 * - reporte por juego;
 * - reporte por taquilla;
 * - reporte por dispositivo;
 * - historial detallado de sesiones de un dispositivo.
 *
 * Las fechas llegan desde el servidor como ISO-8601.
 * La presentación visual en horario de Ciudad de México
 * se hará posteriormente en la UI.
 * =========================================================
 */


/*
 * =========================================================
 * RESUMEN
 * =========================================================
 */
data class AdminReportSummary(
    val from: String,
    val to: String,
    val timezone: String,

    /*
     * Dinero cargado exclusivamente desde TAQUILLAS.
     *
     * NO incluye recargas realizadas directamente por ADMIN.
     */
    val rechargePointAmount: Long,

    /*
     * Saldo consumido en juegos.
     */
    val gameConsumptionAmount: Long,

    /*
     * Número total de personas cobradas en juegos.
     *
     * Se obtiene de SUM(quantity).
     */
    val gamePeopleCount: Long,

    /*
     * Movimientos administrativos.
     */
    val adminRechargeAmount: Long,
    val adminAdjustmentAmount: Long
)


/*
 * =========================================================
 * JUEGO
 * =========================================================
 */
data class AdminGameReport(
    val gameId: String,
    val name: String,

    /*
     * Precio que tiene el juego en este momento.
     *
     * El histórico NO se calcula con este precio.
     * El servidor usa transactions.amount.
     */
    val currentPrice: Long,

    /*
     * Saldo total consumido en el rango seleccionado.
     */
    val consumptionAmount: Long,

    /*
     * Total de personas cobradas.
     */
    val peopleCount: Long,

    /*
     * Desglose cronológico por día.
     * Se usa cuando el reporte abarca más de una fecha.
     */
    val dailyBreakdown: List<AdminGameDailyReport>
)


data class AdminGameDailyReport(
    val date: String,
    val consumptionAmount: Long,
    val peopleCount: Long
)


/*
 * =========================================================
 * TAQUILLA
 * =========================================================
 */
data class AdminRechargePointReport(
    val rechargePointId: String,
    val name: String,

    /*
     * Total recargado desde esta taquilla.
     */
    val rechargedAmount: Long,

    /*
     * Desglose por día del total recargado.
     */
    val dailyBreakdown: List<AdminRechargePointDailyReport>
)


data class AdminRechargePointDailyReport(
    val date: String,
    val rechargedAmount: Long
)


/*
 * =========================================================
 * DISPOSITIVO
 * =========================================================
 */
data class AdminDeviceReport(
    val deviceId: String,
    val code: String,
    val name: String,
    val status: String,

    /*
     * Tiempo total registrado dentro del rango.
     *
     * Se conserva en segundos para no perder precisión.
     * La UI lo mostrará como "16 h 43 min", etc.
     */
    val sessionSeconds: Long
)


/*
 * =========================================================
 * INFORMACIÓN GENERAL DEL HISTORIAL DE UN DISPOSITIVO
 * =========================================================
 */
data class AdminDeviceHistory(
    val from: String,
    val to: String,
    val timezone: String,
    val device: AdminDeviceReportHeader,
    val sessions: List<AdminDeviceSessionReport>
)


data class AdminDeviceReportHeader(
    val deviceId: String,
    val code: String,
    val name: String,
    val status: String
)


/*
 * =========================================================
 * SESIÓN INDIVIDUAL DEL DISPOSITIVO
 * =========================================================
 */
data class AdminDeviceSessionReport(
    val sessionId: String,

    /*
     * GAME
     * RECHARGE
     * ADMIN
     */
    val mode: String,

    /*
     * ACTIVE
     * CLOSED
     */
    val status: String,

    /*
     * Fechas reales completas de la sesión.
     */
    val startedAt: String,
    val endedAt: String?,

    /*
     * Fechas recortadas al rango solicitado.
     *
     * Ejemplo:
     * una sesión empezó el día anterior, pero el reporte
     * solicita solamente el día actual.
     */
    val visibleStartedAt: String,
    val visibleEndedAt: String,

    val durationSeconds: Long,

    /*
     * Solo tendrá valor cuando mode == GAME.
     */
    val game: AdminDeviceSessionGame?,

    /*
     * Solo tendrá valor cuando mode == RECHARGE.
     */
    val rechargePoint: AdminDeviceSessionRechargePoint?,

    /*
     * Solo tendrá valor cuando mode == ADMIN.
     */
    val admin: AdminDeviceSessionAdmin?,

    val metrics: AdminDeviceSessionMetrics
)


data class AdminDeviceSessionGame(
    val gameId: String,
    val name: String
)


data class AdminDeviceSessionRechargePoint(
    val rechargePointId: String,
    val name: String
)


data class AdminDeviceSessionAdmin(
    val cardId: Long?
)


data class AdminDeviceSessionMetrics(

    /*
     * GAME
     */
    val gameConsumptionAmount: Long,
    val gamePeopleCount: Long,

    /*
     * TAQUILLA
     */
    val rechargeAmount: Long,

    /*
     * ADMIN
     */
    val adminRechargeAmount: Long,
    val adminAdjustmentAmount: Long
)
