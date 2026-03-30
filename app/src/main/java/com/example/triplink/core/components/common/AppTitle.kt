package com.example.triplink.core.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.triplink.ui.theme.PastelBlue
import com.example.triplink.ui.theme.PrincipalBlue

@Composable
fun AppTitle(modifier: Modifier = Modifier, fontSize: Int = 28) {
    Row(modifier = modifier) {
        Text(
            text = "Trip",
            style = MaterialTheme.typography.displaySmall.copy(
                fontSize = fontSize.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PastelBlue
            )
        )
        Text(
            text = "Link",
            style = MaterialTheme.typography.displaySmall.copy(
                fontSize = fontSize.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrincipalBlue
            )
        )
    }
}
