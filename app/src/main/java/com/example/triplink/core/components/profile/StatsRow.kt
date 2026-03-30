package com.example.triplink.core.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StatsRow(
    points: Int,
    contributions: Int,
    activeDays: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = points.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFF1B1B1B),
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "PUNTOS",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF90A0B7),
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Box(
            modifier = Modifier
                .height(48.dp)
                .width(1.dp)
                .background(Color(0xFFD5DCE8))
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = contributions.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFF1B1B1B),
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "APORTES",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF90A0B7),
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Box(
            modifier = Modifier
                .height(48.dp)
                .width(1.dp)
                .background(Color(0xFFD5DCE8))
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = activeDays.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFF1B1B1B),
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "DIAS ACTIVOS",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF90A0B7),
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

