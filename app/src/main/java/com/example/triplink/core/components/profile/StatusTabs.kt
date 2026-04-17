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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.triplink.R
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.ui.theme.TextTokens

@Composable
fun StatusTabs(
    selectedIndex: Int,
    onTabSelectedIndex: (Int) -> Unit,
    modifier: Modifier = Modifier,
    favoritesCount: Int = 0,
    verifiedCount: Int = 0,
    pendingCount: Int = 0,
    rejectedCount: Int = 0
) {
    // Indices: 0 = Favoritos, 1 = Verificadas, 2 = Pendientes, 3 = Rechazadas

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // FAVORITES (leftmost, visually separated)
        val favSelected = selectedIndex == 0
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .clickable { onTabSelectedIndex(0) }
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.component_status_tabs_favorites),
                style = TextTokens.emphasized(TextTokens.chip()),
                color = if (favSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = favoritesCount.toString(),
                    style = TextTokens.emphasized(TextTokens.caption(), FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (favSelected) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(0.8f),
                    thickness = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Vertical separator between favorites and the other tabs
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(36.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
        )

        // Remaining tabs
        val tabs = listOf(
            Triple(R.string.component_status_tabs_verified, verifiedCount, MaterialTheme.colorScheme.primaryContainer),
            Triple(R.string.component_status_tabs_pending, pendingCount, MaterialTheme.colorScheme.tertiaryContainer),
            Triple(R.string.component_status_tabs_rejected, rejectedCount, MaterialTheme.colorScheme.errorContainer)
        )

        tabs.forEachIndexed { index, (labelRes, count, badgeBg) ->
            val actualIndex = index + 1 // shift because favorites is 0
            val selected = selectedIndex == actualIndex
            val badgeTextColor = when (actualIndex) {
                1 -> MaterialTheme.colorScheme.onPrimaryContainer
                2 -> MaterialTheme.colorScheme.onTertiaryContainer
                else -> MaterialTheme.colorScheme.onErrorContainer
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelectedIndex(actualIndex) }
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(labelRes),
                    style = TextTokens.emphasized(TextTokens.chip()),
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(badgeBg, CircleShape),
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
