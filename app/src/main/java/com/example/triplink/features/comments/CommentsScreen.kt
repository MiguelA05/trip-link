package com.example.triplink.features.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triplink.core.components.CommentCard
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.core.components.RatingSummaryCard

@Composable
fun CommentsScreen(
	publicationId: String,
	viewModel: CommentsViewModel = hiltViewModel(),
	onBackClick: () -> Unit = {}
) {
	val uiState = remember(publicationId) { viewModel.buildUiState(publicationId) }

	Scaffold(
		modifier = Modifier.fillMaxSize(),
		containerColor = Color(0xFFF2F4F8),
		topBar = {
			GeneralTopBar(
				title = "Resenas y Calificaciones",
				onBack = onBackClick
			)
		}
	) { paddingValues ->
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.background(Color(0xFFF2F4F8))
				.padding(paddingValues),
			contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
			verticalArrangement = Arrangement.spacedBy(14.dp)
		) {
			item {
				RatingSummaryCard(
					average = uiState.averageRating,
					totalReviews = uiState.totalReviews,
					distribution = uiState.distribution
				)
			}

			item {
				Text(
					text = "${uiState.totalReviews} resenas",
					modifier = Modifier.fillMaxWidth(),
					color = Color(0xFF63758E),
					fontWeight = FontWeight.SemiBold
				)
			}

			items(uiState.reviews, key = { it.id }) { review ->
				Column {
					CommentCard(comment = review)
				}
			}
		}
	}
}
