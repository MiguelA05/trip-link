package com.example.triplink.core.components.feedback

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.triplink.R
import com.example.triplink.domain.model.InsigniaIconKey
import com.example.triplink.features.badges.BadgeUi
import com.example.triplink.ui.theme.TextTokens

@Composable
fun BadgeDetailModal(
    badge: BadgeUi,
    totalPoints: Int,
    levelLabel: String,
    onDismiss: () -> Unit
) {
    val badgeColor = when (badge.iconKey) {
        InsigniaIconKey.SPARK -> MaterialTheme.colorScheme.primary
        InsigniaIconKey.COMPASS -> MaterialTheme.colorScheme.secondary
        InsigniaIconKey.CAMERA -> MaterialTheme.colorScheme.tertiary
        InsigniaIconKey.FOOD -> MaterialTheme.colorScheme.error
        InsigniaIconKey.PATH -> MaterialTheme.colorScheme.primary
        InsigniaIconKey.TROPHY -> MaterialTheme.colorScheme.tertiary
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.component_general_alert_dialog_close_content_description),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = badgeColor.copy(alpha = 0.12f)
                ) {
                    Box(
                        modifier = Modifier.size(88.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (badge.iconKey) {
                                InsigniaIconKey.SPARK -> Icons.Default.Stars
                                InsigniaIconKey.COMPASS -> Icons.Default.Explore
                                InsigniaIconKey.CAMERA -> Icons.Default.PhotoCamera
                                InsigniaIconKey.FOOD -> Icons.Default.LocalDining
                                InsigniaIconKey.PATH -> Icons.Default.Terrain
                                InsigniaIconKey.TROPHY -> Icons.Default.MilitaryTech
                            },
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.badge_unlock_title),
                    style = TextTokens.emphasized(TextTokens.sectionTitle(), androidx.compose.ui.text.font.FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Text(
                    text = badge.name,
                    style = TextTokens.emphasized(TextTokens.screenTitle(), androidx.compose.ui.text.font.FontWeight.Bold),
                    color = badgeColor,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Celebration,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.badge_unlock_points, badge.points),
                            style = TextTokens.emphasized(TextTokens.button(), androidx.compose.ui.text.font.FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.badge_unlock_progress, totalPoints, levelLabel),
                    style = TextTokens.body(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.component_badge_detail_modal_view_badges_action),
                        style = TextTokens.emphasized(TextTokens.button(), androidx.compose.ui.text.font.FontWeight.Bold)
                    )
                }
            }
        }
    }
}