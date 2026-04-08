package com.example.triplink.core.components

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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
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
import com.example.triplink.features.admin.moderation.ModerationPublicationCardStatus
import com.example.triplink.features.admin.moderation.ModerationPublicationCardUi
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.PrincipalGreen
import com.example.triplink.ui.theme.PrincipalRed

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
            val backgroundColor = if (isApproveDirection) Color(0xFFDFF4E2) else Color(0xFFFFE4E4)
            val iconTint = if (isApproveDirection) PrincipalGreen else PrincipalRed

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
            color = Color(0xFFF9FAFB),
            shadowElevation = 2.dp,
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
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.52f))
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
                                    .background(Color(0xFFD96A1E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = publication.authorName.split(" ").take(2).joinToString("") { it.first().uppercase() },
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Column {
                                Text(
                                    text = publication.authorName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = publication.timeLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = when (publication.status) {
                            ModerationPublicationCardStatus.PENDING -> Color(0xFFFFEEE0)
                            ModerationPublicationCardStatus.VERIFIED -> Color(0xFFE4F5E7)
                            ModerationPublicationCardStatus.REJECTED -> Color(0xFFFDE6E6)
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
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = when (publication.status) {
                                ModerationPublicationCardStatus.PENDING -> Color(0xFFCC6E00)
                                ModerationPublicationCardStatus.VERIFIED -> Color(0xFF2E7D32)
                                ModerationPublicationCardStatus.REJECTED -> Color(0xFFD84343)
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
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2430)
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
                            color = Color(0xFFFFE9D6)
                        ) {
                            Text(
                                text = publication.categoryLabel,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFF07A17),
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (publication.reportCount > 0) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFF1D7)
                            ) {
                                Text(
                                    text = acceptedReportsText,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFFD97A00),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                                imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF9AA3B2),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = publication.cityLabel,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF6A7688)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF1F4FB)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "\$",
                                style = MaterialTheme.typography.bodyLarge,
                                color = PrincipalBlue,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = publication.priceLabel,
                                style = MaterialTheme.typography.bodyLarge,
                                color = PrincipalBlue,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.size(12.dp))
                            Text(
                                text = "|",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFFD1D8E3),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.size(12.dp))
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = Color(0xFF8A93A3),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = publication.scheduleLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF677487),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    publication.reasonMessage?.takeIf { it.isNotBlank() }?.let { message ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFF2F2)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.WarningAmber,
                                    contentDescription = null,
                                    tint = Color(0xFFD84343)
                                )
                                Text(
                                    text = stringResource(
                                        R.string.component_moderation_publication_card_reason_prefix,
                                        message
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFD84343),
                                    fontWeight = FontWeight.SemiBold
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
                                color = Color(0xFFF35A5A)
                            ) {
                                IconButton(onClick = { onRejectRequested(publication.id) }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = stringResource(
                                            R.string.component_moderation_publication_card_reject_action
                                        ),
                                        tint = Color.White
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
                                    tint = PrincipalBlue
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(
                                    text = stringResource(
                                        R.string.component_moderation_publication_card_view_detail_action
                                    ),
                                    color = PrincipalBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF3CBF2A)
                            ) {
                                IconButton(onClick = { onApproveRequested(publication.id) }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = stringResource(
                                            R.string.component_moderation_publication_card_verify_action
                                        ),
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        Text(
                            text = resolvedSwipeHintText,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFB0B8C5),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

