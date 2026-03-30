package com.example.triplink.core.components.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SectionTitleDivider(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFD8D8D8))
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color(0xFF949494),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp
            )
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFD8D8D8))
    }
}

