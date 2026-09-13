package com.espectacularesmeneses.feria

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.espectacularesmeneses.feria.model.AdminDeviceHistory
import com.espectacularesmeneses.feria.model.AdminDeviceReport
import com.espectacularesmeneses.feria.model.AdminGame
import com.espectacularesmeneses.feria.model.AdminPromotion
import com.espectacularesmeneses.feria.model.AdminGameReport
import com.espectacularesmeneses.feria.model.AdminGameDetailReport
import com.espectacularesmeneses.feria.model.AdminRechargePoint
import com.espectacularesmeneses.feria.model.AdminRechargePointReport
import com.espectacularesmeneses.feria.model.AdminRechargePointDetailReport
import com.espectacularesmeneses.feria.model.AdminReportSummary
import com.espectacularesmeneses.feria.model.AdminSession
import com.espectacularesmeneses.feria.model.CardReadResult
import com.espectacularesmeneses.feria.model.CardStatus
import com.espectacularesmeneses.feria.model.CardType
import com.espectacularesmeneses.feria.model.ChargeAuthorization
import com.espectacularesmeneses.feria.model.CustomerHistory
import com.espectacularesmeneses.feria.model.CustomerHistoryItem
import com.espectacularesmeneses.feria.model.GameSession
import com.espectacularesmeneses.feria.model.MenesesCard
import com.espectacularesmeneses.feria.model.NfcOperation
import com.espectacularesmeneses.feria.model.RechargeAuthorization
import com.espectacularesmeneses.feria.model.RechargePromotion
import com.espectacularesmeneses.feria.model.RechargeSession
import com.espectacularesmeneses.feria.model.TransactionReconciliation
import com.espectacularesmeneses.feria.network.DeviceHeartbeatApiClient
import com.espectacularesmeneses.feria.network.GameManagementApiClient
import com.espectacularesmeneses.feria.network.MenesesApiClient
import com.espectacularesmeneses.feria.network.PromotionApiClient
import com.espectacularesmeneses.feria.network.RechargeManagementApiClient
import com.espectacularesmeneses.feria.network.RechargeCheckoutApiClient
import com.espectacularesmeneses.feria.network.ReportsApiClient
import com.espectacularesmeneses.feria.nfc.MenesesCardCodec
import com.espectacularesmeneses.feria.nfc.Ntag215Reader
import com.espectacularesmeneses.feria.nfc.Ntag215Writer
import com.espectacularesmeneses.feria.ui.admin.AdminDeviceHistoryScreen
import com.espectacularesmeneses.feria.ui.admin.AdminReportsScreen
import com.espectacularesmeneses.feria.ui.admin.AdminPromotionsScreen
import com.espectacularesmeneses.feria.ui.theme.MenesesBlue
import com.espectacularesmeneses.feria.ui.theme.MenesesBlueDark
import com.espectacularesmeneses.feria.ui.theme.MenesesBlueSoft
import com.espectacularesmeneses.feria.ui.theme.MenesesBorder
import com.espectacularesmeneses.feria.ui.theme.MenesesDanger
import com.espectacularesmeneses.feria.ui.theme.MenesesError
import com.espectacularesmeneses.feria.ui.theme.MenesesFeriaTheme
import com.espectacularesmeneses.feria.ui.theme.MenesesGreen
import com.espectacularesmeneses.feria.ui.theme.MenesesGreenDark
import com.espectacularesmeneses.feria.ui.theme.MenesesGreenSoft
import com.espectacularesmeneses.feria.ui.theme.MenesesOrange
import com.espectacularesmeneses.feria.ui.theme.MenesesOrangeSoft
import com.espectacularesmeneses.feria.ui.theme.MenesesPurple
import com.espectacularesmeneses.feria.ui.theme.MenesesPurpleSoft
import com.espectacularesmeneses.feria.ui.theme.MenesesSurface
import com.espectacularesmeneses.feria.ui.theme.MenesesTextSecondary
import com.espectacularesmeneses.feria.util.ByteUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private class PreviousTransactionRecoveredException(
    val reconciliation: TransactionReconciliation
) : Exception(
    "PREVIOUS_TRANSACTION_RECOVERED"
)


private class CheckoutOperationRecoveredException(
    val recoveryAction: String,
    val userMessage: String
) : Exception(
    "CHECKOUT_OPERATION_RECOVERED"
)


class MainActivity :
    ComponentActivity(),
    NfcAdapter.ReaderCallback {

    private var nfcAdapter:
            NfcAdapter? =
        null

    /*
     * =====================================================
     * DEVICE HEARTBEAT
     * =====================================================
     */

    private val heartbeatRunning =
        AtomicBoolean(false)

    @Volatile
    private var heartbeatThread:
            Thread? =
        null

    /*
     * =====================================================
     * BOOTSTRAP UIDs
     * =====================================================
     */

    private val devCustomerUid =
        "04 D5 60 79 BF 61 80"

    private val devGameUid =
        "04 82 92 79 BF 61 81"

    private val devRechargeUid =
        "04 13 AF D3 FF 61 81"

    private val devAdminUid =
        "04 BA 84 D2 FF 61 80"

    /*
     * =====================================================
     * NFC
     * =====================================================
     */

    private var pendingOperation:
            NfcOperation =
        NfcOperation.Read

    private val nfcProcessing =
        AtomicBoolean(false)

    private var cardResult
            by mutableStateOf<CardReadResult>(
                CardReadResult.Waiting
            )

    /*
     * =====================================================
     * SESIONES
     * =====================================================
     */

    private var gameSession
            by mutableStateOf<GameSession?>(
                null
            )

    private var rechargeSession
            by mutableStateOf<RechargeSession?>(
                null
            )

    private var adminSession
            by mutableStateOf<AdminSession?>(
                null
            )

    /*
     * =====================================================
     * ADMIN GAMES
     * =====================================================
     */

    private var adminGames
            by mutableStateOf<List<AdminGame>>(
                emptyList()
            )

    private var adminGamesLoading
            by mutableStateOf(
                false
            )

    private var adminGameCreating
            by mutableStateOf(
                false
            )

    /*
     * =====================================================
     * ADMIN RECHARGE POINTS
     * =====================================================
     */

    private var adminRechargePoints
            by mutableStateOf<List<AdminRechargePoint>>(
                emptyList()
            )

    private var adminRechargePointsLoading
            by mutableStateOf(
                false
            )

    private var adminRechargePointCreating
            by mutableStateOf(
                false
            )

    /*
     * =====================================================
     * ADMIN CASH TODAY
     * =====================================================
     */

    private var adminCashToday
            by mutableStateOf<Long?>(
                null
            )

    private var adminCashLoading
            by mutableStateOf(
                false
            )

    /*
     * =====================================================
     * ADMIN CARD ACTIVATION FEE
     * =====================================================
     */

    private var adminCardActivationFee
            by mutableStateOf<Long?>(
                null
            )

    private var adminCardActivationFeeLoading
            by mutableStateOf(
                false
            )

    private var adminCardActivationFeeSaving
            by mutableStateOf(
                false
            )


    /*
     * =====================================================
     * ADMIN CARD ACTIVATION FEE
     * =====================================================
     */

    private fun loadAdminCardActivationFee() {

        if (
            adminSession == null
        ) {
            return
        }

        runOnUiThread {
            adminCardActivationFeeLoading =
                true
        }

        Thread {

            try {

                val fee =
                    MenesesApiClient
                        .getCustomerCardActivationFee()

                runOnUiThread {
                    adminCardActivationFee =
                        fee

                    adminCardActivationFeeLoading =
                        false
                }

            } catch (
                e: Exception
            ) {

                runOnUiThread {
                    adminCardActivationFeeLoading =
                        false
                }

                Log.e(
                    "MENESES_CARD_PRICE",
                    "Error cargando precio de tarjeta: ${e.message}"
                )
            }

        }.start()
    }


    private fun updateAdminCardActivationFee(
        amount: Long
    ) {

        if (
            adminSession == null
        ) {
            showError(
                "Se necesita una sesión ADMIN."
            )
            return
        }

        if (
            amount < 0
        ) {
            showError(
                "El precio no puede ser negativo."
            )
            return
        }

        runOnUiThread {
            adminCardActivationFeeSaving =
                true
        }

        Thread {

            try {

                val updatedFee =
                    MenesesApiClient
                        .updateCustomerCardActivationFee(
                            amount
                        )

                runOnUiThread {
                    adminCardActivationFee =
                        updatedFee

                    adminCardActivationFeeSaving =
                        false

                    cardResult =
                        CardReadResult.Success(
                            title =
                                "Precio actualizado",
                            message =
                                "Nuevo precio de activación: \$$updatedFee"
                        )
                }

            } catch (
                e: Exception
            ) {

                runOnUiThread {
                    adminCardActivationFeeSaving =
                        false
                }

                showError(
                    e.message
                        ?: "No fue posible actualizar el precio de la tarjeta."
                )
            }

        }.start()
    }


    /*
     * =====================================================
     * ADMIN PROMOTIONS
     * =====================================================
     */

    private var adminPromotions
            by mutableStateOf<List<AdminPromotion>>(
                emptyList()
            )

    private var adminPromotionsLoading
            by mutableStateOf(
                false
            )

    private var adminPromotionSaving
            by mutableStateOf(
                false
            )

    private var adminPromotionsError
            by mutableStateOf<String?>(
                null
            )


    /*
     * =====================================================
     * ADMIN REPORTS
     * =====================================================
     */

    private var adminReportSummary
            by mutableStateOf<AdminReportSummary?>(
                null
            )

    private var adminGameReports
            by mutableStateOf<List<AdminGameReport>>(
                emptyList()
            )

    private var adminRechargePointReports
            by mutableStateOf<List<AdminRechargePointReport>>(
                emptyList()
            )

    private var adminGameDetailReport
            by mutableStateOf<AdminGameDetailReport?>(
                null
            )

    private var adminRechargePointDetailReport
            by mutableStateOf<AdminRechargePointDetailReport?>(
                null
            )

    private var adminReportDetailLoading
            by mutableStateOf(
                false
            )

    private var adminDeviceReports
            by mutableStateOf<List<AdminDeviceReport>>(
                emptyList()
            )

    private var adminReportsLoading
            by mutableStateOf(
                false
            )

    private var adminReportsError
            by mutableStateOf<String?>(
                null
            )

    private var adminReportsFrom
            by mutableStateOf(
                ""
            )

    private var adminReportsTo
            by mutableStateOf(
                ""
            )

    private var adminDeviceHistory
            by mutableStateOf<AdminDeviceHistory?>(
                null
            )

    private var adminDeviceHistoryLoading
            by mutableStateOf(
                false
            )

    private var adminDeviceHistoryError
            by mutableStateOf<String?>(
                null
            )


    /*
     * =====================================================
     * CREATE
     * =====================================================
     */

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        nfcAdapter =
            NfcAdapter.getDefaultAdapter(
                this
            )

        loadCurrentMode()

        setContent {

            MenesesFeriaTheme {

                MenesesHomeScreen(

                    cardResult =
                        cardResult,

                    gameSession =
                        gameSession,

                    rechargeSession =
                        rechargeSession,

                    adminSession =
                        adminSession,

                    adminGames =
                        adminGames,

                    adminGamesLoading =
                        adminGamesLoading,

                    adminGameCreating =
                        adminGameCreating,

                    adminRechargePoints =
                        adminRechargePoints,

                    adminRechargePointsLoading =
                        adminRechargePointsLoading,

                    adminRechargePointCreating =
                        adminRechargePointCreating,

                    adminCashToday =
                        adminCashToday,

                    adminCashLoading =
                        adminCashLoading,

                    adminCardActivationFee =
                        adminCardActivationFee,

                    adminCardActivationFeeLoading =
                        adminCardActivationFeeLoading,

                    adminCardActivationFeeSaving =
                        adminCardActivationFeeSaving,

                    adminPromotions =
                        adminPromotions,

                    adminPromotionsLoading =
                        adminPromotionsLoading,

                    adminPromotionSaving =
                        adminPromotionSaving,

                    adminPromotionsError =
                        adminPromotionsError,

                    adminReportSummary =
                        adminReportSummary,

                    adminGameReports =
                        adminGameReports,

                    adminRechargePointReports =
                        adminRechargePointReports,

                    adminGameDetailReport =
                        adminGameDetailReport,

                    adminRechargePointDetailReport =
                        adminRechargePointDetailReport,

                    adminReportDetailLoading =
                        adminReportDetailLoading,

                    adminDeviceReports =
                        adminDeviceReports,

                    adminReportsLoading =
                        adminReportsLoading,

                    adminReportsError =
                        adminReportsError,

                    adminReportsFrom =
                        adminReportsFrom,

                    adminReportsTo =
                        adminReportsTo,

                    adminDeviceHistory =
                        adminDeviceHistory,

                    adminDeviceHistoryLoading =
                        adminDeviceHistoryLoading,

                    adminDeviceHistoryError =
                        adminDeviceHistoryError,

                    onLoadAdminPromotions = {
                        loadAdminPromotions()
                    },

                    onCreateAdminPromotion = {
                            name,
                            cashAmount,
                            promotionalAmount ->

                        createAdminPromotion(
                            name = name,
                            cashAmount = cashAmount,
                            promotionalAmount = promotionalAmount
                        )
                    },

                    onSetAdminPromotionActive = {
                            promotion,
                            active ->

                        setAdminPromotionActive(
                            promotion = promotion,
                            active = active
                        )
                    },

                    onLoadAdminReports = {
                            from,
                            to ->

                        loadAdminReports(
                            from = from,
                            to = to
                        )
                    },

                    onLoadAdminReportsToday = {
                        loadAdminReportsToday()
                    },

                    onLoadAdminReportsLast7Days = {
                        loadAdminReportsLast7Days()
                    },

                    onLoadAdminGameDetail = { gameId ->
                        loadAdminGameDetail(
                            gameId = gameId
                        )
                    },

                    onLoadAdminRechargePointDetail = { rechargePointId ->
                        loadAdminRechargePointDetail(
                            rechargePointId = rechargePointId
                        )
                    },

                    onClearAdminGameDetail = {
                        adminGameDetailReport = null
                    },

                    onClearAdminRechargePointDetail = {
                        adminRechargePointDetailReport = null
                    },

                    onLoadAdminDeviceHistory = {
                            deviceId ->

                        loadAdminDeviceHistory(
                            deviceId = deviceId
                        )
                    },

                    onCreateGame = {
                            name,
                            price ->

                        createAdminGame(
                            name = name,
                            price = price
                        )
                    },

                    onCreateRechargePoint = {
                            name ->

                        createAdminRechargePoint(
                            name = name
                        )
                    },

                    onUpdateGame = {
                            game,
                            name,
                            price ->

                        updateAdminGame(
                            game = game,
                            name = name,
                            price = price
                        )
                    },

                    onUpdateRechargePoint = {
                            rechargePoint,
                            name ->

                        updateAdminRechargePoint(
                            rechargePoint = rechargePoint,
                            name = name
                        )
                    },

                    onUpdateGameCardStatus = {
                            game,
                            status ->

                        updateAdminGameCardStatus(
                            game = game,
                            status = status
                        )
                    },

                    onUpdateRechargePointCardStatus = {
                            rechargePoint,
                            status ->

                        updateAdminRechargePointCardStatus(
                            rechargePoint = rechargePoint,
                            status = status
                        )
                    },

                    onInitializeCustomer = {
                        prepareCustomerInitialization()
                    },

                    onInitializeGame = {
                        prepareGameInitialization()
                    },

                    onInitializeRecharge = {
                        prepareRechargeInitialization()
                    },

                    onInitializeAdmin = {
                        prepareAdminInitialization()
                    },

                    onCreateCustomer = {
                        prepareCustomerCreation()
                    },

                    onRefreshCardActivationFee = {
                        loadAdminCardActivationFee()
                    },

                    onUpdateCardActivationFee = {
                            amount ->

                        updateAdminCardActivationFee(
                            amount
                        )
                    },

                    onPrepareGameCard = {
                            game ->

                        prepareGameCardCreation(
                            game
                        )
                    },

                    onRefreshGames = {
                        loadAdminGames()
                    },

                    onPrepareRechargePointCard = {
                            rechargePoint ->

                        prepareRechargePointCardCreation(
                            rechargePoint
                        )
                    },

                    onRefreshRechargePoints = {
                        loadAdminRechargePoints()
                    },

                    onPrepareRecharge = {
                            amount ->

                        prepareRecharge(
                            amount
                        )
                    },

                    onPrepareCheckoutRecharge = {
                            amount,
                            paymentMethod ->

                        prepareCheckoutRecharge(
                            amount = amount,
                            paymentMethod = paymentMethod
                        )
                    },

                    onPreparePromotionalRecharge = {
                            promotion,
                            paymentMethod ->

                        preparePromotionalRecharge(
                            promotion = promotion,
                            paymentMethod = paymentMethod
                        )
                    },

                    onPrepareAdminRecharge = {
                            amount ->

                        prepareAdminRecharge(
                            amount
                        )
                    },

                    onPrepareAdminAdjustment = {
                            amount ->

                        prepareAdminAdjustment(
                            amount
                        )
                    },

                    onRefreshAdminCash = {
                        loadAdminCashToday()
                    },

                    onPrepareCharge = {
                            people ->

                        prepareCharge(
                            people
                        )
                    },

                    onPrepareBalance = {
                        prepareBalanceConsultation()
                    },

                    onPrepareHistory = {
                        prepareHistoryConsultation()
                    },

                    onPrepareCardReturn = {
                        prepareCustomerCardReturn()
                    },

                    onCancelOperation = {
                        cancelPendingOperation()
                    },

                    onLogoutGame = {
                        logoutGame()
                    },

                    onLogoutRecharge = {
                        logoutRecharge()
                    },

                    onLogoutAdmin = {
                        logoutAdmin()
                    },

                    onReset = {
                        resetReader()
                    }
                )
            }
        }
    }


    /*
     * =====================================================
     * CARGAR MODO ACTUAL
     * =====================================================
     */

    private fun loadCurrentMode() {

        Thread {

            try {

                if (
                    !MenesesApiClient.healthCheck()
                ) {

                    return@Thread
                }

                val admin =
                    MenesesApiClient
                        .getCurrentAdminSession()

                if (
                    admin != null
                ) {

                    runOnUiThread {

                        adminSession =
                            admin

                        rechargeSession =
                            null

                        gameSession =
                            null

                        /*
                         * Cargamos los datos ADMIN después de que
                         * adminSession ya quedó asignada. Así evitamos
                         * que loadAdminGames()/loadAdminRechargePoints()
                         * regresen antes de tiempo por ver la sesión en null.
                         */
                        loadAdminGames()
                        loadAdminRechargePoints()
                        loadAdminCashToday()
                        loadAdminCardActivationFee()
                    }

                    return@Thread
                }

                val recharge =
                    MenesesApiClient
                        .getCurrentRechargeSession()

                if (
                    recharge != null
                ) {

                    runOnUiThread {

                        rechargeSession =
                            recharge

                        adminSession =
                            null

                        gameSession =
                            null

                    }

                    return@Thread
                }

                val game =
                    MenesesApiClient
                        .getCurrentGameSession()

                runOnUiThread {

                    gameSession =
                        game

                    adminSession =
                        null

                    rechargeSession =
                        null

                }

            } catch (
                e: Exception
            ) {

                Log.e(
                    "MENESES_SERVER",
                    "Error cargando modo: ${e.message}"
                )
            }

        }.start()
    }


    /*
     * =====================================================
     * CARGAR JUEGOS PARA ADMIN
     * =====================================================
     */

    private fun loadAdminGames() {

        if (
            adminSession == null
        ) {
            return
        }

        runOnUiThread {

            adminGamesLoading =
                true
        }

        Thread {

            try {

                val games =
                    GameManagementApiClient
                        .getGames()

                runOnUiThread {

                    adminGames =
                        games

                    adminGamesLoading =
                        false
                }

            } catch (
                e: Exception
            ) {

                runOnUiThread {

                    adminGamesLoading =
                        false
                }

                Log.e(
                    "MENESES_GAMES",
                    "Error cargando juegos: ${e.message}"
                )
            }

        }.start()
    }


    /*
     * =====================================================
     * CARGAR TAQUILLAS PARA ADMIN
     * =====================================================
     */

    private fun loadAdminRechargePoints() {

        if (
            adminSession == null
        ) {
            return
        }

        runOnUiThread {

            adminRechargePointsLoading =
                true
        }

        Thread {

            try {

                val rechargePoints =
                    RechargeManagementApiClient
                        .getRechargePoints()

                runOnUiThread {

                    adminRechargePoints =
                        rechargePoints

                    adminRechargePointsLoading =
                        false
                }

            } catch (
                e: Exception
            ) {

                runOnUiThread {

                    adminRechargePointsLoading =
                        false
                }

                Log.e(
                    "MENESES_RECHARGE_ADMIN",
                    "Error cargando taquillas: ${e.message}"
                )
            }

        }.start()
    }


    /*
     * =====================================================
     * ADMIN CASH TODAY
     * =====================================================
     */

    private fun loadAdminCashToday() {

        if (
            adminSession == null
        ) {
            return
        }

        runOnUiThread {
            adminCashLoading =
                true
        }

        Thread {

            try {

                val total =
                    MenesesApiClient
                        .getAdminCashToday()

                runOnUiThread {
                    adminCashToday =
                        total

                    adminCashLoading =
                        false
                }

            } catch (
                e: Exception
            ) {

                runOnUiThread {
                    adminCashLoading =
                        false
                }

                Log.e(
                    "MENESES_ADMIN_CASH",
                    "Error cargando caja de hoy: ${e.message}"
                )
            }

        }.start()
    }


    /*
     * =====================================================
     * ADMIN PROMOTIONS
     * =====================================================
     */
    private fun loadAdminPromotions() {

        if (adminSession == null) {
            return
        }

        runOnUiThread {
            adminPromotionsLoading = true
            adminPromotionsError = null
        }

        Thread {
            try {
                val promotions =
                    PromotionApiClient.getAdminPromotions()

                runOnUiThread {
                    adminPromotions = promotions
                    adminPromotionsLoading = false
                    adminPromotionsError = null
                }
            } catch (e: Exception) {
                runOnUiThread {
                    adminPromotionsLoading = false
                    adminPromotionsError =
                        e.message ?: "No fue posible cargar las promociones."
                }

                Log.e(
                    "MENESES_PROMOTIONS",
                    "Error cargando promociones ADMIN: ${e.message}"
                )
            }
        }.start()
    }


    private fun createAdminPromotion(
        name: String,
        cashAmount: Long,
        promotionalAmount: Long
    ) {
        if (adminSession == null) {
            return
        }

        runOnUiThread {
            adminPromotionSaving = true
            adminPromotionsError = null
        }

        Thread {
            try {
                PromotionApiClient.createAdminPromotion(
                    name = name,
                    cashAmount = cashAmount,
                    promotionalAmount = promotionalAmount
                )

                val promotions =
                    PromotionApiClient.getAdminPromotions()

                runOnUiThread {
                    adminPromotions = promotions
                    adminPromotionSaving = false
                    adminPromotionsError = null
                }
            } catch (e: Exception) {
                runOnUiThread {
                    adminPromotionSaving = false
                    adminPromotionsError =
                        e.message ?: "No fue posible crear la promoción."
                }

                Log.e(
                    "MENESES_PROMOTIONS",
                    "Error creando promoción ADMIN: ${e.message}"
                )
            }
        }.start()
    }


    private fun setAdminPromotionActive(
        promotion: AdminPromotion,
        active: Boolean
    ) {
        if (adminSession == null) {
            return
        }

        runOnUiThread {
            adminPromotionSaving = true
            adminPromotionsError = null
        }

        Thread {
            try {
                PromotionApiClient.setAdminPromotionActive(
                    promotionId = promotion.id,
                    active = active
                )

                val promotions =
                    PromotionApiClient.getAdminPromotions()

                runOnUiThread {
                    adminPromotions = promotions
                    adminPromotionSaving = false
                    adminPromotionsError = null
                }
            } catch (e: Exception) {
                runOnUiThread {
                    adminPromotionSaving = false
                    adminPromotionsError =
                        e.message ?: if (active) {
                            "No fue posible reactivar la promoción."
                        } else {
                            "No fue posible desactivar la promoción."
                        }
                }

                Log.e(
                    "MENESES_PROMOTIONS",
                    "Error actualizando promoción ADMIN: ${e.message}"
                )
            }
        }.start()
    }


    /*
     * =====================================================
     * ADMIN REPORTS
     * =====================================================
     *
     * Carga cualquier rango YYYY-MM-DD.
     *
     * El backend interpreta el rango usando
     * America/Mexico_City.
     * =====================================================
     */
    private fun loadAdminReports(
        from: String,
        to: String
    ) {

        if (
            adminSession == null
        ) {
            return
        }

        if (
            from.isBlank() ||
            to.isBlank()
        ) {
            return
        }

        runOnUiThread {

            adminReportsLoading =
                true

            adminReportsError =
                null

            adminReportsFrom =
                from

            adminReportsTo =
                to

            adminGameDetailReport =
                null

            adminRechargePointDetailReport =
                null
        }

        Thread {

            try {

                val summary =
                    ReportsApiClient
                        .getSummary(
                            from = from,
                            to = to
                        )

                val games =
                    ReportsApiClient
                        .getGames(
                            from = from,
                            to = to
                        )

                val rechargePoints =
                    ReportsApiClient
                        .getRechargePoints(
                            from = from,
                            to = to
                        )

                val devices =
                    ReportsApiClient
                        .getDevices(
                            from = from,
                            to = to
                        )

                runOnUiThread {

                    adminReportSummary =
                        summary

                    adminGameReports =
                        games

                    adminRechargePointReports =
                        rechargePoints

                    adminDeviceReports =
                        devices

                    adminReportsLoading =
                        false

                    adminReportsError =
                        null
                }

            } catch (
                e: Exception
            ) {

                runOnUiThread {

                    adminReportsLoading =
                        false

                    adminReportsError =
                        e.message
                            ?: "No fue posible cargar los reportes."
                }

                Log.e(
                    "MENESES_REPORTS",
                    "Error cargando reportes: ${e.message}"
                )
            }

        }.start()
    }


    /*
     * =====================================================
     * ADMIN REPORTS - HOY
     * =====================================================
     */
    private fun loadAdminReportsToday() {

        val today =
            mexicoDateString(
                offsetDays = 0
            )

        loadAdminReports(
            from = today,
            to = today
        )
    }


    /*
     * =====================================================
     * ADMIN REPORTS - ÚLTIMOS 7 DÍAS
     * =====================================================
     *
     * Incluye hoy.
     *
     * Ejemplo:
     * hoy 13 agosto
     * from = 07 agosto
     * to   = 13 agosto
     * =====================================================
     */
    private fun loadAdminReportsLast7Days() {

        val from =
            mexicoDateString(
                offsetDays = -6
            )

        val to =
            mexicoDateString(
                offsetDays = 0
            )

        loadAdminReports(
            from = from,
            to = to
        )
    }


    /*
     * =====================================================
     * FECHA CIUDAD DE MÉXICO
     * =====================================================
     */
    private fun mexicoDateString(
        offsetDays: Int
    ): String {

        val calendar =
            Calendar.getInstance(
                TimeZone.getTimeZone(
                    "America/Mexico_City"
                )
            ).apply {

                add(
                    Calendar.DAY_OF_YEAR,
                    offsetDays
                )
            }

        return SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).apply {

            timeZone =
                TimeZone.getTimeZone(
                    "America/Mexico_City"
                )

        }.format(
            calendar.time
        )
    }


    /*
     * =====================================================
     * ADMIN REPORTS - DEVICE HISTORY
     * =====================================================
     */
    private fun loadAdminGameDetail(
        gameId: String
    ) {

        if (
            adminSession == null ||
            gameId.isBlank() ||
            adminReportsFrom.isBlank() ||
            adminReportsTo.isBlank()
        ) {
            return
        }

        runOnUiThread {
            adminReportDetailLoading =
                true

            adminReportsError =
                null
        }

        Thread {
            try {
                val detail =
                    ReportsApiClient
                        .getGameDetail(
                            gameId = gameId,
                            from = adminReportsFrom,
                            to = adminReportsTo
                        )

                runOnUiThread {
                    adminGameDetailReport =
                        detail

                    adminRechargePointDetailReport =
                        null

                    adminReportDetailLoading =
                        false

                    adminReportsError =
                        null
                }
            } catch (e: Exception) {
                runOnUiThread {
                    adminReportDetailLoading =
                        false

                    adminReportsError =
                        e.message
                            ?: "No fue posible cargar la auditoría del juego."
                }
            }
        }.start()
    }


    private fun loadAdminRechargePointDetail(
        rechargePointId: String
    ) {

        if (
            adminSession == null ||
            rechargePointId.isBlank() ||
            adminReportsFrom.isBlank() ||
            adminReportsTo.isBlank()
        ) {
            return
        }

        runOnUiThread {
            adminReportDetailLoading =
                true

            adminReportsError =
                null
        }

        Thread {
            try {
                val detail =
                    ReportsApiClient
                        .getRechargePointDetail(
                            rechargePointId = rechargePointId,
                            from = adminReportsFrom,
                            to = adminReportsTo
                        )

                runOnUiThread {
                    adminRechargePointDetailReport =
                        detail

                    adminGameDetailReport =
                        null

                    adminReportDetailLoading =
                        false

                    adminReportsError =
                        null
                }
            } catch (e: Exception) {
                runOnUiThread {
                    adminReportDetailLoading =
                        false

                    adminReportsError =
                        e.message
                            ?: "No fue posible cargar la auditoría de la taquilla."
                }
            }
        }.start()
    }


    private fun loadAdminDeviceHistory(
        deviceId: String
    ) {

        if (
            adminSession == null
        ) {
            return
        }

        if (
            deviceId.isBlank()
        ) {
            return
        }

        val from =
            adminReportsFrom

        val to =
            adminReportsTo

        if (
            from.isBlank() ||
            to.isBlank()
        ) {
            return
        }

        runOnUiThread {

            adminDeviceHistoryLoading =
                true

            adminDeviceHistoryError =
                null

            adminDeviceHistory =
                null
        }

        Thread {

            try {

                val history =
                    ReportsApiClient
                        .getDeviceHistory(
                            deviceId =
                                deviceId,
                            from =
                                from,
                            to =
                                to
                        )

                runOnUiThread {

                    adminDeviceHistory =
                        history

                    adminDeviceHistoryLoading =
                        false

                    adminDeviceHistoryError =
                        null
                }

            } catch (
                e: Exception
            ) {

                runOnUiThread {

                    adminDeviceHistoryLoading =
                        false

                    adminDeviceHistoryError =
                        e.message
                            ?: "No fue posible cargar el historial del dispositivo."
                }

                Log.e(
                    "MENESES_REPORTS",
                    "Error cargando historial de dispositivo: ${e.message}"
                )
            }

        }.start()
    }


    /*
     * =====================================================
     * DEVICE HEARTBEAT LIFECYCLE
     * =====================================================
     */

    override fun onStart() {

        super.onStart()

        startDeviceHeartbeat()
    }


    override fun onStop() {

        stopDeviceHeartbeat()

        super.onStop()
    }


    private fun startDeviceHeartbeat() {

        if (
            !heartbeatRunning.compareAndSet(
                false,
                true
            )
        ) {
            return
        }

        val thread =
            Thread {
                try {
                    while (heartbeatRunning.get()) {
                        try {
                            val alive =
                                DeviceHeartbeatApiClient
                                    .sendHeartbeat()

                            Log.d(
                                "MENESES_HEARTBEAT",
                                "Heartbeat enviado. alive=$alive"
                            )
                        } catch (e: Exception) {
                            Log.w(
                                "MENESES_HEARTBEAT",
                                "No fue posible enviar heartbeat: ${e.message}"
                            )
                        }

                        try {
                            Thread.sleep(30_000L)
                        } catch (_error: InterruptedException) {
                            break
                        }
                    }
                } finally {
                    heartbeatRunning.set(false)
                }
            }.apply {
                name = "MenesesDeviceHeartbeat"
                isDaemon = true
            }

        heartbeatThread = thread
        thread.start()
    }


    private fun stopDeviceHeartbeat() {

        heartbeatRunning.set(false)
        heartbeatThread?.interrupt()
        heartbeatThread = null
    }


    /*
     * =====================================================
     * NFC READER MODE
     * =====================================================
     */

    override fun onResume() {

        super.onResume()

        nfcAdapter
            ?.enableReaderMode(

                this,

                this,

                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or
                        NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,

                null
            )
    }


    override fun onPause() {

        super.onPause()

        nfcAdapter
            ?.disableReaderMode(
                this
            )
    }


    /*
     * =====================================================
     * BOOTSTRAP
     * =====================================================
     */

    private fun prepareCustomerInitialization() {

        pendingOperation =
            NfcOperation.InitializeDevCustomerCard

        cardResult =
            CardReadResult
                .WaitingForDevCard(

                    title =
                        "Inicializar DEV-01",

                    message =
                        "Acerca únicamente DEV-01."
                )
    }


    private fun prepareGameInitialization() {

        pendingOperation =
            NfcOperation.InitializeDevGameCard

        cardResult =
            CardReadResult
                .WaitingForDevCard(

                    title =
                        "Inicializar DEV-GAME-01",

                    message =
                        "Acerca únicamente DEV-GAME-01."
                )
    }


    private fun prepareRechargeInitialization() {

        pendingOperation =
            NfcOperation.InitializeDevRechargeCard

        cardResult =
            CardReadResult
                .WaitingForDevCard(

                    title =
                        "Inicializar DEV-RECHARGE-01",

                    message =
                        "Acerca únicamente DEV-RECHARGE-01."
                )
    }


    private fun prepareAdminInitialization() {

        pendingOperation =
            NfcOperation.InitializeDevAdminCard

        cardResult =
            CardReadResult
                .WaitingForDevCard(

                    title =
                        "Inicializar DEV-ADMIN-01",

                    message =
                        "Acerca únicamente DEV-ADMIN-01."
                )
    }


    /*
     * =====================================================
     * CUSTOMER CREATION
     * =====================================================
     */

    private fun prepareCustomerCreation() {

        if (
            adminSession == null &&
            rechargeSession == null
        ) {

            showError(
                "Se necesita una sesión ADMIN o TAQUILLA."
            )

            return
        }

        pendingOperation =
            NfcOperation.CreateCustomerCard

        cardResult =
            CardReadResult
                .WaitingForDevCard(

                    title =
                        "Crear nueva CUSTOMER",

                    message =
                        "NFC · LECTOR ACTIVO\n\n" +
                                "Acerca una NTAG215 vacía o una CUSTOMER devuelta."
                )
    }



    /*
     * =====================================================
     * CREATE GAME
     * =====================================================
     */

    private fun createAdminGame(
        name: String,
        price: Long
    ) {

        if (
            adminSession == null
        ) {

            showError(
                "Se necesita una sesión ADMIN."
            )

            return
        }

        if (
            adminGameCreating
        ) {

            return
        }

        if (
            name.trim().length < 2
        ) {

            showError(
                "Escribe el nombre del juego."
            )

            return
        }

        if (
            price <= 0
        ) {

            showError(
                "El precio debe ser mayor a \$0."
            )

            return
        }

        runOnUiThread {

            adminGameCreating =
                true
        }

        Thread {

            try {

                val game =
                    GameManagementApiClient
                        .createGame(
                            name = name,
                            price = price
                        )

                val games =
                    GameManagementApiClient
                        .getGames()

                runOnUiThread {

                    adminGames =
                        games

                    adminGameCreating =
                        false

                    cardResult =
                        CardReadResult
                            .Success(

                                title =
                                    "Juego creado",

                                message =
                                    "${game.name}\n" +
                                            "\$${game.price} por persona\n\n" +
                                            "Ahora crea su tarjeta GAME."
                            )
                }

            } catch (
                e: Exception
            ) {

                runOnUiThread {

                    adminGameCreating =
                        false
                }

                showError(
                    e.message
                        ?: "No fue posible crear el juego."
                )
            }

        }.start()
    }


    /*
     * =====================================================
     * CREATE RECHARGE POINT
     * =====================================================
     */

    private fun createAdminRechargePoint(
        name: String
    ) {

        if (
            adminSession == null
        ) {

            showError(
                "Se necesita una sesión ADMIN."
            )

            return
        }

        if (
            adminRechargePointCreating
        ) {

            return
        }

        if (
            name.trim().length < 2
        ) {

            showError(
                "Escribe el nombre de la taquilla."
            )

            return
        }

        runOnUiThread {

            adminRechargePointCreating =
                true
        }

        Thread {

            try {

                val rechargePoint =
                    RechargeManagementApiClient
                        .createRechargePoint(
                            name = name
                        )

                val rechargePoints =
                    RechargeManagementApiClient
                        .getRechargePoints()

                runOnUiThread {

                    adminRechargePoints =
                        rechargePoints

                    adminRechargePointCreating =
                        false

                    cardResult =
                        CardReadResult
                            .Success(

                                title =
                                    "Taquilla creada",

                                message =
                                    "${rechargePoint.name}\n\n" +
                                            "Ahora crea su tarjeta TAQUILLA."
                            )
                }

            } catch (
                e: Exception
            ) {

                runOnUiThread {

                    adminRechargePointCreating =
                        false
                }

                showError(
                    e.message
                        ?: "No fue posible crear la taquilla."
                )
            }

        }.start()
    }


    /*
     * =====================================================
     * UPDATE GAME
     * =====================================================
     */
    private fun updateAdminGame(
        game: AdminGame,
        name: String,
        price: Long
    ) {

        if (
            adminSession == null
        ) {
            showError(
                "Se necesita una sesión ADMIN."
            )
            return
        }

        if (
            name.trim().length < 2
        ) {
            showError(
                "Escribe el nombre del juego."
            )
            return
        }

        if (
            price <= 0
        ) {
            showError(
                "El precio debe ser mayor a \$0."
            )
            return
        }

        runOnUiThread {
            adminGameCreating =
                true
        }

        Thread {

            try {

                GameManagementApiClient
                    .updateGame(
                        gameId = game.id,
                        name = name.trim(),
                        price = price
                    )

                val games =
                    GameManagementApiClient
                        .getGames()

                runOnUiThread {

                    adminGames =
                        games

                    adminGameCreating =
                        false

                    cardResult =
                        CardReadResult.Success(
                            title =
                                "Juego actualizado",
                            message =
                                "${name.trim()}\n\$${price} por persona"
                        )
                }

            } catch (
                e: Exception
            ) {

                runOnUiThread {
                    adminGameCreating =
                        false
                }

                showError(
                    e.message
                        ?: "No fue posible actualizar el juego."
                )
            }

        }.start()
    }


    /*
     * =====================================================
     * UPDATE RECHARGE POINT
     * =====================================================
     */
    private fun updateAdminRechargePoint(
        rechargePoint: AdminRechargePoint,
        name: String
    ) {

        if (
            adminSession == null
        ) {
            showError(
                "Se necesita una sesión ADMIN."
            )
            return
        }

        if (
            name.trim().length < 2
        ) {
            showError(
                "Escribe el nombre de la taquilla."
            )
            return
        }

        runOnUiThread {
            adminRechargePointCreating =
                true
        }

        Thread {

            try {

                RechargeManagementApiClient
                    .updateRechargePoint(
                        rechargePointId =
                            rechargePoint.id,
                        name =
                            name.trim()
                    )

                val rechargePoints =
                    RechargeManagementApiClient
                        .getRechargePoints()

                runOnUiThread {

                    adminRechargePoints =
                        rechargePoints

                    adminRechargePointCreating =
                        false

                    cardResult =
                        CardReadResult.Success(
                            title =
                                "Taquilla actualizada",
                            message =
                                name.trim()
                        )
                }

            } catch (
                e: Exception
            ) {

                runOnUiThread {
                    adminRechargePointCreating =
                        false
                }

                showError(
                    e.message
                        ?: "No fue posible actualizar la taquilla."
                )
            }

        }.start()
    }



    /*
     * =====================================================
     * ACTIVAR / DESACTIVAR TARJETA GAME
     * =====================================================
     */
    private fun updateAdminGameCardStatus(
        game: AdminGame,
        status: String
    ) {

        if (
            adminSession == null
        ) {
            showError(
                "Se necesita una sesión ADMIN."
            )
            return
        }

        val card =
            game.card

        if (
            card == null
        ) {
            showError(
                "Este juego no tiene una tarjeta GAME asignada."
            )
            return
        }

        runOnUiThread {
            adminGameCreating =
                true
        }

        Thread {

            try {

                if (
                    status == "UNLINK"
                ) {

                    GameManagementApiClient
                        .unlinkGameCard(
                            cardId =
                                card.cardId
                        )

                } else {

                    GameManagementApiClient
                        .updateGameCardStatus(
                            cardId =
                                card.cardId,

                            status =
                                status
                        )
                }

                val games =
                    GameManagementApiClient
                        .getGames()

                runOnUiThread {

                    adminGames =
                        games

                    adminGameCreating =
                        false

                    cardResult =
                        when (
                            status
                        ) {

                            "ACTIVE" ->
                                CardReadResult.Success(
                                    title =
                                        "Tarjeta GAME reactivada",

                                    message =
                                        game.name
                                )

                            "INACTIVE" ->
                                CardReadResult.Success(
                                    title =
                                        "Tarjeta GAME desactivada",

                                    message =
                                        game.name
                                )

                            "UNLINK" ->
                                CardReadResult.Success(
                                    title =
                                        "Tarjeta GAME desvinculada",

                                    message =
                                        "${game.name}\n\n" +
                                                "El juego quedó disponible para registrar una nueva tarjeta."
                                )

                            else ->
                                CardReadResult.Success(
                                    title =
                                        "Tarjeta GAME actualizada",

                                    message =
                                        game.name
                                )
                        }
                }

            } catch (
                e: Exception
            ) {

                runOnUiThread {
                    adminGameCreating =
                        false
                }

                showError(
                    e.message
                        ?: "No fue posible actualizar la tarjeta GAME."
                )
            }

        }.start()
    }


    /*
     * =====================================================
     * ACTIVAR / DESACTIVAR TARJETA TAQUILLA
     * =====================================================
     */
    private fun updateAdminRechargePointCardStatus(
        rechargePoint: AdminRechargePoint,
        status: String
    ) {

        if (
            adminSession == null
        ) {
            showError(
                "Se necesita una sesión ADMIN."
            )
            return
        }

        val card =
            rechargePoint.card

        if (
            card == null
        ) {
            showError(
                "Esta taquilla no tiene una tarjeta asignada."
            )
            return
        }

        runOnUiThread {
            adminRechargePointCreating =
                true
        }

        Thread {

            try {

                if (
                    status == "UNLINK"
                ) {

                    RechargeManagementApiClient
                        .unlinkRechargeCard(
                            cardId =
                                card.cardId
                        )

                } else {

                    RechargeManagementApiClient
                        .updateRechargeCardStatus(
                            cardId =
                                card.cardId,

                            status =
                                status
                        )
                }

                val rechargePoints =
                    RechargeManagementApiClient
                        .getRechargePoints()

                runOnUiThread {

                    adminRechargePoints =
                        rechargePoints

                    adminRechargePointCreating =
                        false

                    cardResult =
                        when (
                            status
                        ) {

                            "ACTIVE" ->
                                CardReadResult.Success(
                                    title =
                                        "Tarjeta TAQUILLA reactivada",

                                    message =
                                        rechargePoint.name
                                )

                            "INACTIVE" ->
                                CardReadResult.Success(
                                    title =
                                        "Tarjeta TAQUILLA desactivada",

                                    message =
                                        rechargePoint.name
                                )

                            "UNLINK" ->
                                CardReadResult.Success(
                                    title =
                                        "Tarjeta TAQUILLA desvinculada",

                                    message =
                                        "${rechargePoint.name}\n\n" +
                                                "La taquilla quedó disponible para registrar una nueva tarjeta."
                                )

                            else ->
                                CardReadResult.Success(
                                    title =
                                        "Tarjeta TAQUILLA actualizada",

                                    message =
                                        rechargePoint.name
                                )
                        }
                }

            } catch (
                e: Exception
            ) {

                runOnUiThread {
                    adminRechargePointCreating =
                        false
                }

                showError(
                    e.message
                        ?: "No fue posible actualizar la tarjeta TAQUILLA."
                )
            }

        }.start()
    }


    /*
     * =====================================================
     * GAME CARD CREATION
     * =====================================================
     */

    private fun prepareGameCardCreation(
        game: AdminGame
    ) {

        if (
            adminSession == null
        ) {

            showError(
                "Se necesita una sesión ADMIN."
            )

            return
        }

        if (
            game.status !=
            "PENDING_SETUP"
        ) {

            showError(
                "Este juego ya no está pendiente de configuración."
            )

            return
        }

        if (
            game.card != null
        ) {

            showError(
                "Este juego ya tiene una tarjeta GAME."
            )

            return
        }

        pendingOperation =
            NfcOperation.CreateGameCard(

                gameId =
                    game.id,

                gameName =
                    game.name,

                gamePrice =
                    game.price
            )

        cardResult =
            CardReadResult
                .WaitingForDevCard(

                    title =
                        "Crear tarjeta GAME",

                    message =
                        "${game.name}\n" +
                                "\$${game.price} por persona\n\n" +
                                "NFC · LECTOR ACTIVO\n\n" +
                                "Acerca una tarjeta NTAG215 vacía."
                )
    }


    /*
     * =====================================================
     * RECHARGE CARD CREATION
     * =====================================================
     */

    private fun prepareRechargePointCardCreation(
        rechargePoint: AdminRechargePoint
    ) {

        if (
            adminSession == null
        ) {

            showError(
                "Se necesita una sesión ADMIN."
            )

            return
        }

        if (
            rechargePoint.status !=
            "PENDING_SETUP"
        ) {

            showError(
                "Esta taquilla ya no está pendiente de configuración."
            )

            return
        }

        if (
            rechargePoint.card != null
        ) {

            showError(
                "Esta taquilla ya tiene una tarjeta TAQUILLA."
            )

            return
        }

        pendingOperation =
            NfcOperation.CreateRechargeCard(

                rechargePointId =
                    rechargePoint.id,

                rechargePointName =
                    rechargePoint.name
            )

        cardResult =
            CardReadResult
                .WaitingForDevCard(

                    title =
                        "Crear tarjeta TAQUILLA",

                    message =
                        "${rechargePoint.name}\n\n" +
                                "NFC · LECTOR ACTIVO\n\n" +
                                "Acerca una tarjeta NTAG215 vacía."
                )
    }


    /*
     * =====================================================
     * BALANCE / HISTORY
     * =====================================================
     */

    private fun prepareBalanceConsultation() {

        if (
            gameSession == null &&
            rechargeSession == null &&
            adminSession == null
        ) {

            showError(
                "No existe una sesión activa."
            )

            return
        }

        pendingOperation =
            NfcOperation.ConsultBalance

        cardResult =
            CardReadResult.WaitingForBalance
    }


    private fun prepareHistoryConsultation() {

        if (
            adminSession == null &&
            rechargeSession == null
        ) {

            showError(
                "El historial solamente está disponible para ADMIN o TAQUILLA."
            )

            return
        }

        pendingOperation =
            NfcOperation.ConsultHistory

        cardResult =
            CardReadResult.WaitingForHistory
    }


    /*
     * =====================================================
     * CUSTOMER CARD RETURN
     * =====================================================
     */

    private fun prepareCustomerCardReturn() {

        if (
            rechargeSession == null
        ) {

            showError(
                "La devolución de tarjetas solamente está disponible desde TAQUILLA."
            )

            return
        }


        pendingOperation =
            NfcOperation.ReturnCustomerCard


        cardResult =
            CardReadResult
                .WaitingForDevCard(

                    title =
                        "Devolver tarjeta",

                    message =
                        "NFC · LECTOR ACTIVO\n\n" +
                                "Acerca una tarjeta CUSTOMER.\n\n" +
                                "El sistema calculará cuánto debe devolverse " +
                                "al cliente y reseteará la tarjeta para poder reutilizarla."
                )
    }


    /*
     * =====================================================
     * RECHARGE / CHARGE
     * =====================================================
     */

    private fun prepareRecharge(
        amount: Long
    ) {

        val session =
            rechargeSession
                ?: run {

                    showError(
                        "No existe una sesión RECHARGE activa."
                    )

                    return
                }

        if (
            amount <= 0
        ) {

            showError(
                "Selecciona un monto válido."
            )

            return
        }

        pendingOperation =
            NfcOperation.Recharge(
                amount
            )

        cardResult =
            CardReadResult
                .WaitingForRecharge(

                    amount =
                        amount,

                    rechargePointName =
                        session.rechargePointName
                )
    }


    private fun prepareCheckoutRecharge(
        amount: Long,
        paymentMethod: RechargeCheckoutApiClient.PaymentMethod,
        promotion: RechargePromotion? = null
    ) {

        val session =
            rechargeSession
                ?: run {

                    showError(
                        "No existe una sesión RECHARGE activa."
                    )

                    return
                }

        if (
            amount <= 0
        ) {

            showError(
                "Selecciona un monto válido."
            )

            return
        }

        if (
            promotion != null &&
            (
                    promotion.id.isBlank() ||
                            promotion.cashAmount <= 0 ||
                            promotion.promotionalAmount < 0 ||
                            promotion.totalCreditAmount <= 0 ||
                            promotion.cashAmount != amount
                    )
        ) {

            showError(
                "La promoción seleccionada no es válida."
            )

            return
        }

        pendingOperation =
            NfcOperation.CheckoutRecharge(
                amount = amount,
                paymentMethod = paymentMethod.name,
                promotionId =
                    promotion?.id,
                promotionName =
                    promotion?.name
            )

        cardResult =
            if (
                promotion == null
            ) {
                CardReadResult
                    .WaitingForRecharge(
                        amount = amount,
                        rechargePointName =
                            session.rechargePointName
                    )
            } else {
                CardReadResult
                    .WaitingForDevCard(
                        title =
                            promotion.name,
                        message =
                            "Taquilla: ${session.rechargePointName}\n\n" +
                                    "Cliente paga: \$${promotion.cashAmount}\n" +
                                    "Bonificación: +\$${promotion.promotionalAmount}\n" +
                                    "Total acreditado: \$${promotion.totalCreditAmount}\n" +
                                    "Método de pago: ${
                                        if (
                                            paymentMethod ==
                                            RechargeCheckoutApiClient.PaymentMethod.CARD
                                        ) {
                                            "Tarjeta"
                                        } else {
                                            "Efectivo"
                                        }
                                    }\n\n" +
                                    "NFC · LECTOR ACTIVO\n\n" +
                                    "Acerca una tarjeta CLIENTE."
                    )
            }
    }


    private fun preparePromotionalRecharge(
        promotion: RechargePromotion,
        paymentMethod: RechargeCheckoutApiClient.PaymentMethod
    ) {

        /*
         * Las promociones de TAQUILLA ya no usan el flujo financiero
         * legacy. Se procesan mediante el mismo recharge_checkout que
         * las recargas normales, conservando:
         *
         * - payment_method = CASH | CARD
         * - paid_recharge_amount
         * - promotional_credit_amount
         * - credited_amount
         * - NEW / EXISTING / REUSED
         * - recuperación BEFORE / AFTER
         */
        prepareCheckoutRecharge(
            amount =
                promotion.cashAmount,
            paymentMethod =
                paymentMethod,
            promotion =
                promotion
        )
    }


    private fun prepareCharge(
        peopleCount: Int
    ) {

        val session =
            gameSession
                ?: run {

                    showError(
                        "No existe una sesión GAME activa."
                    )

                    return
                }

        if (
            peopleCount <= 0
        ) {

            showError(
                "Selecciona al menos una persona."
            )

            return
        }

        pendingOperation =
            NfcOperation.Charge(
                peopleCount
            )

        cardResult =
            CardReadResult
                .WaitingForCharge(

                    peopleCount =
                        peopleCount,

                    gameName =
                        session.gameName,

                    unitPrice =
                        session.price,

                    estimatedTotal =
                        session.price *
                                peopleCount
                )
    }


    private fun prepareAdminRecharge(
        amount: Long
    ) {

        if (
            adminSession == null
        ) {

            showError(
                "Se necesita una sesión ADMIN activa."
            )

            return
        }

        if (
            amount <= 0
        ) {

            showError(
                "Selecciona un monto válido."
            )

            return
        }

        pendingOperation =
            NfcOperation.AdminRecharge(
                amount
            )

        cardResult =
            CardReadResult
                .WaitingForDevCard(

                    title =
                        "Recarga ADMIN preparada",

                    message =
                        "Monto: \$${amount}\n\n" +
                                "NFC · LECTOR ACTIVO\n\n" +
                                "Acerca una tarjeta CLIENTE."
                )
    }


    private fun prepareAdminAdjustment(
        amount: Long
    ) {

        if (
            adminSession == null
        ) {

            showError(
                "Se necesita una sesión ADMIN activa."
            )

            return
        }

        if (
            amount <= 0
        ) {

            showError(
                "Selecciona un monto válido."
            )

            return
        }

        pendingOperation =
            NfcOperation.AdminAdjustment(
                amount
            )

        cardResult =
            CardReadResult
                .WaitingForDevCard(

                    title =
                        "Ajuste ADMIN preparado",

                    message =
                        "Se retirarán \$${amount} del saldo.\n\n" +
                                "NFC · LECTOR ACTIVO\n\n" +
                                "Acerca una tarjeta CLIENTE."
                )
    }


    private fun cancelPendingOperation() {

        pendingOperation =
            NfcOperation.Read

        cardResult =
            CardReadResult.Waiting
    }


    private fun resetReader() {

        cancelPendingOperation()
    }


    /*
     * =====================================================
     * TAG DISCOVERED
     * =====================================================
     */

    override fun onTagDiscovered(
        tag: Tag
    ) {

        if (
            !nfcProcessing.compareAndSet(
                false,
                true
            )
        ) {

            return
        }

        try {

            val uid =
                ByteUtils.bytesToHex(
                    tag.id
                )

            when (
                val operation =
                    pendingOperation
            ) {

                NfcOperation.Read -> {

                    readCard(
                        tag,
                        uid
                    )
                }

                NfcOperation.InitializeDevCustomerCard -> {

                    initializeCustomerCard(
                        tag,
                        uid
                    )
                }

                NfcOperation.InitializeDevGameCard -> {

                    initializeGameCard(
                        tag,
                        uid
                    )
                }

                NfcOperation.InitializeDevRechargeCard -> {

                    initializeRechargeCard(
                        tag,
                        uid
                    )
                }

                NfcOperation.InitializeDevAdminCard -> {

                    initializeAdminCard(
                        tag,
                        uid
                    )
                }

                NfcOperation.CreateCustomerCard -> {

                    createCustomerCard(
                        tag,
                        uid
                    )
                }

                is NfcOperation.CreateGameCard -> {

                    createGameCard(

                        tag =
                            tag,

                        uid =
                            uid,

                        operation =
                            operation
                    )
                }

                is NfcOperation.CreateRechargeCard -> {

                    createRechargePointCard(

                        tag =
                            tag,

                        uid =
                            uid,

                        operation =
                            operation
                    )
                }

                NfcOperation.ConsultBalance -> {

                    consultBalance(
                        tag,
                        uid
                    )
                }

                NfcOperation.ConsultHistory -> {

                    consultHistory(
                        tag,
                        uid
                    )
                }

                NfcOperation.ReturnCustomerCard -> {

                    returnCustomerCard(
                        tag,
                        uid
                    )
                }

                is NfcOperation.Recharge -> {

                    rechargeCard(

                        tag,
                        uid,
                        operation.amount
                    )
                }

                is NfcOperation.CheckoutRecharge -> {

                    checkoutRechargeCard(
                        tag = tag,
                        uid = uid,
                        operation = operation
                    )
                }

                is NfcOperation.PromotionalRecharge -> {

                    promotionalRechargeCard(

                        tag =
                            tag,

                        uid =
                            uid,

                        operation =
                            operation
                    )
                }

                is NfcOperation.Charge -> {

                    chargeCard(

                        tag,
                        uid,
                        operation.peopleCount
                    )
                }

                is NfcOperation.AdminRecharge -> {

                    adminRechargeCard(
                        tag,
                        uid,
                        operation.amount
                    )
                }

                is NfcOperation.AdminAdjustment -> {

                    adminAdjustmentCard(
                        tag,
                        uid,
                        operation.amount
                    )
                }
            }

        } finally {

            nfcProcessing.set(
                false
            )
        }
    }


    /*
     * =====================================================
     * READ CARD
     * =====================================================
     */

    private fun readCard(
        tag: Tag,
        uid: String
    ) {

        try {

            val data =
                Ntag215Reader
                    .readMenesesData(
                        tag
                    )

            if (
                !MenesesCardCodec
                    .isMenesesCard(
                        data
                    )
            ) {

                runOnUiThread {

                    cardResult =
                        CardReadResult
                            .Unregistered(

                                uid =
                                    uid,

                                rawData =
                                    ByteUtils.bytesToHex(
                                        data
                                    )
                            )
                }

                return
            }

            val card =
                MenesesCardCodec
                    .decode(
                        data
                    )

            when (
                card.type
            ) {

                CardType.GAME -> {

                    loginGameCard(
                        card,
                        uid
                    )
                }

                CardType.RECHARGE -> {

                    loginRechargeCard(
                        card,
                        uid
                    )
                }

                CardType.ADMIN -> {

                    loginAdminCard(
                        card,
                        uid
                    )
                }

                CardType.CUSTOMER -> {

                    /*
                     * En reposo una CUSTOMER NO debe mostrar saldo,
                     * contador ni información de cuenta.
                     * La lectura funcional de CUSTOMER solamente ocurre
                     * después de armar ConsultBalance, ConsultHistory,
                     * Recharge o Charge desde un botón de la interfaz.
                     */
                    runOnUiThread {

                        cardResult =
                            CardReadResult.Waiting
                    }
                }
            }

        } catch (
            e: Exception
        ) {

            showError(
                e.message
                    ?: "Error leyendo tarjeta."
            )
        }
    }


    /*
     * =====================================================
     * BALANCE
     * =====================================================
     */

    private fun consultBalance(
        tag: Tag,
        uid: String
    ) {

        try {

            val card =
                readCustomerCard(
                    tag
                )

            pendingOperation =
                NfcOperation.Read

            runOnUiThread {

                cardResult =
                    CardReadResult
                        .Success(

                            title =
                                "Saldo consultado",

                            message =
                                "\$${card.balance}"
                        )
            }

        } catch (
            e: Exception
        ) {

            pendingOperation =
                NfcOperation.Read

            showError(
                e.message
                    ?: "No fue posible consultar saldo."
            )
        }
    }


    /*
     * =====================================================
     * HISTORY
     * =====================================================
     */

    private fun consultHistory(
        tag: Tag,
        uid: String
    ) {

        try {

            val card =
                readCustomerCard(
                    tag
                )

            val history =
                MenesesApiClient
                    .getCustomerHistory(

                        cardId =
                            card.cardId,

                        uid =
                            uid
                    )
                    .copy(
                        /*
                         * El backend conserva el estado autoritativo de
                         * PostgreSQL/Ledger. La lectura NFC actual se agrega
                         * aquí para que el diagnóstico compare las tres
                         * fuentes sin modificar ninguna de ellas.
                         */
                        nfcBalance =
                            card.balance,
                        nfcTransactionCounter =
                            card.transactionCounter
                    )

            pendingOperation =
                NfcOperation.Read

            runOnUiThread {

                cardResult =
                    CardReadResult
                        .HistoryLoaded(
                            history
                        )
            }

        } catch (
            e: Exception
        ) {

            pendingOperation =
                NfcOperation.Read

            showError(
                e.message
                    ?: "No fue posible consultar el historial."
            )
        }
    }


    /*
     * =====================================================
     * LOGINS
     * =====================================================
     */

    private fun loginGameCard(
        card: MenesesCard,
        uid: String
    ) {

        try {

            val session =
                MenesesApiClient
                    .loginGameSession(

                        card.cardId,
                        uid
                    )

            pendingOperation =
                NfcOperation.Read

            runOnUiThread {

                gameSession =
                    session

                rechargeSession =
                    null

                adminSession =
                    null

                cardResult =
                    CardReadResult.Waiting
            }

        } catch (
            e: Exception
        ) {

            showError(
                e.message
                    ?: "Error iniciando GAME."
            )
        }
    }


    private fun loginRechargeCard(
        card: MenesesCard,
        uid: String
    ) {

        try {

            val session =
                MenesesApiClient
                    .loginRechargeSession(

                        card.cardId,
                        uid
                    )

            pendingOperation =
                NfcOperation.Read

            runOnUiThread {

                rechargeSession =
                    session

                gameSession =
                    null

                adminSession =
                    null

                cardResult =
                    CardReadResult.Waiting
            }

        } catch (
            e: Exception
        ) {

            showError(
                e.message
                    ?: "Error iniciando TAQUILLA."
            )
        }
    }


    private fun loginAdminCard(
        card: MenesesCard,
        uid: String
    ) {

        try {

            val session =
                MenesesApiClient
                    .loginAdminSession(

                        card.cardId,
                        uid
                    )

            pendingOperation =
                NfcOperation.Read

            runOnUiThread {

                adminSession =
                    session

                gameSession =
                    null

                rechargeSession =
                    null

                cardResult =
                    CardReadResult.Waiting

                /*
                 * Mantener la información visible de inmediato usando
                 * el último listado cargado y refrescarla automáticamente
                 * al iniciar ADMIN.
                 */
                loadAdminGames()
                loadAdminRechargePoints()
                loadAdminCashToday()
                loadAdminCardActivationFee()
            }

        } catch (
            e: Exception
        ) {

            showError(
                e.message
                    ?: "Error iniciando ADMIN."
            )
        }
    }


    /*
     * =====================================================
     * CREATE CUSTOMER
     * =====================================================
     */

    private fun createCustomerCard(
        tag: Tag,
        uid: String
    ) {

        var registrationId:
                String? =
            null

        var cardMayHaveBeenWritten =
            false

        try {

            if (
                adminSession == null &&
                rechargeSession == null
            ) {

                throw IllegalStateException(
                    "Se necesita ADMIN o TAQUILLA."
                )
            }

            val existingData =
                Ntag215Reader
                    .readMenesesData(
                        tag
                    )


            /*
             * =================================================
             * TARJETA NUEVA O CUSTOMER DEVUELTA
             * =================================================
             *
             * Permitimos:
             *
             * 1. NTAG215 vacía.
             * 2. MEN1 CUSTOMER devuelta:
             *    - INACTIVE
             *    - balance = 0
             *    - transactionCounter = 0
             *
             * El backend sigue siendo la autoridad final para
             * decidir si esa tarjeta puede reutilizarse.
             * =================================================
             */

            val existingMenesesCard =
                if (
                    MenesesCardCodec
                        .isMenesesCard(
                            existingData
                        )
                ) {

                    MenesesCardCodec
                        .decode(
                            existingData
                        )

                } else {

                    null
                }


            if (
                existingMenesesCard !=
                null
            ) {

                if (
                    existingMenesesCard.type !=
                    CardType.CUSTOMER
                ) {

                    throw IllegalStateException(
                        "La tarjeta ya contiene una Meneses Card que no es CUSTOMER."
                    )
                }


                if (
                    existingMenesesCard.status !=
                    CardStatus.INACTIVE
                ) {

                    throw IllegalStateException(
                        "La tarjeta CUSTOMER ya está activa y no puede registrarse nuevamente."
                    )
                }


                if (
                    existingMenesesCard.balance !=
                    0L ||
                    existingMenesesCard.transactionCounter !=
                    0L
                ) {

                    throw IllegalStateException(
                        "La tarjeta CUSTOMER inactiva no está completamente reseteada."
                    )
                }
            }


            val authorization =
                MenesesApiClient
                    .authorizeCustomerRegistration(
                        uid
                    )

            registrationId =
                authorization.registrationId

            val newCard =
                MenesesCard(

                    version =
                        1,

                    type =
                        CardType.CUSTOMER,

                    status =
                        CardStatus.ACTIVE,

                    cardId =
                        authorization.cardId,

                    balance =
                        authorization.balance,

                    transactionCounter =
                        authorization.transactionCounter
                )

            Ntag215Writer
                .writeMenesesData(

                    tag,

                    MenesesCardCodec
                        .encode(
                            newCard
                        )
                )

            cardMayHaveBeenWritten =
                true

            verifyCard(
                tag,
                newCard
            )

            MenesesApiClient
                .confirmCardRegistration(

                    registrationId =
                        authorization.registrationId,

                    targetUid =
                        uid,

                    writtenCardId =
                        newCard.cardId,

                    writtenCardType =
                        "CUSTOMER"
                )

            pendingOperation =
                NfcOperation.Read

            runOnUiThread {

                cardResult =
                    CardReadResult
                        .Success(

                            title =
                                "Cliente nuevo creado",

                            message =
                                "\$0"
                        )
            }

        } catch (
            e: Exception
        ) {

            pendingOperation =
                NfcOperation.Read

            if (
                registrationId != null &&
                cardMayHaveBeenWritten
            ) {

                showError(
                    "La tarjeta pudo haber sido escrita, " +
                            "pero no quedó confirmada.\n\n" +
                            "NO vuelvas a crearla.\n\n" +
                            "Registration ID:\n$registrationId"
                )

                return
            }

            if (
                registrationId != null
            ) {

                try {

                    MenesesApiClient
                        .failCardRegistration(

                            registrationId,

                            e.message
                                ?: "Fallo antes de escritura."
                        )

                } catch (
                    _: Exception
                ) {
                }
            }

            showError(
                e.message
                    ?: "Error creando CUSTOMER."
            )
        }
    }


    /*
     * =====================================================
     * CREATE GAME CARD
     * =====================================================
     */

    private fun createGameCard(

        tag: Tag,

        uid: String,

        operation:
        NfcOperation.CreateGameCard
    ) {

        var registrationId:
                String? =
            null

        var cardMayHaveBeenWritten =
            false

        try {

            if (
                adminSession == null
            ) {

                throw IllegalStateException(
                    "La sesión ADMIN ya no está activa."
                )
            }

            /*
             * Primero comprobamos que la tarjeta
             * todavía no sea una Meneses Card.
             */

            val existingData =
                Ntag215Reader
                    .readMenesesData(
                        tag
                    )

            if (
                MenesesCardCodec
                    .isMenesesCard(
                        existingData
                    )
            ) {

                throw IllegalStateException(
                    "La tarjeta ya está registrada en Meneses."
                )
            }

            /*
             * El servidor reserva Card ID
             * y relaciona la autorización con
             * el GAME específico.
             */

            val authorization =
                GameManagementApiClient
                    .authorizeGameCard(

                        gameId =
                            operation.gameId,

                        targetUid =
                            uid
                    )

            registrationId =
                authorization.registrationId

            /*
             * Verificación adicional:
             * el servidor debe haber autorizado
             * exactamente el juego solicitado.
             */

            if (
                authorization.gameId !=
                operation.gameId
            ) {

                throw IllegalStateException(
                    "La autorización corresponde a otro juego."
                )
            }

            /*
             * Construimos la tarjeta física GAME.
             */

            val newCard =
                MenesesCard(

                    version =
                        1,

                    type =
                        CardType.GAME,

                    status =
                        CardStatus.ACTIVE,

                    cardId =
                        authorization.cardId,

                    balance =
                        authorization.balance,

                    transactionCounter =
                        authorization.transactionCounter
                )

            /*
             * Escritura física.
             */

            Ntag215Writer
                .writeMenesesData(

                    tag,

                    MenesesCardCodec
                        .encode(
                            newCard
                        )
                )

            cardMayHaveBeenWritten =
                true

            /*
             * Verificación física.
             */

            verifyCard(
                tag,
                newCard
            )

            /*
             * Confirmación servidor.
             *
             * Aquí:
             *
             * - se crea cards
             * - se crea game_cards
             * - game pasa a ACTIVE
             */

            GameManagementApiClient
                .confirmGameCard(

                    registrationId =
                        authorization.registrationId,

                    targetUid =
                        uid,

                    writtenCardId =
                        newCard.cardId
                )

            pendingOperation =
                NfcOperation.Read

            /*
             * Recargar listado ADMIN.
             */

            loadAdminGames()

            runOnUiThread {

                cardResult =
                    CardReadResult
                        .Success(

                            title =
                                "Tarjeta GAME creada",

                            message =
                                "${authorization.gameName}\n" +
                                        "\$${authorization.gamePrice} por persona\n\n" +
                                        "Card #${newCard.cardId}\n" +
                                        "UID: $uid\n\n" +
                                        "Juego activado correctamente."
                        )
            }

        } catch (
            e: Exception
        ) {

            pendingOperation =
                NfcOperation.Read

            /*
             * Si NFC pudo haberse escrito,
             * NO marcamos FAILED automáticamente.
             *
             * Necesitaría reconciliación/manual review.
             */

            if (
                registrationId != null &&
                cardMayHaveBeenWritten
            ) {

                showError(
                    "La tarjeta GAME pudo haber sido escrita, " +
                            "pero el servidor no terminó de confirmar.\n\n" +
                            "NO repitas la creación.\n\n" +
                            "Registration ID:\n$registrationId"
                )

                return
            }

            /*
             * Si todavía no escribimos NFC,
             * sí podemos cerrar como FAILED.
             */

            if (
                registrationId != null
            ) {

                try {

                    GameManagementApiClient
                        .failGameCard(

                            registrationId =
                                registrationId,

                            reason =
                                e.message
                                    ?: "Fallo antes de escritura GAME."
                        )

                } catch (
                    _: Exception
                ) {
                }
            }

            showError(
                e.message
                    ?: "Error creando tarjeta GAME."
            )
        }
    }


    /*
     * =====================================================
     * CREATE RECHARGE CARD
     * =====================================================
     */

    private fun createRechargePointCard(

        tag: Tag,

        uid: String,

        operation:
        NfcOperation.CreateRechargeCard
    ) {

        var registrationId:
                String? =
            null

        var cardMayHaveBeenWritten =
            false

        try {

            if (
                adminSession == null
            ) {

                throw IllegalStateException(
                    "La sesión ADMIN ya no está activa."
                )
            }

            val existingData =
                Ntag215Reader
                    .readMenesesData(
                        tag
                    )

            if (
                MenesesCardCodec
                    .isMenesesCard(
                        existingData
                    )
            ) {

                throw IllegalStateException(
                    "La tarjeta ya está registrada en Meneses."
                )
            }

            val authorization =
                RechargeManagementApiClient
                    .authorizeRechargeCard(

                        rechargePointId =
                            operation.rechargePointId,

                        targetUid =
                            uid
                    )

            registrationId =
                authorization.registrationId

            if (
                authorization.rechargePointId !=
                operation.rechargePointId
            ) {

                throw IllegalStateException(
                    "La autorización corresponde a otra taquilla."
                )
            }

            val newCard =
                MenesesCard(

                    version =
                        1,

                    type =
                        CardType.RECHARGE,

                    status =
                        CardStatus.ACTIVE,

                    cardId =
                        authorization.cardId,

                    balance =
                        authorization.balance,

                    transactionCounter =
                        authorization.transactionCounter
                )

            Ntag215Writer
                .writeMenesesData(

                    tag,

                    MenesesCardCodec
                        .encode(
                            newCard
                        )
                )

            cardMayHaveBeenWritten =
                true

            verifyCard(
                tag,
                newCard
            )

            RechargeManagementApiClient
                .confirmRechargeCard(

                    registrationId =
                        authorization.registrationId,

                    targetUid =
                        uid,

                    writtenCardId =
                        newCard.cardId
                )

            pendingOperation =
                NfcOperation.Read

            loadAdminRechargePoints()

            runOnUiThread {

                cardResult =
                    CardReadResult
                        .Success(

                            title =
                                "Tarjeta TAQUILLA creada",

                            message =
                                "${authorization.rechargePointName}\n\n" +
                                        "Card #${newCard.cardId}\n" +
                                        "UID: $uid\n\n" +
                                        "Taquilla activada correctamente."
                        )
            }

        } catch (
            e: Exception
        ) {

            pendingOperation =
                NfcOperation.Read

            if (
                registrationId != null &&
                cardMayHaveBeenWritten
            ) {

                showError(
                    "La tarjeta TAQUILLA pudo haber sido escrita, " +
                            "pero el servidor no terminó de confirmar.\n\n" +
                            "NO repitas la creación.\n\n" +
                            "Registration ID:\n$registrationId"
                )

                return
            }

            if (
                registrationId != null
            ) {

                try {

                    RechargeManagementApiClient
                        .failRechargeCard(

                            registrationId =
                                registrationId,

                            reason =
                                e.message
                                    ?: "Fallo antes de escritura TAQUILLA."
                        )

                } catch (
                    _: Exception
                ) {
                }
            }

            showError(
                e.message
                    ?: "Error creando tarjeta TAQUILLA."
            )
        }
    }


    /*
     * =====================================================
     * BOOTSTRAP
     * =====================================================
     */

    private fun initializeCustomerCard(
        tag: Tag,
        uid: String
    ) {

        initializeProtectedCard(

            tag,
            uid,
            devCustomerUid,

            MenesesCard(
                version = 1,
                type = CardType.CUSTOMER,
                status = CardStatus.ACTIVE,
                cardId = 1,
                balance = 0,
                transactionCounter = 0
            ),

            "DEV-01 inicializada"
        )
    }


    private fun initializeGameCard(
        tag: Tag,
        uid: String
    ) {

        initializeProtectedCard(

            tag,
            uid,
            devGameUid,

            MenesesCard(
                version = 1,
                type = CardType.GAME,
                status = CardStatus.ACTIVE,
                cardId = 2,
                balance = 0,
                transactionCounter = 0
            ),

            "DEV-GAME-01 inicializada"
        )
    }


    private fun initializeRechargeCard(
        tag: Tag,
        uid: String
    ) {

        initializeProtectedCard(

            tag,
            uid,
            devRechargeUid,

            MenesesCard(
                version = 1,
                type = CardType.RECHARGE,
                status = CardStatus.ACTIVE,
                cardId = 3,
                balance = 0,
                transactionCounter = 0
            ),

            "DEV-RECHARGE-01 inicializada"
        )
    }


    private fun initializeAdminCard(
        tag: Tag,
        uid: String
    ) {

        initializeProtectedCard(

            tag,
            uid,
            devAdminUid,

            MenesesCard(
                version = 1,
                type = CardType.ADMIN,
                status = CardStatus.ACTIVE,
                cardId = 4,
                balance = 0,
                transactionCounter = 0
            ),

            "DEV-ADMIN-01 inicializada"
        )
    }


    private fun initializeProtectedCard(
        tag: Tag,
        uid: String,
        expectedUid: String,
        card: MenesesCard,
        successTitle: String
    ) {

        try {

            if (
                !uid.equals(
                    expectedUid,
                    ignoreCase = true
                )
            ) {

                throw IllegalArgumentException(
                    "UID incorrecto."
                )
            }

            val data =
                Ntag215Reader
                    .readMenesesData(
                        tag
                    )

            if (
                MenesesCardCodec
                    .isMenesesCard(
                        data
                    )
            ) {

                throw IllegalStateException(
                    "La tarjeta ya contiene una Meneses Card."
                )
            }

            Ntag215Writer
                .writeMenesesData(

                    tag,

                    MenesesCardCodec
                        .encode(
                            card
                        )
                )

            verifyCard(
                tag,
                card
            )

            pendingOperation =
                NfcOperation.Read

            runOnUiThread {

                cardResult =
                    CardReadResult
                        .Success(

                            successTitle,

                            "Card ID: ${card.cardId}\n" +
                                    "Tipo: ${card.type}\n" +
                                    "Estado: ${card.status}"
                        )
            }

        } catch (
            e: Exception
        ) {

            pendingOperation =
                NfcOperation.Read

            showError(
                e.message
                    ?: "Error inicializando tarjeta."
            )
        }
    }


    /*
     * =====================================================
     * CUSTOMER CARD RETURN / RESET
     * =====================================================
     *
     * Seguridad:
     *
     * 1. Lee CUSTOMER activa.
     * 2. Backend autoriza y calcula refund/discarded.
     * 3. Android escribe INACTIVE / 0 / 0.
     * 4. Android relee y verifica NFC.
     * 5. Backend confirma la devolución.
     *
     * Si la escritura NFC pudo haber comenzado, NO enviamos
     * /fail automáticamente. Dejamos la operación pendiente
     * para evitar decirle al servidor que nada ocurrió cuando
     * la tarjeta física pudo haber cambiado.
     * =====================================================
     */

    private fun returnCustomerCard(
        tag: Tag,
        uid: String
    ) {

        var operationId:
                String? =
            null

        var cardWriteStarted =
            false


        try {

            if (
                rechargeSession == null
            ) {

                throw IllegalStateException(
                    "Se necesita una sesión TAQUILLA activa."
                )
            }


            val currentCard =
                readCustomerCard(
                    tag
                )


            val authorization =
                MenesesApiClient
                    .authorizeCustomerCardReturn(

                        cardId =
                            currentCard.cardId,

                        uid =
                            uid
                    )


            operationId =
                authorization.operationId


            validateAuthorizationBefore(

                card =
                    currentCard,

                authorizedCardId =
                    authorization.cardId,

                authorizedBalance =
                    authorization.balanceBefore,

                authorizedCounter =
                    authorization.transactionCounterBefore
            )


            if (
                authorization.targetStatus !=
                "INACTIVE" ||
                authorization.targetBalance !=
                0L ||
                authorization.targetTransactionCounter !=
                0L
            ) {

                throw IllegalStateException(
                    "El servidor devolvió un estado final de tarjeta inesperado."
                )
            }


            val returnedCard =
                currentCard.copy(

                    status =
                        CardStatus.INACTIVE,

                    balance =
                        authorization.targetBalance,

                    transactionCounter =
                        authorization.targetTransactionCounter
                )


            /*
             * Desde este punto una excepción puede significar
             * que la NFC quedó parcial o totalmente escrita.
             */
            cardWriteStarted =
                true


            Ntag215Writer
                .writeMenesesData(

                    tag,

                    MenesesCardCodec
                        .encode(
                            returnedCard
                        )
                )


            verifyCard(
                tag,
                returnedCard
            )


            confirmCustomerCardReturnWithRetry(

                operationId =
                    authorization.operationId,

                cardId =
                    returnedCard.cardId,

                uid =
                    uid,

                writtenCard =
                    returnedCard
            )


            pendingOperation =
                NfcOperation.Read


            runOnUiThread {

                cardResult =
                    CardReadResult
                        .Success(

                            title =
                                "Devolución completada",

                            message =
                                if (
                                    authorization.refundPolicyReason ==
                                    "ADMIN_CREATED"
                                ) {

                                    "NO DEVOLVER DINERO\n" +
                                            "\$${authorization.refundAmount}\n\n" +
                                            "Tarjeta creada por ADMIN\n\n" +
                                            "Saldo eliminado: \$${authorization.discardedTotal}\n" +
                                            "CASH: \$${authorization.discardedCash}\n" +
                                            "PROMOTIONAL: \$${authorization.discardedPromotional}\n" +
                                            "ADMIN CREDIT: \$${authorization.discardedAdminCredit}"

                                } else {

                                    "ENTREGAR AL CLIENTE\n" +
                                            "\$${authorization.refundAmount}\n\n" +
                                            "Saldo eliminado: \$${authorization.discardedTotal}\n" +
                                            "CASH: \$${authorization.discardedCash}\n" +
                                            "PROMOTIONAL: \$${authorization.discardedPromotional}\n" +
                                            "ADMIN CREDIT: \$${authorization.discardedAdminCredit}"
                                }
                        )


            }


        } catch (
            e: Exception
        ) {

            pendingOperation =
                NfcOperation.Read


            /*
             * Si todavía NO comenzó la escritura NFC,
             * podemos cancelar limpiamente la autorización.
             */
            if (
                operationId != null &&
                !cardWriteStarted
            ) {

                try {

                    MenesesApiClient
                        .failCustomerCardReturn(

                            operationId =
                                operationId,

                            reason =
                                e.message
                                    ?: "Fallo antes de escritura NFC."
                        )

                } catch (
                    _: Exception
                ) {
                }
            }


            /*
             * Una vez iniciada la escritura, no afirmamos que
             * la operación falló de forma limpia. Puede requerir
             * revisión/reconciliación.
             */
            if (
                operationId != null &&
                cardWriteStarted
            ) {

                showError(
                    "La tarjeta pudo haber sido modificada, " +
                            "pero la devolución no quedó confirmada.\n\n" +
                            "NO vuelvas a intentar la devolución.\n\n" +
                            "Acude con un administrador.\n\n" +
                            "Operation ID:\n$operationId"
                )

                return
            }


            showError(
                e.message
                    ?: "No fue posible devolver la tarjeta."
            )
        }
    }


    private fun confirmCustomerCardReturnWithRetry(
        operationId: String,
        cardId: Long,
        uid: String,
        writtenCard: MenesesCard
    ) {

        var lastError:
                Exception? =
            null


        repeat(
            3
        ) { attempt ->

            try {

                MenesesApiClient
                    .confirmCustomerCardReturn(

                        operationId =
                            operationId,

                        cardId =
                            cardId,

                        uid =
                            uid,

                        writtenCardId =
                            writtenCard.cardId,

                        writtenCardType =
                            "CUSTOMER",

                        writtenStatus =
                            "INACTIVE",

                        writtenBalance =
                            writtenCard.balance,

                        writtenTransactionCounter =
                            writtenCard.transactionCounter
                    )


                return


            } catch (
                e: Exception
            ) {

                lastError =
                    e


                if (
                    attempt <
                    2
                ) {

                    try {

                        Thread.sleep(
                            250L
                        )

                    } catch (
                        _: InterruptedException
                    ) {

                        Thread
                            .currentThread()
                            .interrupt()
                    }
                }
            }
        }


        throw lastError
            ?: IllegalStateException(
                "No fue posible confirmar la devolución."
            )
    }


    /*
     * =====================================================
     * RECHARGE CHECKOUT (NEW / EXISTING / RESUME)
     * =====================================================
     *
     * Flujo seguro:
     * 1. RESUME por UID antes de crear otra operación.
     * 2. Si no hay checkout abierto, PREPARE clasifica NEW/EXISTING.
     * 3. AUTHORIZE devuelve el estado NFC final esperado.
     * 4. Android sólo escribe si la NFC está en un estado permitido.
     * 5. Relee y verifica físicamente.
     * 6. CONFIRM materializa el movimiento financiero.
     *
     * Si la NFC no coincide con BEFORE ni AFTER, NO se escribe.
     * =====================================================
     */

    private data class CheckoutExecution(
        val checkout: RechargeCheckoutApiClient.Checkout,
        val beforeCardState: RechargeCheckoutApiClient.CardState?,
        val finalCardState: RechargeCheckoutApiClient.CardState
    )


    private fun checkoutRechargeCard(
        tag: Tag,
        uid: String,
        operation: NfcOperation.CheckoutRecharge
    ) {

        var finalStateReached =
            false

        try {

            val paymentMethod =
                try {
                    RechargeCheckoutApiClient
                        .PaymentMethod
                        .valueOf(
                            operation.paymentMethod
                                .trim()
                                .uppercase()
                        )
                } catch (
                    _: Exception
                ) {
                    throw IllegalArgumentException(
                        "Método de pago inválido."
                    )
                }

            val normalizedUid =
                uid
                    .trim()
                    .uppercase()

            val rawData =
                Ntag215Reader
                    .readMenesesData(
                        tag
                    )

            val physicalCard =
                if (
                    MenesesCardCodec
                        .isMenesesCard(
                            rawData
                        )
                ) {
                    MenesesCardCodec
                        .decode(
                            rawData
                        )
                } else {
                    null
                }

            val resume =
                RechargeCheckoutApiClient
                    .resume(
                        targetUid =
                            normalizedUid
                    )

            val execution =
                if (
                    resume.found
                ) {
                    resumeCheckoutExecution(
                        resume = resume,
                        physicalCard = physicalCard,
                        paymentMethod = paymentMethod,
                        targetUid = normalizedUid,
                        expectedPromotionId =
                            operation.promotionId
                    )
                } else {
                    prepareAndAuthorizeCheckoutExecution(
                        physicalCard = physicalCard,
                        paymentMethod = paymentMethod,
                        amount = operation.amount,
                        targetUid = normalizedUid,
                        promotionId =
                            operation.promotionId
                    )
                }

            val expectedFinalCard =
                checkoutStateToMenesesCard(
                    state =
                        execution.finalCardState,
                    version =
                        physicalCard?.version
                            ?: 1
                )

            val physicalAlreadyFinal =
                physicalCard != null &&
                        checkoutCardMatchesState(
                            card = physicalCard,
                            state = execution.finalCardState
                        )

            if (
                !physicalAlreadyFinal
            ) {

                when (
                    execution.checkout.cardPath
                ) {
                    "NEW" -> {
                        if (
                            physicalCard != null
                        ) {
                            throw IllegalStateException(
                                "La tarjeta ya contiene datos distintos al estado autorizado. " +
                                        "NO se modificó la NFC."
                            )
                        }
                    }

                    "EXISTING",
                    "REUSED" -> {
                        val before =
                            execution.beforeCardState
                                ?: throw IllegalStateException(
                                    "El checkout ${execution.checkout.cardPath} no contiene estado BEFORE."
                                )

                        if (
                            physicalCard == null ||
                            !checkoutCardMatchesState(
                                card = physicalCard,
                                state = before
                            )
                        ) {
                            throw IllegalStateException(
                                "MANUAL_REVIEW_REQUIRED: la NFC no coincide con BEFORE ni AFTER. " +
                                        "NO se modificó la tarjeta."
                            )
                        }
                    }

                    else ->
                        throw IllegalStateException(
                            "Este tipo de tarjeta todavía no puede procesarse automáticamente: " +
                                    execution.checkout.cardPath
                        )
                }

                Ntag215Writer
                    .writeMenesesData(
                        tag,
                        MenesesCardCodec
                            .encode(
                                expectedFinalCard
                            )
                    )
            }

            verifyCard(
                tag,
                expectedFinalCard
            )

            finalStateReached =
                true

            val confirmation =
                RechargeCheckoutApiClient
                    .confirm(
                        checkoutId =
                            execution.checkout.checkoutId,
                        targetUid =
                            normalizedUid,
                        cardId =
                            expectedFinalCard.cardId,
                        writtenBalance =
                            expectedFinalCard.balance,
                        writtenCounter =
                            expectedFinalCard.transactionCounter
                    )

            pendingOperation =
                NfcOperation.Read

            val totalDue =
                confirmation.checkout
                    .amounts
                    .totalDue

            val activationFee =
                confirmation.checkout
                    .amounts
                    .activationFee

            val paymentLabel =
                when (
                    confirmation.checkout
                        .paymentMethod
                ) {
                    "CARD" -> "Tarjeta"
                    else -> "Efectivo"
                }

            val isPromotion =
                !confirmation.checkout
                    .promotionId
                    .isNullOrBlank()

            runOnUiThread {

                cardResult =
                    CardReadResult
                        .Success(
                            if (
                                isPromotion
                            ) {
                                "Promoción aplicada"
                            } else {
                                "Recarga taquilla realizada"
                            },
                            buildString {

                                if (
                                    isPromotion
                                ) {
                                    append(
                                        operation.promotionName
                                            ?.takeIf {
                                                it.isNotBlank()
                                            }
                                            ?: "Recarga promocional"
                                    )
                                    append(
                                        "\nCliente paga: \$${confirmation.checkout.amounts.paidRecharge}"
                                    )
                                    append(
                                        "\nPromocional otorgado: +\$${confirmation.checkout.amounts.promotionalCredit}"
                                    )
                                    append(
                                        "\nCrédito entregado: \$${confirmation.checkout.amounts.credited}"
                                    )
                                    append(
                                        "\nSaldo final: \$${confirmation.card.balance}"
                                    )
                                } else {
                                    append(
                                        "Saldo registrado exitosamente: \$${confirmation.card.balance}"
                                    )
                                }

                                append(
                                    "\nCobrar al cliente: \$$totalDue"
                                )

                                if (
                                    activationFee > 0
                                ) {
                                    append(
                                        "\nIncluye activación de tarjeta: \$$activationFee"
                                    )
                                }

                                append(
                                    "\nMétodo de pago: $paymentLabel"
                                )
                            }
                        )
            }

        } catch (
            e: CheckoutOperationRecoveredException
        ) {

            pendingOperation =
                NfcOperation.Read

            runOnUiThread {

                cardResult =
                    CardReadResult
                        .Success(
                            title =
                                "Operación anterior recuperada",

                            message =
                                e.userMessage
                        )
            }

        } catch (
            e: Exception
        ) {

            if (
                finalStateReached
            ) {
                /*
                 * La NFC ya fue verificada en AFTER, pero CONFIRM pudo
                 * fallar por red/reinicio. Conservamos la operación armada
                 * para que el cajero acerque LA MISMA tarjeta y RESUME
                 * finalice el checkout sin volver a cobrar ni adivinar.
                 */
                runOnUiThread {
                    cardResult =
                        CardReadResult
                            .WaitingForDevCard(
                                title =
                                    "Recarga pendiente de confirmar",
                                message =
                                    "La tarjeta ya alcanzó el estado autorizado, " +
                                            "pero falta confirmar con el servidor.\n\n" +
                                            "NO uses otra tarjeta.\n" +
                                            "Vuelve a acercar esta misma NFC."
                            )
                }

                return
            }

            pendingOperation =
                NfcOperation.Read

            showError(
                e.message
                    ?: "No fue posible completar la recarga."
            )
        }
    }


    private fun resumeCheckoutExecution(
        resume: RechargeCheckoutApiClient.ResumeResult,
        physicalCard: MenesesCard?,
        paymentMethod: RechargeCheckoutApiClient.PaymentMethod,
        targetUid: String,
        expectedPromotionId: String?
    ): CheckoutExecution {

        val checkout =
            resume.checkout
                ?: throw IllegalStateException(
                    "El servidor reportó un checkout abierto sin detalle."
                )

        val checkoutPaymentMethod =
            checkout.paymentMethod
                ?.trim()
                ?.uppercase()

        if (
            checkoutPaymentMethod != null &&
            checkoutPaymentMethod !=
            paymentMethod.name
        ) {
            throw IllegalStateException(
                "Existe una recarga pendiente con método de pago " +
                        if (checkoutPaymentMethod == "CARD") {
                            "Tarjeta."
                        } else {
                            "Efectivo."
                        }
            )
        }

        val checkoutPromotionId =
            checkout.promotionId
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }

        val normalizedExpectedPromotionId =
            expectedPromotionId
                ?.trim()
                ?.takeIf {
                    it.isNotBlank()
                }

        if (
            checkoutPromotionId !=
            normalizedExpectedPromotionId
        ) {
            throw IllegalStateException(
                if (
                    checkoutPromotionId != null
                ) {
                    "Existe una promoción pendiente distinta para esta tarjeta. " +
                            "Completa o recupera esa operación antes de iniciar otra."
                } else {
                    "Existe una recarga normal pendiente para esta tarjeta. " +
                            "Completa o recupera esa operación antes de aplicar una promoción."
                }
            )
        }

        return when (
            resume.state
                .trim()
                .uppercase()
        ) {
            "MANUAL_REVIEW_REQUIRED" ->
                throw IllegalStateException(
                    "MANUAL_REVIEW_REQUIRED: conserva la tarjeta y revisa el incidente antes de continuar."
                )

            "IN_PROGRESS" -> {
                val finalState =
                    resume.finalCardState
                        ?: throw IllegalStateException(
                            "El checkout pendiente no contiene estado AFTER."
                        )

                when (
                    checkout.cardPath
                ) {
                    "NEW" -> {

                        /*
                         * Un checkout NEW que quedó IN_PROGRESS pertenece a
                         * una operación anterior interrumpida.
                         *
                         * NFC virgen = BEFORE:
                         * no continuamos escribiendo silenciosamente. El
                         * backend cierra la reservación y el checkout como
                         * FAILED para que el operador inicie una operación
                         * nueva de forma explícita.
                         */
                        if (
                            physicalCard == null
                        ) {
                            val reconciliation =
                                RechargeCheckoutApiClient
                                    .reconcile(
                                        checkoutId =
                                            checkout.checkoutId,
                                        targetUid =
                                            targetUid,
                                        isVirgin =
                                            true
                                    )

                            if (
                                reconciliation.action !=
                                "FAILED_NEW_CHECKOUT_BEFORE_WRITE" &&
                                reconciliation.action !=
                                "ALREADY_FAILED"
                            ) {
                                throw IllegalStateException(
                                    "Respuesta inesperada al recuperar checkout NEW: ${reconciliation.action}"
                                )
                            }

                            throw CheckoutOperationRecoveredException(
                                recoveryAction =
                                    reconciliation.action,
                                userMessage =
                                    "La recarga anterior no alcanzó a escribirse en la NFC y fue cerrada de forma segura.\n\n" +
                                            "La tarjeta continúa sin esa recarga.\n" +
                                            "Inicia nuevamente la recarga cuando estés listo."
                            )
                        }

                        /*
                         * NFC == AFTER:
                         * pedimos al backend reconciliar el checkout antes de
                         * llamar CONFIRM. Nunca reescribimos la NFC.
                         */
                        if (
                            checkoutCardMatchesState(
                                card = physicalCard,
                                state = finalState
                            )
                        ) {
                            val reconciliation =
                                RechargeCheckoutApiClient
                                    .reconcile(
                                        checkoutId =
                                            checkout.checkoutId,
                                        targetUid =
                                            targetUid,
                                        isVirgin =
                                            false,
                                        cardId =
                                            physicalCard.cardId,
                                        cardBalance =
                                            physicalCard.balance,
                                        cardCounter =
                                            physicalCard.transactionCounter
                                    )

                            if (
                                reconciliation.action !=
                                "CONFIRM_REQUIRED"
                            ) {
                                throw IllegalStateException(
                                    "Respuesta inesperada al recuperar checkout NEW escrito: ${reconciliation.action}"
                                )
                            }

                            reconciliation.expectedAfter
                                ?.let { reconciledAfter ->

                                    if (
                                        reconciledAfter.cardId !=
                                        finalState.cardId ||
                                        reconciledAfter.balance !=
                                        finalState.balance ||
                                        reconciledAfter.transactionCounter !=
                                        finalState.transactionCounter
                                    ) {
                                        throw IllegalStateException(
                                            "El estado AFTER reconciliado no coincide con el checkout pendiente."
                                        )
                                    }
                                }

                            CheckoutExecution(
                                checkout = checkout,
                                beforeCardState =
                                    null,
                                finalCardState =
                                    finalState
                            )

                        } else {

                            /*
                             * Tercer estado:
                             * el backend preserva checkout + registration en
                             * MANUAL_REVIEW_REQUIRED. No escribimos nada.
                             */
                            RechargeCheckoutApiClient
                                .reconcile(
                                    checkoutId =
                                        checkout.checkoutId,
                                    targetUid =
                                        targetUid,
                                    isVirgin =
                                        false,
                                    cardId =
                                        physicalCard.cardId,
                                    cardBalance =
                                        physicalCard.balance,
                                    cardCounter =
                                        physicalCard.transactionCounter
                                )

                            throw IllegalStateException(
                                "MANUAL_REVIEW_REQUIRED: la tarjeta NEW pendiente contiene un estado inesperado."
                            )
                        }
                    }

                    "EXISTING" -> {
                        val before =
                            resume.beforeCardState
                                ?: throw IllegalStateException(
                                    "El checkout EXISTING pendiente no contiene BEFORE."
                                )

                        val card =
                            physicalCard
                                ?: throw IllegalStateException(
                                    "La NFC del checkout EXISTING ya no contiene una Meneses Card válida. " +
                                            "No se modificó nada; requiere revisión antes de continuar."
                                )

                        val isBefore =
                            checkoutCardMatchesState(
                                card = card,
                                state = before
                            )

                        val isAfter =
                            checkoutCardMatchesState(
                                card = card,
                                state = finalState
                            )

                        if (
                            isBefore
                        ) {
                            val reconciliation =
                                RechargeCheckoutApiClient
                                    .reconcile(
                                        checkoutId =
                                            checkout.checkoutId,
                                        targetUid =
                                            targetUid,
                                        isVirgin =
                                            false,
                                        cardId =
                                            card.cardId,
                                        cardBalance =
                                            card.balance,
                                        cardCounter =
                                            card.transactionCounter
                                    )

                            if (
                                reconciliation.action !=
                                "FAILED_EXISTING_CHECKOUT_BEFORE_WRITE" &&
                                reconciliation.action !=
                                "FAILED_CHECKOUT_FROM_FAILED_TRANSACTION" &&
                                reconciliation.action !=
                                "ALREADY_FAILED"
                            ) {
                                throw IllegalStateException(
                                    "Respuesta inesperada al recuperar checkout EXISTING en BEFORE: ${reconciliation.action}"
                                )
                            }

                            throw CheckoutOperationRecoveredException(
                                recoveryAction =
                                    reconciliation.action,
                                userMessage =
                                    "La recarga anterior no alcanzó a escribirse en la tarjeta y fue cerrada de forma segura.\n\n" +
                                            "El saldo físico no cambió.\n" +
                                            "Inicia nuevamente la recarga cuando estés listo."
                            )
                        }

                        if (
                            isAfter
                        ) {
                            val reconciliation =
                                RechargeCheckoutApiClient
                                    .reconcile(
                                        checkoutId =
                                            checkout.checkoutId,
                                        targetUid =
                                            targetUid,
                                        isVirgin =
                                            false,
                                        cardId =
                                            card.cardId,
                                        cardBalance =
                                            card.balance,
                                        cardCounter =
                                            card.transactionCounter
                                    )

                            when (
                                reconciliation.action
                            ) {
                                "CONFIRM_REQUIRED" -> {

                                    reconciliation.expectedAfter
                                        ?.let { reconciledAfter ->

                                            if (
                                                reconciledAfter.cardId !=
                                                finalState.cardId ||
                                                reconciledAfter.balance !=
                                                finalState.balance ||
                                                reconciledAfter.transactionCounter !=
                                                finalState.transactionCounter
                                            ) {
                                                throw IllegalStateException(
                                                    "El estado AFTER reconciliado no coincide con el checkout pendiente."
                                                )
                                            }
                                        }

                                    CheckoutExecution(
                                        checkout = checkout,
                                        beforeCardState =
                                            before,
                                        finalCardState =
                                            finalState
                                    )
                                }

                                "CONFIRMED_CHECKOUT_FROM_CONFIRMED_TRANSACTION",
                                "ALREADY_CONFIRMED" ->
                                    throw CheckoutOperationRecoveredException(
                                        recoveryAction =
                                            reconciliation.action,
                                        userMessage =
                                            "La recarga anterior ya había quedado confirmada correctamente.\n\n" +
                                                    "No se volvió a acreditar saldo ni se reescribió la NFC."
                                    )

                                else ->
                                    throw IllegalStateException(
                                        "Respuesta inesperada al recuperar checkout EXISTING en AFTER: ${reconciliation.action}"
                                    )
                            }

                        } else {

                            /*
                             * Tercer estado:
                             * reconcile crea la fotografía forense,
                             * REVERSAL_REQUIRED y financial_hold.
                             */
                            RechargeCheckoutApiClient
                                .reconcile(
                                    checkoutId =
                                        checkout.checkoutId,
                                    targetUid =
                                        targetUid,
                                    isVirgin =
                                        false,
                                    cardId =
                                        card.cardId,
                                    cardBalance =
                                        card.balance,
                                    cardCounter =
                                        card.transactionCounter
                                )

                            throw IllegalStateException(
                                "MANUAL_REVIEW_REQUIRED: la NFC no coincide con BEFORE ni AFTER."
                            )
                        }
                    }

                    "REUSED" -> {
                        val before =
                            resume.beforeCardState
                                ?: throw IllegalStateException(
                                    "El checkout REUSED pendiente no contiene BEFORE."
                                )

                        val card =
                            physicalCard
                                ?: run {
                                    RechargeCheckoutApiClient
                                        .reconcile(
                                            checkoutId = checkout.checkoutId,
                                            targetUid = targetUid,
                                            isVirgin = true
                                        )

                                    throw IllegalStateException(
                                        "MANUAL_REVIEW_REQUIRED: la tarjeta REUSED esperada ya no contiene una Meneses Card válida."
                                    )
                                }

                        val isBefore =
                            checkoutCardMatchesState(
                                card = card,
                                state = before
                            )

                        val isAfter =
                            checkoutCardMatchesState(
                                card = card,
                                state = finalState
                            )

                        if (isBefore) {
                            val reconciliation =
                                RechargeCheckoutApiClient
                                    .reconcile(
                                        checkoutId = checkout.checkoutId,
                                        targetUid = targetUid,
                                        isVirgin = false,
                                        cardId = card.cardId,
                                        cardBalance = card.balance,
                                        cardCounter = card.transactionCounter
                                    )

                            if (
                                reconciliation.action != "FAILED_REUSED_CHECKOUT_BEFORE_WRITE" &&
                                reconciliation.action != "ALREADY_FAILED"
                            ) {
                                throw IllegalStateException(
                                    "Respuesta inesperada al recuperar checkout REUSED en BEFORE: ${reconciliation.action}"
                                )
                            }

                            throw CheckoutOperationRecoveredException(
                                recoveryAction = reconciliation.action,
                                userMessage =
                                    "La reactivación anterior no alcanzó a escribirse en la tarjeta y fue cerrada de forma segura.\n\n" +
                                            "La tarjeta continúa devuelta e inactiva.\n" +
                                            "Inicia nuevamente la recarga cuando estés listo."
                            )
                        }

                        if (isAfter) {
                            val reconciliation =
                                RechargeCheckoutApiClient
                                    .reconcile(
                                        checkoutId = checkout.checkoutId,
                                        targetUid = targetUid,
                                        isVirgin = false,
                                        cardId = card.cardId,
                                        cardBalance = card.balance,
                                        cardCounter = card.transactionCounter
                                    )

                            if (reconciliation.action != "CONFIRM_REQUIRED") {
                                throw IllegalStateException(
                                    "Respuesta inesperada al recuperar checkout REUSED en AFTER: ${reconciliation.action}"
                                )
                            }

                            reconciliation.expectedAfter
                                ?.let { reconciledAfter ->
                                    if (
                                        reconciledAfter.cardId != finalState.cardId ||
                                        reconciledAfter.balance != finalState.balance ||
                                        reconciledAfter.transactionCounter != finalState.transactionCounter
                                    ) {
                                        throw IllegalStateException(
                                            "El estado AFTER reconciliado no coincide con el checkout REUSED pendiente."
                                        )
                                    }
                                }

                            CheckoutExecution(
                                checkout = checkout,
                                beforeCardState = before,
                                finalCardState = finalState
                            )

                        } else {
                            RechargeCheckoutApiClient
                                .reconcile(
                                    checkoutId = checkout.checkoutId,
                                    targetUid = targetUid,
                                    isVirgin = false,
                                    cardId = card.cardId,
                                    cardBalance = card.balance,
                                    cardCounter = card.transactionCounter
                                )

                            throw IllegalStateException(
                                "MANUAL_REVIEW_REQUIRED: la NFC REUSED no coincide con BEFORE ni AFTER."
                            )
                        }
                    }

                    else ->
                        throw IllegalStateException(
                            "Este tipo de tarjeta todavía no puede recuperarse automáticamente: " +
                                    checkout.cardPath
                        )
                }
            }

            "PREPARED" -> {
                val authorization =
                    when (
                        checkout.cardPath
                    ) {
                        "NEW" -> {
                            if (
                                physicalCard != null
                            ) {
                                throw IllegalStateException(
                                    "La tarjeta ya no está virgen. NO se modificó."
                                )
                            }

                            RechargeCheckoutApiClient
                                .authorizeNew(
                                    checkoutId =
                                        checkout.checkoutId,
                                    targetUid =
                                        targetUid
                                )
                        }

                        "EXISTING" -> {
                            val card =
                                requireActiveCustomerForCheckout(
                                    physicalCard
                                )

                            RechargeCheckoutApiClient
                                .authorizeExisting(
                                    checkoutId =
                                        checkout.checkoutId,
                                    targetUid =
                                        targetUid,
                                    cardBalance =
                                        card.balance,
                                    cardCounter =
                                        card.transactionCounter
                                )
                        }

                        "REUSED" -> {
                            val card =
                                requireReusableCustomerForCheckout(
                                    physicalCard
                                )

                            RechargeCheckoutApiClient
                                .authorizeExisting(
                                    checkoutId =
                                        checkout.checkoutId,
                                    targetUid =
                                        targetUid,
                                    cardBalance =
                                        card.balance,
                                    cardCounter =
                                        card.transactionCounter
                                )
                        }

                        else ->
                            throw IllegalStateException(
                                "Este tipo de tarjeta todavía no puede procesarse automáticamente: " +
                                        checkout.cardPath
                            )
                    }

                CheckoutExecution(
                    checkout =
                        authorization.checkout,
                    beforeCardState =
                        authorization.beforeCardState,
                    finalCardState =
                        authorization.finalCardState
                )
            }

            else ->
                throw IllegalStateException(
                    "Estado de checkout no soportado: ${resume.state}"
                )
        }
    }

    private fun prepareAndAuthorizeCheckoutExecution(
        physicalCard: MenesesCard?,
        paymentMethod: RechargeCheckoutApiClient.PaymentMethod,
        amount: Long,
        targetUid: String,
        promotionId: String? = null
    ): CheckoutExecution {

        val prepared =
            RechargeCheckoutApiClient
                .prepare(
                    targetUid =
                        targetUid,
                    amount =
                        amount,
                    paymentMethod =
                        paymentMethod,
                    promotionId =
                        promotionId
                )

        val authorization =
            when (
                prepared.checkout.cardPath
            ) {
                "NEW" -> {
                    if (
                        physicalCard != null
                    ) {
                        throw IllegalStateException(
                            "El servidor clasificó NEW pero la NFC ya contiene una Meneses Card."
                        )
                    }

                    RechargeCheckoutApiClient
                        .authorizeNew(
                            checkoutId =
                                prepared.checkout.checkoutId,
                            targetUid =
                                targetUid
                        )
                }

                "EXISTING" -> {
                    val card =
                        requireActiveCustomerForCheckout(
                            physicalCard
                        )

                    RechargeCheckoutApiClient
                        .authorizeExisting(
                            checkoutId =
                                prepared.checkout.checkoutId,
                            targetUid =
                                targetUid,
                            cardBalance =
                                card.balance,
                            cardCounter =
                                card.transactionCounter
                        )
                }

                "REUSED" -> {
                    val card =
                        requireReusableCustomerForCheckout(
                            physicalCard
                        )

                    RechargeCheckoutApiClient
                        .authorizeExisting(
                            checkoutId =
                                prepared.checkout.checkoutId,
                            targetUid =
                                targetUid,
                            cardBalance =
                                card.balance,
                            cardCounter =
                                card.transactionCounter
                        )
                }

                else ->
                    throw IllegalStateException(
                        "La tarjeta está registrada, pero no es elegible para recarga automática (${prepared.checkout.cardPath})."
                    )
            }

        return CheckoutExecution(
            checkout =
                authorization.checkout,
            beforeCardState =
                authorization.beforeCardState,
            finalCardState =
                authorization.finalCardState
        )
    }


    private fun requireActiveCustomerForCheckout(
        card: MenesesCard?
    ): MenesesCard {

        val existing =
            card
                ?: throw IllegalArgumentException(
                    "La tarjeta registrada no pudo leerse como CUSTOMER."
                )

        if (
            existing.type !=
            CardType.CUSTOMER
        ) {
            throw IllegalArgumentException(
                "La tarjeta no es CUSTOMER."
            )
        }

        if (
            existing.status !=
            CardStatus.ACTIVE
        ) {
            throw IllegalArgumentException(
                "La tarjeta CUSTOMER no está activa."
            )
        }

        return existing
    }


    private fun checkoutStateToMenesesCard(
        state: RechargeCheckoutApiClient.CardState,
        version: Int
    ): MenesesCard {

        if (
            !state.cardType.equals(
                "CUSTOMER",
                ignoreCase = true
            ) ||
            !state.status.equals(
                "ACTIVE",
                ignoreCase = true
            )
        ) {
            throw IllegalStateException(
                "El servidor devolvió un estado NFC no válido para CUSTOMER."
            )
        }

        return MenesesCard(
            version = version,
            type = CardType.CUSTOMER,
            status = CardStatus.ACTIVE,
            cardId = state.cardId,
            balance = state.balance,
            transactionCounter =
                state.transactionCounter
        )
    }


    private fun requireReusableCustomerForCheckout(
        card: MenesesCard?
    ): MenesesCard {

        val reusable =
            card
                ?: throw IllegalArgumentException(
                    "La tarjeta reutilizable no pudo leerse como CUSTOMER."
                )

        if (
            reusable.type !=
            CardType.CUSTOMER
        ) {
            throw IllegalArgumentException(
                "La tarjeta no es CUSTOMER."
            )
        }

        if (
            reusable.status !=
            CardStatus.INACTIVE ||
            reusable.balance != 0L ||
            reusable.transactionCounter != 0L
        ) {
            throw IllegalArgumentException(
                "La tarjeta CUSTOMER no se encuentra en el estado físico esperado para reutilización."
            )
        }

        return reusable
    }


    private fun checkoutCardMatchesState(
        card: MenesesCard,
        state: RechargeCheckoutApiClient.CardState
    ): Boolean {

        return card.cardId ==
                state.cardId &&
                card.type ==
                CardType.CUSTOMER &&
                state.cardType.equals(
                    "CUSTOMER",
                    ignoreCase = true
                ) &&
                card.status.name.equals(
                    state.status,
                    ignoreCase = true
                ) &&
                card.balance ==
                state.balance &&
                card.transactionCounter ==
                state.transactionCounter
    }


    /*
     * =====================================================
     * RECHARGE
     * =====================================================
     */

    private fun rechargeCard(
        tag: Tag,
        uid: String,
        amount: Long
    ) {

        var transactionId:
                String? =
            null

        var cardWasWritten =
            false

        try {

            val currentCard =
                readCustomerCard(
                    tag
                )

            val authorization =
                authorizeRechargeWithRecovery(

                    card =
                        currentCard,

                    uid =
                        uid,

                    amount =
                        amount
                )

            transactionId =
                authorization.transactionId

            validateAuthorizationBefore(

                currentCard,
                authorization.cardId,
                authorization.balanceBefore,
                authorization.counterBefore
            )

            val updatedCard =
                currentCard.copy(

                    balance =
                        authorization.balanceAfter,

                    transactionCounter =
                        authorization.counterAfter
                )

            Ntag215Writer
                .writeMenesesData(

                    tag,

                    MenesesCardCodec
                        .encode(
                            updatedCard
                        )
                )

            cardWasWritten =
                true

            verifyCard(
                tag,
                updatedCard
            )

            confirmTransactionWithRetry(

                transactionId =
                    authorization.transactionId,

                cardId =
                    updatedCard.cardId,

                uid =
                    uid,

                writtenBalance =
                    updatedCard.balance,

                writtenCounter =
                    updatedCard.transactionCounter
            )

            pendingOperation =
                NfcOperation.Read

            runOnUiThread {

                cardResult =
                    CardReadResult
                        .Success(

                            "Recarga taquilla realizada",

                            "Saldo anterior: \$${authorization.balanceBefore}\n" +
                                    "Recarga: +\$${amount}\n" +
                                    "Saldo nuevo: \$${authorization.balanceAfter}"
                        )
            }

        } catch (
            e: Exception
        ) {

            handleTransactionFailure(

                transactionId,
                cardWasWritten,
                "recarga",
                e
            )
        }
    }


    /*
     * =====================================================
     * PROMOTIONAL RECHARGE
     * =====================================================
     */

    private fun promotionalRechargeCard(
        tag: Tag,
        uid: String,
        operation: NfcOperation.PromotionalRecharge
    ) {

        var transactionId:
                String? =
            null

        var cardWasWritten =
            false

        try {

            val currentCard =
                readCustomerCard(
                    tag
                )

            val authorization =
                authorizePromotionalRechargeWithRecovery(

                    card =
                        currentCard,

                    uid =
                        uid,

                    promotionId =
                        operation.promotionId
                )

            transactionId =
                authorization.transactionId

            validateAuthorizationBefore(
                currentCard,
                authorization.cardId,
                authorization.balanceBefore,
                authorization.counterBefore
            )

            /*
             * El servidor es la autoridad financiera.
             *
             * Aunque la operación conserva los importes de la
             * promoción para mostrarlos en pantalla, el saldo
             * que se escribe físicamente siempre proviene de
             * authorization.balanceAfter.
             */

            val updatedCard =
                currentCard.copy(

                    balance =
                        authorization.balanceAfter,

                    transactionCounter =
                        authorization.counterAfter
                )

            Ntag215Writer
                .writeMenesesData(

                    tag,

                    MenesesCardCodec
                        .encode(
                            updatedCard
                        )
                )

            cardWasWritten =
                true

            verifyCard(
                tag,
                updatedCard
            )

            confirmTransactionWithRetry(

                transactionId =
                    authorization.transactionId,

                cardId =
                    updatedCard.cardId,

                uid =
                    uid,

                writtenBalance =
                    updatedCard.balance,

                writtenCounter =
                    updatedCard.transactionCounter
            )

            pendingOperation =
                NfcOperation.Read

            runOnUiThread {

                cardResult =
                    CardReadResult
                        .Success(

                            title =
                                "Promoción aplicada",

                            message =
                                "${operation.promotionName}\n\n" +
                                        "Cliente pagó: \$${operation.cashAmount}\n" +
                                        "Bonificación: +\$${operation.promotionalAmount}\n" +
                                        "Total acreditado: \$${operation.totalCreditAmount}\n\n" +
                                        "Saldo anterior: \$${authorization.balanceBefore}\n" +
                                        "Saldo nuevo: \$${authorization.balanceAfter}"
                        )
            }

        } catch (
            e: Exception
        ) {

            handleTransactionFailure(
                transactionId,
                cardWasWritten,
                "recarga promocional",
                e
            )
        }
    }


    /*
     * =====================================================
     * CHARGE
     * =====================================================
     */

    private fun chargeCard(
        tag: Tag,
        uid: String,
        peopleCount: Int
    ) {

        var transactionId:
                String? =
            null

        var cardWasWritten =
            false

        /*
         * Conservamos el saldo leído físicamente de la tarjeta para poder
         * mostrar un mensaje útil si el servidor rechaza el cobro por saldo
         * insuficiente.
         */
        var currentBalanceForError: Long? =
            null

        try {

            val currentCard =
                readCustomerCard(
                    tag
                )

            currentBalanceForError =
                currentCard.balance

            val authorization =
                authorizeChargeWithRecovery(

                    card =
                        currentCard,

                    uid =
                        uid,

                    peopleCount =
                        peopleCount
                )

            transactionId =
                authorization.transactionId

            validateAuthorizationBefore(

                currentCard,
                authorization.cardId,
                authorization.balanceBefore,
                authorization.counterBefore
            )

            val updatedCard =
                currentCard.copy(

                    balance =
                        authorization.balanceAfter,

                    transactionCounter =
                        authorization.counterAfter
                )

            Ntag215Writer
                .writeMenesesData(

                    tag,

                    MenesesCardCodec
                        .encode(
                            updatedCard
                        )
                )

            cardWasWritten =
                true

            verifyCard(
                tag,
                updatedCard
            )

            confirmTransactionWithRetry(

                transactionId =
                    authorization.transactionId,

                cardId =
                    updatedCard.cardId,

                uid =
                    uid,

                writtenBalance =
                    updatedCard.balance,

                writtenCounter =
                    updatedCard.transactionCounter
            )

            pendingOperation =
                NfcOperation.Read

            runOnUiThread {

                cardResult =
                    CardReadResult
                        .Success(

                            "Cobro realizado",

                            "${authorization.gameName}\n" +
                                    "Personas: ${authorization.peopleCount}\n" +
                                    "Total: \$${authorization.total}\n\n" +
                                    "Saldo anterior: \$${authorization.balanceBefore}\n" +
                                    "Saldo nuevo: \$${authorization.balanceAfter}"
                        )
            }

        } catch (
            e: Exception
        ) {

            val errorMessage =
                e.message.orEmpty()

            /*
             * El backend responde INSUFFICIENT_BALANCE cuando la tarjeta no
             * alcanza para cubrir el cobro. En ese caso no mostramos el error
             * técnico: enseñamos al operador el saldo real leído del NFC.
             */
            if (
                errorMessage.contains(
                    "INSUFFICIENT_BALANCE",
                    ignoreCase = true
                ) &&
                !cardWasWritten
            ) {

                pendingOperation =
                    NfcOperation.Read

                val balance =
                    currentBalanceForError ?: 0L

                runOnUiThread {
                    cardResult =
                        CardReadResult.Error(
                            "SALDO INSUFICIENTE\nSaldo actual: \$$balance"
                        )
                }

                return
            }

            handleTransactionFailure(

                transactionId,
                cardWasWritten,
                "cobro",
                e
            )
        }
    }


    /*
     * =====================================================
     * ADMIN RECHARGE
     * =====================================================
     */

    private fun adminRechargeCard(
        tag: Tag,
        uid: String,
        amount: Long
    ) {

        var transactionId:
                String? =
            null

        var cardWasWritten =
            false

        try {

            if (
                adminSession == null
            ) {
                throw IllegalStateException(
                    "La sesión ADMIN ya no está activa."
                )
            }

            val currentCard =
                readCustomerCard(
                    tag
                )

            val authorization =
                authorizeAdminRechargeWithRecovery(
                    card =
                        currentCard,
                    uid =
                        uid,
                    amount =
                        amount
                )

            transactionId =
                authorization.transactionId

            validateAuthorizationBefore(
                currentCard,
                authorization.cardId,
                authorization.balanceBefore,
                authorization.counterBefore
            )

            val updatedCard =
                currentCard.copy(
                    balance =
                        authorization.balanceAfter,
                    transactionCounter =
                        authorization.counterAfter
                )

            Ntag215Writer
                .writeMenesesData(
                    tag,
                    MenesesCardCodec
                        .encode(
                            updatedCard
                        )
                )

            cardWasWritten =
                true

            verifyCard(
                tag,
                updatedCard
            )

            confirmTransactionWithRetry(
                transactionId =
                    authorization.transactionId,
                cardId =
                    updatedCard.cardId,
                uid =
                    uid,
                writtenBalance =
                    updatedCard.balance,
                writtenCounter =
                    updatedCard.transactionCounter
            )

            pendingOperation =
                NfcOperation.Read

            loadAdminCashToday()

            runOnUiThread {
                cardResult =
                    CardReadResult
                        .Success(
                            "Recarga realizada",
                            "Saldo anterior: \$${authorization.balanceBefore}\n" +
                                    "Recarga: +\$${amount}\n" +
                                    "Saldo nuevo: \$${authorization.balanceAfter}"
                        )
            }

        } catch (
            e: Exception
        ) {

            handleTransactionFailure(
                transactionId,
                cardWasWritten,
                "recarga ADMIN",
                e
            )
        }
    }


    /*
     * =====================================================
     * ADMIN ADJUSTMENT
     * =====================================================
     */

    private fun adminAdjustmentCard(
        tag: Tag,
        uid: String,
        amount: Long
    ) {

        var transactionId:
                String? =
            null

        var cardWasWritten =
            false

        try {

            if (
                adminSession == null
            ) {
                throw IllegalStateException(
                    "La sesión ADMIN ya no está activa."
                )
            }

            val currentCard =
                readCustomerCard(
                    tag
                )

            val authorization =
                MenesesApiClient
                    .authorizeAdminAdjustment(
                        currentCard.cardId,
                        uid,
                        amount,
                        currentCard.balance,
                        currentCard.transactionCounter
                    )

            transactionId =
                authorization.transactionId

            validateAuthorizationBefore(
                currentCard,
                authorization.cardId,
                authorization.balanceBefore,
                authorization.counterBefore
            )

            val updatedCard =
                currentCard.copy(
                    balance =
                        authorization.balanceAfter,
                    transactionCounter =
                        authorization.counterAfter
                )

            Ntag215Writer
                .writeMenesesData(
                    tag,
                    MenesesCardCodec
                        .encode(
                            updatedCard
                        )
                )

            cardWasWritten =
                true

            verifyCard(
                tag,
                updatedCard
            )

            MenesesApiClient
                .confirmTransaction(
                    authorization.transactionId,
                    updatedCard.cardId,
                    uid,
                    updatedCard.balance,
                    updatedCard.transactionCounter
                )

            pendingOperation =
                NfcOperation.Read

            runOnUiThread {
                cardResult =
                    CardReadResult
                        .Success(
                            "Saldo retirado",
                            "Saldo anterior: \$${authorization.balanceBefore}\n" +
                                    "Retiro: -\$${amount}\n" +
                                    "Saldo nuevo: \$${authorization.balanceAfter}"
                        )
            }

        } catch (
            e: Exception
        ) {

            handleTransactionFailure(
                transactionId,
                cardWasWritten,
                "ajuste ADMIN",
                e
            )
        }
    }


    /*
     * =====================================================
     * HELPERS
     * =====================================================
     */

    /*
     * =====================================================
     * TRANSACTION RECOVERY
     * =====================================================
     */

    private fun hasServerErrorCode(
        error: Throwable,
        code: String
    ): Boolean {

        val message =
            error.message
                .orEmpty()
                .trim()

        return (
                message.equals(
                    code,
                    ignoreCase = true
                ) ||
                        message.startsWith(
                            "$code:",
                            ignoreCase = true
                        )
                )
    }


    private fun reconcileCustomerCard(
        card: MenesesCard,
        uid: String
    ): TransactionReconciliation {

        try {

            val result =
                MenesesApiClient
                    .reconcileTransaction(

                        cardId =
                            card.cardId,

                        uid =
                            uid,

                        cardBalance =
                            card.balance,

                        cardCounter =
                            card.transactionCounter
                    )

            if (
                !result.reconciled
            ) {

                throw IllegalStateException(
                    "No fue posible reconciliar la tarjeta."
                )
            }

            Log.w(
                "MENESES_RECONCILIATION",
                "Tarjeta reconciliada. " +
                        "cardId=${result.cardId}, " +
                        "action=${result.action}, " +
                        "balance=${result.balance}, " +
                        "counter=${result.transactionCounter}, " +
                        "transactionId=${result.transactionId}"
            )

            return result

        } catch (
            e: Exception
        ) {

            if (
                hasServerErrorCode(
                    e,
                    "MANUAL_REVIEW_REQUIRED"
                ) ||
                hasServerErrorCode(
                    e,
                    "UNEXPLAINED_CARD_STATE"
                )
            ) {

                throw IllegalStateException(
                    "La tarjeta presenta un estado que no puede " +
                            "recuperarse automáticamente.\n\n" +
                            "Acude con un administrador."
                )
            }

            throw e
        }
    }


    private fun resolveInterruptedTransactionOrContinue(
        card: MenesesCard,
        uid: String,
        operationLabel: String
    ) {

        val result =
            reconcileCustomerCard(
                card,
                uid
            )

        if (
            result.action ==
            "CONFIRMED_PENDING_TRANSACTION"
        ) {

            Log.w(
                "MENESES_RECONCILIATION",
                "Se confirmó una operación pendiente antes de $operationLabel. " +
                        "No se creará una segunda operación. " +
                        "transactionId=${result.transactionId}"
            )

            throw PreviousTransactionRecoveredException(
                result
            )
        }

        /*
         * FAILED_PENDING_TRANSACTION significa que la NFC seguía
         * exactamente en BEFORE, por lo que la operación anterior
         * nunca se escribió físicamente y es seguro intentar de nuevo.
         *
         * NONE significa que no había nada pendiente y el estado ya
         * coincide con PostgreSQL.
         */
    }


    private fun authorizeRechargeWithRecovery(
        card: MenesesCard,
        uid: String,
        amount: Long
    ): RechargeAuthorization {

        try {

            return MenesesApiClient
                .authorizeRecharge(
                    card.cardId,
                    uid,
                    amount,
                    card.balance,
                    card.transactionCounter
                )

        } catch (
            e: Exception
        ) {

            if (
                !hasServerErrorCode(
                    e,
                    "CARD_STATE_MISMATCH"
                ) &&
                !hasServerErrorCode(
                    e,
                    "CARD_HAS_PENDING_TRANSACTION"
                )
            ) {

                throw e
            }

            Log.w(
                "MENESES_RECONCILIATION",
                "CARD_STATE_MISMATCH en RECHARGE. " +
                        "Intentando reconciliación automática. " +
                        "cardId=${card.cardId}, " +
                        "balance=${card.balance}, " +
                        "counter=${card.transactionCounter}"
            )

            /*
             * Sólo se intenta reconciliar una vez.
             * Si el segundo AUTHORIZE vuelve a fallar, el error
             * sale hacia el flujo normal y la operación se detiene.
             */
            resolveInterruptedTransactionOrContinue(
                card =
                    card,
                uid =
                    uid,
                operationLabel =
                    "RECHARGE"
            )

            return MenesesApiClient
                .authorizeRecharge(
                    card.cardId,
                    uid,
                    amount,
                    card.balance,
                    card.transactionCounter
                )
        }
    }


    private fun authorizePromotionalRechargeWithRecovery(
        card: MenesesCard,
        uid: String,
        promotionId: String
    ): RechargeAuthorization {

        try {

            return PromotionApiClient
                .authorizePromotionalRecharge(

                    cardId =
                        card.cardId,

                    uid =
                        uid,

                    promotionId =
                        promotionId,

                    cardBalance =
                        card.balance,

                    cardCounter =
                        card.transactionCounter
                )

        } catch (
            e: Exception
        ) {

            if (
                !hasServerErrorCode(
                    e,
                    "CARD_STATE_MISMATCH"
                ) &&
                !hasServerErrorCode(
                    e,
                    "CARD_HAS_PENDING_TRANSACTION"
                )
            ) {

                throw e
            }

            Log.w(
                "MENESES_RECONCILIATION",

                "CARD_STATE_MISMATCH en PROMOTIONAL_RECHARGE. " +
                        "Intentando reconciliación automática. " +
                        "cardId=${card.cardId}, " +
                        "balance=${card.balance}, " +
                        "counter=${card.transactionCounter}, " +
                        "promotionId=$promotionId"
            )

            /*
             * Sólo se intenta reconciliar una vez.
             *
             * Si el segundo AUTHORIZE vuelve a fallar,
             * el error continúa por el flujo normal.
             */

            resolveInterruptedTransactionOrContinue(
                card =
                    card,
                uid =
                    uid,
                operationLabel =
                    "PROMOTIONAL_RECHARGE"
            )

            return PromotionApiClient
                .authorizePromotionalRecharge(

                    cardId =
                        card.cardId,

                    uid =
                        uid,

                    promotionId =
                        promotionId,

                    cardBalance =
                        card.balance,

                    cardCounter =
                        card.transactionCounter
                )
        }
    }


    private fun authorizeChargeWithRecovery(
        card: MenesesCard,
        uid: String,
        peopleCount: Int
    ): ChargeAuthorization {

        try {

            return MenesesApiClient
                .authorizeCharge(
                    card.cardId,
                    uid,
                    peopleCount,
                    card.balance,
                    card.transactionCounter
                )

        } catch (
            e: Exception
        ) {

            if (
                !hasServerErrorCode(
                    e,
                    "CARD_STATE_MISMATCH"
                ) &&
                !hasServerErrorCode(
                    e,
                    "CARD_HAS_PENDING_TRANSACTION"
                )
            ) {

                throw e
            }

            Log.w(
                "MENESES_RECONCILIATION",
                "CARD_STATE_MISMATCH en CHARGE. " +
                        "Intentando reconciliación automática. " +
                        "cardId=${card.cardId}, " +
                        "balance=${card.balance}, " +
                        "counter=${card.transactionCounter}"
            )

            /*
             * Sólo se intenta reconciliar una vez.
             */
            resolveInterruptedTransactionOrContinue(
                card =
                    card,
                uid =
                    uid,
                operationLabel =
                    "CHARGE"
            )

            return MenesesApiClient
                .authorizeCharge(
                    card.cardId,
                    uid,
                    peopleCount,
                    card.balance,
                    card.transactionCounter
                )
        }
    }


    private fun authorizeAdminRechargeWithRecovery(
        card: MenesesCard,
        uid: String,
        amount: Long
    ): RechargeAuthorization {

        try {

            return MenesesApiClient
                .authorizeAdminRecharge(
                    card.cardId,
                    uid,
                    amount,
                    card.balance,
                    card.transactionCounter
                )

        } catch (
            e: Exception
        ) {

            if (
                !hasServerErrorCode(
                    e,
                    "CARD_STATE_MISMATCH"
                ) &&
                !hasServerErrorCode(
                    e,
                    "CARD_HAS_PENDING_TRANSACTION"
                )
            ) {

                throw e
            }

            Log.w(
                "MENESES_RECONCILIATION",
                "Conflicto de estado en ADMIN_RECHARGE. " +
                        "Intentando reconciliación automática. " +
                        "cardId=${card.cardId}, " +
                        "balance=${card.balance}, " +
                        "counter=${card.transactionCounter}"
            )

            resolveInterruptedTransactionOrContinue(
                card =
                    card,
                uid =
                    uid,
                operationLabel =
                    "ADMIN_RECHARGE"
            )

            return MenesesApiClient
                .authorizeAdminRecharge(
                    card.cardId,
                    uid,
                    amount,
                    card.balance,
                    card.transactionCounter
                )
        }
    }


    private fun confirmTransactionWithRetry(
        transactionId: String,
        cardId: Long,
        uid: String,
        writtenBalance: Long,
        writtenCounter: Long
    ) {

        var lastError:
                Exception? =
            null

        repeat(
            3
        ) {
                attempt ->

            try {

                MenesesApiClient
                    .confirmTransaction(
                        transactionId,
                        cardId,
                        uid,
                        writtenBalance,
                        writtenCounter
                    )

                return

            } catch (
                e: Exception
            ) {

                /*
                 * Los errores HTTP válidos del backend llegan como
                 * IllegalStateException mediante serverException().
                 * No los reintentamos porque no son un problema de transporte.
                 */
                if (
                    e is IllegalStateException
                ) {

                    throw e
                }

                lastError =
                    e

                Log.w(
                    "MENESES_CONFIRM",
                    "Fallo confirmando transacción. " +
                            "Intento ${attempt + 1}/3. " +
                            "transactionId=$transactionId, " +
                            "error=${e.message}"
                )

                if (
                    attempt < 2
                ) {

                    try {

                        Thread.sleep(
                            350L *
                                    (attempt + 1)
                        )

                    } catch (
                        _: InterruptedException
                    ) {

                        Thread
                            .currentThread()
                            .interrupt()

                        throw e
                    }
                }
            }
        }

        throw lastError
            ?: IllegalStateException(
                "No fue posible confirmar la operación."
            )
    }


    private fun readCustomerCard(
        tag: Tag
    ): MenesesCard {

        val data =
            Ntag215Reader
                .readMenesesData(
                    tag
                )

        if (
            !MenesesCardCodec
                .isMenesesCard(
                    data
                )
        ) {

            throw IllegalArgumentException(
                "La tarjeta no está registrada."
            )
        }

        val card =
            MenesesCardCodec
                .decode(
                    data
                )

        if (
            card.type !=
            CardType.CUSTOMER
        ) {

            throw IllegalArgumentException(
                "La tarjeta no es CUSTOMER."
            )
        }

        if (
            card.status !=
            CardStatus.ACTIVE
        ) {

            throw IllegalArgumentException(
                "La tarjeta no está activa."
            )
        }

        return card
    }


    private fun validateAuthorizationBefore(
        card: MenesesCard,
        authorizedCardId: Long,
        authorizedBalance: Long,
        authorizedCounter: Long
    ) {

        if (
            card.cardId != authorizedCardId ||
            card.balance != authorizedBalance ||
            card.transactionCounter != authorizedCounter
        ) {

            throw IllegalStateException(
                "La autorización no coincide con la tarjeta física."
            )
        }
    }


    private fun verifyCard(
        tag: Tag,
        expected: MenesesCard
    ) {

        val data =
            Ntag215Reader
                .readMenesesData(
                    tag
                )

        if (
            !MenesesCardCodec
                .isMenesesCard(
                    data
                )
        ) {

            throw IllegalStateException(
                "La tarjeta no pudo verificarse."
            )
        }

        val actual =
            MenesesCardCodec
                .decode(
                    data
                )

        if (
            actual.cardId != expected.cardId ||
            actual.type != expected.type ||
            actual.status != expected.status ||
            actual.balance != expected.balance ||
            actual.transactionCounter != expected.transactionCounter
        ) {

            throw IllegalStateException(
                "La verificación NFC no coincide."
            )
        }
    }


    /*
     * =====================================================
     * LOGOUT
     * =====================================================
     */

    private fun logoutGame() {

        Thread {

            try {

                MenesesApiClient
                    .logoutGameSession()

                runOnUiThread {

                    gameSession =
                        null

                    cancelPendingOperation()
                }

            } catch (
                e: Exception
            ) {

                showError(
                    e.message
                        ?: "Error cerrando GAME."
                )
            }

        }.start()
    }


    private fun logoutRecharge() {

        Thread {

            try {

                MenesesApiClient
                    .logoutRechargeSession()

                runOnUiThread {

                    rechargeSession =
                        null

                    cancelPendingOperation()
                }

            } catch (
                e: Exception
            ) {

                showError(
                    e.message
                        ?: "Error cerrando TAQUILLA."
                )
            }

        }.start()
    }


    private fun logoutAdmin() {

        Thread {

            try {

                MenesesApiClient
                    .logoutAdminSession()

                runOnUiThread {

                    adminSession =
                        null

                    cancelPendingOperation()
                }

            } catch (
                e: Exception
            ) {

                showError(
                    e.message
                        ?: "Error cerrando ADMIN."
                )
            }

        }.start()
    }


    /*
     * =====================================================
     * TRANSACTION FAILURE
     * =====================================================
     */

    private fun handleTransactionFailure(
        transactionId: String?,
        cardWasWritten: Boolean,
        operationName: String,
        error: Exception
    ) {

        pendingOperation =
            NfcOperation.Read

        if (
            error is
                    PreviousTransactionRecoveredException
        ) {

            val recovery =
                error.reconciliation

            val recoveredMessage =
                when (operationName) {
                    "cobro" ->
                        "Transacción anterior recuperada exitosamente.\n\n" +
                                "El nuevo cobro NO fue realizado.\n" +
                                "Favor de volver a ejecutar la operación para cobrar la tarjeta.\n\n" +
                                "Saldo actual: \$${recovery.balance}\n" +
                                "Contador: ${recovery.transactionCounter}\n\n" +
                                "Transaction ID: ${recovery.transactionId ?: "N/D"}"

                    "recarga",
                    "recarga promocional" ->
                        "Transacción anterior recuperada exitosamente.\n\n" +
                                "La nueva recarga NO fue realizada.\n" +
                                "Favor de volver a ejecutar la operación para recargar la tarjeta.\n\n" +
                                "Saldo actual: \$${recovery.balance}\n" +
                                "Contador: ${recovery.transactionCounter}\n\n" +
                                "Transaction ID: ${recovery.transactionId ?: "N/D"}"

                    else ->
                        "Transacción anterior recuperada exitosamente.\n\n" +
                                "La nueva operación NO fue ejecutada.\n" +
                                "Favor de volver a ejecutar la operación.\n\n" +
                                "Saldo actual: \$${recovery.balance}\n" +
                                "Contador: ${recovery.transactionCounter}\n\n" +
                                "Transaction ID: ${recovery.transactionId ?: "N/D"}"
                }

            runOnUiThread {

                cardResult =
                    CardReadResult
                        .Success(
                            title =
                                "Transacción anterior recuperada",
                            message =
                                recoveredMessage
                        )
            }

            return
        }

        if (
            transactionId != null &&
            !cardWasWritten
        ) {

            try {

                MenesesApiClient
                    .failTransaction(

                        transactionId,

                        "Fallo durante $operationName: " +
                                (
                                        error.message
                                            ?: "ERROR"
                                        )
                    )

            } catch (
                _: Exception
            ) {
            }
        }

        if (
            transactionId != null &&
            cardWasWritten
        ) {

            showError(
                "La tarjeta pudo haber cambiado, " +
                        "pero el servidor no confirmó.\n\n" +
                        "NO repitas la operación.\n\n" +
                        "Transaction ID:\n$transactionId"
            )

            return
        }

        showError(
            error.message
                ?: "Error durante $operationName."
        )
    }


    private fun showError(
        message: String
    ) {

        /*
         * Los operadores nunca deberían ver mensajes técnicos de Android,
         * NFC o del backend. Centralizamos aquí las traducciones más comunes.
         */
        val userMessage =
            when {

                message.contains(
                    "Tag was lost",
                    ignoreCase = true
                ) ||
                        message.contains(
                            "TagLostException",
                            ignoreCase = true
                        ) ->
                    "Error al leer la tarjeta"

                message.contains(
                    "INSUFFICIENT_BALANCE",
                    ignoreCase = true
                ) ->
                    "SALDO INSUFICIENTE"

                else ->
                    message
            }

        runOnUiThread {

            cardResult =
                CardReadResult.Error(
                    userMessage
                )
        }
    }
}


/*
 * =========================================================
 * UI
 * =========================================================
 */

private enum class AdminPage {
    HOME,
    ADMIN_RECHARGE,
    ADMIN_ADJUSTMENT,
    NEW_GAME,
    NEW_RECHARGE_POINT,
    GAME_CARDS,
    RECHARGE_CARDS,
    EDIT_GAME,
    EDIT_RECHARGE_POINT,
    CARD_PRICE,
    PROMOTIONS,
    REPORTS,
    DEVICE_HISTORY
}

@Composable
fun MenesesHomeScreen(
    cardResult: CardReadResult,
    gameSession: GameSession?,
    rechargeSession: RechargeSession?,
    adminSession: AdminSession?,
    adminGames: List<AdminGame>,
    adminGamesLoading: Boolean,
    adminGameCreating: Boolean,
    adminRechargePoints: List<AdminRechargePoint>,
    adminRechargePointsLoading: Boolean,
    adminRechargePointCreating: Boolean,
    adminCashToday: Long?,
    adminCashLoading: Boolean,
    adminCardActivationFee: Long?,
    adminCardActivationFeeLoading: Boolean,
    adminCardActivationFeeSaving: Boolean,
    adminPromotions: List<AdminPromotion>,
    adminPromotionsLoading: Boolean,
    adminPromotionSaving: Boolean,
    adminPromotionsError: String?,
    adminReportSummary: AdminReportSummary?,
    adminGameReports: List<AdminGameReport>,
    adminRechargePointReports: List<AdminRechargePointReport>,
    adminGameDetailReport: AdminGameDetailReport?,
    adminRechargePointDetailReport: AdminRechargePointDetailReport?,
    adminReportDetailLoading: Boolean,
    adminDeviceReports: List<AdminDeviceReport>,
    adminReportsLoading: Boolean,
    adminReportsError: String?,
    adminReportsFrom: String,
    adminReportsTo: String,
    adminDeviceHistory: AdminDeviceHistory?,
    adminDeviceHistoryLoading: Boolean,
    adminDeviceHistoryError: String?,
    onLoadAdminPromotions: () -> Unit,
    onCreateAdminPromotion: (String, Long, Long) -> Unit,
    onSetAdminPromotionActive: (AdminPromotion, Boolean) -> Unit,
    onLoadAdminReports: (String, String) -> Unit,
    onLoadAdminReportsToday: () -> Unit,
    onLoadAdminReportsLast7Days: () -> Unit,
    onLoadAdminGameDetail: (String) -> Unit,
    onLoadAdminRechargePointDetail: (String) -> Unit,
    onClearAdminGameDetail: () -> Unit,
    onClearAdminRechargePointDetail: () -> Unit,
    onLoadAdminDeviceHistory: (String) -> Unit,
    onCreateGame: (String, Long) -> Unit,
    onCreateRechargePoint: (String) -> Unit,
    onUpdateGame: (AdminGame, String, Long) -> Unit,
    onUpdateRechargePoint: (AdminRechargePoint, String) -> Unit,
    onUpdateGameCardStatus: (AdminGame, String) -> Unit,
    onUpdateRechargePointCardStatus: (AdminRechargePoint, String) -> Unit,
    onInitializeCustomer: () -> Unit,
    onInitializeGame: () -> Unit,
    onInitializeRecharge: () -> Unit,
    onInitializeAdmin: () -> Unit,
    onCreateCustomer: () -> Unit,
    onRefreshCardActivationFee: () -> Unit,
    onUpdateCardActivationFee: (Long) -> Unit,
    onPrepareGameCard: (AdminGame) -> Unit,
    onRefreshGames: () -> Unit,
    onPrepareRechargePointCard: (AdminRechargePoint) -> Unit,
    onRefreshRechargePoints: () -> Unit,
    onPrepareRecharge: (Long) -> Unit,
    onPrepareCheckoutRecharge: (Long, RechargeCheckoutApiClient.PaymentMethod) -> Unit,
    onPreparePromotionalRecharge: (RechargePromotion, RechargeCheckoutApiClient.PaymentMethod) -> Unit,
    onPrepareAdminRecharge: (Long) -> Unit,
    onPrepareAdminAdjustment: (Long) -> Unit,
    onRefreshAdminCash: () -> Unit,
    onPrepareCharge: (Int) -> Unit,
    onPrepareBalance: () -> Unit,
    onPrepareHistory: () -> Unit,
    onPrepareCardReturn: () -> Unit,
    onCancelOperation: () -> Unit,
    onLogoutGame: () -> Unit,
    onLogoutRecharge: () -> Unit,
    onLogoutAdmin: () -> Unit,
    onReset: () -> Unit
) {
    var peopleCount by remember { mutableIntStateOf(1) }
    var selectedRechargeAmount by remember { mutableStateOf<Long?>(100) }
    var customAmountText by remember { mutableStateOf("") }
    var adminPage by remember { mutableStateOf(AdminPage.HOME) }
    var newGameName by remember { mutableStateOf("") }
    var newGamePriceText by remember { mutableStateOf("") }
    var newRechargePointName by remember { mutableStateOf("") }

    var selectedAdminGame by remember {
        mutableStateOf<AdminGame?>(null)
    }

    var selectedAdminRechargePoint by remember {
        mutableStateOf<AdminRechargePoint?>(null)
    }

    var selectedReportDevice by remember {
        mutableStateOf<AdminDeviceReport?>(null)
    }

    /*
     * Los resultados finales de una operación NFC ya no se descartan por
     * tiempo. Success y Error se presentan en un diálogo independiente y el
     * operador debe cerrarlo explícitamente antes de continuar.
     *
     * Los estados intermedios (esperando tarjeta, operación preparada,
     * procesando, etc.) permanecen dentro de la pantalla normal.
     */

    val scroll = rememberScrollState()

    /*
     * El contenedor principal conserva su ScrollState cuando
     * cambiamos entre el listado de reportes y una auditoría.
     * Al abrir el detalle de un juego o taquilla queremos empezar
     * siempre desde la parte superior de la nueva vista.
     */
    LaunchedEffect(
        adminGameDetailReport?.game?.gameId,
        adminRechargePointDetailReport
            ?.rechargePoint
            ?.rechargePointId
    ) {
        if (
            adminGameDetailReport != null ||
            adminRechargePointDetailReport != null
        ) {
            scroll.scrollTo(0)
        }
    }

    val operationArmed =
        cardResult is CardReadResult.WaitingForCharge ||
                cardResult is CardReadResult.WaitingForRecharge ||
                cardResult is CardReadResult.WaitingForBalance ||
                cardResult is CardReadResult.WaitingForHistory ||
                cardResult is CardReadResult.WaitingForDevCard ||
                adminGameCreating ||
                adminRechargePointCreating

    val gameNfcWaiting =
        gameSession != null &&
                (
                        cardResult is CardReadResult.WaitingForBalance ||
                                cardResult is CardReadResult.WaitingForCharge
                        )

    /*
     * TAQUILLA utiliza la misma experiencia de espera NFC dedicada.
     * Solo entramos aquí para operaciones propias de taquilla.
     */
    val rechargeNfcWaiting =
        rechargeSession != null &&
                (
                        cardResult is CardReadResult.WaitingForRecharge ||
                                cardResult is CardReadResult.WaitingForBalance ||
                                cardResult is CardReadResult.WaitingForHistory ||
                                cardResult is CardReadResult.WaitingForDevCard
                        )

    val activeGames = adminGames.count { it.status == "ACTIVE" }
    val activeRechargePoints = adminRechargePoints.count { it.status == "ACTIVE" }

    val hasActiveSession =
        adminSession != null ||
                gameSession != null ||
                rechargeSession != null

    val sessionLabel =
        when {
            adminSession != null -> "Panel Administrativo"
            gameSession != null -> gameSession.gameName
            rechargeSession != null -> rechargeSession.rechargePointName
            else -> ""
        }

    val sessionType =
        when {
            adminSession != null -> "ADMINISTRADOR"
            gameSession != null -> "JUEGO"
            rechargeSession != null -> "TAQUILLA"
            else -> ""
        }

    /*
     * La identidad de la sesión forma parte de la clave del
     * DrawerState. Cuando cambia ADMIN / GAME / TAQUILLA,
     * Compose crea inmediatamente un DrawerState NUEVO que
     * comienza cerrado. Así evitamos incluso el micro-flash
     * del drawer anterior antes de ejecutar una animación de cierre.
     */
    val activeSessionKey =
        when {
            adminSession != null ->
                "ADMIN:${adminSession.adminCardId}"

            gameSession != null ->
                "GAME:${gameSession.sessionId}"

            rechargeSession != null ->
                "RECHARGE:${rechargeSession.sessionId}"

            else ->
                "NONE"
        }

    val drawerState =
        key(activeSessionKey) {
            rememberDrawerState(
                initialValue = DrawerValue.Closed
            )
        }

    val drawerScope =
        rememberCoroutineScope()

    OperationResultDialog(
        cardResult = cardResult,
        onClose = onReset
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = hasActiveSession,
        drawerContent = {
            if (hasActiveSession) {
                ModalDrawerSheet(
                    modifier =
                        Modifier.width(290.dp),
                    drawerContainerColor =
                        MenesesSurface
                ) {
                    SessionDrawerContent(
                        sessionType = sessionType,
                        sessionLabel = sessionLabel,
                        operationArmed = operationArmed,
                        showAdminNavigation =
                            adminSession != null,
                        currentAdminPage =
                            adminPage,
                        onAdminHome = {
                            drawerScope.launch {
                                drawerState.close()
                            }

                            adminPage =
                                AdminPage.HOME
                        },
                        onAdminPromotions = {
                            drawerScope.launch {
                                drawerState.close()
                            }

                            adminPage = AdminPage.PROMOTIONS
                            onLoadAdminPromotions()
                        },
                        onAdminReports = {
                            drawerScope.launch {
                                drawerState.close()
                            }

                            selectedReportDevice =
                                null

                            adminPage =
                                AdminPage.REPORTS

                            if (
                                adminReportsFrom.isBlank() ||
                                adminReportsTo.isBlank()
                            ) {
                                onLoadAdminReportsToday()
                            }
                        },
                        onCloseDrawer = {
                            drawerScope.launch {
                                drawerState.close()
                            }
                        },
                        onLogout = {
                            drawerScope.launch {
                                drawerState.close()
                            }

                            when {
                                adminSession != null ->
                                    onLogoutAdmin()

                                gameSession != null ->
                                    onLogoutGame()

                                rechargeSession != null ->
                                    onLogoutRecharge()
                            }
                        }
                    )
                }
            } else {
                Box(
                    modifier =
                        Modifier.width(1.dp)
                )
            }
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (!hasActiveSession) {
                /*
                 * Sin una sesión activa dejamos la bienvenida
                 * verdaderamente centrada en toda la pantalla.
                 */
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp, vertical = 24.dp),
                    contentAlignment =
                        Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        WelcomeCard()

                        NfcResultArea(
                            cardResult = cardResult,
                            onCancelOperation = onCancelOperation,
                            onReset = onReset
                        )
                    }
                }
            } else {
                if (gameNfcWaiting && gameSession != null) {
                    GameNfcWaitingPage(
                        cardResult = cardResult,
                        onBack = onCancelOperation
                    )
                } else if (rechargeNfcWaiting && rechargeSession != null) {
                    RechargeNfcWaitingPage(
                        cardResult = cardResult,
                        onBack = onCancelOperation
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scroll)
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SessionMenuButton(
                            onClick = {
                                drawerScope.launch {
                                    drawerState.open()
                                }
                            }
                        )

                        when {
                            adminSession != null -> {
                                AdminDashboard(
                                    adminPage = adminPage,
                                    onAdminPageChange = { adminPage = it },
                                    activeGames = activeGames,
                                    activeRechargePoints = activeRechargePoints,
                                    adminCashToday = adminCashToday,
                                    adminCashLoading = adminCashLoading,
                                    adminCardActivationFee = adminCardActivationFee,
                                    adminCardActivationFeeLoading = adminCardActivationFeeLoading,
                                    adminCardActivationFeeSaving = adminCardActivationFeeSaving,
                                    adminPromotions = adminPromotions,
                                    adminPromotionsLoading = adminPromotionsLoading,
                                    adminPromotionSaving = adminPromotionSaving,
                                    adminPromotionsError = adminPromotionsError,
                                    adminReportSummary = adminReportSummary,
                                    adminGameReports = adminGameReports,
                                    adminRechargePointReports = adminRechargePointReports,
                                    adminGameDetailReport = adminGameDetailReport,
                                    adminRechargePointDetailReport = adminRechargePointDetailReport,
                                    adminReportDetailLoading = adminReportDetailLoading,
                                    adminDeviceReports = adminDeviceReports,
                                    adminReportsLoading = adminReportsLoading,
                                    adminReportsError = adminReportsError,
                                    adminReportsFrom = adminReportsFrom,
                                    adminReportsTo = adminReportsTo,
                                    adminDeviceHistory = adminDeviceHistory,
                                    adminDeviceHistoryLoading = adminDeviceHistoryLoading,
                                    adminDeviceHistoryError = adminDeviceHistoryError,
                                    selectedReportDevice = selectedReportDevice,
                                    onSelectReportDevice = { device ->
                                        selectedReportDevice = device
                                        adminPage = AdminPage.DEVICE_HISTORY
                                        onLoadAdminDeviceHistory(
                                            device.deviceId
                                        )
                                    },
                                    onLoadAdminDeviceHistory = {
                                        selectedReportDevice
                                            ?.deviceId
                                            ?.let {
                                                onLoadAdminDeviceHistory(
                                                    it
                                                )
                                            }
                                    },
                                    onLoadAdminPromotions = onLoadAdminPromotions,
                                    onCreateAdminPromotion = onCreateAdminPromotion,
                                    onSetAdminPromotionActive = onSetAdminPromotionActive,
                                    onLoadAdminReports = onLoadAdminReports,
                                    onLoadAdminReportsToday = onLoadAdminReportsToday,
                                    onLoadAdminReportsLast7Days = onLoadAdminReportsLast7Days,
                                    onLoadAdminGameDetail = onLoadAdminGameDetail,
                                    onLoadAdminRechargePointDetail = onLoadAdminRechargePointDetail,
                                    onClearAdminGameDetail = onClearAdminGameDetail,
                                    onClearAdminRechargePointDetail = onClearAdminRechargePointDetail,
                                    adminGames = adminGames,
                                    adminGamesLoading = adminGamesLoading,
                                    adminGameCreating = adminGameCreating,
                                    adminRechargePoints = adminRechargePoints,
                                    adminRechargePointsLoading = adminRechargePointsLoading,
                                    adminRechargePointCreating = adminRechargePointCreating,
                                    cardResult = cardResult,
                                    onCancelOperation = onCancelOperation,
                                    onReset = onReset,
                                    newGameName = newGameName,
                                    onNewGameNameChange = { newGameName = it },
                                    newGamePriceText = newGamePriceText,
                                    onNewGamePriceChange = { newGamePriceText = it.filter(Char::isDigit) },
                                    newRechargePointName = newRechargePointName,
                                    onNewRechargePointNameChange = { newRechargePointName = it },
                                    operationArmed = operationArmed,
                                    onCreateGame = { name, price ->
                                        onCreateGame(name, price)
                                        newGameName = ""
                                        newGamePriceText = ""
                                        adminPage = AdminPage.HOME
                                    },
                                    onCreateRechargePoint = { name ->
                                        onCreateRechargePoint(name)
                                        newRechargePointName = ""
                                        adminPage = AdminPage.HOME
                                    },
                                    selectedAdminGame = selectedAdminGame,
                                    selectedAdminRechargePoint = selectedAdminRechargePoint,
                                    onSelectGameForEdit = { game ->
                                        selectedAdminGame = game
                                        adminPage = AdminPage.EDIT_GAME
                                    },
                                    onSelectRechargePointForEdit = { rechargePoint ->
                                        selectedAdminRechargePoint = rechargePoint
                                        adminPage = AdminPage.EDIT_RECHARGE_POINT
                                    },
                                    onUpdateGame = { game, name, price ->
                                        onUpdateGame(game, name, price)
                                        selectedAdminGame = null
                                        adminPage = AdminPage.GAME_CARDS
                                    },
                                    onUpdateRechargePoint = { rechargePoint, name ->
                                        onUpdateRechargePoint(rechargePoint, name)
                                        selectedAdminRechargePoint = null
                                        adminPage = AdminPage.RECHARGE_CARDS
                                    },
                                    onUpdateGameCardStatus = { game, status ->
                                        onUpdateGameCardStatus(game, status)
                                        selectedAdminGame = null
                                        adminPage = AdminPage.GAME_CARDS
                                    },
                                    onUpdateRechargePointCardStatus = { rechargePoint, status ->
                                        onUpdateRechargePointCardStatus(rechargePoint, status)
                                        selectedAdminRechargePoint = null
                                        adminPage = AdminPage.RECHARGE_CARDS
                                    },
                                    onCreateCustomer = onCreateCustomer,
                                    onRefreshCardActivationFee = onRefreshCardActivationFee,
                                    onUpdateCardActivationFee = onUpdateCardActivationFee,
                                    onPrepareAdminRecharge = onPrepareAdminRecharge,
                                    onPrepareAdminAdjustment = onPrepareAdminAdjustment,
                                    onRefreshAdminCash = onRefreshAdminCash,
                                    onPrepareBalance = onPrepareBalance,
                                    onPrepareHistory = onPrepareHistory,
                                    onPrepareGameCard = onPrepareGameCard,
                                    onPrepareRechargePointCard = onPrepareRechargePointCard,
                                    onRefreshGames = onRefreshGames,
                                    onRefreshRechargePoints = onRefreshRechargePoints,
                                    onLogoutAdmin = onLogoutAdmin
                                )
                            }

                            gameSession != null -> {
                                GameDashboard(
                                    gameSession = gameSession,
                                    peopleCount = peopleCount,
                                    onPeopleCountChange = { peopleCount = it },
                                    operationArmed = operationArmed,
                                    onPrepareCharge = onPrepareCharge,
                                    onPrepareBalance = onPrepareBalance,
                                    onLogoutGame = onLogoutGame
                                )
                            }

                            rechargeSession != null -> {
                                RechargeDashboard(
                                    rechargeSession = rechargeSession,
                                    selectedRechargeAmount = selectedRechargeAmount,
                                    onSelectedRechargeAmountChange = { selectedRechargeAmount = it },
                                    customAmountText = customAmountText,
                                    onCustomAmountTextChange = { value ->
                                        val filtered = value.filter(Char::isDigit)
                                        customAmountText = filtered
                                        selectedRechargeAmount = filtered.toLongOrNull()
                                    },
                                    operationArmed = operationArmed,
                                    onPrepareRecharge = onPrepareRecharge,
                                    onPrepareCheckoutRecharge = onPrepareCheckoutRecharge,
                                    onPreparePromotionalRecharge = onPreparePromotionalRecharge,
                                    onPrepareBalance = onPrepareBalance,
                                    onPrepareHistory = onPrepareHistory,
                                    onPrepareCardReturn = onPrepareCardReturn,
                                    onCreateCustomer = onCreateCustomer,
                                    onLogoutRecharge = onLogoutRecharge
                                )
                            }
                        }

                        /*
                         * En ADMIN, las operaciones de dinero muestran su resultado
                         * dentro de su propia pantalla. Al volver a HOME no dejamos
                         * esos mensajes pegados al final del dashboard.
                         *
                         * Las demás respuestas de ADMIN (saldo, historial, creación,
                         * errores, etc.) continúan mostrándose normalmente en HOME.
                         */
                        if (adminSession != null) {
                            if (adminPage == AdminPage.HOME) {
                                val isAdminMoneySuccess =
                                    cardResult is CardReadResult.Success &&
                                            (
                                                    cardResult.title == "Recarga realizada" ||
                                                            cardResult.title == "Saldo retirado"
                                                    )

                                if (!isAdminMoneySuccess) {
                                    NfcResultArea(
                                        cardResult = cardResult,
                                        onCancelOperation = onCancelOperation,
                                        onReset = onReset
                                    )
                                }
                            }
                        } else {
                            NfcResultArea(
                                cardResult = cardResult,
                                onCancelOperation = onCancelOperation,
                                onReset = onReset
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionMenuButton(
    onClick: () -> Unit
) {
    Row(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.Start
    ) {
        OutlinedButton(
            modifier =
                Modifier.size(46.dp),
            onClick =
                onClick,
            shape =
                RoundedCornerShape(14.dp),
            contentPadding =
                PaddingValues(0.dp)
        ) {
            Text(
                text = "☰",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SessionDrawerContent(
    sessionType: String,
    sessionLabel: String,
    operationArmed: Boolean,
    showAdminNavigation: Boolean,
    currentAdminPage: AdminPage,
    onAdminHome: () -> Unit,
    onAdminPromotions: () -> Unit,
    onAdminReports: () -> Unit,
    onCloseDrawer: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(22.dp),
        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier =
                Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = "Menú",
                    style =
                        MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = sessionType,
                    color = MenesesTextSecondary,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            OutlinedButton(
                onClick = onCloseDrawer
            ) {
                Text("Cerrar")
            }
        }

        Card(
            modifier =
                Modifier.fillMaxWidth(),
            shape =
                RoundedCornerShape(20.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.background
                )
        ) {
            Column(
                modifier =
                    Modifier.padding(16.dp),
                verticalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Sesión actual",
                    color = MenesesTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = sessionLabel,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        if (
            showAdminNavigation
        ) {
            HorizontalDivider(
                color =
                    MenesesBorder
            )

            val isPromotionsSelected =
                currentAdminPage == AdminPage.PROMOTIONS

            val isReportsSelected =
                currentAdminPage ==
                        AdminPage.REPORTS ||
                        currentAdminPage ==
                        AdminPage.DEVICE_HISTORY

            val isHomeSelected =
                !isPromotionsSelected && !isReportsSelected

            if (isHomeSelected) {
                Button(
                    modifier =
                        Modifier.fillMaxWidth(),
                    enabled =
                        !operationArmed,
                    onClick =
                        onAdminHome,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                MenesesPurple
                        )
                ) {
                    Text(
                        "🏠 Panel administrativo"
                    )
                }
            } else {
                OutlinedButton(
                    modifier =
                        Modifier.fillMaxWidth(),
                    enabled =
                        !operationArmed,
                    onClick =
                        onAdminHome
                ) {
                    Text(
                        "🏠 Panel administrativo"
                    )
                }
            }

            if (isPromotionsSelected) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !operationArmed,
                    onClick = onAdminPromotions,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MenesesPurple
                        )
                ) {
                    Text("🎁 Promociones")
                }
            } else {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !operationArmed,
                    onClick = onAdminPromotions
                ) {
                    Text("🎁 Promociones")
                }
            }

            if (isReportsSelected) {
                Button(
                    modifier =
                        Modifier.fillMaxWidth(),
                    enabled =
                        !operationArmed,
                    onClick =
                        onAdminReports,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                MenesesPurple
                        )
                ) {
                    Text(
                        "📊 Reportes"
                    )
                }
            } else {
                OutlinedButton(
                    modifier =
                        Modifier.fillMaxWidth(),
                    enabled =
                        !operationArmed,
                    onClick =
                        onAdminReports
                ) {
                    Text(
                        "📊 Reportes"
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.weight(1f)
        )

        if (operationArmed) {
            Text(
                text =
                    "Finaliza o cancela la operación NFC antes de cerrar la sesión.",
                color = MenesesTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        DestructiveButton(
            text = "Cerrar sesión",
            enabled = !operationArmed,
            onClick = onLogout
        )
    }
}

@Composable
private fun AdminDashboard(
    adminPage: AdminPage,
    onAdminPageChange: (AdminPage) -> Unit,
    activeGames: Int,
    activeRechargePoints: Int,
    adminCashToday: Long?,
    adminCashLoading: Boolean,
    adminCardActivationFee: Long?,
    adminCardActivationFeeLoading: Boolean,
    adminCardActivationFeeSaving: Boolean,
    adminPromotions: List<AdminPromotion>,
    adminPromotionsLoading: Boolean,
    adminPromotionSaving: Boolean,
    adminPromotionsError: String?,
    adminReportSummary: AdminReportSummary?,
    adminGameReports: List<AdminGameReport>,
    adminRechargePointReports: List<AdminRechargePointReport>,
    adminGameDetailReport: AdminGameDetailReport?,
    adminRechargePointDetailReport: AdminRechargePointDetailReport?,
    adminReportDetailLoading: Boolean,
    adminDeviceReports: List<AdminDeviceReport>,
    adminReportsLoading: Boolean,
    adminReportsError: String?,
    adminReportsFrom: String,
    adminReportsTo: String,
    adminDeviceHistory: AdminDeviceHistory?,
    adminDeviceHistoryLoading: Boolean,
    adminDeviceHistoryError: String?,
    selectedReportDevice: AdminDeviceReport?,
    onSelectReportDevice: (AdminDeviceReport) -> Unit,
    onLoadAdminDeviceHistory: () -> Unit,
    onLoadAdminPromotions: () -> Unit,
    onCreateAdminPromotion: (String, Long, Long) -> Unit,
    onSetAdminPromotionActive: (AdminPromotion, Boolean) -> Unit,
    onLoadAdminReports: (String, String) -> Unit,
    onLoadAdminReportsToday: () -> Unit,
    onLoadAdminReportsLast7Days: () -> Unit,
    onLoadAdminGameDetail: (String) -> Unit,
    onLoadAdminRechargePointDetail: (String) -> Unit,
    onClearAdminGameDetail: () -> Unit,
    onClearAdminRechargePointDetail: () -> Unit,
    adminGames: List<AdminGame>,
    adminGamesLoading: Boolean,
    adminGameCreating: Boolean,
    adminRechargePoints: List<AdminRechargePoint>,
    adminRechargePointsLoading: Boolean,
    adminRechargePointCreating: Boolean,
    cardResult: CardReadResult,
    onCancelOperation: () -> Unit,
    onReset: () -> Unit,
    newGameName: String,
    onNewGameNameChange: (String) -> Unit,
    newGamePriceText: String,
    onNewGamePriceChange: (String) -> Unit,
    newRechargePointName: String,
    onNewRechargePointNameChange: (String) -> Unit,
    operationArmed: Boolean,
    onCreateGame: (String, Long) -> Unit,
    onCreateRechargePoint: (String) -> Unit,
    selectedAdminGame: AdminGame?,
    selectedAdminRechargePoint: AdminRechargePoint?,
    onSelectGameForEdit: (AdminGame) -> Unit,
    onSelectRechargePointForEdit: (AdminRechargePoint) -> Unit,
    onUpdateGame: (AdminGame, String, Long) -> Unit,
    onUpdateRechargePoint: (AdminRechargePoint, String) -> Unit,
    onUpdateGameCardStatus: (AdminGame, String) -> Unit,
    onUpdateRechargePointCardStatus: (AdminRechargePoint, String) -> Unit,
    onCreateCustomer: () -> Unit,
    onRefreshCardActivationFee: () -> Unit,
    onUpdateCardActivationFee: (Long) -> Unit,
    onPrepareAdminRecharge: (Long) -> Unit,
    onPrepareAdminAdjustment: (Long) -> Unit,
    onRefreshAdminCash: () -> Unit,
    onPrepareBalance: () -> Unit,
    onPrepareHistory: () -> Unit,
    onPrepareGameCard: (AdminGame) -> Unit,
    onPrepareRechargePointCard: (AdminRechargePoint) -> Unit,
    onRefreshGames: () -> Unit,
    onRefreshRechargePoints: () -> Unit,
    onLogoutAdmin: () -> Unit
) {
    when (adminPage) {
        AdminPage.HOME -> {
            AdminHome(
                activeGames = activeGames,
                activeRechargePoints = activeRechargePoints,
                adminCashToday = adminCashToday,
                adminCashLoading = adminCashLoading,
                adminCardActivationFee = adminCardActivationFee,
                adminCardActivationFeeLoading = adminCardActivationFeeLoading,
                operationArmed = operationArmed,
                onNewGame = { onAdminPageChange(AdminPage.NEW_GAME) },
                onNewRechargePoint = { onAdminPageChange(AdminPage.NEW_RECHARGE_POINT) },
                onGameCards = { onAdminPageChange(AdminPage.GAME_CARDS) },
                onRechargeCards = { onAdminPageChange(AdminPage.RECHARGE_CARDS) },
                onCreateCustomer = onCreateCustomer,
                onCardPrice = {
                    onRefreshCardActivationFee()
                    onAdminPageChange(AdminPage.CARD_PRICE)
                },
                onAdminRecharge = { onAdminPageChange(AdminPage.ADMIN_RECHARGE) },
                onAdminAdjustment = { onAdminPageChange(AdminPage.ADMIN_ADJUSTMENT) },
                onRefreshAdminCash = onRefreshAdminCash,
                onPrepareBalance = onPrepareBalance,
                onPrepareHistory = onPrepareHistory,
                onLogoutAdmin = onLogoutAdmin
            )
        }

        AdminPage.ADMIN_RECHARGE -> {
            AdminMoneyOperationPage(
                title = "💳 Recargar",
                description = "Selecciona el monto que deseas agregar a la tarjeta del cliente.",
                actionLabel = "PREPARAR RECARGA",
                accent = MenesesGreen,
                operationArmed = operationArmed,
                cardResult = cardResult,
                onCancelOperation = onCancelOperation,
                onReset = onReset,
                onBack = {
                    onCancelOperation()
                    onAdminPageChange(AdminPage.HOME)
                },
                onPrepare = onPrepareAdminRecharge
            )
        }

        AdminPage.ADMIN_ADJUSTMENT -> {
            AdminMoneyOperationPage(
                title = "➖ Quitar saldo",
                description = "Selecciona el monto que deseas retirar de la tarjeta del cliente.",
                actionLabel = "PREPARAR AJUSTE",
                accent = MenesesError,
                operationArmed = operationArmed,
                cardResult = cardResult,
                onCancelOperation = onCancelOperation,
                onReset = onReset,
                onBack = {
                    onCancelOperation()
                    onAdminPageChange(AdminPage.HOME)
                },
                onPrepare = onPrepareAdminAdjustment
            )
        }

        AdminPage.NEW_GAME -> {
            SubPageHeader("🎠 Nuevo juego") { onAdminPageChange(AdminPage.HOME) }
            FormCard {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = newGameName,
                    onValueChange = onNewGameNameChange,
                    enabled = !adminGameCreating,
                    label = { Text("Nombre del juego") },
                    singleLine = true
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = newGamePriceText,
                    onValueChange = onNewGamePriceChange,
                    enabled = !adminGameCreating,
                    label = { Text("Precio por persona") },
                    prefix = { Text("$") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                PrimaryGreenButton(
                    text = if (adminGameCreating) "Creando..." else "CREAR JUEGO",
                    enabled = !adminGameCreating &&
                            newGameName.trim().length >= 2 &&
                            (newGamePriceText.toLongOrNull() ?: 0) > 0,
                    onClick = {
                        newGamePriceText.toLongOrNull()?.let { price ->
                            onCreateGame(
                                newGameName.trim(),
                                price
                            )
                        }
                    }
                )
            }
        }

        AdminPage.NEW_RECHARGE_POINT -> {
            SubPageHeader("🏪 Nueva taquilla") { onAdminPageChange(AdminPage.HOME) }
            FormCard {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = newRechargePointName,
                    onValueChange = onNewRechargePointNameChange,
                    enabled = !adminRechargePointCreating,
                    label = { Text("Nombre de la taquilla") },
                    singleLine = true
                )
                PrimaryGreenButton(
                    text = if (adminRechargePointCreating) "Creando..." else "CREAR TAQUILLA",
                    enabled = !adminRechargePointCreating && newRechargePointName.trim().length >= 2,
                    onClick = { onCreateRechargePoint(newRechargePointName.trim()) }
                )
            }
        }

        AdminPage.GAME_CARDS -> {

            SubPageHeader(
                "🎟️ Tarjetas de juego"
            ) {
                onAdminPageChange(
                    AdminPage.HOME
                )
            }


            /*
             * =====================================================
             * NFC ARMADO PARA CREAR TARJETA GAME
             * =====================================================
             *
             * Si el operador ya presionó CREAR TARJETA GAME,
             * mostramos el estado NFC aquí mismo y ocultamos
             * temporalmente el listado.
             * =====================================================
             */

            if (
                operationArmed &&
                cardResult is CardReadResult.WaitingForDevCard
            ) {

                NfcResultArea(
                    cardResult =
                        cardResult,

                    onCancelOperation =
                        onCancelOperation,

                    onReset =
                        onReset
                )

            } else {

                ManagementInfoCard(
                    "Consulta tus juegos, precio y tarjeta asignada. " +
                            "Toca EDITAR para modificar nombre o precio."
                )


                OutlinedButton(
                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled =
                        !operationArmed &&
                                !adminGamesLoading,

                    onClick =
                        onRefreshGames
                ) {

                    Text(
                        if (
                            adminGamesLoading
                        ) {
                            "Actualizando..."
                        } else {
                            "Actualizar"
                        }
                    )
                }


                if (
                    adminGamesLoading
                ) {

                    Text(
                        "Cargando juegos...",
                        color =
                            MenesesTextSecondary
                    )

                } else if (
                    adminGames.isEmpty()
                ) {

                    Text(
                        "No hay juegos registrados.",
                        color =
                            MenesesTextSecondary
                    )

                } else {

                    adminGames.forEach {
                            game ->

                        AdminGameCardView(
                            game =
                                game,

                            operationArmed =
                                operationArmed,

                            onPrepareGameCard = {
                                onPrepareGameCard(
                                    game
                                )
                            },

                            onEdit = {
                                onSelectGameForEdit(
                                    game
                                )
                            }
                        )
                    }
                }
            }
        }

        AdminPage.RECHARGE_CARDS -> {

            SubPageHeader(
                "💳 Tarjetas de taquilla"
            ) {
                onAdminPageChange(
                    AdminPage.HOME
                )
            }


            /*
             * =====================================================
             * NFC ARMADO PARA CREAR TARJETA TAQUILLA
             * =====================================================
             */

            if (
                operationArmed &&
                cardResult is CardReadResult.WaitingForDevCard
            ) {

                NfcResultArea(
                    cardResult =
                        cardResult,

                    onCancelOperation =
                        onCancelOperation,

                    onReset =
                        onReset
                )

            } else {

                ManagementInfoCard(
                    "Consulta las taquillas y su tarjeta asignada. " +
                            "Toca EDITAR para modificar su nombre."
                )


                OutlinedButton(
                    modifier =
                        Modifier.fillMaxWidth(),

                    enabled =
                        !operationArmed &&
                                !adminRechargePointsLoading,

                    onClick =
                        onRefreshRechargePoints
                ) {

                    Text(
                        if (
                            adminRechargePointsLoading
                        ) {
                            "Actualizando..."
                        } else {
                            "Actualizar"
                        }
                    )
                }


                if (
                    adminRechargePointsLoading
                ) {

                    Text(
                        "Cargando taquillas...",
                        color =
                            MenesesTextSecondary
                    )

                } else if (
                    adminRechargePoints.isEmpty()
                ) {

                    Text(
                        "No hay taquillas registradas.",
                        color =
                            MenesesTextSecondary
                    )

                } else {

                    adminRechargePoints.forEach {
                            rechargePoint ->

                        AdminRechargePointCardView(
                            rechargePoint =
                                rechargePoint,

                            operationArmed =
                                operationArmed,

                            onPrepareRechargePointCard = {
                                onPrepareRechargePointCard(
                                    rechargePoint
                                )
                            },

                            onEdit = {
                                onSelectRechargePointForEdit(
                                    rechargePoint
                                )
                            }
                        )
                    }
                }
            }
        }

        AdminPage.EDIT_GAME -> {
            val game =
                selectedAdminGame

            if (game == null) {
                LaunchedEffect(Unit) {
                    onAdminPageChange(
                        AdminPage.GAME_CARDS
                    )
                }
            } else {
                EditGamePage(
                    game = game,
                    saving = adminGameCreating,
                    onBack = {
                        onAdminPageChange(
                            AdminPage.GAME_CARDS
                        )
                    },
                    onSave = { name, price ->
                        onUpdateGame(
                            game,
                            name,
                            price
                        )
                    },
                    onChangeCardStatus = { status ->
                        onUpdateGameCardStatus(
                            game,
                            status
                        )
                    }
                )
            }
        }

        AdminPage.CARD_PRICE -> {
            CardActivationFeePage(
                currentFee =
                    adminCardActivationFee,
                loading =
                    adminCardActivationFeeLoading,
                saving =
                    adminCardActivationFeeSaving,
                onRefresh =
                    onRefreshCardActivationFee,
                onBack = {
                    onAdminPageChange(
                        AdminPage.HOME
                    )
                },
                onSave = { amount ->
                    onUpdateCardActivationFee(
                        amount
                    )
                }
            )
        }


        AdminPage.PROMOTIONS -> {

            AdminPromotionsScreen(
                promotions = adminPromotions,
                loading = adminPromotionsLoading,
                saving = adminPromotionSaving,
                errorMessage = adminPromotionsError,
                onBack = {
                    onAdminPageChange(AdminPage.HOME)
                },
                onRefresh = onLoadAdminPromotions,
                onCreatePromotion = {
                        name,
                        cashAmount,
                        promotionalAmount ->

                    onCreateAdminPromotion(
                        name,
                        cashAmount,
                        promotionalAmount
                    )
                },
                onSetPromotionActive = { promotion, active ->
                    onSetAdminPromotionActive(
                        promotion,
                        active
                    )
                }
            )
        }


        AdminPage.REPORTS -> {

            AdminReportsScreen(
                from =
                    adminReportsFrom,
                to =
                    adminReportsTo,
                summary =
                    adminReportSummary,
                games =
                    adminGameReports,
                rechargePoints =
                    adminRechargePointReports,
                devices =
                    adminDeviceReports,
                loading =
                    adminReportsLoading,
                errorMessage =
                    adminReportsError,
                onBack = {
                    onAdminPageChange(
                        AdminPage.HOME
                    )
                },
                onRefresh = {
                    if (
                        adminReportsFrom.isNotBlank() &&
                        adminReportsTo.isNotBlank()
                    ) {
                        onLoadAdminReports(
                            adminReportsFrom,
                            adminReportsTo
                        )
                    }
                },
                onToday =
                    onLoadAdminReportsToday,
                onLast7Days =
                    onLoadAdminReportsLast7Days,
                onApplyCustomRange = {
                        from,
                        to ->

                    onLoadAdminReports(
                        from,
                        to
                    )
                },
                onViewDeviceHistory = {
                        device ->

                    onSelectReportDevice(
                        device
                    )
                },
                selectedGameDetail =
                    adminGameDetailReport,
                selectedRechargePointDetail =
                    adminRechargePointDetailReport,
                detailLoading =
                    adminReportDetailLoading,
                onViewGameDetail = { game ->
                    onLoadAdminGameDetail(
                        game.gameId
                    )
                },
                onViewRechargePointDetail = { rechargePoint ->
                    onLoadAdminRechargePointDetail(
                        rechargePoint.rechargePointId
                    )
                },
                onCloseGameDetail =
                    onClearAdminGameDetail,
                onCloseRechargePointDetail =
                    onClearAdminRechargePointDetail
            )
        }


        AdminPage.DEVICE_HISTORY -> {

            AdminDeviceHistoryScreen(
                selectedDevice =
                    selectedReportDevice,
                history =
                    adminDeviceHistory,
                loading =
                    adminDeviceHistoryLoading,
                errorMessage =
                    adminDeviceHistoryError,
                onBack = {
                    onAdminPageChange(
                        AdminPage.REPORTS
                    )
                },
                onRetry =
                    onLoadAdminDeviceHistory
            )
        }


        AdminPage.EDIT_RECHARGE_POINT -> {
            val rechargePoint =
                selectedAdminRechargePoint

            if (rechargePoint == null) {
                LaunchedEffect(Unit) {
                    onAdminPageChange(
                        AdminPage.RECHARGE_CARDS
                    )
                }
            } else {
                EditRechargePointPage(
                    rechargePoint =
                        rechargePoint,
                    saving =
                        adminRechargePointCreating,
                    onBack = {
                        onAdminPageChange(
                            AdminPage.RECHARGE_CARDS
                        )
                    },
                    onSave = { name ->
                        onUpdateRechargePoint(
                            rechargePoint,
                            name
                        )
                    },
                    onChangeCardStatus = { status ->
                        onUpdateRechargePointCardStatus(
                            rechargePoint,
                            status
                        )
                    }
                )
            }
        }

    }
}

@Composable
private fun AdminHome(
    activeGames: Int,
    activeRechargePoints: Int,
    adminCashToday: Long?,
    adminCashLoading: Boolean,
    adminCardActivationFee: Long?,
    adminCardActivationFeeLoading: Boolean,
    operationArmed: Boolean,
    onNewGame: () -> Unit,
    onNewRechargePoint: () -> Unit,
    onGameCards: () -> Unit,
    onRechargeCards: () -> Unit,
    onCreateCustomer: () -> Unit,
    onCardPrice: () -> Unit,
    onAdminRecharge: () -> Unit,
    onAdminAdjustment: () -> Unit,
    onRefreshAdminCash: () -> Unit,
    onPrepareBalance: () -> Unit,
    onPrepareHistory: () -> Unit,
    onLogoutAdmin: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MenesesPurple)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Bienvenido al", color = Color.White.copy(alpha = 0.82f), style = MaterialTheme.typography.titleMedium)
            Text("Panel Administrativo", color = Color.White, style = MaterialTheme.typography.headlineMedium)
            Text("Control general del sistema", color = Color.White.copy(alpha = 0.78f))
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SummaryTile(modifier = Modifier.weight(1f), emoji = "🎡", value = activeGames.toString(), label = "Juegos")
        SummaryTile(modifier = Modifier.weight(1f), emoji = "🏪", value = activeRechargePoints.toString(), label = "Taquillas", accent = MenesesBlue)
        SummaryTile(
            modifier = Modifier.weight(1f),
            emoji = "💵",
            value = when {
                adminCashLoading -> "…"
                adminCashToday != null -> "\$${adminCashToday}"
                else -> "—"
            },
            label = "Caja hoy",
            accent = MenesesGreen
        )
    }

    OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        enabled = !operationArmed && !adminCashLoading,
        onClick = onRefreshAdminCash
    ) {
        Text(if (adminCashLoading) "Actualizando caja..." else "Actualizar Caja hoy")
    }

    SectionTitle("Acciones rápidas")
    Text("Operaciones de atención y administración", color = MenesesTextSecondary, style = MaterialTheme.typography.bodyMedium)

    ActionGridRow(
        leftEmoji = "💳",
        leftTitle = "Recargar",
        leftAccent = MenesesGreen,
        leftEnabled = !operationArmed,
        leftOnClick = onAdminRecharge,
        rightEmoji = "➖",
        rightTitle = "Quitar saldo",
        rightAccent = MenesesError,
        rightEnabled = !operationArmed,
        rightOnClick = onAdminAdjustment
    )

    ActionGridRow(
        leftEmoji = "🔎",
        leftTitle = "Consultar saldo",
        leftAccent = MenesesBlue,
        leftEnabled = !operationArmed,
        leftOnClick = onPrepareBalance,
        rightEmoji = "🧾",
        rightTitle = "Historial de tarjeta",
        rightAccent = MenesesOrange,
        rightEnabled = !operationArmed,
        rightOnClick = onPrepareHistory
    )

    SectionTitle("Gestión")
    ActionGridRow(
        leftEmoji = "🎠",
        leftTitle = "Nuevo juego",
        leftAccent = MenesesPurple,
        leftEnabled = !operationArmed,
        leftOnClick = onNewGame,
        rightEmoji = "🏪",
        rightTitle = "Nueva taquilla",
        rightAccent = MenesesBlue,
        rightEnabled = !operationArmed,
        rightOnClick = onNewRechargePoint
    )
    ActionGridRow(
        leftEmoji = "🎟️",
        leftTitle = "Tarjeta juego",
        leftAccent = MenesesOrange,
        leftEnabled = !operationArmed,
        leftOnClick = onGameCards,
        rightEmoji = "💳",
        rightTitle = "Tarjeta taquilla",
        rightAccent = MenesesBlueDark,
        rightEnabled = !operationArmed,
        rightOnClick = onRechargeCards
    )

    ActionTile(
        modifier = Modifier.fillMaxWidth(),
        emoji = "💰",
        title =
            when {
                adminCardActivationFeeLoading ->
                    "Precio de tarjeta · cargando..."

                adminCardActivationFee != null ->
                    "Precio de tarjeta · \$${adminCardActivationFee}"

                else ->
                    "Precio de tarjeta"
            },
        accent = MenesesGreen,
        enabled =
            !operationArmed &&
                    !adminCardActivationFeeLoading,
        onClick = onCardPrice
    )

}

@Composable
private fun CardActivationFeePage(
    currentFee: Long?,
    loading: Boolean,
    saving: Boolean,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
    onSave: (Long) -> Unit
) {
    var priceText
            by remember(
                currentFee
            ) {
                mutableStateOf(
                    currentFee
                        ?.toString()
                        ?: ""
                )
            }

    SubPageHeader(
        title =
            "💰 Precio de tarjeta",
        onBack =
            onBack
    )

    ManagementInfoCard(
        "Este importe se cobra en TAQUILLA al crear una nueva tarjeta CUSTOMER. " +
                "No se agrega al saldo del cliente y los movimientos anteriores conservan el precio con el que fueron registrados."
    )

    FormCard {

        Text(
            text =
                "Precio de activación",
            style =
                MaterialTheme.typography.titleLarge,
            fontWeight =
                FontWeight.Bold
        )

        Text(
            text =
                when {
                    loading ->
                        "Precio actual: cargando..."

                    currentFee != null ->
                        "Precio actual: \$${currentFee}"

                    else ->
                        "Precio actual no disponible"
                },
            color =
                MenesesTextSecondary,
            style =
                MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            modifier =
                Modifier.fillMaxWidth(),
            value =
                priceText,
            onValueChange = { value ->
                priceText =
                    value.filter(
                        Char::isDigit
                    )
            },
            enabled =
                !loading &&
                        !saving,
            label = {
                Text(
                    "Nuevo precio"
                )
            },
            prefix = {
                Text(
                    "\$"
                )
            },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Number
                ),
            singleLine =
                true
        )

        PrimaryGreenButton(
            text =
                if (saving) {
                    "GUARDANDO..."
                } else {
                    "GUARDAR PRECIO"
                },
            enabled =
                !loading &&
                        !saving &&
                        (priceText.toLongOrNull() ?: -1L) >= 0,
            onClick = {
                priceText
                    .toLongOrNull()
                    ?.let { amount ->
                        onSave(
                            amount
                        )
                    }
            }
        )

        OutlinedButton(
            modifier =
                Modifier.fillMaxWidth(),
            enabled =
                !loading &&
                        !saving,
            onClick =
                onRefresh
        ) {
            Text(
                if (loading) {
                    "ACTUALIZANDO..."
                } else {
                    "ACTUALIZAR PRECIO"
                }
            )
        }
    }
}


@Composable
private fun AdminMoneyOperationPage(
    title: String,
    description: String,
    actionLabel: String,
    accent: Color,
    operationArmed: Boolean,
    cardResult: CardReadResult,
    onCancelOperation: () -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit,
    onPrepare: (Long) -> Unit
) {
    var selectedAmount by remember { mutableStateOf<Long?>(100) }
    var customAmountText by remember { mutableStateOf("") }

    SubPageHeader(title, onBack)

    ManagementInfoCard(description)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MenesesSurface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Selecciona el monto", style = MaterialTheme.typography.titleLarge)

            AmountRow(100, 200, selectedAmount, operationArmed) { amount ->
                customAmountText = ""
                selectedAmount = amount
            }

            AmountRow(500, 1000, selectedAmount, operationArmed) { amount ->
                customAmountText = ""
                selectedAmount = amount
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = customAmountText,
                enabled = !operationArmed,
                onValueChange = { value ->
                    val filtered = value.filter(Char::isDigit)
                    customAmountText = filtered
                    selectedAmount = filtered.toLongOrNull()
                },
                label = { Text("Otro monto") },
                prefix = { Text("$") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }

    TotalCard(
        label = if (actionLabel.contains("AJUSTE")) "Saldo a retirar" else "Monto a recargar",
        amount = selectedAmount ?: 0,
        detail = if ((selectedAmount ?: 0) > 0) "Acerca la tarjeta solo después de preparar la operación" else "Selecciona un monto"
    )

    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = !operationArmed && (selectedAmount ?: 0) > 0,
        colors = ButtonDefaults.buttonColors(containerColor = accent),
        contentPadding = PaddingValues(vertical = 16.dp),
        onClick = { selectedAmount?.let(onPrepare) }
    ) {
        Text(actionLabel, color = Color.White, fontWeight = FontWeight.Bold)
    }

    /*
     * El estado NFC y el resultado de la operación se muestran aquí,
     * dentro de Recargar / Quitar saldo. De este modo el operador ve
     * la confirmación donde realizó la acción y no en el HOME de ADMIN.
     */
    NfcResultArea(
        cardResult = cardResult,
        onCancelOperation = onCancelOperation,
        onReset = onReset
    )
}


@Composable
private fun GameNfcWaitingPage(
    cardResult: CardReadResult,
    onBack: () -> Unit
) {
    val infiniteTransition =
        rememberInfiniteTransition(
            label = "game-nfc-pulse"
        )

    val pulseScale by
    infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 850,
                        easing = FastOutSlowInEasing
                    ),
                repeatMode = RepeatMode.Reverse
            ),
        label = "game-nfc-scale"
    )

    val pulseAlpha by
    infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 850,
                        easing = FastOutSlowInEasing
                    ),
                repeatMode = RepeatMode.Reverse
            ),
        label = "game-nfc-alpha"
    )

    val title: String
    val description: String

    when (cardResult) {
        CardReadResult.WaitingForBalance -> {
            title = "Consulta preparada"
            description =
                "Acerca una tarjeta CLIENTE para consultar su saldo."
        }

        is CardReadResult.WaitingForCharge -> {
            title = "Cobro preparado"
            description =
                "${cardResult.gameName}\n\n" +
                        "Personas: ${cardResult.peopleCount}\n" +
                        "Total: \$${cardResult.estimatedTotal}\n\n" +
                        "Acerca una tarjeta CLIENTE para realizar el cobro."
        }

        else -> {
            title = "Lector NFC"
            description =
                "Acerca una tarjeta CLIENTE."
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(18.dp)
    ) {
        OutlinedButton(
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .size(52.dp),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(16.dp),
            onClick = onBack
        ) {
            Text(
                text = "←",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                            alpha = 0.92f + (pulseAlpha * 0.08f)
                        },
                shape = RoundedCornerShape(32.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MenesesBlueSoft
                    )
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Surface(
                        modifier =
                            Modifier.graphicsLayer {
                                alpha = 0.65f + (pulseAlpha * 0.35f)
                            },
                        shape = RoundedCornerShape(50.dp),
                        color = MenesesBlue
                    ) {
                        Text(
                            text = "NFC",
                            modifier =
                                Modifier.padding(
                                    horizontal = 24.dp,
                                    vertical = 14.dp
                                ),
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = title,
                        color = MenesesBlueDark,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MenesesBlue
                    ) {
                        Text(
                            text = "NFC · LECTOR ACTIVO",
                            modifier =
                                Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 9.dp
                                ),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = description,
                        color = MenesesBlueDark,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onBack,
                contentPadding = PaddingValues(vertical = 15.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MenesesDanger,
                        contentColor = Color.White
                    )
            ) {
                Text(
                    text = "CANCELAR",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RechargeNfcWaitingPage(
    cardResult: CardReadResult,
    onBack: () -> Unit
) {
    val infiniteTransition =
        rememberInfiniteTransition(
            label = "recharge-nfc-pulse"
        )

    val pulseScale by
    infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 850,
                        easing = FastOutSlowInEasing
                    ),
                repeatMode = RepeatMode.Reverse
            ),
        label = "recharge-nfc-scale"
    )

    val pulseAlpha by
    infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 850,
                        easing = FastOutSlowInEasing
                    ),
                repeatMode = RepeatMode.Reverse
            ),
        label = "recharge-nfc-alpha"
    )

    val title: String
    val description: String
    val accent: Color
    val softBackground: Color
    val darkText: Color

    when (cardResult) {
        is CardReadResult.WaitingForRecharge -> {
            title = "Recarga preparada"
            description =
                "Monto: \$${cardResult.amount}\n\n" +
                        "Acerca una tarjeta CLIENTE para realizar la recarga."
            accent = MenesesGreen
            softBackground = MenesesGreenSoft
            darkText = MenesesGreenDark
        }

        CardReadResult.WaitingForBalance -> {
            title = "Consulta preparada"
            description =
                "Acerca una tarjeta CLIENTE para consultar su saldo."
            accent = MenesesBlue
            softBackground = MenesesBlueSoft
            darkText = MenesesBlueDark
        }

        CardReadResult.WaitingForHistory -> {
            title = "Historial preparado"
            description =
                "Acerca una tarjeta CLIENTE para consultar sus movimientos."
            accent = MenesesOrange
            softBackground = MenesesOrangeSoft
            darkText = MenesesOrange
        }

        is CardReadResult.WaitingForDevCard -> {
            if (
                cardResult.title ==
                "Crear nueva CUSTOMER"
            ) {
                title =
                    "Crear nuevo cliente"

                description =
                    "Acerca una tarjeta NTAG215 vacía para crear un nuevo CLIENTE."

                accent =
                    MenesesBlue

                softBackground =
                    MenesesBlueSoft

                darkText =
                    MenesesBlueDark

            } else {
                /*
                 * En una sesión TAQUILLA, WaitingForDevCard también
                 * representa una recarga promocional preparada.
                 *
                 * preparePromotionalRecharge() ya construyó el título
                 * y el detalle financiero que debemos mostrar.
                 */
                title =
                    cardResult.title

                description =
                    cardResult.message

                accent =
                    MenesesGreen

                softBackground =
                    MenesesGreenSoft

                darkText =
                    MenesesGreenDark
            }
        }

        else -> {
            title = "Lector NFC"
            description = "Acerca la tarjeta correspondiente."
            accent = MenesesBlue
            softBackground = MenesesBlueSoft
            darkText = MenesesBlueDark
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(18.dp)
    ) {
        OutlinedButton(
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .size(52.dp),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(16.dp),
            onClick = onBack
        ) {
            Text(
                text = "←",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                            alpha = 0.92f + (pulseAlpha * 0.08f)
                        },
                shape = RoundedCornerShape(32.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = softBackground
                    )
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Surface(
                        modifier =
                            Modifier.graphicsLayer {
                                alpha = 0.65f + (pulseAlpha * 0.35f)
                            },
                        shape = RoundedCornerShape(50.dp),
                        color = accent
                    ) {
                        Text(
                            text = "NFC",
                            modifier =
                                Modifier.padding(
                                    horizontal = 24.dp,
                                    vertical = 14.dp
                                ),
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = title,
                        color = darkText,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = accent
                    ) {
                        Text(
                            text = "NFC · LECTOR ACTIVO",
                            modifier =
                                Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 9.dp
                                ),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = description,
                        color = darkText,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onBack,
                contentPadding = PaddingValues(vertical = 15.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MenesesDanger,
                        contentColor = Color.White
                    )
            ) {
                Text(
                    text = "CANCELAR",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun GameDashboard(
    gameSession: GameSession,
    peopleCount: Int,
    onPeopleCountChange: (Int) -> Unit,
    operationArmed: Boolean,
    onPrepareCharge: (Int) -> Unit,
    onPrepareBalance: () -> Unit,
    onLogoutGame: () -> Unit
) {
    ModeHeroCard(
        emoji = "🎡",
        title = gameSession.gameName,
        subtitle = "\$${gameSession.price} por persona",
        background = MenesesBlue
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MenesesSurface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text("Personas", style = MaterialTheme.typography.titleLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CounterButton("−", enabled = !operationArmed && peopleCount > 1) {
                    onPeopleCountChange(peopleCount - 1)
                }
                Text(
                    peopleCount.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    color = MenesesBlue,
                    fontWeight = FontWeight.Bold
                )
                CounterButton("+", enabled = !operationArmed && peopleCount < 10) {
                    onPeopleCountChange(peopleCount + 1)
                }
            }
        }
    }

    TotalCard(
        label = "Total a cobrar",
        amount = gameSession.price * peopleCount,
        detail = "$peopleCount × \$${gameSession.price}"
    )

    OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        enabled = !operationArmed,
        onClick = onPrepareBalance
    ) { Text("🔎  Consultar saldo") }

    PrimaryGreenButton(
        text = "💳  Realizar cobro  \$${gameSession.price * peopleCount}",
        enabled = !operationArmed,
        onClick = { onPrepareCharge(peopleCount) }
    )

}

@Composable
private fun RechargeDashboard(
    rechargeSession: RechargeSession,
    selectedRechargeAmount: Long?,
    onSelectedRechargeAmountChange: (Long?) -> Unit,
    customAmountText: String,
    onCustomAmountTextChange: (String) -> Unit,
    operationArmed: Boolean,
    onPrepareRecharge: (Long) -> Unit,
    onPrepareCheckoutRecharge: (Long, RechargeCheckoutApiClient.PaymentMethod) -> Unit,
    onPreparePromotionalRecharge: (RechargePromotion, RechargeCheckoutApiClient.PaymentMethod) -> Unit,
    onPrepareBalance: () -> Unit,
    onPrepareHistory: () -> Unit,
    onPrepareCardReturn: () -> Unit,
    onCreateCustomer: () -> Unit,
    onLogoutRecharge: () -> Unit
) {
    var paymentMethodAmount
            by remember(
                rechargeSession.sessionId
            ) {
                mutableStateOf<Long?>(
                    null
                )
            }

    var paymentMethodPromotion
            by remember(
                rechargeSession.sessionId
            ) {
                mutableStateOf<RechargePromotion?>(
                    null
                )
            }

    /*
     * =====================================================
     * PROMOTIONS UI STATE
     * =====================================================
     *
     * La pantalla principal de TAQUILLA conserva exactamente
     * el flujo de recarga normal.
     *
     * "Promociones" abre una segunda pantalla dentro del mismo
     * dashboard. La lista viene siempre del servidor.
     * =====================================================
     */

    var showPromotions
            by remember(
                rechargeSession.sessionId
            ) {
                mutableStateOf(
                    false
                )
            }

    var promotions
            by remember(
                rechargeSession.sessionId
            ) {
                mutableStateOf<
                        List<RechargePromotion>
                        >(
                    emptyList()
                )
            }

    var promotionsLoading
            by remember(
                rechargeSession.sessionId
            ) {
                mutableStateOf(
                    false
                )
            }

    var promotionsError
            by remember(
                rechargeSession.sessionId
            ) {
                mutableStateOf<String?>(
                    null
                )
            }

    var promotionsRefreshKey
            by remember(
                rechargeSession.sessionId
            ) {
                mutableIntStateOf(
                    0
                )
            }

    /*
     * La llamada HTTP es síncrona, por lo que se ejecuta en IO
     * para no bloquear el hilo principal de Compose.
     *
     * Cada vez que abrimos Promociones consultamos nuevamente
     * el servidor para reflejar altas, cambios y desactivaciones
     * hechas por ADMIN.
     */
    LaunchedEffect(
        showPromotions,
        promotionsRefreshKey,
        rechargeSession.sessionId
    ) {

        if (
            !showPromotions
        ) {
            return@LaunchedEffect
        }

        promotionsLoading =
            true

        promotionsError =
            null

        try {

            val loadedPromotions =
                withContext(
                    Dispatchers.IO
                ) {
                    PromotionApiClient
                        .getActivePromotions()
                }

            promotions =
                loadedPromotions

        } catch (
            e: Exception
        ) {

            promotionsError =
                e.message
                    ?: "No fue posible cargar las promociones."

        } finally {

            promotionsLoading =
                false
        }
    }

    /*
     * =====================================================
     * PROMOTIONS PAGE
     * =====================================================
     */


    /*
     * =====================================================
     * PAYMENT METHOD SCREEN
     * =====================================================
     *
     * Tanto una recarga normal como una promoción deben
     * seleccionar CASH / CARD antes de armar la lectura NFC.
     *
     * La promoción conserva aquí su identidad hasta que el
     * cajero elige el método de pago; después se ejecuta por
     * recharge_checkout.
     * =====================================================
     */
    if (
        (
                paymentMethodAmount != null ||
                        paymentMethodPromotion != null
                ) &&
        !operationArmed
    ) {
        val selectedPromotion =
            paymentMethodPromotion

        val amount =
            selectedPromotion
                ?.cashAmount
                ?: paymentMethodAmount
                ?: 0L

        /*
         * Esta selección se muestra en un Dialog de ancho completo para que
         * no herede las restricciones del Column con verticalScroll del
         * dashboard de TAQUILLA. Así el botón CANCELAR sí puede quedar
         * anclado al borde inferior real de la pantalla.
         */
        androidx.compose.ui.window.Dialog(
            onDismissRequest = {},
            properties =
                androidx.compose.ui.window.DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false
                )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .widthIn(max = 620.dp)
                                .align(Alignment.TopCenter)
                                .padding(top = 40.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(18.dp),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Método de pago",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            "¿Cómo pagó el cliente?",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        if (selectedPromotion != null) {
                            Text(
                                selectedPromotion.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MenesesPurple,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                "Cliente paga: \$${selectedPromotion.cashAmount}\n" +
                                        "Promocional: +\$${selectedPromotion.promotionalAmount}\n" +
                                        "Crédito entregado: \$${selectedPromotion.totalCreditAmount}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MenesesGreen,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Text(
                                "Recarga: \$$amount",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MenesesGreen
                            )
                        }

                        Text(
                            "Selecciona el método de pago antes de acercar la tarjeta NFC.",
                            color = MenesesTextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            Button(
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .height(116.dp),
                                onClick = {
                                    paymentMethodAmount = null
                                    paymentMethodPromotion = null

                                    if (selectedPromotion != null) {
                                        onPreparePromotionalRecharge(
                                            selectedPromotion,
                                            RechargeCheckoutApiClient
                                                .PaymentMethod
                                                .CARD
                                        )
                                    } else {
                                        onPrepareCheckoutRecharge(
                                            amount,
                                            RechargeCheckoutApiClient
                                                .PaymentMethod
                                                .CARD
                                        )
                                    }
                                },
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = MenesesBlue
                                    ),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        "💳",
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                    Text(
                                        "TARJETA",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Button(
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .height(116.dp),
                                onClick = {
                                    paymentMethodAmount = null
                                    paymentMethodPromotion = null

                                    if (selectedPromotion != null) {
                                        onPreparePromotionalRecharge(
                                            selectedPromotion,
                                            RechargeCheckoutApiClient
                                                .PaymentMethod
                                                .CASH
                                        )
                                    } else {
                                        onPrepareCheckoutRecharge(
                                            amount,
                                            RechargeCheckoutApiClient
                                                .PaymentMethod
                                                .CASH
                                        )
                                    }
                                },
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = MenesesGreen
                                    ),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        "💵",
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                    Text(
                                        "EFECTIVO",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        modifier =
                            Modifier
                                .align(Alignment.BottomCenter)
                                .widthIn(min = 220.dp, max = 320.dp)
                                .height(56.dp),
                        onClick = {
                            val wasPromotion =
                                paymentMethodPromotion != null

                            paymentMethodAmount = null
                            paymentMethodPromotion = null

                            if (wasPromotion) {
                                showPromotions = true
                            }
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MenesesDanger
                            ),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            "✕  CANCELAR",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        return
    }

    if (
        showPromotions
    ) {

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.Start
        ) {
            OutlinedButton(
                enabled =
                    !operationArmed,
                onClick = {
                    showPromotions =
                        false
                }
            ) {
                Text(
                    "←  Volver a recargas"
                )
            }
        }

        ModeHeroCard(
            emoji =
                "🎁",
            title =
                "Promociones",
            subtitle =
                rechargeSession
                    .rechargePointName,
            background =
                MenesesPurple
        )

        when {

            promotionsLoading -> {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(
                            24.dp
                        ),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MenesesSurface
                        )
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    22.dp
                                ),
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.spacedBy(
                                10.dp
                            )
                    ) {
                        Text(
                            "Cargando promociones…",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            "Consultando las promociones activas del servidor.",
                            color =
                                MenesesTextSecondary,
                            textAlign =
                                TextAlign.Center
                        )
                    }
                }
            }

            promotionsError != null -> {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(
                            24.dp
                        ),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MenesesSurface
                        )
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    22.dp
                                ),
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.spacedBy(
                                12.dp
                            )
                    ) {
                        Text(
                            "No fue posible cargar las promociones",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.Bold,
                            textAlign =
                                TextAlign.Center
                        )

                        Text(
                            promotionsError
                                ?: "Error desconocido.",
                            color =
                                MenesesTextSecondary,
                            textAlign =
                                TextAlign.Center
                        )

                        OutlinedButton(
                            enabled =
                                !operationArmed,
                            onClick = {
                                promotionsRefreshKey +=
                                    1
                            }
                        ) {
                            Text(
                                "↻  Intentar nuevamente"
                            )
                        }
                    }
                }
            }

            promotions.isEmpty() -> {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(
                            24.dp
                        ),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MenesesSurface
                        )
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    22.dp
                                ),
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.spacedBy(
                                10.dp
                            )
                    ) {
                        Text(
                            "No hay promociones disponibles",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            "Cuando ADMIN active una promoción aparecerá aquí.",
                            color =
                                MenesesTextSecondary,
                            textAlign =
                                TextAlign.Center
                        )

                        OutlinedButton(
                            enabled =
                                !operationArmed,
                            onClick = {
                                promotionsRefreshKey +=
                                    1
                            }
                        ) {
                            Text(
                                "↻  Actualizar"
                            )
                        }
                    }
                }
            }

            else -> {

                Text(
                    "Selecciona una promoción",
                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,
                    fontWeight =
                        FontWeight.Bold
                )

                promotions.forEach {
                        promotion ->

                    Button(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(
                                    112.dp
                                ),
                        enabled =
                            !operationArmed,
                        shape =
                            RoundedCornerShape(
                                22.dp
                            ),
                        colors =
                            ButtonDefaults
                                .buttonColors(
                                    containerColor =
                                        MenesesSurface,
                                    contentColor =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurface,
                                    disabledContainerColor =
                                        MenesesSurface,
                                    disabledContentColor =
                                        MenesesTextSecondary
                                ),
                        onClick = {

                            /*
                             * La promoción todavía no arma una operación NFC.
                             * Primero conservamos la selección y preguntamos
                             * cómo pagó el cliente. CASH / CARD se enviará al
                             * recharge_checkout junto con promotionId.
                             */
                            showPromotions =
                                false

                            paymentMethodAmount =
                                null

                            paymentMethodPromotion =
                                promotion
                        }
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal =
                                            4.dp
                                    ),
                            verticalAlignment =
                                Alignment.CenterVertically,
                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    14.dp
                                )
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .size(
                                            54.dp
                                        )
                                        .background(
                                            MenesesPurpleSoft,
                                            RoundedCornerShape(
                                                17.dp
                                            )
                                        ),
                                contentAlignment =
                                    Alignment.Center
                            ) {
                                Text(
                                    "🎁",
                                    fontSize =
                                        25.sp
                                )
                            }

                            Column(
                                modifier =
                                    Modifier.weight(
                                        1f
                                    ),
                                verticalArrangement =
                                    Arrangement.spacedBy(
                                        4.dp
                                    )
                            ) {
                                Text(
                                    promotion.name,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    "Paga \$${promotion.cashAmount}  ·  " +
                                            "Recibe \$${promotion.totalCreditAmount}",
                                    color =
                                        MenesesTextSecondary,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyMedium
                                )

                                if (
                                    promotion.promotionalAmount >
                                    0
                                ) {
                                    Text(
                                        "Bonificación: +\$${promotion.promotionalAmount}",
                                        color =
                                            MenesesPurple,
                                        style =
                                            MaterialTheme
                                                .typography
                                                .bodyMedium,
                                        fontWeight =
                                            FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                "›",
                                color =
                                    MenesesPurple,
                                fontSize =
                                    30.sp,
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }

                OutlinedButton(
                    modifier =
                        Modifier.fillMaxWidth(),
                    enabled =
                        !operationArmed,
                    onClick = {
                        promotionsRefreshKey +=
                            1
                    }
                ) {
                    Text(
                        "↻  Actualizar promociones"
                    )
                }
            }
        }

        return
    }

    /*
     * =====================================================
     * NORMAL RECHARGE PAGE
     * =====================================================
     */

    ModeHeroCard(
        emoji = "🏪",
        title = rechargeSession.rechargePointName,
        subtitle = "Recargas y atención al cliente",
        background = MenesesBlue
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MenesesSurface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Selecciona el monto", style = MaterialTheme.typography.titleLarge)
            AmountRow(100, 200, selectedRechargeAmount, operationArmed) {
                // Primero limpiamos el monto manual.
                // Ese callback pone selectedRechargeAmount en null,
                // por eso la selección predeterminada debe aplicarse DESPUÉS.
                onCustomAmountTextChange("")
                onSelectedRechargeAmountChange(it)
            }
            AmountRow(500, 1000, selectedRechargeAmount, operationArmed) {
                onCustomAmountTextChange("")
                onSelectedRechargeAmountChange(it)
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = customAmountText,
                enabled = !operationArmed,
                onValueChange = onCustomAmountTextChange,
                label = { Text("Otro monto") },
                prefix = { Text("$") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }

    TotalCard(
        label = "Monto a recargar",
        amount = selectedRechargeAmount ?: 0,
        detail = null
    )

    PrimaryGreenButton(
        text = selectedRechargeAmount?.let { "💳  Realizar recarga  \$$it" } ?: "💳  Realizar recarga",
        enabled = !operationArmed && (selectedRechargeAmount ?: 0) > 0,
        onClick = {
            paymentMethodPromotion =
                null

            paymentMethodAmount =
                selectedRechargeAmount
        }
    )


    /*
     * Las promociones son una ruta adicional.
     * La recarga normal de TAQUILLA permanece intacta.
     */
    OutlinedButton(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(
                    58.dp
                ),
        enabled =
            !operationArmed,
        shape =
            RoundedCornerShape(
                18.dp
            ),
        onClick = {
            showPromotions =
                true

            /*
             * Fuerza una lectura fresca cada vez que el cajero
             * abre la pantalla, incluso si ya la había visitado.
             */
            promotionsRefreshKey +=
                1
        }
    ) {
        Text(
            "🎁  Promociones",
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
            fontWeight =
                FontWeight.Bold
        )
    }

    SectionTitle("Atención al cliente")

    ActionGridRow(
        leftEmoji = "🧾",
        leftTitle = "Historial",
        leftAccent = MenesesOrange,
        leftEnabled = !operationArmed,
        leftOnClick = onPrepareHistory,
        rightEmoji = "🔎",
        rightTitle = "Consultar saldo",
        rightAccent = MenesesBlue,
        rightEnabled = !operationArmed,
        rightOnClick = onPrepareBalance
    )

    ActionTile(
        modifier = Modifier.fillMaxWidth(),
        emoji = "↩️",
        title = "Devolver / Resetear Tarjeta",
        accent = MenesesDanger,
        enabled = !operationArmed,
        onClick = onPrepareCardReturn
    )
}

@Composable
private fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MenesesBlue)
    ) {
        Column(
            modifier = Modifier.padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("🎡", fontSize = 48.sp)
            Text("Espectaculares Meneses", color = Color.White, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
            Text("Acerca una tarjeta ADMIN, GAME o TAQUILLA para comenzar.", color = Color.White.copy(alpha = 0.84f), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ModeHeroCard(emoji: String, title: String, subtitle: String, background: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = background)
    ) {
        Row(
            modifier = Modifier.padding(22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .background(Color.White.copy(alpha = 0.16f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) { Text(emoji, fontSize = 30.sp) }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = Color.White, style = MaterialTheme.typography.headlineMedium)
                Text(subtitle, color = Color.White.copy(alpha = 0.88f), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun SummaryTile(modifier: Modifier, emoji: String, value: String, label: String, accent: Color = MenesesPurple) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MenesesSurface)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Text(emoji, fontSize = 24.sp)
            Text(value, color = accent, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(label, color = MenesesTextSecondary, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
}

@Composable
private fun ActionGridRow(
    leftEmoji: String,
    leftTitle: String,
    leftAccent: Color,
    leftEnabled: Boolean,
    leftOnClick: () -> Unit,
    rightEmoji: String,
    rightTitle: String,
    rightAccent: Color,
    rightEnabled: Boolean,
    rightOnClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionTile(
            modifier = Modifier.weight(1f),
            emoji = leftEmoji,
            title = leftTitle,
            accent = leftAccent,
            enabled = leftEnabled,
            onClick = leftOnClick
        )
        ActionTile(
            modifier = Modifier.weight(1f),
            emoji = rightEmoji,
            title = rightTitle,
            accent = rightAccent,
            enabled = rightEnabled,
            onClick = rightOnClick
        )
    }
}

@Composable
private fun ActionTile(modifier: Modifier, emoji: String, title: String, accent: Color, enabled: Boolean, onClick: () -> Unit) {
    Button(
        modifier = modifier.height(124.dp),
        enabled = enabled,
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MenesesSurface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MenesesSurface,
            disabledContentColor = MenesesTextSecondary
        ),
        onClick = onClick,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(accent.copy(alpha = if (enabled) 0.13f else 0.06f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) { Text(emoji, fontSize = 22.sp) }
            Text(title, textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun CounterButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        modifier = Modifier.size(72.dp),
        enabled = enabled,
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MenesesBlue),
        onClick = onClick
    ) { Text(text, fontSize = 32.sp, color = Color.White) }
}

@Composable
private fun TotalCard(label: String, amount: Long, detail: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MenesesGreenSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Text(label, color = MenesesTextSecondary, style = MaterialTheme.typography.titleMedium)
            Text("\$$amount", color = MenesesGreen, fontSize = 48.sp, fontWeight = FontWeight.Bold)

            if (!detail.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.72f)
                ) {
                    Text(
                        detail,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun AmountRow(first: Long, second: Long, selected: Long?, operationArmed: Boolean, onSelect: (Long) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AmountButton(Modifier.weight(1f), first, selected == first, !operationArmed) { onSelect(first) }
        AmountButton(Modifier.weight(1f), second, selected == second, !operationArmed) { onSelect(second) }
    }
}

@Composable
private fun AmountButton(modifier: Modifier, amount: Long, selected: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Button(
        modifier = modifier.height(58.dp),
        enabled = enabled,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MenesesBlue else MenesesBlueSoft,
            contentColor = if (selected) Color.White else MenesesBlueDark
        ),
        onClick = onClick
    ) { Text("\$$amount", style = MaterialTheme.typography.titleMedium) }
}

@Composable
private fun DestructiveButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MenesesDanger,
            contentColor = Color.White,
            disabledContainerColor = MenesesDanger.copy(alpha = 0.40f),
            disabledContentColor = Color.White.copy(alpha = 0.72f)
        ),
        onClick = onClick
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PrimaryGreenButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth().height(58.dp),
        enabled = enabled,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MenesesGreen, contentColor = Color.White),
        onClick = onClick
    ) { Text(text, style = MaterialTheme.typography.titleMedium) }
}

@Composable
private fun SubPageHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(onClick = onBack) { Text("←") }
        Text(title, style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
private fun FormCard(content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MenesesSurface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
private fun ManagementInfoCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MenesesBlueSoft)
    ) {
        Text(text, modifier = Modifier.padding(14.dp), color = MenesesBlueDark, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun AdminGameCardView(
    game: AdminGame,
    operationArmed: Boolean,
    onPrepareGameCard: () -> Unit,
    onEdit: () -> Unit
) {

    val card =
        game.card

    val statusText =
        when (
            card?.status
        ) {
            "ACTIVE" -> "ACTIVA"
            "INACTIVE" -> "INACTIVA"
            "BLOCKED" -> "BLOQUEADA"
            else -> "SIN TARJETA"
        }

    val statusColor =
        if (
            card?.status == "ACTIVE"
        ) {
            MenesesGreenDark
        } else {
            MenesesOrange
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MenesesSurface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    game.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = statusText,
                    color = statusColor,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Text(
                "\$${game.price} por persona",
                color = MenesesBlue,
                style = MaterialTheme.typography.titleMedium
            )

            if (
                card != null
            ) {
                Text(
                    "UID: ${card.uid}",
                    color = MenesesTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    "Pendiente de registrar una tarjeta GAME.",
                    color = MenesesTextSecondary
                )
            }

            if (
                game.status == "PENDING_SETUP" &&
                card == null
            ) {
                PrimaryGreenButton(
                    "CREAR TARJETA GAME",
                    !operationArmed,
                    onPrepareGameCard
                )
            } else {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !operationArmed,
                    onClick = onEdit
                ) {
                    Text("EDITAR")
                }
            }
        }
    }
}

@Composable
private fun AdminRechargePointCardView(
    rechargePoint: AdminRechargePoint,
    operationArmed: Boolean,
    onPrepareRechargePointCard: () -> Unit,
    onEdit: () -> Unit
) {

    val card =
        rechargePoint.card

    val statusText =
        when (
            card?.status
        ) {
            "ACTIVE" -> "ACTIVA"
            "INACTIVE" -> "INACTIVA"
            "BLOCKED" -> "BLOQUEADA"
            else -> "SIN TARJETA"
        }

    val statusColor =
        if (
            card?.status == "ACTIVE"
        ) {
            MenesesGreenDark
        } else {
            MenesesOrange
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MenesesSurface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    rechargePoint.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = statusText,
                    color = statusColor,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            if (
                card != null
            ) {
                Text(
                    "UID: ${card.uid}",
                    color = MenesesTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    "Pendiente de registrar una tarjeta TAQUILLA.",
                    color = MenesesTextSecondary
                )
            }

            if (
                rechargePoint.status == "PENDING_SETUP" &&
                card == null
            ) {
                PrimaryGreenButton(
                    "CREAR TARJETA TAQUILLA",
                    !operationArmed,
                    onPrepareRechargePointCard
                )
            } else {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !operationArmed,
                    onClick = onEdit
                ) {
                    Text("EDITAR")
                }
            }
        }
    }
}


@Composable
private fun EditGamePage(
    game: AdminGame,
    saving: Boolean,
    onBack: () -> Unit,
    onSave: (String, Long) -> Unit,
    onChangeCardStatus: (String) -> Unit
) {

    var name by remember(game.id) {
        mutableStateOf(game.name)
    }

    var priceText by remember(game.id) {
        mutableStateOf(game.price.toString())
    }

    var showUnlinkDialog by remember(game.id) {
        mutableStateOf(false)
    }

    SubPageHeader(
        title = "Editar juego",
        onBack = onBack
    )

    FormCard {

        Text(
            text = "Información del juego",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = {
                name = it
            },
            enabled = !saving,
            label = {
                Text("Nombre del juego")
            },
            singleLine = true
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = priceText,
            onValueChange = {
                priceText =
                    it.filter(Char::isDigit)
            },
            enabled = !saving,
            label = {
                Text("Precio por persona")
            },
            prefix = {
                Text("$")
            },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
            singleLine = true
        )

        PrimaryGreenButton(
            text =
                if (saving) {
                    "GUARDANDO..."
                } else {
                    "GUARDAR CAMBIOS"
                },
            enabled =
                !saving &&
                        name.trim().length >= 2 &&
                        (priceText.toLongOrNull() ?: 0) > 0,
            onClick = {
                val price =
                    priceText.toLongOrNull()

                if (
                    price != null
                ) {
                    onSave(
                        name.trim(),
                        price
                    )
                }
            }
        )
    }

    val card =
        game.card

    if (
        card != null
    ) {

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        FormCard {

            Text(
                text = "Tarjeta GAME",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            val cardActive =
                card.status == "ACTIVE"

            Text(
                text =
                    if (cardActive) {
                        "Estado: ACTIVA"
                    } else {
                        "Estado: INACTIVA"
                    },
                color =
                    if (cardActive) {
                        MenesesGreenDark
                    } else {
                        MenesesOrange
                    },
                fontWeight = FontWeight.SemiBold
            )

            if (
                cardActive
            ) {
                DestructiveButton(
                    text =
                        if (saving) {
                            "ACTUALIZANDO..."
                        } else {
                            "DESACTIVAR TARJETA"
                        },
                    enabled = !saving,
                    onClick = {
                        onChangeCardStatus(
                            "INACTIVE"
                        )
                    }
                )
            } else {
                PrimaryGreenButton(
                    text =
                        if (saving) {
                            "ACTUALIZANDO..."
                        } else {
                            "REACTIVAR TARJETA"
                        },
                    enabled = !saving,
                    onClick = {
                        onChangeCardStatus(
                            "ACTIVE"
                        )
                    }
                )
            }

            Text(
                text =
                    "Desactivar es temporal. La tarjeta permanece asociada al juego y puede reactivarse después.",
                color = MenesesTextSecondary,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            DestructiveButton(
                text = "DESVINCULAR TARJETA",
                enabled = !saving,
                onClick = {
                    showUnlinkDialog =
                        true
                }
            )

            Text(
                text =
                    "Desvincular retira definitivamente esta tarjeta del juego. " +
                            "El historial se conserva y el juego podrá recibir una nueva tarjeta.",
                color = MenesesTextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    if (
        showUnlinkDialog
    ) {

        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                showUnlinkDialog =
                    false
            },
            title = {
                Text("Desvincular tarjeta")
            },
            text = {
                Text(
                    "La tarjeta dejará de estar asociada a ${game.name}.\n\n" +
                            "El juego quedará disponible para registrar una nueva tarjeta.\n\n" +
                            "El historial anterior se conservará."
                )
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showUnlinkDialog =
                            false
                    }
                ) {
                    Text("CANCELAR")
                }
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    enabled = !saving,
                    onClick = {
                        showUnlinkDialog =
                            false

                        onChangeCardStatus(
                            "UNLINK"
                        )
                    }
                ) {
                    Text("DESVINCULAR")
                }
            }
        )
    }
}


@Composable
private fun EditRechargePointPage(
    rechargePoint: AdminRechargePoint,
    saving: Boolean,
    onBack: () -> Unit,
    onSave: (String) -> Unit,
    onChangeCardStatus: (String) -> Unit
) {

    var name by remember(rechargePoint.id) {
        mutableStateOf(rechargePoint.name)
    }

    var showUnlinkDialog by remember(rechargePoint.id) {
        mutableStateOf(false)
    }

    SubPageHeader(
        title = "Editar taquilla",
        onBack = onBack
    )

    FormCard {

        Text(
            text = "Información de la taquilla",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = {
                name = it
            },
            enabled = !saving,
            label = {
                Text("Nombre de la taquilla")
            },
            singleLine = true
        )

        PrimaryGreenButton(
            text =
                if (saving) {
                    "GUARDANDO..."
                } else {
                    "GUARDAR CAMBIOS"
                },
            enabled =
                !saving &&
                        name.trim().length >= 2,
            onClick = {
                onSave(
                    name.trim()
                )
            }
        )
    }

    val card =
        rechargePoint.card

    if (
        card != null
    ) {

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        FormCard {

            Text(
                text = "Tarjeta TAQUILLA",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            val cardActive =
                card.status == "ACTIVE"

            Text(
                text =
                    if (cardActive) {
                        "Estado: ACTIVA"
                    } else {
                        "Estado: INACTIVA"
                    },
                color =
                    if (cardActive) {
                        MenesesGreenDark
                    } else {
                        MenesesOrange
                    },
                fontWeight = FontWeight.SemiBold
            )

            if (
                cardActive
            ) {
                DestructiveButton(
                    text =
                        if (saving) {
                            "ACTUALIZANDO..."
                        } else {
                            "DESACTIVAR TARJETA"
                        },
                    enabled = !saving,
                    onClick = {
                        onChangeCardStatus(
                            "INACTIVE"
                        )
                    }
                )
            } else {
                PrimaryGreenButton(
                    text =
                        if (saving) {
                            "ACTUALIZANDO..."
                        } else {
                            "REACTIVAR TARJETA"
                        },
                    enabled = !saving,
                    onClick = {
                        onChangeCardStatus(
                            "ACTIVE"
                        )
                    }
                )
            }

            Text(
                text =
                    "Desactivar es temporal. La tarjeta permanece asociada a la taquilla y puede reactivarse después.",
                color = MenesesTextSecondary,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            DestructiveButton(
                text = "DESVINCULAR TARJETA",
                enabled = !saving,
                onClick = {
                    showUnlinkDialog =
                        true
                }
            )

            Text(
                text =
                    "Desvincular retira definitivamente esta tarjeta de la taquilla. " +
                            "El historial se conserva y la taquilla podrá recibir una nueva tarjeta.",
                color = MenesesTextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    if (
        showUnlinkDialog
    ) {

        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                showUnlinkDialog =
                    false
            },
            title = {
                Text("Desvincular tarjeta")
            },
            text = {
                Text(
                    "La tarjeta dejará de estar asociada a ${rechargePoint.name}.\n\n" +
                            "La taquilla quedará disponible para registrar una nueva tarjeta.\n\n" +
                            "El historial anterior se conservará."
                )
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showUnlinkDialog =
                            false
                    }
                ) {
                    Text("CANCELAR")
                }
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    enabled = !saving,
                    onClick = {
                        showUnlinkDialog =
                            false

                        onChangeCardStatus(
                            "UNLINK"
                        )
                    }
                ) {
                    Text("DESVINCULAR")
                }
            }
        )
    }
}



@Composable
private fun OperationResultDialog(
    cardResult: CardReadResult,
    onClose: () -> Unit
) {
    val isTerminalResult =
        cardResult is CardReadResult.Success ||
                cardResult is CardReadResult.Error ||
                cardResult is CardReadResult.HistoryLoaded

    if (!isTerminalResult) {
        return
    }

    /*
     * No permitimos cerrar tocando fuera del diálogo. El operador debe
     * confirmar que vio el resultado mediante el botón CERRAR.
     *
     * onClose reutiliza onReset, el mismo mecanismo que antes utilizaba el
     * auto-dismiss de 60 segundos. No ejecuta ni repite ninguna transacción.
     */
    val resultScrollState =
        rememberScrollState()

    androidx.compose.material3.AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onClose
            ) {
                Text("CERRAR")
            }
        },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(resultScrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TerminalNfcResultContent(
                    cardResult = cardResult,
                    resultScrollState = resultScrollState
                )
            }
        }
    )
}

@Composable
private fun TerminalNfcResultContent(
    cardResult: CardReadResult,
    resultScrollState: androidx.compose.foundation.ScrollState
) {
    when (cardResult) {
        is CardReadResult.Success -> {
            when (cardResult.title) {
                "Saldo consultado" ->
                    BalanceResultCard(cardResult.message)

                "Cobro realizado" ->
                    GameChargeSuccessCard(cardResult.message)

                "Recarga taquilla realizada" ->
                    RechargeSuccessCard(cardResult.message)

                "Promoción aplicada" ->
                    PromotionSuccessCard(cardResult.message)

                "Cliente nuevo creado" ->
                    NewCustomerSuccessCard(cardResult.message)

                "Devolución completada" ->
                    CustomerCardReturnSuccessCard(
                        cardResult.message
                    )

                else ->
                    StatusCard(
                        cardResult.title,
                        cardResult.message
                    )
            }
        }

        is CardReadResult.Error ->
            ErrorCard(
                message = cardResult.message
            )

        is CardReadResult.HistoryLoaded ->
            CustomerHistoryCard(
                history =
                    cardResult.history,
                resultScrollState =
                    resultScrollState
            )

        else -> Unit
    }
}

@Composable
private fun NfcResultArea(cardResult: CardReadResult, onCancelOperation: () -> Unit, onReset: () -> Unit) {
    when (cardResult) {
        CardReadResult.Waiting -> Unit
        CardReadResult.WaitingForBalance -> ArmedOperationCard("Consulta preparada", "Acerca una tarjeta CLIENTE para consultar su saldo.", onCancelOperation)
        CardReadResult.WaitingForHistory -> ArmedOperationCard("Historial preparado", "Acerca una tarjeta CLIENTE para consultar sus movimientos.", onCancelOperation)
        is CardReadResult.WaitingForRecharge -> ArmedOperationCard("Recarga preparada", "${cardResult.rechargePointName}\n\nMonto: \$${cardResult.amount}\n\nAcerca una tarjeta CUSTOMER.", onCancelOperation)
        is CardReadResult.WaitingForCharge -> ArmedOperationCard("Cobro preparado", "${cardResult.gameName}\n\nPersonas: ${cardResult.peopleCount}\nTotal: \$${cardResult.estimatedTotal}\n\nAcerca una tarjeta CUSTOMER.", onCancelOperation)
        is CardReadResult.WaitingForDevCard -> ArmedOperationCard(cardResult.title, cardResult.message, onCancelOperation)
        is CardReadResult.Registered -> Unit
        is CardReadResult.HistoryLoaded -> Unit
        is CardReadResult.Unregistered -> StatusCard("Tarjeta no registrada", "UID: ${cardResult.uid}")
        /*
         * Los resultados terminales se muestran ahora en
         * OperationResultDialog(), fuera del layout normal.
         */
        is CardReadResult.Success -> Unit
        is CardReadResult.Error -> Unit
    }
}

@Composable
private fun ArmedOperationCard(title: String, description: String, onCancel: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MenesesBlueSoft)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, color = MenesesBlueDark, style = MaterialTheme.typography.titleLarge)
            Surface(shape = RoundedCornerShape(12.dp), color = MenesesBlue) {
                Text("NFC · LECTOR ACTIVO", modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Text(description)
            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onCancel) { Text("CANCELAR") }
        }
    }
}

@Composable
private fun GameChargeSuccessCard(
    description: String
) {
    val lines =
        description
            .lines()
            .filter { it.isNotBlank() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MenesesGreenSoft
            )
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = MenesesGreen
            ) {
                Text(
                    text = "✓",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Cobro realizado",
                color = MenesesGreenDark,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            HorizontalDivider(
                color = MenesesGreen.copy(alpha = 0.20f)
            )

            lines.forEachIndexed { index, line ->
                val isGameName = index == 0
                val isTotal = line.startsWith("Total:")
                val isNewBalance = line.startsWith("Saldo nuevo:")

                Text(
                    text = line,
                    color =
                        when {
                            isTotal || isNewBalance -> MenesesGreenDark
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                    style =
                        when {
                            isGameName -> MaterialTheme.typography.titleLarge
                            isTotal || isNewBalance -> MaterialTheme.typography.titleMedium
                            else -> MaterialTheme.typography.bodyLarge
                        },
                    fontWeight =
                        when {
                            isGameName || isTotal || isNewBalance -> FontWeight.Bold
                            else -> FontWeight.Normal
                        },
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = "Revisa el resultado y presiona CERRAR para continuar.",
                color = MenesesTextSecondary,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CustomerCardReturnSuccessCard(
    description: String
) {

    val lines =
        description
            .lines()
            .filter {
                it.isNotBlank()
            }


    val noRefund =
        lines.firstOrNull() ==
                "NO DEVOLVER DINERO"


    val actionLabel =
        if (
            noRefund
        ) {
            "NO DEVOLVER DINERO"
        } else {
            "ENTREGAR AL CLIENTE"
        }


    val refundAmount =
        lines
            .dropWhile {
                it !=
                        actionLabel
            }
            .drop(
                1
            )
            .firstOrNull()
            ?: ""


    val detailLines =
        lines
            .filterNot {
                it ==
                        actionLabel ||
                        it ==
                        refundAmount
            }


    val containerColor =
        if (
            noRefund
        ) {
            MenesesOrangeSoft
        } else {
            MenesesGreenSoft
        }


    val accentColor =
        if (
            noRefund
        ) {
            MenesesOrange
        } else {
            MenesesGreen
        }


    val accentTextColor =
        if (
            noRefund
        ) {
            MenesesDanger
        } else {
            MenesesGreenDark
        }


    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                28.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    containerColor
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal =
                            22.dp,
                        vertical =
                            28.dp
                    ),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(
                    12.dp
                )
        ) {

            Surface(
                shape =
                    RoundedCornerShape(
                        50.dp
                    ),
                color =
                    accentColor
            ) {

                Text(
                    text =
                        if (
                            noRefund
                        ) {
                            "!"
                        } else {
                            "✓"
                        },
                    modifier =
                        Modifier.padding(
                            horizontal =
                                18.dp,
                            vertical =
                                8.dp
                        ),
                    color =
                        Color.White,
                    fontSize =
                        30.sp,
                    fontWeight =
                        FontWeight.Bold
                )
            }


            Text(
                text =
                    "Devolución completada",
                color =
                    accentTextColor,
                style =
                    MaterialTheme
                        .typography
                        .headlineMedium,
                fontWeight =
                    FontWeight.Bold,
                textAlign =
                    TextAlign.Center
            )


            Text(
                text =
                    actionLabel,
                color =
                    if (
                        noRefund
                    ) {
                        MenesesDanger
                    } else {
                        MenesesTextSecondary
                    },
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                fontWeight =
                    FontWeight.Bold,
                textAlign =
                    TextAlign.Center
            )


            Text(
                text =
                    refundAmount,
                color =
                    accentTextColor,
                fontSize =
                    44.sp,
                fontWeight =
                    FontWeight.Bold,
                textAlign =
                    TextAlign.Center
            )


            if (
                noRefund
            ) {

                Text(
                    text =
                        "Esta tarjeta fue creada por ADMIN.\n" +
                                "No entregar efectivo al cliente.",
                    color =
                        MenesesDanger,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold,
                    textAlign =
                        TextAlign.Center
                )
            }


            HorizontalDivider(
                color =
                    accentColor.copy(
                        alpha =
                            0.20f
                    )
            )


            detailLines
                .filterNot {
                    noRefund &&
                            it ==
                            "Tarjeta creada por ADMIN"
                }
                .forEach { line ->

                    Text(
                        text =
                            line,
                        style =
                            MaterialTheme
                                .typography
                                .bodyLarge,
                        fontWeight =
                            if (
                                line.startsWith(
                                    "Saldo eliminado:"
                                )
                            ) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                        textAlign =
                            TextAlign.Center
                    )
                }


            Text(
                text =
                    "Revisa el resultado y presiona CERRAR para continuar.",
                color =
                    MenesesTextSecondary,
                style =
                    MaterialTheme
                        .typography
                        .labelMedium,
                textAlign =
                    TextAlign.Center
            )
        }
    }
}

@Composable
private fun RechargeSuccessCard(
    description: String
) {
    val lines =
        description
            .lines()
            .filter {
                it.isNotBlank()
            }

    val isCheckoutSuccess =
        lines.any {
            it.startsWith(
                "Cobrar al cliente:"
            )
        }

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(28.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MenesesGreenSoft
            )
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 22.dp,
                        vertical = 26.dp
                    ),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape =
                    RoundedCornerShape(50.dp),
                color =
                    MenesesGreen
            ) {
                Text(
                    text = "✓",
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp,
                            vertical = 8.dp
                        ),
                    color =
                        Color.White,
                    fontSize =
                        30.sp,
                    fontWeight =
                        FontWeight.Bold
                )
            }

            Text(
                text =
                    "Recarga realizada",
                color =
                    MenesesGreenDark,
                style =
                    MaterialTheme.typography.headlineMedium,
                fontWeight =
                    FontWeight.Bold,
                textAlign =
                    TextAlign.Center
            )

            HorizontalDivider(
                color =
                    MenesesGreen.copy(
                        alpha = 0.20f
                    )
            )

            lines.forEach { line ->

                val isRecharge =
                    line.startsWith(
                        "Recarga:"
                    )

                val isNewBalance =
                    line.startsWith(
                        "Saldo nuevo:"
                    )

                val isRegisteredBalance =
                    line.startsWith(
                        "Saldo registrado exitosamente:"
                    )

                val isAmountToCharge =
                    line.startsWith(
                        "Cobrar al cliente:"
                    )

                if (
                    isCheckoutSuccess &&
                    isAmountToCharge
                ) {
                    val amountToCharge =
                        line
                            .substringAfter(
                                "Cobrar al cliente:"
                            )
                            .trim()

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Surface(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(22.dp),
                        color =
                            MenesesBlue.copy(
                                alpha = 0.08f
                            )
                    ) {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 18.dp,
                                        vertical = 18.dp
                                    ),
                            horizontalAlignment =
                                Alignment.CenterHorizontally,
                            verticalArrangement =
                                Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text =
                                    "Cobrar al cliente",
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurface,
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium,
                                fontWeight =
                                    FontWeight.SemiBold,
                                textAlign =
                                    TextAlign.Center
                            )

                            Text(
                                text =
                                    amountToCharge,
                                color =
                                    MenesesBlue,
                                fontSize =
                                    42.sp,
                                fontWeight =
                                    FontWeight.Bold,
                                textAlign =
                                    TextAlign.Center
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                } else {

                    Text(
                        text =
                            line,
                        color =
                            when {
                                isRecharge ||
                                        isNewBalance ||
                                        isRegisteredBalance ->
                                    MenesesGreenDark

                                else ->
                                    MaterialTheme
                                        .colorScheme
                                        .onSurface
                            },
                        style =
                            when {
                                isRecharge ||
                                        isNewBalance ->
                                    MaterialTheme
                                        .typography
                                        .titleLarge

                                isRegisteredBalance ->
                                    MaterialTheme
                                        .typography
                                        .titleMedium

                                else ->
                                    MaterialTheme
                                        .typography
                                        .bodyLarge
                            },
                        fontWeight =
                            if (
                                isRecharge ||
                                isNewBalance ||
                                isRegisteredBalance
                            ) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Medium
                            },
                        textAlign =
                            TextAlign.Center
                    )
                }
            }

            Text(
                text =
                    "Revisa el resultado y presiona CERRAR para continuar.",
                color =
                    MenesesTextSecondary,
                style =
                    MaterialTheme.typography.labelMedium,
                textAlign =
                    TextAlign.Center
            )
        }
    }
}

@Composable
private fun PromotionSuccessCard(
    description: String
) {
    val lines =
        description
            .lines()
            .filter {
                it.isNotBlank()
            }

    val promotionName =
        lines.firstOrNull()
            ?: "Promoción"

    val paidLine =
        lines.firstOrNull {
            it.startsWith("Cliente pagó:")
        }

    val bonusLine =
        lines.firstOrNull {
            it.startsWith("Bonificación:")
        }

    val totalLine =
        lines.firstOrNull {
            it.startsWith("Total acreditado:")
        }

    val previousBalanceLine =
        lines.firstOrNull {
            it.startsWith("Saldo anterior:")
        }

    val newBalanceLine =
        lines.firstOrNull {
            it.startsWith("Saldo nuevo:")
        }

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(28.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MenesesGreenSoft
            )
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 22.dp,
                        vertical = 26.dp
                    ),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            /*
             * Indicador principal de operación exitosa.
             */
            Surface(
                shape =
                    RoundedCornerShape(50.dp),
                color =
                    MenesesGreen
            ) {
                Text(
                    text = "✓",
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp,
                            vertical = 8.dp
                        ),
                    color =
                        Color.White,
                    fontSize =
                        30.sp,
                    fontWeight =
                        FontWeight.Bold
                )
            }

            Text(
                text =
                    "Promoción aplicada",
                color =
                    MenesesGreenDark,
                style =
                    MaterialTheme.typography.headlineMedium,
                fontWeight =
                    FontWeight.Bold,
                textAlign =
                    TextAlign.Center
            )

            /*
             * Nombre comercial de la promoción.
             */
            Surface(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(18.dp),
                color =
                    Color.White.copy(
                        alpha = 0.72f
                    )
            ) {
                Text(
                    text =
                        promotionName,
                    modifier =
                        Modifier.padding(
                            horizontal = 18.dp,
                            vertical = 16.dp
                        ),
                    color =
                        MenesesGreenDark,
                    style =
                        MaterialTheme.typography.titleLarge,
                    fontWeight =
                        FontWeight.Bold,
                    textAlign =
                        TextAlign.Center
                )
            }

            HorizontalDivider(
                color =
                    MenesesGreen.copy(
                        alpha = 0.20f
                    )
            )

            /*
             * Desglose económico.
             */
            Column(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                if (paidLine != null) {
                    PromotionResultRow(
                        label =
                            "Cliente pagó",
                        value =
                            paidLine.substringAfter(":")
                                .trim(),
                        emphasized =
                            false
                    )
                }

                if (bonusLine != null) {
                    PromotionResultRow(
                        label =
                            "Bonificación",
                        value =
                            bonusLine.substringAfter(":")
                                .trim(),
                        emphasized =
                            true
                    )
                }
            }

            /*
             * Total acreditado.
             */
            if (totalLine != null) {
                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(20.dp),
                    color =
                        MenesesGreen.copy(
                            alpha = 0.12f
                        )
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text =
                                "TOTAL ACREDITADO",
                            color =
                                MenesesTextSecondary,
                            style =
                                MaterialTheme.typography.labelLarge,
                            fontWeight =
                                FontWeight.Bold,
                            textAlign =
                                TextAlign.Center
                        )

                        Text(
                            text =
                                totalLine.substringAfter(":")
                                    .trim(),
                            color =
                                MenesesGreen,
                            fontSize =
                                38.sp,
                            lineHeight =
                                44.sp,
                            fontWeight =
                                FontWeight.Bold,
                            textAlign =
                                TextAlign.Center
                        )
                    }
                }
            }

            HorizontalDivider(
                color =
                    MenesesGreen.copy(
                        alpha = 0.20f
                    )
            )

            /*
             * Comparación de saldo anterior y saldo nuevo.
             */
            if (
                previousBalanceLine != null ||
                newBalanceLine != null
            ) {
                Column(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {

                    if (previousBalanceLine != null) {
                        Text(
                            text =
                                previousBalanceLine,
                            color =
                                MenesesTextSecondary,
                            style =
                                MaterialTheme.typography.bodyLarge,
                            textAlign =
                                TextAlign.Center
                        )
                    }

                    if (newBalanceLine != null) {
                        Text(
                            text =
                                "SALDO NUEVO",
                            color =
                                MenesesTextSecondary,
                            style =
                                MaterialTheme.typography.labelLarge,
                            fontWeight =
                                FontWeight.Bold,
                            textAlign =
                                TextAlign.Center
                        )

                        Text(
                            text =
                                newBalanceLine
                                    .substringAfter(":")
                                    .trim(),
                            color =
                                MenesesGreenDark,
                            fontSize =
                                44.sp,
                            lineHeight =
                                50.sp,
                            fontWeight =
                                FontWeight.Bold,
                            textAlign =
                                TextAlign.Center
                        )
                    }
                }
            }

            Text(
                text =
                    "✓ Recarga completada correctamente",
                color =
                    MenesesGreenDark,
                style =
                    MaterialTheme.typography.titleSmall,
                fontWeight =
                    FontWeight.Bold,
                textAlign =
                    TextAlign.Center
            )

            Text(
                text =
                    "Revisa el resultado y presiona CERRAR para continuar.",
                color =
                    MenesesTextSecondary,
                style =
                    MaterialTheme.typography.labelMedium,
                textAlign =
                    TextAlign.Center
            )
        }
    }
}


@Composable
private fun PromotionResultRow(
    label: String,
    value: String,
    emphasized: Boolean
) {
    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(14.dp),
        color =
            Color.White.copy(
                alpha = 0.55f
            )
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 13.dp
                    ),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Text(
                text =
                    label,
                color =
                    MenesesTextSecondary,
                style =
                    MaterialTheme.typography.bodyLarge,
                fontWeight =
                    FontWeight.Medium
            )

            Text(
                text =
                    value,
                color =
                    if (emphasized) {
                        MenesesGreen
                    } else {
                        MaterialTheme
                            .colorScheme
                            .onSurface
                    },
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}

@Composable
private fun BalanceResultCard(balanceText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MenesesGreenSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = MenesesBlue
            ) {
                Text(
                    text = "🔎",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 24.sp
                )
            }

            Text(
                text = "Consulta realizada",
                color = MenesesGreenDark,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Saldo disponible",
                color = MenesesTextSecondary,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = balanceText,
                color = MenesesGreen,
                fontSize = 52.sp,
                lineHeight = 58.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Revisa el resultado y presiona CERRAR para continuar.",
                color = MenesesTextSecondary,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StatusCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MenesesSurface)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(description)
        }
    }
}

@Composable
private fun NewCustomerSuccessCard(
    balanceText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MenesesGreenSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = MenesesGreen
            ) {
                Text(
                    text = "✓",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Cliente nuevo creado",
                color = MenesesGreenDark,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Saldo",
                color = MenesesTextSecondary,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = balanceText,
                color = MenesesGreen,
                fontSize = 52.sp,
                lineHeight = 58.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Revisa el resultado y presiona CERRAR para continuar.",
                color = MenesesTextSecondary,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
private fun ErrorCard(
    message: String
) {
    val insufficientBalance =
        message.startsWith(
            "SALDO INSUFICIENTE",
            ignoreCase = true
        )

    val currentBalance =
        if (insufficientBalance) {
            message
                .substringAfter(
                    "Saldo actual:",
                    ""
                )
                .trim()
        } else {
            ""
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Color(0xFFFFEEEE)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = MenesesError
            ) {
                Text(
                    text = "!",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Error",
                color = MenesesError,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (insufficientBalance) {
                Text(
                    text = "SALDO INSUFICIENTE",
                    color = MenesesError,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Saldo actual",
                    color = MenesesTextSecondary,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = currentBalance.ifBlank { "\$0" },
                    color = MenesesError,
                    fontSize = 46.sp,
                    lineHeight = 52.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Revisa el resultado y presiona CERRAR para continuar.",
                    color = MenesesTextSecondary,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = message,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

        }
    }
}

@Composable
private fun CustomerHistoryCard(
    history: CustomerHistory,
    resultScrollState: androidx.compose.foundation.ScrollState
) {
    var displayedHistory
            by remember(
                history
            ) {
                mutableStateOf(
                    history
                )
            }

    var pendingPaymentChange
            by remember {
                mutableStateOf<
                        Pair<
                                CustomerHistoryItem,
                                String
                                >?
                        >(
                    null
                )
            }

    var changingCheckoutId
            by remember {
                mutableStateOf<String?>(
                    null
                )
            }

    var paymentChangeError
            by remember {
                mutableStateOf<String?>(
                    null
                )
            }

    val scope =
        rememberCoroutineScope()

    val financialHoldActive =
        displayedHistory
            .financialHold
            .active

    val latestIncident =
        displayedHistory
            .financialIncidents
            .firstOrNull()

    val currentNfcBalance =
        displayedHistory
            .nfcBalance
            ?: latestIncident
                ?.nfc
                ?.balance

    val currentNfcCounter =
        displayedHistory
            .nfcTransactionCounter
            ?: latestIncident
                ?.nfc
                ?.transactionCounter

    val ledgerBalance =
        latestIncident
            ?.ledger
            ?.balance

    val visibleItems =
        displayedHistory
            .items
            .filter {
                /*
                 * CARD_CREATED se representa abajo mediante
                 * CustomerHistoryActivationCard().
                 *
                 * Así la activación queda SIEMPRE como el primer
                 * registro histórico de la activación actual y
                 * nunca se duplica.
                 */
                it.type !=
                        "CARD_CREATED"
            }

    fun signedMoneyDifference(
        value: Long
    ): String =
        when {
            value > 0 -> "+\$$value"
            value < 0 -> "-\$${-value}"
            else -> "\$0"
        }

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                24.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MenesesSurface
            )
    ) {
        Column(
            modifier =
                Modifier.padding(
                    18.dp
                ),
            verticalArrangement =
                Arrangement.spacedBy(
                    14.dp
                )
        ) {
            Text(
                text =
                    "Historial de tarjeta",
                style =
                    MaterialTheme
                        .typography
                        .titleLarge
            )

            Text(
                text =
                    "Card #${displayedHistory.cardId} · Activación #${displayedHistory.activation.activationNumber}",
                color =
                    MenesesTextSecondary,
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )

            if (
                financialHoldActive
            ) {
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    shape =
                        RoundedCornerShape(
                            22.dp
                        ),
                    colors =
                        CardDefaults
                            .cardColors(
                                containerColor =
                                    Color(
                                        0xFFFFEEEE
                                    )
                            )
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    18.dp
                                ),
                        verticalArrangement =
                            Arrangement
                                .spacedBy(
                                    8.dp
                                )
                    ) {
                        Text(
                            text =
                                "⚠ REVISIÓN MANUAL REQUERIDA",
                            color =
                                MenesesError,
                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "TARJETA #${displayedHistory.cardId} BLOQUEADA PARA OPERACIONES FINANCIERAS",
                            color =
                                MenesesError,
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "La tarjeta conserva un estado físico que no coincide con el estado financiero del sistema. No recargar, cobrar, ajustar ni devolver hasta completar la revisión.",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium
                        )

                        displayedHistory
                            .financialHold
                            .reason
                            ?.takeIf {
                                it.isNotBlank()
                            }
                            ?.let {
                                    reason ->
                                Text(
                                    text =
                                        "Motivo: $reason",
                                    color =
                                        MenesesTextSecondary,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodySmall
                                )
                            }

                        displayedHistory
                            .financialHold
                            .heldAt
                            ?.takeIf {
                                it.isNotBlank()
                            }
                            ?.let {
                                    heldAt ->
                                Text(
                                    text =
                                        "Bloqueo registrado: $heldAt",
                                    color =
                                        MenesesTextSecondary,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodySmall
                                )
                            }
                    }
                }

                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    shape =
                        RoundedCornerShape(
                            22.dp
                        ),
                    colors =
                        CardDefaults
                            .cardColors(
                                containerColor =
                                    MenesesOrangeSoft
                            )
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    18.dp
                                ),
                        verticalArrangement =
                            Arrangement
                                .spacedBy(
                                    12.dp
                                )
                    ) {
                        Text(
                            text =
                                "Diagnóstico financiero",
                            color =
                                MenesesDanger,
                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "Lectura realizada sin modificar NFC, PostgreSQL ni Ledger V2.",
                            color =
                                MenesesTextSecondary,
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )

                        Surface(
                            modifier =
                                Modifier
                                    .fillMaxWidth(),
                            shape =
                                RoundedCornerShape(
                                    16.dp
                                ),
                            color =
                                Color.White
                                    .copy(
                                        alpha =
                                            0.72f
                                    )
                        ) {
                            Column(
                                modifier =
                                    Modifier
                                        .padding(
                                            14.dp
                                        ),
                                verticalArrangement =
                                    Arrangement
                                        .spacedBy(
                                            8.dp
                                        )
                            ) {
                                Text(
                                    text =
                                        "NFC · ESTADO FÍSICO ACTUAL",
                                    color =
                                        MenesesBlueDark,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    text =
                                        if (
                                            currentNfcBalance !=
                                            null &&
                                            currentNfcCounter !=
                                            null
                                        ) {
                                            "Saldo: \$$currentNfcBalance   ·   Contador: $currentNfcCounter"
                                        } else {
                                            "Estado NFC no disponible"
                                        },
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium
                                )
                            }
                        }

                        Surface(
                            modifier =
                                Modifier
                                    .fillMaxWidth(),
                            shape =
                                RoundedCornerShape(
                                    16.dp
                                ),
                            color =
                                Color.White
                                    .copy(
                                        alpha =
                                            0.72f
                                    )
                        ) {
                            Column(
                                modifier =
                                    Modifier
                                        .padding(
                                            14.dp
                                        ),
                                verticalArrangement =
                                    Arrangement
                                        .spacedBy(
                                            8.dp
                                        )
                            ) {
                                Text(
                                    text =
                                        "POSTGRESQL · CARDS",
                                    color =
                                        MenesesPurple,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    text =
                                        "Saldo: \$${displayedHistory.balance}   ·   Contador: ${displayedHistory.transactionCounter}",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium
                                )
                            }
                        }

                        Surface(
                            modifier =
                                Modifier
                                    .fillMaxWidth(),
                            shape =
                                RoundedCornerShape(
                                    16.dp
                                ),
                            color =
                                Color.White
                                    .copy(
                                        alpha =
                                            0.72f
                                    )
                        ) {
                            Column(
                                modifier =
                                    Modifier
                                        .padding(
                                            14.dp
                                        ),
                                verticalArrangement =
                                    Arrangement
                                        .spacedBy(
                                            8.dp
                                        )
                            ) {
                                Text(
                                    text =
                                        "LEDGER V2 · LOTES",
                                    color =
                                        MenesesGreenDark,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    text =
                                        ledgerBalance
                                            ?.let {
                                                "Saldo: \$$it"
                                            }
                                            ?: "Estado Ledger no disponible",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium
                                )
                            }
                        }

                        if (
                            currentNfcBalance !=
                            null &&
                            currentNfcCounter !=
                            null
                        ) {
                            Text(
                                text =
                                    "NFC − PostgreSQL: " +
                                            signedMoneyDifference(
                                                currentNfcBalance -
                                                        displayedHistory.balance
                                            ) +
                                            "   ·   Δ contador: " +
                                            (
                                                    currentNfcCounter -
                                                            displayedHistory.transactionCounter
                                                    ),
                                color =
                                    MenesesDanger,
                                fontWeight =
                                    FontWeight.SemiBold
                            )
                        }

                        if (
                            currentNfcBalance !=
                            null &&
                            ledgerBalance !=
                            null
                        ) {
                            Text(
                                text =
                                    "NFC − Ledger: " +
                                            signedMoneyDifference(
                                                currentNfcBalance -
                                                        ledgerBalance
                                            ),
                                color =
                                    MenesesDanger,
                                fontWeight =
                                    FontWeight.SemiBold
                            )
                        }

                        if (
                            latestIncident !=
                            null
                        ) {
                            HorizontalDivider()

                            Text(
                                text =
                                    "Último incidente",
                                fontWeight =
                                    FontWeight.Bold
                            )

                            Text(
                                text =
                                    "Detectado: ${latestIncident.detectedAt}"
                            )

                            Text(
                                text =
                                    "Tipo: ${latestIncident.type}"
                            )

                            Text(
                                text =
                                    "Transacción: ${latestIncident.transaction.type ?: "N/D"}" +
                                            (
                                                    latestIncident
                                                        .transaction
                                                        .amount
                                                        ?.let {
                                                            " · \$$it"
                                                        }
                                                        ?: ""
                                                    )
                            )

                            Text(
                                text =
                                    "Estado previo: ${latestIncident.transaction.statusBefore ?: "N/D"}"
                            )

                            Text(
                                text =
                                    "BEFORE esperado: \$${latestIncident.expectedBefore.balance} / ${latestIncident.expectedBefore.transactionCounter}"
                            )

                            Text(
                                text =
                                    "AFTER esperado: \$${latestIncident.expectedAfter.balance} / ${latestIncident.expectedAfter.transactionCounter}"
                            )

                            Text(
                                text =
                                    "NFC al detectar incidente: \$${latestIncident.nfc.balance} / ${latestIncident.nfc.transactionCounter}"
                            )

                            Text(
                                text =
                                    "PostgreSQL al detectar: \$${latestIncident.postgreSQL.balance} / ${latestIncident.postgreSQL.transactionCounter}"
                            )

                            Text(
                                text =
                                    "Ledger al detectar: \$${latestIncident.ledger.balance}"
                            )

                            HorizontalDivider()

                            Text(
                                text =
                                    latestIncident
                                        .failureReason,
                                color =
                                    MenesesDanger,
                                fontWeight =
                                    FontWeight.SemiBold
                            )

                            Text(
                                text =
                                    "Transaction ID: ${latestIncident.transactionId}",
                                color =
                                    MenesesTextSecondary,
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall
                            )

                            Text(
                                text =
                                    "Incidentes registrados: ${displayedHistory.financialIncidentsCount}",
                                color =
                                    MenesesTextSecondary,
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall
                            )
                        }
                    }
                }
            } else {
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    shape =
                        RoundedCornerShape(
                            20.dp
                        ),
                    colors =
                        CardDefaults
                            .cardColors(
                                containerColor =
                                    MenesesGreenSoft
                            )
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal =
                                        18.dp,
                                    vertical =
                                        20.dp
                                ),
                        horizontalAlignment =
                            Alignment
                                .CenterHorizontally,
                        verticalArrangement =
                            Arrangement
                                .spacedBy(
                                    6.dp
                                )
                    ) {
                        Text(
                            text =
                                "Saldo disponible",
                            color =
                                MenesesGreenDark,
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium
                        )

                        Text(
                            text =
                                "\$${displayedHistory.balance}",
                            color =
                                MenesesGreen,
                            fontSize =
                                42.sp,
                            lineHeight =
                                48.sp,
                            fontWeight =
                                FontWeight.Bold,
                            textAlign =
                                TextAlign.Center
                        )
                    }
                }
            }

            paymentChangeError
                ?.let {
                        error ->
                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                        colors =
                            CardDefaults
                                .cardColors(
                                    containerColor =
                                        Color(
                                            0xFFFFEEEE
                                        )
                                )
                    ) {
                        Text(
                            text =
                                error,
                            modifier =
                                Modifier
                                    .padding(
                                        14.dp
                                    ),
                            color =
                                MenesesError
                        )
                    }
                }

            HorizontalDivider()

            Text(
                text =
                    "Movimientos confirmados",
                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )

            if (
                visibleItems
                    .isEmpty()
            ) {
                Text(
                    text =
                        "Esta tarjeta no tiene movimientos confirmados.",
                    color =
                        MenesesTextSecondary
                )
            } else {
                visibleItems
                    .forEach {
                            item ->
                        CustomerHistoryItemCard(
                            item =
                                item,
                            changing =
                                changingCheckoutId ==
                                        item.checkoutId,
                            onChangePaymentMethod = {
                                    newMethod ->
                                pendingPaymentChange =
                                    item to
                                            newMethod
                            }
                        )
                    }
            }

            /*
             * La activación se muestra SIEMPRE al final.
             * El historial de movimientos está ordenado DESC,
             * por lo que visualmente esto representa el primer
             * registro histórico de la activación actual.
             */
            CustomerHistoryActivationCard(
                activation =
                    displayedHistory
                        .activation
            )
        }
    }


    pendingPaymentChange
        ?.let {
                pending ->
            val item =
                pending.first

            val newMethod =
                pending.second

            val oldMethodLabel =
                when (
                    item.paymentMethod
                        ?.uppercase()
                ) {
                    "CARD" ->
                        "Tarjeta"

                    else ->
                        "Efectivo"
                }

            val newMethodLabel =
                if (
                    newMethod ==
                    "CARD"
                ) {
                    "Tarjeta"
                } else {
                    "Efectivo"
                }

            androidx.compose
                .material3
                .AlertDialog(
                    onDismissRequest = {
                        if (
                            changingCheckoutId ==
                            null
                        ) {
                            pendingPaymentChange =
                                null
                        }
                    },
                    title = {
                        Text(
                            "Corregir método de pago"
                        )
                    },
                    text = {
                        Text(
                            "Cambiar esta recarga de $oldMethodLabel a $newMethodLabel?\n\n" +
                                    "Este cambio NO modifica saldo, NFC ni Ledger. " +
                                    "Solo corrige la clasificación del pago y quedará auditado."
                        )
                    },
                    dismissButton = {
                        androidx.compose
                            .material3
                            .TextButton(
                                enabled =
                                    changingCheckoutId ==
                                            null,
                                onClick = {
                                    pendingPaymentChange =
                                        null
                                }
                            ) {
                                Text(
                                    "CANCELAR"
                                )
                            }
                    },
                    confirmButton = {
                        androidx.compose
                            .material3
                            .TextButton(
                                enabled =
                                    changingCheckoutId ==
                                            null,
                                onClick = {
                                    val checkoutId =
                                        item.checkoutId
                                            ?: return@TextButton

                                    /*
                                     * Cerramos el diálogo de confirmación inmediatamente.
                                     * Así, tanto en éxito como en error, el operador vuelve
                                     * al historial y no queda una ventana secundaria abierta.
                                     */
                                    pendingPaymentChange =
                                        null

                                    changingCheckoutId =
                                        checkoutId

                                    paymentChangeError =
                                        null

                                    scope.launch {

                                        try {

                                            val refreshed =
                                                withContext(
                                                    Dispatchers.IO
                                                ) {
                                                    MenesesApiClient
                                                        .changeCustomerHistoryPaymentMethod(
                                                            checkoutId =
                                                                checkoutId,
                                                            cardId =
                                                                displayedHistory
                                                                    .cardId,
                                                            uid =
                                                                displayedHistory
                                                                    .uid,
                                                            newPaymentMethod =
                                                                newMethod
                                                        )

                                                    MenesesApiClient
                                                        .getCustomerHistory(
                                                            cardId =
                                                                displayedHistory
                                                                    .cardId,
                                                            uid =
                                                                displayedHistory
                                                                    .uid
                                                        )
                                                }

                                            displayedHistory =
                                                refreshed
                                                    .copy(
                                                        nfcBalance =
                                                            displayedHistory
                                                                .nfcBalance,
                                                        nfcTransactionCounter =
                                                            displayedHistory
                                                                .nfcTransactionCounter
                                                    )

                                        } catch (
                                            e: Exception
                                        ) {

                                            paymentChangeError =
                                                e.message
                                                    ?: "No fue posible corregir el método de pago."

                                            /*
                                             * El error se presenta en la parte superior del
                                             * historial. Si el movimiento estaba abajo, subimos
                                             * automáticamente para que el operador lo vea de
                                             * inmediato.
                                             */
                                            resultScrollState
                                                .animateScrollTo(
                                                    0
                                                )

                                        } finally {

                                            changingCheckoutId =
                                                null
                                        }
                                    }
                                }
                            ) {
                                Text(
                                    "CONFIRMAR"
                                )
                            }
                    }
                )
        }
}


@Composable
private fun CustomerHistoryItemCard(
    item: CustomerHistoryItem,
    changing: Boolean,
    onChangePaymentMethod: (
        String
    ) -> Unit
) {
    val isAdminOperation =
        item.type ==
                "ADJUSTMENT" ||
                (
                        item.type ==
                                "RECHARGE" &&
                                item.rechargePointName ==
                                null
                        )

    val isPromotionalRecharge =
        item.type ==
                "RECHARGE" &&
                item.promotionId !=
                null

    val amountText =
        when (
            item.direction
        ) {
            "CREDIT" ->
                "+\$${item.amount}"

            "DEBIT" ->
                "-\$${item.amount}"

            else ->
                "\$${item.amount}"
        }

    val title =
        when {
            item.type ==
                    "ADJUSTMENT" ->
                "Quitar saldo"

            item.type ==
                    "RECHARGE" &&
                    isAdminOperation ->
                "Recarga de saldo"

            isPromotionalRecharge ->
                "Recarga con promoción"

            item.type ==
                    "RECHARGE" ->
                "Recarga"

            item.type ==
                    "CHARGE" ->
                "Juego"

            else ->
                item.type
        }

    val location =
        when {
            isAdminOperation ->
                "ADMIN"

            item.rechargePointName !=
                    null ->
                item.rechargePointName

            item.gameName !=
                    null ->
                item.gameName

            else ->
                "Operación"
        }

    val cardColor =
        if (
            isAdminOperation
        ) {
            MenesesPurpleSoft
        } else {
            MaterialTheme
                .colorScheme
                .background
        }

    val titleColor =
        if (
            isAdminOperation
        ) {
            MenesesPurple
        } else {
            MaterialTheme
                .colorScheme
                .onSurface
        }

    val amountColor =
        when {
            isAdminOperation ->
                MenesesPurple

            item.direction ==
                    "CREDIT" ->
                MenesesGreenDark

            else ->
                MenesesError
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth(),
        shape =
            RoundedCornerShape(
                18.dp
            ),
        colors =
            CardDefaults
                .cardColors(
                    containerColor =
                        cardColor
                )
    ) {
        Column(
            modifier =
                Modifier
                    .padding(
                        14.dp
                    ),
            verticalArrangement =
                Arrangement
                    .spacedBy(
                        6.dp
                    )
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                horizontalArrangement =
                    Arrangement
                        .SpaceBetween
            ) {
                Text(
                    text =
                        title,
                    color =
                        titleColor,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        amountText,
                    color =
                        amountColor,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold
                )
            }

            Text(
                text =
                    location,
                color =
                    if (
                        isAdminOperation
                    ) {
                        MenesesPurple
                    } else {
                        MenesesTextSecondary
                    },
                fontWeight =
                    if (
                        isAdminOperation
                    ) {
                        FontWeight
                            .SemiBold
                    } else {
                        FontWeight
                            .Normal
                    }
            )

            item.promotionName
                ?.takeIf {
                    it.isNotBlank()
                }
                ?.let {
                        promotionName ->
                    Text(
                        text =
                            promotionName,
                        color =
                            MenesesPurple,
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }

            if (
                isPromotionalRecharge &&
                item.paidRechargeAmount !=
                null &&
                item.promotionalCreditAmount !=
                null
            ) {
                Text(
                    text =
                        "Pago del cliente: \$${item.paidRechargeAmount}",
                    fontWeight =
                        FontWeight.SemiBold
                )

                Text(
                    text =
                        "Promocional: +\$${item.promotionalCreditAmount}",
                    color =
                        MenesesPurple,
                    fontWeight =
                        FontWeight.SemiBold
                )
            }

            if (
                item.quantity !=
                null &&
                item.unitPrice !=
                null
            ) {
                Text(
                    "${item.quantity} personas × \$${item.unitPrice}"
                )
            }

            if (
                item.type ==
                "RECHARGE" &&
                !isAdminOperation
            ) {
                when {
                    item.paymentMethod !=
                            null -> {
                        Text(
                            text =
                                "Método de pago: " +
                                        if (
                                            item.paymentMethod ==
                                            "CARD"
                                        ) {
                                            "Tarjeta"
                                        } else {
                                            "Efectivo"
                                        },
                            color =
                                MenesesTextSecondary,
                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }

                    else -> {
                        Text(
                            text =
                                "Método de pago: No disponible (operación legacy)",
                            color =
                                MenesesTextSecondary,
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )
                    }
                }

                if (
                    item.paymentMethodEditable &&
                    item.checkoutId !=
                    null
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement
                                .spacedBy(
                                    8.dp
                                )
                    ) {
                        OutlinedButton(
                            modifier =
                                Modifier
                                    .weight(
                                        1f
                                    ),
                            enabled =
                                !changing &&
                                        item.paymentMethod !=
                                        "CASH",
                            onClick = {
                                onChangePaymentMethod(
                                    "CASH"
                                )
                            }
                        ) {
                            Text(
                                if (
                                    item.paymentMethod ==
                                    "CASH"
                                ) {
                                    "EFECTIVO ✓"
                                } else {
                                    "EFECTIVO"
                                }
                            )
                        }

                        OutlinedButton(
                            modifier =
                                Modifier
                                    .weight(
                                        1f
                                    ),
                            enabled =
                                !changing &&
                                        item.paymentMethod !=
                                        "CARD",
                            onClick = {
                                onChangePaymentMethod(
                                    "CARD"
                                )
                            }
                        ) {
                            Text(
                                if (
                                    item.paymentMethod ==
                                    "CARD"
                                ) {
                                    "TARJETA ✓"
                                } else {
                                    "TARJETA"
                                }
                            )
                        }
                    }
                }
            }

            Text(
                text =
                    "Saldo: \$${item.balanceBefore} → \$${item.balanceAfter}"
            )

            Text(
                text =
                    formatServerDate(
                        item.createdAt
                    ),
                color =
                    MenesesTextSecondary,
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )
        }
    }
}


@Composable
private fun CustomerHistoryActivationCard(
    activation:
    com.espectacularesmeneses.feria.model.CustomerHistoryActivation
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth(),
        shape =
            RoundedCornerShape(
                18.dp
            ),
        colors =
            CardDefaults
                .cardColors(
                    containerColor =
                        MenesesBlueSoft
                )
    ) {
        Column(
            modifier =
                Modifier
                    .padding(
                        14.dp
                    ),
            verticalArrangement =
                Arrangement
                    .spacedBy(
                        6.dp
                    )
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                horizontalArrangement =
                    Arrangement
                        .SpaceBetween
            ) {
                Text(
                    text =
                        "Activación de tarjeta",
                    color =
                        MenesesBlueDark,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        if (
                            activation
                                .activationFeeKnown
                        ) {
                            "\$${activation.activationFee}"
                        } else {
                            "N/D"
                        },
                    color =
                        MenesesBlueDark,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold
                )
            }

            Text(
                text =
                    "Activación #${activation.activationNumber}",
                color =
                    MenesesTextSecondary
            )

            Text(
                text =
                    if (
                        activation
                            .activationFeeKnown
                    ) {
                        "Tarifa de activación"
                    } else {
                        "Tarifa de activación no registrada"
                    }
            )

            Text(
                text =
                    formatServerDate(
                        activation.startedAt
                    ),
                color =
                    MenesesTextSecondary,
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )
        }
    }
}


private fun formatServerDate(value: String): String {
    /*
     * PostgreSQL/API conservan las fechas en UTC, que es lo correcto
     * para almacenamiento. La interfaz las presenta en horario de
     * Ciudad de México (America/Mexico_City, UTC-6 actualmente).
     */
    val inputFormats =
        listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            "yyyy-MM-dd'T'HH:mm:ssX"
        )

    val parsedDate =
        inputFormats
            .firstNotNullOfOrNull { pattern ->
                try {
                    SimpleDateFormat(pattern, Locale.US).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                        isLenient = false
                    }.parse(value)
                } catch (_: Exception) {
                    null
                }
            }

    if (parsedDate == null) {
        return value.replace("T", " ").replace("Z", "").take(16)
    }

    return SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        Locale("es", "MX")
    ).apply {
        timeZone = TimeZone.getTimeZone("America/Mexico_City")
    }.format(parsedDate)
}


