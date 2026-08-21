package com.espectacularesmeneses.feria.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MenesesColorScheme = lightColorScheme(

    primary = MenesesBlue,
    onPrimary = Color.White,

    primaryContainer = MenesesBlueSoft,
    onPrimaryContainer = MenesesBlueDark,

    secondary = MenesesGreen,
    onSecondary = Color.White,

    secondaryContainer = MenesesGreenSoft,
    onSecondaryContainer = MenesesGreenDark,

    tertiary = MenesesPurple,
    onTertiary = Color.White,

    tertiaryContainer = MenesesPurpleSoft,
    onTertiaryContainer = MenesesPurple,

    background = MenesesBackground,
    onBackground = MenesesText,

    surface = MenesesSurface,
    onSurface = MenesesText,

    surfaceVariant = MenesesBackground,
    onSurfaceVariant = MenesesTextSecondary,

    outline = MenesesBorder,

    error = MenesesError,
    onError = Color.White
)

@Composable
fun MenesesFeriaTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = MenesesColorScheme,
        typography = Typography,
        content = content
    )
}