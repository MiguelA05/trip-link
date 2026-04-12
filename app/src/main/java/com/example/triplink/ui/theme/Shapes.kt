package com.example.triplink.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Define los shapes (formas y bordes redondeados) para la aplicación.
 * Sigue Material 3 Design System.
 */
val AppShapes = Shapes(
    // Extra small - para componentes pequeños (chips, pills)
    extraSmall = RoundedCornerShape(4.dp),

    // Small - para elementos como inputs, buttons pequeños
    small = RoundedCornerShape(8.dp),

    // Medium - para cards, dialogs, modals
    medium = RoundedCornerShape(12.dp),

    // Large - para componentes grandes
    large = RoundedCornerShape(16.dp),

    // Extra large - para fondos de pantalla completa
    extraLarge = RoundedCornerShape(28.dp)
)

