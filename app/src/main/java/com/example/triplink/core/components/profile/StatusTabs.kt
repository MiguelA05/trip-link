package com.example.triplink.core.components.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.triplink.R
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.ui.theme.PrincipalBlue

@Composable
fun StatusTabs(
    selectedTab: EstadoPublicacion,
    onTabSelected: (EstadoPublicacion) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        EstadoPublicacion.VERIFICADA,
        EstadoPublicacion.PENDIENTE,
        EstadoPublicacion.RECHAZADA
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        tabs.forEach { tab ->
            val selected = selectedTab == tab
            val label = stringResource(
                when (tab) {
                    EstadoPublicacion.VERIFICADA -> R.string.component_status_tabs_verified
                    EstadoPublicacion.PENDIENTE -> R.string.component_status_tabs_pending
                    EstadoPublicacion.RECHAZADA -> R.string.component_status_tabs_rejected
                }
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (selected) PrincipalBlue else Color(0xFF8FA1BA),
                        fontWeight = FontWeight.SemiBold
                    )
                )
                if (selected) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth(0.9f),
                        thickness = 3.dp,
                        color = PrincipalBlue
                    )
                }
            }
        }
    }
}

