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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.triplink.features.user.info.ContributionTab
import com.example.triplink.ui.theme.PrincipalBlue

@Composable
fun StatusTabs(
    selectedTab: ContributionTab,
    onTabSelected: (ContributionTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        ContributionTab.VERIFIED to "Verificados",
        ContributionTab.PENDING to "Pendientes",
        ContributionTab.REJECTED to "Rechazados"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        tabs.forEach { (tab, label) ->
            val selected = selectedTab == tab
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

