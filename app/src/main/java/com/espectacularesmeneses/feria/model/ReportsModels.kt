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
 * - detalle/auditoría de un juego;
 * - reporte por taquilla;
 * - detalle/auditoría de una taquilla;
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
 * JUEGOS — LISTADO
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
     * Composición financiera del consumo.
     */
    val cashConsumed: Long = 0,
    val promotionalConsumed: Long = 0,
    val adminCreditConsumed: Long = 0,
    val legacyConsumed: Long = 0,

    /*
     * Total de personas cobradas.
     */
    val peopleCount: Long,

    /*
     * Número de operaciones confirmadas.
     */
    val operationsCount: Long = 0,

    /*
     * Desglose cronológico por día.
     */
    val dailyBreakdown: List<AdminGameDailyReport>
)


data class AdminGameDailyReport(
    val date: String,
    val consumptionAmount: Long,
    val cashConsumed: Long = 0,
    val promotionalConsumed: Long = 0,
    val adminCreditConsumed: Long = 0,
    val legacyConsumed: Long = 0,
    val peopleCount: Long,
    val operationsCount: Long = 0
)


/*
 * =========================================================
 * JUEGO — DETALLE / AUDITORÍA
 * =========================================================
 */
data class AdminGameDetailReport(
    val from: String,
    val to: String,
    val timezone: String,
    val game: AdminGameDetailHeader,
    val summary: AdminGameDetailSummary,
    val dailyBreakdown: List<AdminGameDailyReport>,
    val transactions: List<AdminGameTransactionReport>
)


data class AdminGameDetailHeader(
    val gameId: String,
    val name: String,
    val currentPrice: Long
)


data class AdminGameDetailSummary(
    val consumptionAmount: Long,
    val cashConsumed: Long,
    val promotionalConsumed: Long,
    val adminCreditConsumed: Long,
    val legacyConsumed: Long,
    val peopleCount: Long,
    val operationsCount: Long
)


data class AdminGameTransactionReport(
    val transactionId: String,
    val cardId: Long,
    val deviceId: String?,
    val amount: Long,
    val quantity: Long,
    val unitPrice: Long?,
    val balanceBefore: Long,
    val balanceAfter: Long,
    val counterBefore: Long,
    val counterAfter: Long,
    val status: String,
    val createdAt: String,
    val confirmedAt: String?,
    val fundBreakdown: AdminGameTransactionFundBreakdown
)


data class AdminGameTransactionFundBreakdown(
    val cash: Long,
    val promotional: Long,
    val adminCredit: Long,
    val legacy: Long
)


/*
 * =========================================================
 * TAQUILLAS — LISTADO
 * =========================================================
 */
data class AdminRechargePointReport(
    val rechargePointId: String,
    val name: String,

    /*
     * Dinero real recibido físicamente por la taquilla.
     */
    val cashReceived: Long = 0,

    /*
     * Saldo regalado mediante promociones.
     */
    val promotionalGiven: Long = 0,

    /*
     * Total acreditado a las tarjetas.
     *
     * creditedAmount =
     * cashReceived + promotionalGiven
     */
    val creditedAmount: Long = 0,

    /*
     * Se conserva por compatibilidad con la UI actual.
     *
     * Representa el total acreditado en la implementación
     * previa del reporte Android.
     */
    val rechargedAmount: Long = creditedAmount,

    /*
     * Número de operaciones confirmadas.
     */
    val operationsCount: Long = 0,

    val dailyBreakdown: List<AdminRechargePointDailyReport>
)


data class AdminRechargePointDailyReport(
    val date: String,

    val cashReceived: Long = 0,
    val promotionalGiven: Long = 0,
    val creditedAmount: Long = 0,

    /*
     * Compatibilidad con la UI actual.
     */
    val rechargedAmount: Long = creditedAmount,

    val operationsCount: Long = 0
)


/*
 * =========================================================
 * TAQUILLA — DETALLE / AUDITORÍA
 * =========================================================
 */
data class AdminRechargePointDetailReport(
    val from: String,
    val to: String,
    val timezone: String,
    val rechargePoint: AdminRechargePointDetailHeader,
    val summary: AdminRechargePointDetailSummary,
    val dailyBreakdown: List<AdminRechargePointDailyReport>,
    val transactions: List<AdminRechargePointTransactionReport>
)


data class AdminRechargePointDetailHeader(
    val rechargePointId: String,
    val name: String
)


data class AdminRechargePointDetailSummary(
    val cashReceived: Long,
    val promotionalGiven: Long,
    val creditedAmount: Long,
    val operationsCount: Long
)


data class AdminRechargePointTransactionReport(
    val transactionId: String,
    val cardId: Long,
    val deviceId: String?,
    val creditedAmount: Long,
    val cashReceived: Long,
    val promotionalGiven: Long,
    val adminCreditAmount: Long,
    val balanceBefore: Long,
    val balanceAfter: Long,
    val counterBefore: Long,
    val counterAfter: Long,
    val status: String,
    val promotion: AdminRechargePointTransactionPromotion?,
    val createdAt: String,
    val confirmedAt: String?
)


data class AdminRechargePointTransactionPromotion(
    val promotionId: String,
    val name: String,
    val cashAmount: Long,
    val promotionalAmount: Long,
    val creditedAmount: Long
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
