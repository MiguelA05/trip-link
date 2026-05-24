package com.example.triplink.core.components.publicationdetails.hero

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.triplink.R
import com.example.triplink.core.image.AppImageLoader
import com.example.triplink.ui.theme.TextTokens

@Composable
fun ImageCarousel(
    imageUrls: List<String>,
    title: String,
    categoryLabel: String,
    modifier: Modifier = Modifier,
    height: Dp = 300.dp,
    showReportAction: Boolean = false,
    reportActionEnabled: Boolean = true,
    onReportClick: () -> Unit = {}
) {
    val images = remember(imageUrls) { imageUrls.filter { it.isNotBlank() }.ifEmpty { listOf("") } }
    val pagerState = rememberPagerState(pageCount = { images.size })
    val scope = rememberCoroutineScope()
    val hasMultipleImages = images.size > 1

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val imageUrl = images[page]
            if (imageUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    imageLoader = AppImageLoader.get(LocalContext.current),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }

        if (hasMultipleImages) {
            DotsIndicator(
                totalDots = images.size,
                selectedIndex = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 14.dp)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = categoryLabel.uppercase(),
                color = MaterialTheme.colorScheme.secondary,
                style = TextTokens.emphasized(TextTokens.button(), androidx.compose.ui.text.font.FontWeight.Bold)
            )
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = TextTokens.emphasized(TextTokens.screenTitle(), androidx.compose.ui.text.font.FontWeight.Bold)
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .size(44.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f)
        ) {
            IconButton(
                enabled = hasMultipleImages && pagerState.currentPage > 0,
                onClick = {
                    if (pagerState.currentPage > 0) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    }
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary.copy(
                        alpha = if (hasMultipleImages && pagerState.currentPage > 0) 1f else 0.45f
                    )
                )
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .size(44.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f)
        ) {
            IconButton(
                enabled = hasMultipleImages && pagerState.currentPage < images.lastIndex,
                onClick = {
                    if (pagerState.currentPage < images.lastIndex) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary.copy(
                        alpha = if (hasMultipleImages && pagerState.currentPage < images.lastIndex) 1f else 0.45f
                    )
                )
            }
        }

        if (showReportAction) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                shape = CircleShape,
                color = if (reportActionEnabled) {
                    MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f)
                } else {
                    MaterialTheme.colorScheme.scrim.copy(alpha = 0.22f)
                }
            ) {
                IconButton(onClick = onReportClick, enabled = reportActionEnabled) {
                    Icon(
                        imageVector = Icons.Outlined.ErrorOutline,
                        contentDescription = stringResource(R.string.feature_publication_details_report_content_description),
                        tint = if (reportActionEnabled) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.55f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalDots) { index ->
            val isSelected = index == selectedIndex
            Surface(
                modifier = Modifier.size(if (isSelected) 10.dp else 8.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = if (isSelected) 0.95f else 0.45f)
            ) {}
        }
    }
}



