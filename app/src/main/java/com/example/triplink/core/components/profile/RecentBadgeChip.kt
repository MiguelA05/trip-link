package com.example.triplink.core.components.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.triplink.R
import com.example.triplink.domain.model.InsigniaIconKey
import com.example.triplink.features.user.info.UserRecentBadgeItem
import com.example.triplink.ui.theme.TextTokens

@Composable
fun RecentBadgeChip(
    badge: UserRecentBadgeItem,
    modifier: Modifier = Modifier
) {
    val badgeColor = resolveBadgeColor(badge.iconKey)
    val badgeIcon = resolveBadgeIcon(badge.iconKey)

    Card(
        modifier = modifier
            .size(width = 162.dp, height = 56.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = badgeColor.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.24f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                BadgeIconBox(icon = badgeIcon, color = badgeColor)
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text(
                    text = badge.name,
                    style = TextTokens.emphasized(TextTokens.caption()),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.component_badge_detail_modal_points, badge.points),
                    style = TextTokens.caption(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun BadgeIconBox(icon: ImageVector, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun resolveBadgeColor(iconKey: InsigniaIconKey): Color {
    return when (iconKey) {
        InsigniaIconKey.SPARK -> MaterialTheme.colorScheme.primary
        InsigniaIconKey.COMPASS -> MaterialTheme.colorScheme.secondary
        InsigniaIconKey.CAMERA -> MaterialTheme.colorScheme.tertiary
        InsigniaIconKey.FOOD -> MaterialTheme.colorScheme.error
        InsigniaIconKey.PATH -> MaterialTheme.colorScheme.primary
        InsigniaIconKey.TROPHY -> MaterialTheme.colorScheme.tertiary
    }
}

private fun resolveBadgeIcon(iconKey: InsigniaIconKey): ImageVector {
    return when (iconKey) {
        InsigniaIconKey.SPARK -> Icons.Default.RocketLaunch
        InsigniaIconKey.COMPASS -> Icons.Default.Explore
        InsigniaIconKey.CAMERA -> Icons.Default.PhotoCamera
        InsigniaIconKey.FOOD -> Icons.Default.LocalDining
        InsigniaIconKey.PATH -> Icons.Default.Flag
        InsigniaIconKey.TROPHY -> Icons.Default.MilitaryTech
    }
}



