package com.espectacularesmeneses.feria.ui.admin

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.espectacularesmeneses.feria.model.AdminDeviceHistory
import com.espectacularesmeneses.feria.model.AdminDeviceReport
import com.espectacularesmeneses.feria.model.AdminDeviceSessionReport
import com.espectacularesmeneses.feria.ui.theme.MenesesBlue
import com.espectacularesmeneses.feria.ui.theme.MenesesBlueSoft
import com.espectacularesmeneses.feria.ui.theme.MenesesGreenDark
import com.espectacularesmeneses.feria.ui.theme.MenesesGreenSoft
import com.espectacularesmeneses.feria.ui.theme.MenesesPurple
import com.espectacularesmeneses.feria.ui.theme.MenesesPurpleSoft
import com.espectacularesmeneses.feria.ui.theme.MenesesSurface
import com.espectacularesmeneses.feria.ui.theme.MenesesTextSecondary
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/*
 * =========================================================
 * ADMIN DEVICE HISTORY
 * =========================================================
 *
 * Historial cronológico de un Ulefone.
 *
 * GAME      -> azul
 * RECHARGE  -> verde
 * ADMIN     -> morado
 *
 * No mostramos UUIDs, Card IDs ni datos internos al operador.
 * =========================================================
 */

@Composable
fun AdminDeviceHistoryScreen(
    selectedDevice: AdminDeviceReport?,
    history: AdminDeviceHistory?,
    loading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier =
            Modifier.fillMaxWidth(),
        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

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
                    text =
                        "Historial del dispositivo",
                    style =
                        MaterialTheme.typography.headlineSmall,
                    fontWeight =
                        FontWeight.Bold
                )

                val deviceName =
                    history
                        ?.device
                        ?.name
                        ?: selectedDevice
                            ?.name
                        ?: "Dispositivo"

                Text(
                    text = deviceName,
                    color =
                        MenesesTextSecondary
                )
            }
        }

        /*
         * Cabecera del Ulefone.
         */
        val device =
            history?.device

        Card(
            modifier =
                Modifier.fillMaxWidth(),
            shape =
                RoundedCornerShape(22.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        MenesesBlueSoft
                )
        ) {
            Column(
                modifier =
                    Modifier.padding(18.dp),
                verticalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text =
                        "📱 " +
                                (
                                        device?.name
                                            ?: selectedDevice?.name
                                            ?: "Ulefone"
                                        ),
                    color =
                        MenesesBlue,
                    style =
                        MaterialTheme.typography.titleLarge,
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        device?.code
                            ?: selectedDevice?.code
                            ?: "",
                    color =
                        MenesesTextSecondary
                )

                if (
                    history != null
                ) {
                    Text(
                        text =
                            if (
                                history.from ==
                                history.to
                            ) {
                                "Fecha: ${history.from}"
                            } else {
                                "${history.from} → ${history.to}"
                            },
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        }

        if (
            loading &&
            history == null
        ) {
            DeviceHistoryStatusCard(
                title =
                    "Cargando historial...",
                message =
                    "Consultando las sesiones de este dispositivo."
            )

            return
        }

        if (
            errorMessage != null &&
            history == null
        ) {
            DeviceHistoryErrorCard(
                message =
                    errorMessage,
                onRetry =
                    onRetry
            )

            return
        }

        if (
            errorMessage != null
        ) {
            Card(
                modifier =
                    Modifier.fillMaxWidth(),
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
                        "⚠️ $errorMessage",
                    color =
                        Color(0xFF8A5500)
                )
            }
        }

        Text(
            text = "Sesiones",
            style =
                MaterialTheme.typography.titleLarge,
            fontWeight =
                FontWeight.Bold
        )

        if (
            history == null ||
            history.sessions.isEmpty()
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
                    text =
                        "No hay sesiones registradas para este dispositivo en el periodo seleccionado.",
                    color =
                        MenesesTextSecondary,
                    textAlign =
                        TextAlign.Center
                )
            }
        } else {
            history.sessions.forEach { session ->
                DeviceSessionCard(
                    session =
                        session
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
private fun DeviceSessionCard(
    session: AdminDeviceSessionReport
) {
    val mode =
        session.mode.uppercase()

    val accent =
        when (mode) {
            "GAME" ->
                MenesesBlue

            "RECHARGE" ->
                MenesesGreenDark

            "ADMIN" ->
                MenesesPurple

            else ->
                MaterialTheme
                    .colorScheme
                    .primary
        }

    val background =
        when (mode) {
            "GAME" ->
                MenesesBlueSoft

            "RECHARGE" ->
                MenesesGreenSoft

            "ADMIN" ->
                MenesesPurpleSoft

            else ->
                MenesesSurface
        }

    val title =
        when (mode) {
            "GAME" ->
                "🎠 GAME"

            "RECHARGE" ->
                "🏪 TAQUILLA"

            "ADMIN" ->
                "👤 ADMIN"

            else ->
                mode
        }

    val location =
        when (mode) {
            "GAME" ->
                session.game?.name
                    ?: "Juego"

            "RECHARGE" ->
                session
                    .rechargePoint
                    ?.name
                    ?: "Taquilla"

            "ADMIN" ->
                "Administrador"

            else ->
                ""
        }

    val start =
        formatMexicoTime(
            session.visibleStartedAt
        )

    val end =
        if (
            session.status.equals(
                "ACTIVE",
                ignoreCase = true
            )
        ) {
            "Ahora"
        } else {
            formatMexicoTime(
                session.visibleEndedAt
            )
        }

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
            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = accent,
                    fontWeight =
                        FontWeight.Bold,
                    style =
                        MaterialTheme.typography.titleMedium
                )

                if (
                    session.status.equals(
                        "ACTIVE",
                        ignoreCase = true
                    )
                ) {
                    Text(
                        text = "ACTIVA",
                        color = accent,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Text(
                text = location,
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text =
                    formatMexicoDate(
                        session.visibleStartedAt
                    ),
                color =
                    accent,
                fontWeight =
                    FontWeight.SemiBold
            )

            Text(
                text =
                    "$start → $end",
                color =
                    MenesesTextSecondary
            )

            Text(
                text =
                    "Duración: " +
                            formatDuration(
                                session.durationSeconds
                            ),
                color = accent,
                fontWeight =
                    FontWeight.SemiBold
            )

            HorizontalDivider(
                color =
                    accent.copy(
                        alpha = 0.18f
                    )
            )

            when (mode) {

                "GAME" -> {
                    SessionMetricRow(
                        label = "Personas",
                        value =
                            session
                                .metrics
                                .gamePeopleCount
                                .toString(),
                        accent = accent
                    )

                    SessionMetricRow(
                        label = "Consumo",
                        value =
                            "$${session.metrics.gameConsumptionAmount}",
                        accent = accent
                    )
                }

                "RECHARGE" -> {
                    SessionMetricRow(
                        label =
                            "Total recargado",
                        value =
                            "$${session.metrics.rechargeAmount}",
                        accent = accent
                    )
                }

                "ADMIN" -> {
                    SessionMetricRow(
                        label =
                            "Recargas de saldo",
                        value =
                            "$${session.metrics.adminRechargeAmount}",
                        accent = accent
                    )

                    SessionMetricRow(
                        label =
                            "Saldo retirado",
                        value =
                            "$${session.metrics.adminAdjustmentAmount}",
                        accent = accent
                    )
                }
            }
        }
    }
}


@Composable
private fun SessionMetricRow(
    label: String,
    value: String,
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
            text = label,
            color =
                MenesesTextSecondary
        )

        Text(
            text = value,
            color = accent,
            fontWeight =
                FontWeight.Bold,
            style =
                MaterialTheme.typography.titleMedium
        )
    }
}


@Composable
private fun DeviceHistoryStatusCard(
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
private fun DeviceHistoryErrorCard(
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
                    "No fue posible cargar el historial",
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

            Button(
                onClick =
                    onRetry,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            MenesesBlue
                    )
            ) {
                Text("REINTENTAR")
            }
        }
    }
}


/*
 * El backend envía ISO-8601 en UTC.
 * La UI lo presenta en America/Mexico_City.
 */
private fun formatMexicoDate(
    isoDate: String
): String {

    val sourcePatterns =
        listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            "yyyy-MM-dd'T'HH:mm:ssX"
        )

    for (
    pattern in sourcePatterns
    ) {
        try {
            val parser =
                SimpleDateFormat(
                    pattern,
                    Locale.US
                ).apply {
                    timeZone =
                        TimeZone.getTimeZone(
                            "UTC"
                        )
                }

            val date =
                parser.parse(
                    isoDate
                )
                    ?: continue

            return SimpleDateFormat(
                "dd-MM-yyyy",
                Locale("es", "MX")
            ).apply {
                timeZone =
                    TimeZone.getTimeZone(
                        "America/Mexico_City"
                    )
            }.format(
                date
            )

        } catch (
            _: Exception
        ) {
        }
    }

    return isoDate
}


private fun formatMexicoTime(
    isoDate: String
): String {

    val sourcePatterns =
        listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            "yyyy-MM-dd'T'HH:mm:ssX"
        )

    for (
    pattern in sourcePatterns
    ) {
        try {
            val parser =
                SimpleDateFormat(
                    pattern,
                    Locale.US
                ).apply {
                    timeZone =
                        TimeZone.getTimeZone(
                            "UTC"
                        )
                }

            val date =
                parser.parse(
                    isoDate
                )
                    ?: continue

            return SimpleDateFormat(
                "HH:mm",
                Locale("es", "MX")
            ).apply {
                timeZone =
                    TimeZone.getTimeZone(
                        "America/Mexico_City"
                    )
            }.format(
                date
            )

        } catch (
            _: Exception
        ) {
        }
    }

    return isoDate
}


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

    val seconds =
        totalSeconds % 60

    return when {
        hours > 0 &&
                minutes > 0 ->
            "${hours} h ${minutes} min"

        hours > 0 ->
            "${hours} h"

        minutes > 0 ->
            "${minutes} min"

        else ->
            "${seconds} s"
    }
}
