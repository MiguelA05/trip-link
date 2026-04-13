package com.example.triplink.core.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.example.triplink.ui.theme.TextTokens

@Composable
fun StatusTabs(
    selectedTab: EstadoPublicacion,
    onTabSelected: (EstadoPublicacion) -> Unit,
    modifier: Modifier = Modifier,
    verifiedCount: Int = 0,
    pendingCount: Int = 0,
    rejectedCount: Int = 0
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
            val count = when (tab) {
                EstadoPublicacion.VERIFICADA -> verifiedCount
                EstadoPublicacion.PENDIENTE -> pendingCount
                EstadoPublicacion.RECHAZADA -> rejectedCount
            }
            
            val badgeBgColor = when (tab) {
                EstadoPublicacion.VERIFICADA -> MaterialTheme.colorScheme.primaryContainer
                EstadoPublicacion.PENDIENTE -> MaterialTheme.colorScheme.tertiaryContainer
                EstadoPublicacion.RECHAZADA -> MaterialTheme.colorScheme.errorContainer
            }
            
            val badgeTextColor = when (tab) {
                EstadoPublicacion.VERIFICADA -> MaterialTheme.colorScheme.onPrimaryContainer
                EstadoPublicacion.PENDIENTE -> MaterialTheme.colorScheme.onTertiaryContainer
                EstadoPublicacion.RECHAZADA -> MaterialTheme.colorScheme.onErrorContainer
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = label,
                    style = TextTokens.emphasized(TextTokens.chip()),
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(badgeBgColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = count.toString(),
                        style = TextTokens.emphasized(TextTokens.caption(), FontWeight.Bold),
                        color = badgeTextColor
                    )
                }

                if (selected) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(0.8f),
                        thickness = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
