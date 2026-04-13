package com.example.triplink.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

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
    fun appTitle(variant: AppTitleVariant = AppTitleVariant.Standard): TextStyle = when (variant) {
        AppTitleVariant.Compact -> MaterialTheme.typography.titleLarge
        AppTitleVariant.Standard -> MaterialTheme.typography.headlineSmall
        AppTitleVariant.Hero -> MaterialTheme.typography.displaySmall
    }

    @Composable
    fun screenTitle(): TextStyle = MaterialTheme.typography.titleLarge

    @Composable
    fun sectionTitle(): TextStyle = MaterialTheme.typography.headlineSmall

    @Composable
    fun title(): TextStyle = MaterialTheme.typography.titleMedium

    @Composable
    fun body(): TextStyle = MaterialTheme.typography.bodyMedium

    @Composable
    fun bodySecondary(): TextStyle = MaterialTheme.typography.bodySmall

    @Composable
    fun input(): TextStyle = MaterialTheme.typography.bodyLarge

    @Composable
    fun chip(): TextStyle = MaterialTheme.typography.labelMedium

    @Composable
    fun label(): TextStyle = MaterialTheme.typography.labelLarge

    @Composable
    fun button(): TextStyle = MaterialTheme.typography.labelLarge

    @Composable
    fun caption(): TextStyle = MaterialTheme.typography.labelSmall

    fun emphasized(style: TextStyle, weight: FontWeight = FontWeight.SemiBold): TextStyle =
        style.copy(fontWeight = weight)

    fun centered(style: TextStyle): TextStyle = style.copy(textAlign = TextAlign.Center)

    fun colored(style: TextStyle, color: Color): TextStyle = style.copy(color = color)

    fun boldSpanStyle(): SpanStyle = SpanStyle(fontWeight = FontWeight.Bold)
}

object TextColors {
    val Primary: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface

    val Secondary: Color
        @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

    val Accent: Color
        @Composable get() = MaterialTheme.colorScheme.primary

    val OnImage: Color
        @Composable get() = MaterialTheme.colorScheme.onPrimary

    val Muted: Color
        @Composable get() = MaterialTheme.colorScheme.outline
}





