package com.espectacularesmeneses.feria.ui.admin

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.espectacularesmeneses.feria.model.AdminDeviceReport
import com.espectacularesmeneses.feria.model.AdminGameReport
import com.espectacularesmeneses.feria.model.AdminRechargePointReport
import com.espectacularesmeneses.feria.model.AdminReportSummary
import com.espectacularesmeneses.feria.ui.theme.MenesesBlue
import com.espectacularesmeneses.feria.ui.theme.MenesesBlueSoft
import com.espectacularesmeneses.feria.ui.theme.MenesesBorder
import com.espectacularesmeneses.feria.ui.theme.MenesesGreen
import com.espectacularesmeneses.feria.ui.theme.MenesesGreenDark
import com.espectacularesmeneses.feria.ui.theme.MenesesGreenSoft
import com.espectacularesmeneses.feria.ui.theme.MenesesPurple
import com.espectacularesmeneses.feria.ui.theme.MenesesPurpleSoft
import com.espectacularesmeneses.feria.ui.theme.MenesesSurface
import com.espectacularesmeneses.feria.ui.theme.MenesesTextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/*
 * =========================================================
 * ADMIN REPORTS SCREEN
 * =========================================================
 *
 * Esta pantalla NO realiza llamadas HTTP directamente.
 *
 * MainActivity controla:
 * - carga de datos;
 * - errores;
 * - rango de fechas;
 * - navegación;
 * - apertura del historial por dispositivo.
 *
 * Eso mantiene la UI separada de la lógica de red.
 * =========================================================
 */

@Composable
fun AdminReportsScreen(
    from: String,
    to: String,
    summary: AdminReportSummary?,
    games: List<AdminGameReport>,
    rechargePoints: List<AdminRechargePointReport>,
    devices: List<AdminDeviceReport>,
    loading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onToday: () -> Unit,
    onLast7Days: () -> Unit,
    onApplyCustomRange: (String, String) -> Unit,
    onViewDeviceHistory: (AdminDeviceReport) -> Unit
) {
    var showCustomRange by remember {
        mutableStateOf(false)
    }

    var customFrom by remember(from) {
        mutableStateOf(from)
    }

    var customTo by remember(to) {
        mutableStateOf(to)
    }

    val context =
        LocalContext.current

    Column(
        modifier =
            Modifier.fillMaxWidth(),
        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        /*
         * Encabezado.
         */
        Row(
            modifier =
                Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack
            ) {
                Text("←")
            }

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {
                Text(
                    text = "📊 Reportes",
                    style =
                        MaterialTheme.typography.headlineMedium,
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        if (from == to) {
                            "Fecha: $from"
                        } else {
                            "$from  →  $to"
                        },
                    color =
                        MenesesTextSecondary
                )
            }
        }

        /*
         * =================================================
         * FILTROS
         * =================================================
         */
        Card(
            modifier =
                Modifier.fillMaxWidth(),
            shape =
                RoundedCornerShape(18.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        MenesesBlueSoft
                )
        ) {
            Column(
                modifier =
                    Modifier.padding(14.dp),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Periodo",
                    color =
                        MenesesTextSecondary
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    ReportFilterButton(
                        modifier =
                            Modifier.weight(1f),
                        text = "HOY",
                        selected =
                            from == to &&
                                    !showCustomRange,
                        enabled =
                            !loading,
                        onClick = {
                            showCustomRange =
                                false

                            onToday()
                        }
                    )

                    ReportFilterButton(
                        modifier =
                            Modifier.weight(1f),
                        text = "7 DÍAS",
                        selected =
                            from != to &&
                                    !showCustomRange,
                        enabled =
                            !loading,
                        onClick = {
                            showCustomRange =
                                false

                            onLast7Days()
                        }
                    )

                    ReportFilterButton(
                        modifier =
                            Modifier.weight(1f),
                        text = "FECHA",
                        selected =
                            showCustomRange,
                        enabled =
                            !loading,
                        onClick = {
                            showCustomRange =
                                !showCustomRange

                            customFrom =
                                from

                            customTo =
                                to
                        }
                    )
                }

                Text(
                    text =
                        if (from == to) {
                            "Mostrando: $from"
                        } else {
                            "Mostrando: $from → $to"
                        },
                    color =
                        MenesesBlue,
                    fontWeight =
                        FontWeight.SemiBold
                )

                if (
                    showCustomRange
                ) {
                    HorizontalDivider(
                        color =
                            MenesesBorder
                    )

                    Text(
                        text =
                            "Rango personalizado",
                        fontWeight =
                            FontWeight.Bold
                    )

                    OutlinedButton(
                        modifier =
                            Modifier.fillMaxWidth(),
                        enabled =
                            !loading,
                        onClick = {
                            showDatePicker(
                                context = context,
                                initialDate =
                                    customFrom.ifBlank {
                                        from
                                    },
                                onDateSelected = {
                                        selectedDate ->

                                    customFrom =
                                        selectedDate

                                    /*
                                     * Si la nueva fecha inicial queda
                                     * después de "Hasta", igualamos ambas
                                     * para mantener un rango válido.
                                     */
                                    if (
                                        customTo.isBlank() ||
                                        selectedDate >
                                        customTo
                                    ) {
                                        customTo =
                                            selectedDate
                                    }
                                }
                            )
                        }
                    ) {
                        Text(
                            text =
                                "Desde: " +
                                        formatReportDate(
                                            customFrom
                                        )
                        )
                    }

                    OutlinedButton(
                        modifier =
                            Modifier.fillMaxWidth(),
                        enabled =
                            !loading,
                        onClick = {
                            showDatePicker(
                                context = context,
                                initialDate =
                                    customTo.ifBlank {
                                        to
                                    },
                                onDateSelected = {
                                        selectedDate ->

                                    customTo =
                                        selectedDate

                                    /*
                                     * Si "Hasta" queda antes que "Desde",
                                     * movemos "Desde" a la misma fecha.
                                     */
                                    if (
                                        customFrom.isBlank() ||
                                        selectedDate <
                                        customFrom
                                    ) {
                                        customFrom =
                                            selectedDate
                                    }
                                }
                            )
                        }
                    ) {
                        Text(
                            text =
                                "Hasta: " +
                                        formatReportDate(
                                            customTo
                                        )
                        )
                    }

                    Button(
                        modifier =
                            Modifier.fillMaxWidth(),
                        enabled =
                            !loading &&
                                    isValidReportDate(
                                        customFrom
                                    ) &&
                                    isValidReportDate(
                                        customTo
                                    ) &&
                                    customFrom <= customTo,
                        onClick = {
                            /*
                             * Cerramos inmediatamente el selector
                             * personalizado y luego consultamos.
                             */
                            showCustomRange =
                                false

                            onApplyCustomRange(
                                customFrom,
                                customTo
                            )
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    MenesesBlue
                            )
                    ) {
                        Text(
                            "APLICAR RANGO"
                        )
                    }
                }

                OutlinedButton(
                    modifier =
                        Modifier.fillMaxWidth(),
                    enabled =
                        !loading,
                    onClick =
                        onRefresh
                ) {
                    Text(
                        if (loading) {
                            "Actualizando..."
                        } else {
                            "Actualizar"
                        }
                    )
                }
            }
        }

        if (loading && summary == null) {
            ReportStatusCard(
                title = "Cargando reportes...",
                message =
                    "Consultando la información del sistema."
            )

            return
        }

        if (
            errorMessage != null &&
            summary == null
        ) {
            ReportErrorCard(
                message = errorMessage,
                onRetry = onRefresh
            )

            return
        }

        if (errorMessage != null) {
            ReportWarningCard(
                message = errorMessage
            )
        }

        /*
         * =================================================
         * RESUMEN
         * =================================================
         */
        ReportSectionTitle(
            title = "Resumen"
        )

        if (summary == null) {
            ReportStatusCard(
                title = "Sin información",
                message =
                    "No hay datos disponibles para el periodo seleccionado."
            )
        } else {
            ReportMetricCard(
                emoji = "💵",
                title =
                    "Total recargas taquilla",
                value =
                    "$${summary.rechargePointAmount}",
                subtitle =
                    "Saldo cargado exclusivamente desde taquillas.",
                accent =
                    MenesesGreen,
                background =
                    MenesesGreenSoft
            )

            ReportMetricCard(
                emoji = "🎟️",
                title =
                    "Consumo en juegos",
                value =
                    "$${summary.gameConsumptionAmount}",
                subtitle =
                    "Saldo utilizado por clientes en juegos.",
                accent =
                    MenesesBlue,
                background =
                    MenesesBlueSoft
            )

            ReportMetricCard(
                emoji = "👥",
                title =
                    "# Total de Personas en Juegos",
                value =
                    summary.gamePeopleCount.toString(),
                subtitle =
                    "Suma de personas cobradas en todos los juegos.",
                accent =
                    MenesesPurple,
                background =
                    MenesesPurpleSoft
            )

            /*
             * =============================================
             * OPERACIONES ADMIN
             * =============================================
             */
            ReportSectionTitle(
                title =
                    "Operaciones administrativas"
            )

            Card(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(20.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MenesesPurpleSoft
                    )
            ) {
                Column(
                    modifier =
                        Modifier.padding(18.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    AdminAmountRow(
                        label =
                            "Recargas de saldo",
                        amount =
                            summary.adminRechargeAmount
                    )

                    HorizontalDivider(
                        color =
                            MenesesBorder
                    )

                    AdminAmountRow(
                        label =
                            "Saldo retirado",
                        amount =
                            summary.adminAdjustmentAmount
                    )
                }
            }
        }

        /*
         * =================================================
         * POR JUEGO
         * =================================================
         */
        ReportSectionTitle(
            title = "Por juego"
        )

        if (games.isEmpty()) {
            EmptyReportCard(
                text =
                    "No hubo consumo en juegos durante este periodo."
            )
        } else {
            games.forEach { game ->
                GameReportCard(
                    game = game,
                    showDailyBreakdown =
                        from != to
                )
            }
        }

        /*
         * =================================================
         * POR TAQUILLA
         * =================================================
         */
        ReportSectionTitle(
            title = "Por taquilla"
        )

        if (rechargePoints.isEmpty()) {
            EmptyReportCard(
                text =
                    "No hubo recargas de taquilla durante este periodo."
            )
        } else {
            rechargePoints.forEach { point ->
                RechargePointReportCard(
                    rechargePoint =
                        point,
                    showDailyBreakdown =
                        from != to
                )
            }
        }

        /*
         * =================================================
         * POR DISPOSITIVO
         * =================================================
         */
        ReportSectionTitle(
            title = "Por dispositivo"
        )

        if (devices.isEmpty()) {
            EmptyReportCard(
                text =
                    "No hay dispositivos con tiempo registrado en este periodo."
            )
        } else {
            devices.forEach { device ->
                DeviceReportCard(
                    device = device,
                    onViewHistory = {
                        onViewDeviceHistory(
                            device
                        )
                    }
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )
    }
}


@Composable
private fun ReportFilterButton(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    if (selected) {
        Button(
            modifier =
                modifier,
            enabled =
                enabled,
            onClick =
                onClick,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        MenesesBlue
                )
        ) {
            Text(
                text,
                textAlign =
                    TextAlign.Center
            )
        }
    } else {
        OutlinedButton(
            modifier =
                modifier,
            enabled =
                enabled,
            onClick =
                onClick
        ) {
            Text(
                text,
                textAlign =
                    TextAlign.Center
            )
        }
    }
}


@Composable
private fun ReportSectionTitle(
    title: String
) {
    Text(
        text = title,
        style =
            MaterialTheme.typography.titleLarge,
        fontWeight =
            FontWeight.Bold
    )
}


@Composable
private fun ReportMetricCard(
    emoji: String,
    title: String,
    value: String,
    subtitle: String,
    accent: Color,
    background: Color
) {
    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(22.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    background
            )
    ) {
        Column(
            modifier =
                Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "$emoji  $title",
                color =
                    accent,
                fontWeight =
                    FontWeight.Bold,
                style =
                    MaterialTheme.typography.titleMedium
            )

            Text(
                text = value,
                color =
                    accent,
                fontWeight =
                    FontWeight.Bold,
                style =
                    MaterialTheme.typography.headlineMedium
            )

            Text(
                text = subtitle,
                color =
                    MenesesTextSecondary
            )
        }
    }
}


@Composable
private fun AdminAmountRow(
    label: String,
    amount: Long
) {
    Row(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color =
                MenesesPurple,
            fontWeight =
                FontWeight.SemiBold
        )

        Text(
            text = "$$amount",
            color =
                MenesesPurple,
            fontWeight =
                FontWeight.Bold,
            style =
                MaterialTheme.typography.titleLarge
        )
    }
}


@Composable
private fun GameReportCard(
    game: AdminGameReport,
    showDailyBreakdown: Boolean
) {
    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MenesesSurface
            )
    ) {
        Column(
            modifier =
                Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(7.dp)
        ) {
            Text(
                text =
                    "🎠 ${game.name}",
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text =
                    "${game.peopleCount} personas",
                color =
                    MenesesPurple,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text =
                    "$${game.consumptionAmount} consumidos",
                color =
                    MenesesBlue,
                fontWeight =
                    FontWeight.Bold,
                style =
                    MaterialTheme.typography.titleLarge
            )

            Text(
                text =
                    "Precio actual: $${game.currentPrice} por persona",
                color =
                    MenesesTextSecondary
            )

            if (
                showDailyBreakdown &&
                game.dailyBreakdown.isNotEmpty()
            ) {
                HorizontalDivider(
                    color =
                        MenesesBorder
                )

                Text(
                    text =
                        "Desglose por día",
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        MenesesTextSecondary
                )

                game.dailyBreakdown.forEach {
                        daily ->

                    DailyAmountRow(
                        date =
                            formatReportDate(
                                daily.date
                            ),
                        amount =
                            daily.consumptionAmount,
                        accent =
                            MenesesBlue
                    )
                }
            }
        }
    }
}


@Composable
private fun RechargePointReportCard(
    rechargePoint:
    AdminRechargePointReport,
    showDailyBreakdown: Boolean
) {
    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MenesesGreenSoft
            )
    ) {
        Column(
            modifier =
                Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(7.dp)
        ) {
            Text(
                text =
                    "🏪 ${rechargePoint.name}",
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text =
                    "$${rechargePoint.rechargedAmount}",
                color =
                    MenesesGreenDark,
                fontWeight =
                    FontWeight.Bold,
                style =
                    MaterialTheme.typography.headlineMedium
            )

            Text(
                text =
                    "Total recargado",
                color =
                    MenesesTextSecondary
            )

            if (
                showDailyBreakdown &&
                rechargePoint.dailyBreakdown.isNotEmpty()
            ) {
                HorizontalDivider(
                    color =
                        MenesesGreenDark.copy(
                            alpha = 0.18f
                        )
                )

                Text(
                    text =
                        "Desglose por día",
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        MenesesTextSecondary
                )

                rechargePoint.dailyBreakdown.forEach {
                        daily ->

                    DailyAmountRow(
                        date =
                            formatReportDate(
                                daily.date
                            ),
                        amount =
                            daily.rechargedAmount,
                        accent =
                            MenesesGreenDark
                    )
                }
            }
        }
    }
}


@Composable
private fun DailyAmountRow(
    date: String,
    amount: Long,
    accent: Color
) {
    Row(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Text(
            text = date,
            color =
                MenesesTextSecondary
        )

        Text(
            text =
                "$$amount",
            color =
                accent,
            fontWeight =
                FontWeight.Bold
        )
    }
}


@Composable
private fun DeviceReportCard(
    device: AdminDeviceReport,
    onViewHistory: () -> Unit
) {
    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MenesesSurface
            )
    ) {
        Column(
            modifier =
                Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text =
                    "📱 ${device.name}",
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text =
                    device.code,
                color =
                    MenesesTextSecondary
            )

            Text(
                text =
                    "Tiempo registrado: " +
                            formatDuration(
                                device.sessionSeconds
                            ),
                color =
                    MenesesBlue,
                fontWeight =
                    FontWeight.SemiBold
            )

            Button(
                modifier =
                    Modifier.fillMaxWidth(),
                onClick =
                    onViewHistory,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            MenesesBlue
                    )
            ) {
                Text(
                    text =
                        "VER HISTORIAL"
                )
            }
        }
    }
}


@Composable
private fun EmptyReportCard(
    text: String
) {
    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(18.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MenesesSurface
            )
    ) {
        Text(
            modifier =
                Modifier.padding(18.dp),
            text = text,
            color =
                MenesesTextSecondary,
            textAlign =
                TextAlign.Center
        )
    }
}


@Composable
private fun ReportStatusCard(
    title: String,
    message: String
) {
    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MenesesBlueSoft
            )
    ) {
        Column(
            modifier =
                Modifier.padding(20.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                color =
                    MenesesBlue,
                fontWeight =
                    FontWeight.Bold,
                style =
                    MaterialTheme.typography.titleLarge
            )

            Text(
                text = message,
                color =
                    MenesesTextSecondary,
                textAlign =
                    TextAlign.Center
            )
        }
    }
}


@Composable
private fun ReportWarningCard(
    message: String
) {
    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(18.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFFFFF4E5)
            )
    ) {
        Text(
            modifier =
                Modifier.padding(16.dp),
            text =
                "⚠️ $message",
            color =
                Color(0xFF8A5500)
        )
    }
}


@Composable
private fun ReportErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFFFFECEC)
            )
    ) {
        Column(
            modifier =
                Modifier.padding(20.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text =
                    "No fue posible cargar los reportes",
                fontWeight =
                    FontWeight.Bold,
                textAlign =
                    TextAlign.Center
            )

            Text(
                text = message,
                color =
                    MenesesTextSecondary,
                textAlign =
                    TextAlign.Center
            )

            OutlinedButton(
                onClick =
                    onRetry
            ) {
                Text("REINTENTAR")
            }
        }
    }
}


private fun showDatePicker(
    context: android.content.Context,
    initialDate: String,
    onDateSelected: (String) -> Unit
) {
    val mexicoTimeZone =
        TimeZone.getTimeZone(
            "America/Mexico_City"
        )

    val calendar =
        Calendar.getInstance(
            mexicoTimeZone
        )

    /*
     * Si ya existe una fecha válida, la usamos
     * como posición inicial del calendario.
     */
    try {
        val parser =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).apply {
                timeZone =
                    mexicoTimeZone
                isLenient =
                    false
            }

        val parsed =
            parser.parse(
                initialDate
                    .substringBefore("T")
                    .take(10)
            )

        if (
            parsed != null
        ) {
            calendar.time =
                parsed
        }
    } catch (
        _: Exception
    ) {
    }

    DatePickerDialog(
        context,
        {
                _,
                year,
                month,
                dayOfMonth ->

            val selectedCalendar =
                Calendar.getInstance(
                    mexicoTimeZone
                ).apply {

                    set(
                        Calendar.YEAR,
                        year
                    )

                    set(
                        Calendar.MONTH,
                        month
                    )

                    set(
                        Calendar.DAY_OF_MONTH,
                        dayOfMonth
                    )
                }

            val formatted =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.US
                ).apply {
                    timeZone =
                        mexicoTimeZone
                }.format(
                    selectedCalendar.time
                )

            onDateSelected(
                formatted
            )
        },
        calendar.get(
            Calendar.YEAR
        ),
        calendar.get(
            Calendar.MONTH
        ),
        calendar.get(
            Calendar.DAY_OF_MONTH
        )
    ).show()
}


private fun formatReportDate(
    value: String
): String {

    if (
        value.isBlank()
    ) {
        return "Seleccionar fecha"
    }

    /*
     * El backend actual ya envía YYYY-MM-DD.
     * Dejamos esta normalización defensiva para
     * instalaciones que todavía respondan ISO completo:
     *
     * 2026-08-13T06:00:00.000Z
     *         ↓
     * 2026-08-13
     */
    val normalized =
        value
            .substringBefore("T")
            .take(10)

    val parts =
        normalized.split("-")

    if (
        parts.size != 3
    ) {
        return value
    }

    return "${parts[2]}-${parts[1]}-${parts[0]}"
}


private fun isValidReportDate(
    value: String
): Boolean {

    return Regex(
        """^\d{4}-\d{2}-\d{2}$"""
    ).matches(
        value
    )
}


/*
 * Recibe segundos para conservar precisión en backend/modelo.
 */
private fun formatDuration(
    totalSeconds: Long
): String {

    if (
        totalSeconds <= 0
    ) {
        return "0 min"
    }

    val hours =
        totalSeconds / 3_600

    val minutes =
        (
                totalSeconds %
                        3_600
                ) / 60

    return when {
        hours > 0 &&
                minutes > 0 ->
            "${hours} h ${minutes} min"

        hours > 0 ->
            "${hours} h"

        else ->
            "${minutes} min"
    }
}
