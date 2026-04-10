package com.example.triplink.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

enum class AppTitleVariant {
    Compact,
    Standard,
    Hero
}

/**
 * Tokens tipograficos de uso frecuente para mantener consistencia visual.
 */
object TextTokens {
    @Composable
    fun screenTitle(): TextStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)

    @Composable
    fun sectionTitle(): TextStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)

    @Composable
    fun sectionAction(): TextStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)

    @Composable
    fun cardTitle(): TextStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)

    @Composable
    fun cardSubtitle(): TextStyle = MaterialTheme.typography.bodySmall

    @Composable
    fun inputText(): TextStyle = MaterialTheme.typography.bodyMedium

    @Composable
    fun helperText(): TextStyle = MaterialTheme.typography.bodySmall

    @Composable
    fun numberInput(): TextStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)

    @Composable
    fun chipLabel(): TextStyle = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)

    @Composable
    fun buttonLabel(): TextStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)

    @Composable
    fun avatarInitial(): TextStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)

    @Composable
    fun statValue(): TextStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)

    @Composable
    fun statLabel(): TextStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)

    @Composable
    fun counterLabel(): TextStyle = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)

    @Composable
    fun appTitle(variant: AppTitleVariant = AppTitleVariant.Standard): TextStyle {
        val screenWidthDp = LocalConfiguration.current.screenWidthDp
        val fontSize = when (variant) {
            AppTitleVariant.Compact -> when {
                screenWidthDp < 360 -> 22.sp
                screenWidthDp < 400 -> 24.sp
                else -> 26.sp
            }

            AppTitleVariant.Standard -> when {
                screenWidthDp < 360 -> 26.sp
                screenWidthDp < 400 -> 28.sp
                else -> 30.sp
            }

            AppTitleVariant.Hero -> when {
                screenWidthDp < 360 -> 32.sp
                screenWidthDp < 400 -> 36.sp
                else -> 40.sp
            }
        }

        return MaterialTheme.typography.displaySmall.copy(
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold
        )
    }

    @Composable
    fun heroTitle(): TextStyle = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)

    @Composable
    fun heroSubtitle(): TextStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
}

object TextColors {
    val Primary: Color = PrincipalBlack
    val Secondary: Color = DarkGray
    val Accent: Color = PrincipalBlue
    val OnImage: Color = PrincipalWhite
    val Muted: Color = PrincipalGray
}





