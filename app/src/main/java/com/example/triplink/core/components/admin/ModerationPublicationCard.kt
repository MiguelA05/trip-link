package com.example.triplink.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.triplink.R
import com.example.triplink.core.image.AppImageLoader
import com.example.triplink.features.admin.moderation.ModerationPublicationCardStatus
import com.example.triplink.features.admin.moderation.ModerationPublicationCardUi
import com.example.triplink.ui.theme.TextColors
import com.example.triplink.ui.theme.TextTokens

@Composable
fun ModerationPublicationCard(
    publication: ModerationPublicationCardUi,
    onApproveRequested: (String) -> Unit,
    onRejectRequested: (String) -> Unit,
    onDetailsClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    swipeHintText: String? = null
) {
    val resolvedSwipeHintText = swipeHintText ?: stringResource(
        R.string.component_moderation_publication_card_swipe_hint
    )
    val interactionEnabled = publication.status == ModerationPublicationCardStatus.PENDING
    val cardBorderColor = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.92f)
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.9f)
    }
    val authorName = publication.authorName.ifBlank {
        stringResource(R.string.vm_user_info_default_user_name)
    }
    val authorInitials = buildAuthorInitials(
        value = authorName,
        fallbackInitial = stringResource(R.string.component_publication_card_default_initial)
    )

    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { it * 0.32f },
        confirmValueChange = { value ->
            if (!interactionEnabled) return@rememberSwipeToDismissBoxState false

            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> onApproveRequested(publication.id)
                SwipeToDismissBoxValue.EndToStart -> onRejectRequested(publication.id)
                SwipeToDismissBoxValue.Settled -> Unit
            }
            false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = interactionEnabled,
        enableDismissFromEndToStart = interactionEnabled,
        backgroundContent = {
            val isApproveDirection = dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd
            val backgroundColor = if (isApproveDirection) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
            val iconTint = if (isApproveDirection) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(backgroundColor)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (isApproveDirection) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = if (isApproveDirection) Icons.Outlined.Check else Icons.Outlined.Close,
                    contentDescription = null,
                    tint = iconTint
                )
            }
        }
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(1.25.dp, cardBorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(168.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(publication.imageUrl)
                            .crossfade(true)
                            .build(),
                        imageLoader = AppImageLoader.get(LocalContext.current),
                        contentDescription = publication.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f))
                                )
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.tertiary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = authorInitials,
                                    color = MaterialTheme.colorScheme.onTertiary,
                                    style = TextTokens.emphasized(TextTokens.caption(), FontWeight.Bold)
                                )
                            }

                            Column {
                                Text(
                                    text = authorName,
                                    style = TextTokens.emphasized(TextTokens.chip(), FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = publication.timeLabel,
                                    style = TextTokens.caption(),
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = when (publication.status) {
                            ModerationPublicationCardStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer
                            ModerationPublicationCardStatus.VERIFIED -> MaterialTheme.colorScheme.primaryContainer
                            ModerationPublicationCardStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = when (publication.status) {
                                ModerationPublicationCardStatus.PENDING -> stringResource(
                                    R.string.component_moderation_publication_card_status_pending
                                )
                                ModerationPublicationCardStatus.VERIFIED -> stringResource(
                                    R.string.component_moderation_publication_card_status_verified
                                )
                                ModerationPublicationCardStatus.REJECTED -> stringResource(
                                    R.string.component_moderation_publication_card_status_rejected
                                )
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = TextTokens.emphasized(TextTokens.button(), FontWeight.Bold),
                            color = when (publication.status) {
                                ModerationPublicationCardStatus.PENDING -> MaterialTheme.colorScheme.onTertiaryContainer
                                ModerationPublicationCardStatus.VERIFIED -> MaterialTheme.colorScheme.onPrimaryContainer
                                ModerationPublicationCardStatus.REJECTED -> MaterialTheme.colorScheme.onErrorContainer
                            }
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = publication.title,
                        style = TextTokens.emphasized(TextTokens.screenTitle(), FontWeight.Bold),
                        color = TextColors.Primary
                    )

                    val acceptedReportsText = pluralStringResource(
                        R.plurals.component_moderation_publication_card_accepted_reports,
                        publication.reportCount,
                        publication.reportCount
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Text(
                                text = publication.categoryLabel,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = TextTokens.emphasized(TextTokens.chip(), FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }

                        if (publication.reportCount > 0) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Text(
                                    text = acceptedReportsText,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = TextTokens.emphasized(TextTokens.chip(), FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "\$",
                                style = TextTokens.emphasized(TextTokens.body(), FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = publication.priceLabel,
                                style = TextTokens.emphasized(TextTokens.body(), FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.size(12.dp))
                            Text(
                                text = "|",
                                style = TextTokens.emphasized(TextTokens.body(), FontWeight.Bold),
                                color = MaterialTheme.colorScheme.outlineVariant,
                            )
                            Spacer(modifier = Modifier.size(12.dp))
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = publication.scheduleLabel,
                                style = TextTokens.emphasized(TextTokens.body(), FontWeight.Medium),
                                color = TextColors.Secondary
                            )
                        }
                    }

                    publication.reasonMessage?.takeIf { it.isNotBlank() }?.let { message ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.WarningAmber,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = stringResource(
                                        R.string.component_moderation_publication_card_reason_prefix,
                                        message
                                    ),
                                    style = TextTokens.emphasized(TextTokens.chip(), FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                )
                            }
                        }
                    }

                    if (interactionEnabled) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.error
                            ) {
                                IconButton(onClick = { onRejectRequested(publication.id) }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = stringResource(
                                            R.string.component_moderation_publication_card_reject_action
                                        ),
                                        tint = MaterialTheme.colorScheme.onError
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = { onDetailsClick(publication.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.RemoveRedEye,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(
                                    text = stringResource(
                                        R.string.component_moderation_publication_card_view_detail_action
                                    ),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = TextTokens.emphasized(TextTokens.label(), FontWeight.SemiBold)
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                IconButton(onClick = { onApproveRequested(publication.id) }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = stringResource(
                                            R.string.component_moderation_publication_card_verify_action
                                        ),
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }

                        Text(
                            text = resolvedSwipeHintText,
                            style = TextTokens.caption(),
                            color = TextColors.Muted,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

private fun buildAuthorInitials(value: String, fallbackInitial: String): String {
    val parts = value.trim().split(" ").filter { it.isNotBlank() }
    if (parts.isEmpty()) return fallbackInitial
    return parts.take(2).joinToString("") { part ->
        part.firstOrNull()?.uppercaseChar()?.toString().orEmpty()
    }.ifBlank { fallbackInitial }
}
