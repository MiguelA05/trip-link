package com.example.triplink.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrincipalBlue,
    secondary = PrincipalGreen,
    tertiary = PrincipalOrange,
    background = PrincipalWhite,
    surface = PrincipalWhite,
    onBackground = PrincipalBlack,
    onSurface = PrincipalBlack,
    error = PrincipalRed,
    onPrimary = PrincipalWhite
)

private val LightColorScheme = lightColorScheme(
    primary = PrincipalBlue,
    secondary = PrincipalGreen,
    tertiary = PrincipalOrange,
    background = PrincipalWhite,
    surface = PrincipalWhite,
    onBackground = PrincipalBlack,
    onSurface = PrincipalBlack,
    error = PrincipalRed,
    onPrimary = PrincipalWhite
)

@Composable
fun DescubreuqTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Forzamos el uso de LightColorScheme para que siempre se vea con fondo claro,
    // ignorando si el teléfono está en modo oscuro o tiene colores dinámicos.
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
