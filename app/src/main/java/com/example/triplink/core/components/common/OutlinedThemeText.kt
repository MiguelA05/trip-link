package com.example.triplink.core.components.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun OutlinedThemeText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    strokeWidth: Float = 4f
) {
    val isDark = isSystemInDarkTheme()
    val fillColor = if (isDark) Color.White else Color.Black
    val strokeColor = if (isDark) Color.Black else Color.White

    Box(modifier = modifier) {
        Text(
            text = text,
            color = strokeColor,
            style = style.copy(drawStyle = Stroke(width = strokeWidth)),
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = text,
            color = fillColor,
            style = style,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}
