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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.espectacularesmeneses.feria.ui.theme.MenesesGreen
import com.espectacularesmeneses.feria.ui.theme.MenesesPurple
import com.espectacularesmeneses.feria.ui.theme.MenesesPurpleSoft
import com.espectacularesmeneses.feria.ui.theme.MenesesSurface
import com.espectacularesmeneses.feria.ui.theme.MenesesTextSecondary

/*
 * =========================================================
 * ADMIN PROMOTIONS SCREEN
 * =========================================================
 *
 * Esta pantalla NO realiza llamadas HTTP directamente.
 *
 * MainActivity controla:
 * - carga de promociones;
 * - creación;
 * - activación/desactivación;
 * - errores;
 * - navegación.
 *
 * La UI únicamente representa el estado recibido y dispara
 * callbacks hacia MainActivity.
 * =========================================================
 */

@Composable
fun AdminPromotionsScreen(
    promotions: List<AdminPromotion>,
    loading: Boolean,
    saving: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onCreatePromotion: (
        name: String,
        cashAmount: Long,
        promotionalAmount: Long
    ) -> Unit,
    onSetPromotionActive: (
        promotion: AdminPromotion,
        active: Boolean
    ) -> Unit
) {
    var showCreateForm by remember {
        mutableStateOf(false)
    }

    var name by remember {
        mutableStateOf("")
    }

    var cashAmountText by remember {
        mutableStateOf("")
    }

    var promotionalAmountText by remember {
        mutableStateOf("")
    }

    val cashAmount =
        cashAmountText.toLongOrNull()

    val promotionalAmount =
        promotionalAmountText.toLongOrNull()

    val createEnabled =
        !saving &&
                name.trim().isNotBlank() &&
                cashAmount != null &&
                cashAmount > 0 &&
                promotionalAmount != null &&
                promotionalAmount >= 0

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
                enabled = !saving,
                onClick = onBack
            ) {
                Text("←")
            }

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {
                Text(
                    text = "🎁 Promociones",
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        "Crear, desactivar y reactivar promociones.",
                    color =
                        MenesesTextSecondary
                )
            }
        }

        if (
            errorMessage != null
        ) {
            Card(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(18.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MenesesPurpleSoft
                    )
            ) {
                Column(
                    modifier =
                        Modifier.padding(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text =
                            "No fue posible completar la operación.",
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text =
                            errorMessage,
                        color =
                            MenesesTextSecondary
                    )

                    OutlinedButton(
                        enabled =
                            !loading &&
                                    !saving,
                        onClick =
                            onRefresh
                    ) {
                        Text("↻ Intentar nuevamente")
                    }
                }
            }
        }

        Button(
            modifier =
                Modifier.fillMaxWidth(),
            enabled =
                !loading &&
                        !saving,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        MenesesPurple
                ),
            onClick = {
                showCreateForm =
                    !showCreateForm
            }
        ) {
            Text(
                if (
                    showCreateForm
                ) {
                    "Cancelar nueva promoción"
                } else {
                    "+ Crear promoción"
                }
            )
        }

        if (
            showCreateForm
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
                        Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text =
                            "Nueva promoción",
                        fontWeight =
                            FontWeight.Bold
                    )

                    OutlinedTextField(
                        modifier =
                            Modifier.fillMaxWidth(),
                        value =
                            name,
                        onValueChange = {
                            name =
                                it
                        },
                        enabled =
                            !saving,
                        label = {
                            Text(
                                "Nombre"
                            )
                        },
                        singleLine =
                            true
                    )

                    OutlinedTextField(
                        modifier =
                            Modifier.fillMaxWidth(),
                        value =
                            cashAmountText,
                        onValueChange = {
                                value ->

                            cashAmountText =
                                value.filter {
                                    it.isDigit()
                                }
                        },
                        enabled =
                            !saving,
                        label = {
                            Text(
                                "Cliente paga"
                            )
                        },
                        prefix = {
                            Text("$")
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Number
                            ),
                        singleLine =
                            true
                    )

                    OutlinedTextField(
                        modifier =
                            Modifier.fillMaxWidth(),
                        value =
                            promotionalAmountText,
                        onValueChange = {
                                value ->

                            promotionalAmountText =
                                value.filter {
                                    it.isDigit()
                                }
                        },
                        enabled =
                            !saving,
                        label = {
                            Text(
                                "Bono promocional"
                            )
                        },
                        prefix = {
                            Text("$")
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Number
                            ),
                        singleLine =
                            true
                    )

                    if (
                        cashAmount != null &&
                        promotionalAmount != null
                    ) {
                        Text(
                            text =
                                "La tarjeta recibirá: " +
                                        "$${cashAmount + promotionalAmount}",
                            color =
                                MenesesTextSecondary
                        )
                    }

                    Button(
                        modifier =
                            Modifier.fillMaxWidth(),
                        enabled =
                            createEnabled,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    MenesesGreen
                            ),
                        onClick = {
                            val finalCashAmount =
                                cashAmount
                                    ?: return@Button

                            val finalPromotionalAmount =
                                promotionalAmount
                                    ?: return@Button

                            onCreatePromotion(
                                name.trim(),
                                finalCashAmount,
                                finalPromotionalAmount
                            )

                            showCreateForm =
                                false

                            name =
                                ""

                            cashAmountText =
                                ""

                            promotionalAmountText =
                                ""
                        }
                    ) {
                        Text(
                            if (
                                saving
                            ) {
                                "Guardando…"
                            } else {
                                "Crear promoción"
                            }
                        )
                    }
                }
            }
        }

        when {
            loading -> {
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
                    Text(
                        modifier =
                            Modifier.padding(18.dp),
                        text =
                            "Cargando promociones…",
                        color =
                            MenesesTextSecondary,
                        textAlign =
                            TextAlign.Center
                    )
                }
            }

            promotions.isEmpty() -> {
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
                            Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text =
                                "No hay promociones",
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "Crea la primera promoción para comenzar.",
                            color =
                                MenesesTextSecondary,
                            textAlign =
                                TextAlign.Center
                        )
                    }
                }
            }

            else -> {
                promotions.forEach {
                        promotion ->

                    AdminPromotionCard(
                        promotion =
                            promotion,
                        saving =
                            saving,
                        onSetPromotionActive =
                            onSetPromotionActive
                    )
                }

                OutlinedButton(
                    modifier =
                        Modifier.fillMaxWidth(),
                    enabled =
                        !saving,
                    onClick =
                        onRefresh
                ) {
                    Text("↻ Actualizar promociones")
                }
            }
        }
    }
}

@Composable
private fun AdminPromotionCard(
    promotion: AdminPromotion,
    saving: Boolean,
    onSetPromotionActive: (
        promotion: AdminPromotion,
        active: Boolean
    ) -> Unit
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
                    promotion.name,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text =
                    "Paga $${promotion.cashAmount}  →  " +
                            "recibe $${promotion.totalCreditAmount}"
            )

            Text(
                text =
                    if (
                        promotion.active
                    ) {
                        "Estado: ACTIVA"
                    } else {
                        "Estado: INACTIVA"
                    },
                fontWeight =
                    FontWeight.SemiBold
            )

            if (
                promotion.active
            ) {
                OutlinedButton(
                    modifier =
                        Modifier.fillMaxWidth(),
                    enabled =
                        !saving,
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
                    modifier =
                        Modifier.fillMaxWidth(),
                    enabled =
                        !saving,
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
