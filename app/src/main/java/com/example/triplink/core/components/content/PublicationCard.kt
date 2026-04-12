package com.example.triplink.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.triplink.R
import com.example.triplink.core.localization.localizedLabel
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.ui.theme.TextColors
import com.example.triplink.ui.theme.TextTokens
import java.util.Locale

@Composable
fun PublicationCard(
    puntoInteres: PuntoInteres,
    ratingLabel: String = "4.8",
    modifier: Modifier = Modifier,
    showFooter: Boolean = true,
    onCardClick: (() -> Unit)? = null,
    onFavoriteToggle: (Boolean) -> Unit = {},
    onCommentsClick: () -> Unit = {}
) {
    var isFavorite by remember(puntoInteres.id) { mutableStateOf(false) }
    val cardBorderColor = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.92f)
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.88f)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        // Toda la tarjeta abre detalle; los botones internos mantienen su accion propia.
        onClick = { onCardClick?.invoke() },
        enabled = onCardClick != null,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.25.dp, cardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = buildInitials(
                            value = puntoInteres.usuarioAutorId,
                            fallbackInitial = stringResource(R.string.component_publication_card_default_initial)
                        ),
                        color = TextColors.Accent,
                        style = TextTokens.avatarInitial()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formatAuthorName(puntoInteres.usuarioAutorId),
                        style = TextTokens.cardTitle(),
                        color = TextColors.Primary
                    )
                    Text(
                        text = puntoInteres.fechaCreacion.toRelativeTimeLabel(),
                        style = TextTokens.cardSubtitle(),
                        color = TextColors.Secondary
                    )
                }

                // Distance Badge
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.component_publication_card_near_you),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = TextTokens.chipLabel()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(puntoInteres.fotos.firstOrNull())
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Category Badge (Top Left)
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = puntoInteres.categoria.localizedLabel(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = TextTokens.chipLabel()
                    )
                }

                // Rating Badge (Top Right)
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = ratingLabel,
                            style = TextTokens.chipLabel(),
                            color = TextColors.Primary
                        )
                    }
                }

                // Bottom Overlay (Gradient + Title + Location)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.scrim.copy(alpha = 0.55f)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = puntoInteres.titulo,
                            color = TextColors.OnImage,
                            style = TextTokens.cardTitle(),
                            maxLines = 1
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = puntoInteres.ubicacion.ciudad,
                                color = TextColors.OnImage,
                                style = TextTokens.cardSubtitle(),
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Optional Footer
            if (showFooter) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onCommentsClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = stringResource(
                                    R.string.component_publication_card_comments_content_description
                                ),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = puntoInteres.commentCount.toString(),
                            color = TextColors.Secondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                isFavorite = !isFavorite
                                onFavoriteToggle(isFavorite)
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                 imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = stringResource(
                                    R.string.component_publication_card_add_to_favorites_content_description
                                ),
                                tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = puntoInteres.favoriteCount.toString(),
                            color = TextColors.Secondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

private fun buildInitials(value: String, fallbackInitial: String): String {
    val normalized = value.substringBefore('@').replace('.', ' ').replace('_', ' ').trim()
    val parts = normalized.split(" ").filter { it.isNotBlank() }
    if (parts.isEmpty()) return fallbackInitial
    return parts.take(2).joinToString("") { it.first().uppercaseChar().toString() }
}

private fun formatAuthorName(userId: String): String =
    userId.substringBefore('@')
        .replace('.', ' ')
        .replace('_', ' ')
        .split(' ')
        .filter { it.isNotBlank() }
        .joinToString(" ") { token ->
            token.lowercase(Locale.ROOT).replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
            }
        }


@Composable
private fun Long.toRelativeTimeLabel(now: Long = System.currentTimeMillis()): String {
    val delta = (now - this).coerceAtLeast(0L)
    val minutes = delta / 60_000L
    val hours = minutes / 60L
    val days = hours / 24L
    return when {
        minutes < 1 -> stringResource(R.string.component_publication_card_time_just_now)
        minutes < 60 -> pluralStringResource(
            R.plurals.component_publication_card_time_minutes_ago,
            minutes.toInt(),
            minutes.toInt()
        )
        hours < 24 -> pluralStringResource(
            R.plurals.component_publication_card_time_hours_ago,
            hours.toInt(),
            hours.toInt()
        )
        days < 7 -> pluralStringResource(
            R.plurals.component_publication_card_time_days_ago,
            days.toInt(),
            days.toInt()
        )
        else -> {
            val weeks = (days / 7L).toInt()
            pluralStringResource(
                R.plurals.component_publication_card_time_weeks_ago,
                weeks,
                weeks
            )
        }
    }
}

