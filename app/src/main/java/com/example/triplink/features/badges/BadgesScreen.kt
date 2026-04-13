package com.example.triplink.features.badges

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.triplink.R
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.core.components.feedback.BadgeDetailModal
import com.example.triplink.core.components.feedback.BadgeUnlockModal
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.domain.model.InsigniaIconKey
import com.example.triplink.ui.theme.TextTokens


@Composable
fun BadgesScreen(
    onBack: () -> Unit = {},
    viewModel: BadgesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Scaffold(
        topBar = {
            GeneralTopBar(
                title = stringResource(R.string.feature_badges_title),
                onBack = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                item {
                    UserStatusCard(
                        levelLabel = uiState.currentLevel,
                        points = uiState.points,
                        contributions = uiState.contributions
                    )
                }

                item {
                    Text(
                        text = stringResource(R.string.feature_badges_unlocked_section_title),
                        style = TextTokens.emphasized(TextTokens.title(), FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                item {
                    if (uiState.unlockedBadges.isEmpty()) {
                        EmptyBadgeSection(message = stringResource(R.string.feature_badges_unlocked_empty))
                    } else {
                        BadgeGrid(badges = uiState.unlockedBadges, onClick = viewModel::onBadgeClick)
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.feature_badges_locked_section_title),
                        style = TextTokens.emphasized(TextTokens.title(), FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                item {
                    if (uiState.lockedBadges.isEmpty()) {
                        EmptyBadgeSection(message = stringResource(R.string.feature_badges_locked_empty))
                    } else {
                        BadgeGrid(badges = uiState.lockedBadges, onClick = viewModel::onBadgeClick)
                    }
                }
            }

            uiState.selectedBadge?.let { badge ->
                BadgeUnlockModal(
                    badge = badge,
                    onDismiss = viewModel::dismissBadgeDetail
                )
            }

            uiState.unlockDialog?.let { unlock ->
                BadgeDetailModal(
                    badge = unlock.badge,
                    totalPoints = unlock.totalPoints,
                    levelLabel = unlock.currentLevel,
                    onDismiss = viewModel::dismissUnlockDialog
                )
            }
        }
    }
}

@Composable
private fun UserStatusCard(
    levelLabel: String,
    points: Int,
    contributions: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = levelLabel,
                style = TextTokens.emphasized(TextTokens.sectionTitle(), FontWeight.Bold)
            )

            Text(
                text = stringResource(R.string.feature_badges_current_level_label),
                style = TextTokens.emphasized(TextTokens.caption(), FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = points.toString(),
                        style = TextTokens.emphasized(TextTokens.sectionTitle(), FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.feature_badges_total_points_label),
                        style = TextTokens.caption(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                VerticalDivider(
                    modifier = Modifier.height(40.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = contributions.toString(),
                        style = TextTokens.emphasized(TextTokens.sectionTitle(), FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.feature_badges_contributions_label),
                        style = TextTokens.caption(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.feature_badges_hint_progress),
                style = TextTokens.caption(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyBadgeSection(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            style = TextTokens.body(),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BadgeGrid(badges: List<BadgeUi>, onClick: (BadgeUi) -> Unit) {
    val rows = badges.chunked(3)
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        rows.forEach { rowBadges ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowBadges.forEach { badge ->
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        BadgeItem(badge = badge, onClick = { onClick(badge) })
                    }
                }
                repeat(3 - rowBadges.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun BadgeItem(badge: BadgeUi, onClick: () -> Unit) {
    val badgeColor = resolveBadgeColor(badge.iconKey)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(4.dp, CircleShape)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = if (badge.isUnlocked) badgeColor.copy(alpha = 0.16f)
                        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.24f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = resolveBadgeIcon(badge.iconKey),
                    contentDescription = badge.name,
                    tint = if (badge.isUnlocked) badgeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = badge.name,
            style = TextTokens.emphasized(TextTokens.bodySecondary(), FontWeight.Medium),
            textAlign = TextAlign.Center,
            color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun resolveBadgeColor(iconKey: InsigniaIconKey): androidx.compose.ui.graphics.Color {
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
        InsigniaIconKey.CAMERA -> Icons.Default.CameraAlt
        InsigniaIconKey.FOOD -> Icons.Default.LocalDining
        InsigniaIconKey.PATH -> Icons.Default.Flag
        InsigniaIconKey.TROPHY -> Icons.Default.MilitaryTech
    }
}

@Preview(showBackground = true)
@Composable
fun BadgesScreenPreview() {
    BadgesScreen()
}
