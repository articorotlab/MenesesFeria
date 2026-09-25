package com.espectacularesmeneses.feria.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.espectacularesmeneses.feria.model.AdminPromotion
import com.espectacularesmeneses.feria.model.AdminRechargePoint
import com.espectacularesmeneses.feria.ui.theme.MenesesGreen
import com.espectacularesmeneses.feria.ui.theme.MenesesPurple
import com.espectacularesmeneses.feria.ui.theme.MenesesPurpleSoft
import com.espectacularesmeneses.feria.ui.theme.MenesesSurface
import com.espectacularesmeneses.feria.ui.theme.MenesesTextSecondary

@Composable
fun AdminPromotionsScreen(
    promotions: List<AdminPromotion>,
    loading: Boolean,
    saving: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onCreatePromotion: () -> Unit,
    onModifyPromotion: (AdminPromotion) -> Unit,
    onSetPromotionActive: (
        promotion: AdminPromotion,
        active: Boolean
    ) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PromotionHeader(
            title = "🎁 Promociones",
            subtitle = "Crear, modificar, desactivar y reactivar promociones.",
            onBack = onBack,
            enabled = !saving
        )

        PromotionErrorCard(
            errorMessage = errorMessage,
            enabled = !loading && !saving,
            onRetry = onRefresh
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading && !saving,
            colors = ButtonDefaults.buttonColors(
                containerColor = MenesesPurple
            ),
            onClick = onCreatePromotion
        ) {
            Text("+ Crear promoción")
        }

        when {
            loading -> {
                PromotionMessageCard("Cargando promociones…")
            }

            promotions.isEmpty() -> {
                PromotionMessageCard(
                    "No hay promociones. Crea la primera promoción para comenzar."
                )
            }

            else -> {
                promotions.forEach { promotion ->
                    AdminPromotionCard(
                        promotion = promotion,
                        saving = saving,
                        onModifyPromotion = onModifyPromotion,
                        onSetPromotionActive =
                            onSetPromotionActive
                    )
                }

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !saving,
                    onClick = onRefresh
                ) {
                    Text("↻ Actualizar promociones")
                }
            }
        }
    }
}

@Composable
fun AdminPromotionCreateScreen(
    rechargePoints: List<AdminRechargePoint>,
    rechargePointsLoading: Boolean,
    saving: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onRefreshRechargePoints: () -> Unit,
    onCreatePromotion: (
        name: String,
        cashAmount: Long,
        promotionalAmount: Long,
        scope: String,
        rechargePointIds: List<String>
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var cashAmountText by remember { mutableStateOf("") }
    var promotionalAmountText by remember {
        mutableStateOf("")
    }
    var promotionScope by remember {
        mutableStateOf("ALL")
    }
    var selectedRechargePointIds by remember {
        mutableStateOf<Set<String>>(emptySet())
    }

    val cashAmount = cashAmountText.toLongOrNull()
    val promotionalAmount =
        promotionalAmountText.toLongOrNull()

    val saveEnabled =
        !saving &&
                name.trim().isNotBlank() &&
                cashAmount != null &&
                cashAmount > 0 &&
                promotionalAmount != null &&
                promotionalAmount >= 0 &&
                (
                        promotionScope == "ALL" ||
                                selectedRechargePointIds.isNotEmpty()
                        )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PromotionHeader(
            title = "🎁 Nueva promoción",
            subtitle = "Define el beneficio y las taquillas donde estará disponible.",
            onBack = onBack,
            enabled = !saving
        )

        PromotionErrorCard(
            errorMessage = errorMessage,
            enabled = !saving,
            onRetry = onRefreshRechargePoints
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MenesesSurface
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    onValueChange = { name = it },
                    enabled = !saving,
                    label = { Text("Nombre") },
                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = cashAmountText,
                    onValueChange = { value ->
                        cashAmountText =
                            value.filter { it.isDigit() }
                    },
                    enabled = !saving,
                    label = { Text("Cliente paga") },
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = promotionalAmountText,
                    onValueChange = { value ->
                        promotionalAmountText =
                            value.filter { it.isDigit() }
                    },
                    enabled = !saving,
                    label = { Text("Bono promocional") },
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                if (
                    cashAmount != null &&
                    promotionalAmount != null
                ) {
                    Text(
                        text =
                            "La tarjeta recibirá: " +
                                    "$${cashAmount + promotionalAmount}",
                        color = MenesesTextSecondary
                    )
                }

                PromotionScopeSelector(
                    scope = promotionScope,
                    selectedRechargePointIds =
                        selectedRechargePointIds,
                    rechargePoints = rechargePoints,
                    rechargePointsLoading =
                        rechargePointsLoading,
                    saving = saving,
                    onScopeChange = { newScope ->
                        promotionScope = newScope

                        if (newScope == "ALL") {
                            selectedRechargePointIds =
                                emptySet()
                        }
                    },
                    onSelectionChange = {
                        selectedRechargePointIds = it
                    },
                    onRefreshRechargePoints =
                        onRefreshRechargePoints
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = saveEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MenesesGreen
                    ),
                    onClick = {
                        val finalCashAmount =
                            cashAmount ?: return@Button

                        val finalPromotionalAmount =
                            promotionalAmount
                                ?: return@Button

                        onCreatePromotion(
                            name.trim(),
                            finalCashAmount,
                            finalPromotionalAmount,
                            promotionScope,
                            if (
                                promotionScope == "SELECTED"
                            ) {
                                selectedRechargePointIds
                                    .toList()
                            } else {
                                emptyList()
                            }
                        )
                    }
                ) {
                    Text(
                        if (saving) {
                            "Guardando…"
                        } else {
                            "Crear promoción"
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminPromotionEditScreen(
    promotion: AdminPromotion,
    rechargePoints: List<AdminRechargePoint>,
    rechargePointsLoading: Boolean,
    saving: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onRefreshRechargePoints: () -> Unit,
    onSaveScope: (
        scope: String,
        rechargePointIds: List<String>
    ) -> Unit
) {
    var promotionScope by remember(promotion.id) {
        mutableStateOf(
            if (promotion.scope == "SELECTED") {
                "SELECTED"
            } else {
                "ALL"
            }
        )
    }

    var selectedRechargePointIds by remember(
        promotion.id
    ) {
        mutableStateOf(
            promotion.rechargePoints
                .map { it.id }
                .toSet()
        )
    }

    val saveEnabled =
        !saving &&
                (
                        promotionScope == "ALL" ||
                                selectedRechargePointIds.isNotEmpty()
                        )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PromotionHeader(
            title = "✏️ Modificar promoción",
            subtitle = promotion.name,
            onBack = onBack,
            enabled = !saving
        )

        PromotionErrorCard(
            errorMessage = errorMessage,
            enabled = !saving,
            onRetry = onRefreshRechargePoints
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MenesesSurface
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text =
                        "Paga $${promotion.cashAmount}  →  " +
                                "recibe $${promotion.totalCreditAmount}",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text =
                        if (promotion.active) {
                            "Estado: ACTIVA"
                        } else {
                            "Estado: INACTIVA"
                        },
                    color = MenesesTextSecondary
                )

                Text(
                    text =
                        "Selecciona dónde estará disponible esta promoción.",
                    color = MenesesTextSecondary
                )

                PromotionScopeSelector(
                    scope = promotionScope,
                    selectedRechargePointIds =
                        selectedRechargePointIds,
                    rechargePoints = rechargePoints,
                    rechargePointsLoading =
                        rechargePointsLoading,
                    saving = saving,
                    onScopeChange = { newScope ->
                        promotionScope = newScope

                        if (newScope == "ALL") {
                            selectedRechargePointIds =
                                emptySet()
                        }
                    },
                    onSelectionChange = {
                        selectedRechargePointIds = it
                    },
                    onRefreshRechargePoints =
                        onRefreshRechargePoints
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = saveEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MenesesGreen
                    ),
                    onClick = {
                        onSaveScope(
                            promotionScope,
                            if (
                                promotionScope == "SELECTED"
                            ) {
                                selectedRechargePointIds
                                    .toList()
                            } else {
                                emptyList()
                            }
                        )
                    }
                ) {
                    Text(
                        if (saving) {
                            "Guardando…"
                        } else {
                            "Guardar cambios"
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PromotionScopeSelector(
    scope: String,
    selectedRechargePointIds: Set<String>,
    rechargePoints: List<AdminRechargePoint>,
    rechargePointsLoading: Boolean,
    saving: Boolean,
    onScopeChange: (String) -> Unit,
    onSelectionChange: (Set<String>) -> Unit,
    onRefreshRechargePoints: () -> Unit
) {
    Text(
        text = "Aplicar promoción en",
        fontWeight = FontWeight.SemiBold
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = scope == "ALL",
            enabled = !saving,
            onClick = {
                onScopeChange("ALL")
            }
        )

        Text("Todos los puntos de recarga")
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = scope == "SELECTED",
            enabled = !saving,
            onClick = {
                onScopeChange("SELECTED")
            }
        )

        Text("Puntos específicos")
    }

    if (scope == "SELECTED") {
        when {
            rechargePointsLoading -> {
                Text(
                    text =
                        "Cargando puntos de recarga…",
                    color = MenesesTextSecondary
                )
            }

            rechargePoints.isEmpty() -> {
                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text =
                            "No hay puntos de recarga disponibles.",
                        color = MenesesTextSecondary
                    )

                    OutlinedButton(
                        enabled = !saving,
                        onClick =
                            onRefreshRechargePoints
                    ) {
                        Text(
                            "↻ Actualizar puntos de recarga"
                        )
                    }
                }
            }

            else -> {
                rechargePoints.forEach {
                        rechargePoint ->

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked =
                                rechargePoint.id in
                                        selectedRechargePointIds,
                            enabled = !saving,
                            onCheckedChange = {
                                    checked ->

                                onSelectionChange(
                                    if (checked) {
                                        selectedRechargePointIds +
                                                rechargePoint.id
                                    } else {
                                        selectedRechargePointIds -
                                                rechargePoint.id
                                    }
                                )
                            }
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = rechargePoint.name,
                                fontWeight =
                                    FontWeight.SemiBold
                            )

                            Text(
                                text = rechargePoint.code,
                                color =
                                    MenesesTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminPromotionCard(
    promotion: AdminPromotion,
    saving: Boolean,
    onModifyPromotion: (AdminPromotion) -> Unit,
    onSetPromotionActive: (
        promotion: AdminPromotion,
        active: Boolean
    ) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MenesesSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = promotion.name,
                fontWeight = FontWeight.Bold
            )

            Text(
                text =
                    "Paga $${promotion.cashAmount}  →  " +
                            "recibe $${promotion.totalCreditAmount}"
            )

            Text(
                text =
                    if (promotion.active) {
                        "Estado: ACTIVA"
                    } else {
                        "Estado: INACTIVA"
                    },
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = promotionScopeDescription(
                    promotion
                ),
                color = MenesesTextSecondary
            )

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = !saving,
                onClick = {
                    onModifyPromotion(promotion)
                }
            ) {
                Text("Modificar")
            }

            if (promotion.active) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !saving,
                    onClick = {
                        onSetPromotionActive(
                            promotion,
                            false
                        )
                    }
                ) {
                    Text("Desactivar")
                }
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !saving,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                MenesesGreen
                        ),
                    onClick = {
                        onSetPromotionActive(
                            promotion,
                            true
                        )
                    }
                ) {
                    Text("Reactivar")
                }
            }
        }
    }
}

private fun promotionScopeDescription(
    promotion: AdminPromotion
): String {
    return if (
        promotion.scope == "SELECTED"
    ) {
        if (promotion.rechargePoints.isEmpty()) {
            "Aplicación: puntos específicos"
        } else {
            "Aplicación: " +
                    promotion.rechargePoints
                        .joinToString(
                            separator = ", "
                        ) {
                            it.name
                        }
        }
    } else {
        "Aplicación: todos los puntos de recarga"
    }
}

@Composable
private fun PromotionHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            enabled = enabled,
            onClick = onBack
        ) {
            Text("←")
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                color = MenesesTextSecondary
            )
        }
    }
}

@Composable
private fun PromotionErrorCard(
    errorMessage: String?,
    enabled: Boolean,
    onRetry: () -> Unit
) {
    if (errorMessage == null) {
        return
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MenesesPurpleSoft
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text =
                    "No fue posible completar la operación.",
                fontWeight = FontWeight.Bold
            )

            Text(
                text = errorMessage,
                color = MenesesTextSecondary
            )

            OutlinedButton(
                enabled = enabled,
                onClick = onRetry
            ) {
                Text("↻ Intentar nuevamente")
            }
        }
    }
}

@Composable
private fun PromotionMessageCard(
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MenesesSurface
        )
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            text = message,
            color = MenesesTextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
