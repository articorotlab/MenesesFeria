package com.espectacularesmeneses.feria

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.espectacularesmeneses.feria.device.DeviceIdentity
import com.espectacularesmeneses.feria.device.DeviceIdentityStore
import com.espectacularesmeneses.feria.network.DeviceProvisioningApiClient
import com.espectacularesmeneses.feria.ui.theme.MenesesBlue
import com.espectacularesmeneses.feria.ui.theme.MenesesBlueDark
import com.espectacularesmeneses.feria.ui.theme.MenesesDanger
import com.espectacularesmeneses.feria.ui.theme.MenesesFeriaTheme
import com.espectacularesmeneses.feria.ui.theme.MenesesGreen
import com.espectacularesmeneses.feria.ui.theme.MenesesSurface
import com.espectacularesmeneses.feria.ui.theme.MenesesTextSecondary

import com.espectacularesmeneses.feria.device.DeviceRuntimeIdentity


class DeviceBootstrapActivity :
    ComponentActivity() {

    private var bootstrapState
            by mutableStateOf<BootstrapState>(
                BootstrapState.Loading
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


        setContent {

            MenesesFeriaTheme {

                Surface(
                    modifier =
                        Modifier.fillMaxSize(),

                    color =
                        MenesesSurface
                ) {

                    BootstrapContent(
                        state =
                            bootstrapState,

                        onProvision = {
                                code ->

                            provisionDevice(
                                code
                            )
                        },

                        onRetry = {
                            resolveDeviceIdentity()
                        }
                    )
                }
            }
        }


        resolveDeviceIdentity()
    }


    /*
     * =====================================================
     * RESOLVER IDENTIDAD
     * =====================================================
     */

    private fun resolveDeviceIdentity() {

        val identity =
            DeviceIdentityStore
                .getIdentity(
                    this
                )


        /*
         * Si no existe identidad local:
         *
         * - instalación nueva;
         * - dispositivo legacy todavía no migrado;
         * - datos de aplicación eliminados.
         */

        if (
            identity == null
        ) {

            DeviceRuntimeIdentity
                .clear()


            bootstrapState =
                BootstrapState
                    .NeedsProvisioning

            return
        }


        DeviceRuntimeIdentity
            .setIdentity(
                identity
            )


        bootstrapState =
            BootstrapState
                .Verifying(
                    deviceCode =
                        identity.deviceCode,

                    deviceName =
                        identity.deviceName
                )


        Thread {

            try {

                val valid =
                    DeviceProvisioningApiClient
                        .verify(
                            identity
                        )


                runOnUiThread {

                    if (
                        valid
                    ) {

                        openMainApplication()

                    } else {

                        bootstrapState =
                            BootstrapState
                                .Rejected(
                                    deviceCode =
                                        identity.deviceCode,

                                    deviceName =
                                        identity.deviceName,

                                    message =
                                        "Este dispositivo ya no está autorizado para operar."
                                )
                    }
                }


            } catch (
                e: Exception
            ) {

                runOnUiThread {

                    bootstrapState =
                        BootstrapState
                            .ConnectionError(
                                deviceCode =
                                    identity.deviceCode,

                                deviceName =
                                    identity.deviceName,

                                message =
                                    e.message
                                        ?: "No fue posible conectar con el servidor."
                            )
                }
            }

        }.start()
    }


    /*
     * =====================================================
     * PROVISIONAR
     * =====================================================
     */

    private fun provisionDevice(
        code: String
    ) {

        val normalizedCode =
            code
                .trim()
                .uppercase()


        if (
            normalizedCode.isBlank()
        ) {

            bootstrapState =
                BootstrapState
                    .ProvisioningError(
                        message =
                            "Escribe el código de activación."
                    )

            return
        }


        bootstrapState =
            BootstrapState
                .Provisioning


        Thread {

            try {

                val identity =
                    DeviceProvisioningApiClient
                        .provision(
                            normalizedCode
                        )


                DeviceIdentityStore
                    .saveIdentity(
                        context =
                            this,

                        identity =
                            identity
                    )


                DeviceRuntimeIdentity
                    .setIdentity(
                        identity
                    )


                runOnUiThread {

                    bootstrapState =
                        BootstrapState
                            .Provisioned(
                                identity =
                                    identity
                            )


                    /*
                     * Mostramos brevemente la confirmación
                     * antes de abrir la aplicación normal.
                     */

                    window
                        .decorView
                        .postDelayed(
                            {
                                openMainApplication()
                            },

                            900L
                        )
                }


            } catch (
                e: Exception
            ) {

                runOnUiThread {

                    bootstrapState =
                        BootstrapState
                            .ProvisioningError(
                                message =
                                    provisioningErrorMessage(
                                        e
                                    )
                            )
                }
            }

        }.start()
    }


    /*
     * =====================================================
     * APP NORMAL
     * =====================================================
     */

    private fun openMainApplication() {

        val intent =
            Intent(
                this,
                MainActivity::class.java
            )


        startActivity(
            intent
        )


        finish()
    }


    /*
     * =====================================================
     * ERRORES
     * =====================================================
     */

    private fun provisioningErrorMessage(
        error: Exception
    ): String {

        val raw =
            error.message
                ?.uppercase()
                ?: ""


        return when {

            raw.contains(
                "PROVISIONING_CODE_NOT_FOUND"
            ) -> {

                "El código de activación no es válido."
            }


            raw.contains(
                "PROVISIONING_CODE_EXPIRED"
            ) -> {

                "El código de activación expiró. Genera uno nuevo desde Administración."
            }


            raw.contains(
                "PROVISIONING_CODE_NOT_PENDING"
            ) -> {

                "Este código de activación ya fue utilizado o revocado."
            }


            raw.contains(
                "DEVICE_ALREADY_PROVISIONED"
            ) -> {

                "Este dispositivo ya fue activado anteriormente."
            }


            raw.contains(
                "DEVICE_BLOCKED"
            ) -> {

                "Este dispositivo está bloqueado desde Administración."
            }


            else -> {

                error.message
                    ?: "No fue posible activar el dispositivo."
            }
        }
    }
}


/*
 * =========================================================
 * BOOTSTRAP STATE
 * =========================================================
 */

private sealed interface BootstrapState {

    data object Loading :
        BootstrapState


    data object NeedsProvisioning :
        BootstrapState


    data object Provisioning :
        BootstrapState


    data class Verifying(
        val deviceCode: String,
        val deviceName: String
    ) :
        BootstrapState


    data class Provisioned(
        val identity: DeviceIdentity
    ) :
        BootstrapState


    data class ProvisioningError(
        val message: String
    ) :
        BootstrapState


    data class ConnectionError(
        val deviceCode: String,
        val deviceName: String,
        val message: String
    ) :
        BootstrapState


    data class Rejected(
        val deviceCode: String,
        val deviceName: String,
        val message: String
    ) :
        BootstrapState
}


/*
 * =========================================================
 * UI
 * =========================================================
 */

@Composable
private fun BootstrapContent(
    state: BootstrapState,
    onProvision: (String) -> Unit,
    onRetry: () -> Unit
) {

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    MenesesSurface
                )
                .padding(
                    24.dp
                ),

        contentAlignment =
            Alignment.Center
    ) {

        when (
            state
        ) {

            BootstrapState.Loading -> {

                LoadingCard(
                    title =
                        "Iniciando dispositivo",

                    message =
                        "Comprobando la configuración del equipo."
                )
            }


            BootstrapState.NeedsProvisioning -> {

                ProvisioningCard(
                    errorMessage =
                        null,

                    loading =
                        false,

                    onProvision =
                        onProvision
                )
            }


            BootstrapState.Provisioning -> {

                ProvisioningCard(
                    errorMessage =
                        null,

                    loading =
                        true,

                    onProvision =
                        onProvision
                )
            }


            is BootstrapState.ProvisioningError -> {

                ProvisioningCard(
                    errorMessage =
                        state.message,

                    loading =
                        false,

                    onProvision =
                        onProvision
                )
            }


            is BootstrapState.Verifying -> {

                LoadingCard(
                    title =
                        state.deviceCode,

                    message =
                        "Verificando ${state.deviceName} con el servidor."
                )
            }


            is BootstrapState.Provisioned -> {

                SuccessCard(
                    identity =
                        state.identity
                )
            }


            is BootstrapState.ConnectionError -> {

                ProblemCard(
                    title =
                        "Servidor no disponible",

                    deviceCode =
                        state.deviceCode,

                    deviceName =
                        state.deviceName,

                    message =
                        "La identidad del dispositivo está guardada, pero no fue posible comunicarse con el servidor.\n\n${state.message}",

                    buttonText =
                        "Reintentar",

                    onButton =
                        onRetry,

                    danger =
                        false
                )
            }


            is BootstrapState.Rejected -> {

                ProblemCard(
                    title =
                        "Dispositivo no autorizado",

                    deviceCode =
                        state.deviceCode,

                    deviceName =
                        state.deviceName,

                    message =
                        state.message,

                    buttonText =
                        "Verificar nuevamente",

                    onButton =
                        onRetry,

                    danger =
                        true
                )
            }
        }
    }
}


/*
 * =========================================================
 * PROVISIONING CARD
 * =========================================================
 */

@Composable
private fun ProvisioningCard(
    errorMessage: String?,
    loading: Boolean,
    onProvision: (String) -> Unit
) {

    /*
     * IMPORTANTE:
     *
     * remember conserva el texto durante las
     * recomposiciones de Compose.
     *
     * Sin remember, cada tecla provoca una recomposición
     * y el valor vuelve inmediatamente a "".
     */

    var code
            by remember {
                mutableStateOf(
                    ""
                )
            }


    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .widthIn(
                    max =
                        480.dp
                )
                .background(
                    color =
                        Color.White,

                    shape =
                        RoundedCornerShape(
                            28.dp
                        )
                )
                .padding(
                    30.dp
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text =
                "MENESES",

            color =
                MenesesBlue,

            fontSize =
                14.sp,

            fontWeight =
                FontWeight.Black,

            letterSpacing =
                2.sp
        )


        Spacer(
            modifier =
                Modifier.height(
                    16.dp
                )
        )


        Text(
            text =
                "Configurar dispositivo",

            color =
                MenesesBlueDark,

            fontSize =
                30.sp,

            fontWeight =
                FontWeight.Black,

            textAlign =
                TextAlign.Center
        )


        Spacer(
            modifier =
                Modifier.height(
                    12.dp
                )
        )


        Text(
            text =
                "Este Ulefone todavía no tiene una identidad asignada. Genera un código de activación desde el panel administrativo e introdúcelo aquí.",

            color =
                MenesesTextSecondary,

            fontSize =
                14.sp,

            lineHeight =
                20.sp,

            textAlign =
                TextAlign.Center
        )


        Spacer(
            modifier =
                Modifier.height(
                    28.dp
                )
        )


        OutlinedTextField(
            value =
                code,

            onValueChange = {
                    value ->

                code =
                    value
                        .uppercase()
            },

            modifier =
                Modifier.fillMaxWidth(),

            enabled =
                !loading,

            label = {
                Text(
                    "Código de activación"
                )
            },

            placeholder = {
                Text(
                    "XXXX-XXXX-XXXX"
                )
            },

            singleLine =
                true,

            keyboardOptions =
                KeyboardOptions(
                    capitalization =
                        KeyboardCapitalization
                            .Characters
                )
        )


        if (
            !errorMessage.isNullOrBlank()
        ) {

            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )


            Text(
                text =
                    errorMessage,

                color =
                    MenesesDanger,

                fontSize =
                    13.sp,

                fontWeight =
                    FontWeight.SemiBold,

                textAlign =
                    TextAlign.Center
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    24.dp
                )
        )


        Button(
            onClick = {

                onProvision(
                    code
                )
            },

            enabled =
                !loading &&
                        code.isNotBlank(),

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(
                        54.dp
                    ),

            colors =
                ButtonDefaults
                    .buttonColors(
                        containerColor =
                            MenesesBlue
                    ),

            shape =
                RoundedCornerShape(
                    14.dp
                )
        ) {

            if (
                loading
            ) {

                CircularProgressIndicator(
                    modifier =
                        Modifier.size(
                            22.dp
                        ),

                    color =
                        Color.White,

                    strokeWidth =
                        2.dp
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            4.dp
                        )
                )


            } else {

                Text(
                    text =
                        "ACTIVAR DISPOSITIVO",

                    fontWeight =
                        FontWeight.Black
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(
                    18.dp
                )
        )


        Text(
            text =
                "El código sólo puede utilizarse una vez.",

            color =
                MenesesTextSecondary,

            fontSize =
                11.sp,

            textAlign =
                TextAlign.Center
        )
    }
}


/*
 * =========================================================
 * LOADING
 * =========================================================
 */

@Composable
private fun LoadingCard(
    title: String,
    message: String
) {

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .widthIn(
                    max =
                        440.dp
                )
                .background(
                    color =
                        Color.White,

                    shape =
                        RoundedCornerShape(
                            28.dp
                        )
                )
                .padding(
                    32.dp
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        CircularProgressIndicator(
            color =
                MenesesBlue
        )


        Spacer(
            modifier =
                Modifier.height(
                    22.dp
                )
        )


        Text(
            text =
                title,

            color =
                MenesesBlueDark,

            fontSize =
                23.sp,

            fontWeight =
                FontWeight.Black,

            textAlign =
                TextAlign.Center
        )


        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )


        Text(
            text =
                message,

            color =
                MenesesTextSecondary,

            fontSize =
                13.sp,

            textAlign =
                TextAlign.Center
        )
    }
}


/*
 * =========================================================
 * SUCCESS
 * =========================================================
 */

@Composable
private fun SuccessCard(
    identity: DeviceIdentity
) {

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .widthIn(
                    max =
                        440.dp
                )
                .background(
                    color =
                        Color.White,

                    shape =
                        RoundedCornerShape(
                            28.dp
                        )
                )
                .padding(
                    32.dp
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text =
                "✓",

            color =
                MenesesGreen,

            fontSize =
                48.sp,

            fontWeight =
                FontWeight.Black
        )


        Spacer(
            modifier =
                Modifier.height(
                    12.dp
                )
        )


        Text(
            text =
                "Dispositivo activado",

            color =
                MenesesBlueDark,

            fontSize =
                25.sp,

            fontWeight =
                FontWeight.Black
        )


        Spacer(
            modifier =
                Modifier.height(
                    16.dp
                )
        )


        Text(
            text =
                identity.deviceCode,

            color =
                MenesesBlue,

            fontSize =
                20.sp,

            fontWeight =
                FontWeight.Black
        )


        Text(
            text =
                identity.deviceName,

            color =
                MenesesTextSecondary,

            fontSize =
                13.sp,

            textAlign =
                TextAlign.Center
        )
    }
}


/*
 * =========================================================
 * PROBLEM
 * =========================================================
 */

@Composable
private fun ProblemCard(
    title: String,
    deviceCode: String,
    deviceName: String,
    message: String,
    buttonText: String,
    onButton: () -> Unit,
    danger: Boolean
) {

    val accent =
        if (
            danger
        ) {

            MenesesDanger

        } else {

            MenesesBlue
        }


    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .widthIn(
                    max =
                        460.dp
                )
                .background(
                    color =
                        Color.White,

                    shape =
                        RoundedCornerShape(
                            28.dp
                        )
                )
                .padding(
                    30.dp
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text =
                title,

            color =
                accent,

            fontSize =
                26.sp,

            fontWeight =
                FontWeight.Black,

            textAlign =
                TextAlign.Center
        )


        Spacer(
            modifier =
                Modifier.height(
                    14.dp
                )
        )


        Text(
            text =
                deviceCode,

            color =
                MenesesBlueDark,

            fontSize =
                18.sp,

            fontWeight =
                FontWeight.Bold
        )


        Text(
            text =
                deviceName,

            color =
                MenesesTextSecondary,

            fontSize =
                12.sp
        )


        Spacer(
            modifier =
                Modifier.height(
                    20.dp
                )
        )


        Text(
            text =
                message,

            color =
                MenesesTextSecondary,

            fontSize =
                13.sp,

            lineHeight =
                19.sp,

            textAlign =
                TextAlign.Center
        )


        Spacer(
            modifier =
                Modifier.height(
                    24.dp
                )
        )


        Button(
            onClick =
                onButton,

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(
                        50.dp
                    ),

            colors =
                ButtonDefaults
                    .buttonColors(
                        containerColor =
                            accent
                    )
        ) {

            Text(
                text =
                    buttonText,

                fontWeight =
                    FontWeight.Black
            )
        }
    }
}
