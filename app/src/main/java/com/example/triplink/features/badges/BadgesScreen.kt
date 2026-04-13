package com.example.triplink.features.badges

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.triplink.R
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.core.components.feedback.BadgeDetailModal
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.ui.theme.TextTokens


@Composable
fun BadgesScreen(
    onBack: () -> Unit = {},
    viewModel: BadgesViewModel = hiltViewModel()
) {


    Scaffold(
        topBar = {
            GeneralTopBar(
                title = stringResource(R.string.feature_badges_title),
                onBack = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                UserStatusCard()

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(R.string.feature_badges_title),
                    style = TextTokens.emphasized(TextTokens.screenTitle(), FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(viewModel.badges) { badge ->
                        BadgeItem(badge = badge, onClick = { viewModel.selectedBadge = badge })
                    }
                }
            }

            // Modal
            viewModel.selectedBadge?.let { badge ->
                BadgeDetailModal(
                    badge = badge,
                    onDismiss = { viewModel.selectedBadge = null }
                )
            }
        }
    }
}

@Composable
fun UserStatusCard() {
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
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.feature_badges_current_level_name),
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
                        text = "0",
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
                        text = "0",
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

            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(
                            R.string.feature_badges_last_activity,
                            "22/02/2026"
                        ),
                        style = TextTokens.caption(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun BadgeItem(badge: Badge, onClick: () -> Unit) {
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
                    .background(resolveBadgeColor(badge.colorRole).copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = badge.icon,
                    contentDescription = badge.name,
                    tint = resolveBadgeColor(badge.colorRole),
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = badge.name,
            style = TextTokens.emphasized(TextTokens.bodySecondary(), FontWeight.Medium),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun resolveBadgeColor(colorRole: BadgeColorRole): androidx.compose.ui.graphics.Color {
    return when (colorRole) {
        BadgeColorRole.PRIMARY -> MaterialTheme.colorScheme.primary
        BadgeColorRole.SECONDARY -> MaterialTheme.colorScheme.secondary
        BadgeColorRole.TERTIARY -> MaterialTheme.colorScheme.tertiary
        BadgeColorRole.ERROR -> MaterialTheme.colorScheme.error
    }
}

@Preview(showBackground = true)
@Composable
fun BadgesScreenPreview() {
    BadgesScreen()
}
