package com.example.triplink.core.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppTitle(modifier: Modifier = Modifier, fontSize: Int = 28) {
    Row(modifier = modifier.padding(bottom = 40.dp)) {
        Text(
            text = "Trip",
            fontSize = fontSize.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF42A5F5) // Azul claro
        )
        Text(
            text = "Link",
            fontSize = fontSize.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1976D2) // Azul fuerte
        )

    }
}