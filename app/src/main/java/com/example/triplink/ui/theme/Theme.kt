package com.example.triplink.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ============================================================================
// LIGHT COLOR SCHEME - Paleta para modo claro
// ============================================================================
private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimary,
    onPrimaryContainer = LightOnPrimary,

    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = PastelOrange,
    onSecondaryContainer = LightOnSecondary,

    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiary,
    onTertiaryContainer = LightOnTertiary,

    error = LightError,
    onError = LightOnError,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = LightError,

    background = LightBackground,
    onBackground = LightOnBackground,

    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = Color(0xFFEFF0F1),
    onSurfaceVariant = PrincipalGray,

    outline = PrincipalGray,
    outlineVariant = Color(0xFFCAC4D0),
    scrim = PrincipalBlack
)

// ============================================================================
// DARK COLOR SCHEME - Paleta para modo oscuro
// ============================================================================
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFF90CAF9),

    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = Color(0xFF2E7D32),
    onSecondaryContainer = Color(0xFF81C784),

    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = Color(0xFFE65100),
    onTertiaryContainer = Color(0xFFFFB74D),

    error = DarkError,
    onError = DarkOnError,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = DarkBackground,
    onBackground = DarkOnBackground,

    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = Color(0xFF49454E),
    onSurfaceVariant = Color(0xFFCAC4D0),

    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454E),
    scrim = PrincipalBlack
)

// ============================================================================
// MAIN THEME COMPOSABLE
// ============================================================================

/**
 * Composable que aplica el tema a toda la aplicación.
 *
 * Caracteristicas:
 * - Soporta Dynamic Color (Material You) en Android 12+
 * - Sigue automaticamente el modo del sistema
 * - Fallback a paletas estaticas si Dynamic Color no esta disponible
 *
 * @param useDynamicColor Si true, usa colores dinámicos en Android 12+
 * @param content El contenido de la aplicación
 */
@Composable
fun DescubreuqTheme(
    useDynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    // Selecciona la paleta de colores
    val colorScheme = when {
        // Dynamic Color: Solo disponible en Android 12 (API 31) o superior

        // Fallback: Paleta estática personalizada
        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
