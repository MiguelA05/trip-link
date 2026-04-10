package com.example.triplink.features.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.R
import com.example.triplink.core.components.CompactDestructiveConfirmDialog
import com.example.triplink.core.components.CommentCard
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.core.components.RatingSummaryCard
import com.example.triplink.core.navigation.SessionState
import com.example.triplink.core.navigation.SessionViewModel
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.ui.theme.PrincipalRed

@Composable
fun CommentsScreen(
	publicationId: String,
	viewModel: CommentsViewModel = hiltViewModel(),
	onBackClick: () -> Unit = {}
) {
	val saveResult by viewModel.saveCommentResult.collectAsState()
	val refreshTick by viewModel.refreshTick.collectAsState()
	val sessionViewModel: SessionViewModel = hiltViewModel()
	val sessionState by sessionViewModel.sessionState.collectAsState()
	val currentUserId = (sessionState as? SessionState.Authenticated)?.session?.userId.orEmpty()
	val snackbarHostState = remember { SnackbarHostState() }
	var deletingCommentId by remember { mutableStateOf<String?>(null) }
	val uiState = remember(publicationId, refreshTick) { viewModel.buildUiState(publicationId) }

	LaunchedEffect(saveResult) {
		saveResult?.let { result ->
			val message = when (result) {
				is RequestResult.Success -> result.message
				is RequestResult.Failure -> result.errorMessage
			}
			snackbarHostState.showSnackbar(message)
			viewModel.clearSaveResult()
		}
	}

	Scaffold(
		modifier = Modifier.fillMaxSize(),
		containerColor = Color(0xFFF2F4F8),
		snackbarHost = { SnackbarHost(snackbarHostState) },
		topBar = {
			GeneralTopBar(
				title = stringResource(R.string.feature_comments_title),
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
					text = stringResource(R.string.feature_comments_total_reviews, uiState.totalReviews),
					modifier = Modifier.fillMaxWidth(),
					color = Color(0xFF63758E),
					fontWeight = FontWeight.SemiBold
				)
			}

			items(uiState.reviews, key = { it.id }) { review ->
				Box {
					CommentCard(comment = review)
					val canDelete = review.usuarioId.equals(currentUserId, ignoreCase = true)
					if (canDelete) {
						OutlinedButton(
							onClick = { deletingCommentId = review.id },
							modifier = Modifier
								.align(Alignment.TopEnd)
								.padding(top = 12.dp, end = 12.dp)
								.size(34.dp),
							shape = CircleShape,
							border = BorderStroke(1.dp, Color(0xFFF6C6CC)),
							colors = ButtonDefaults.outlinedButtonColors(
								containerColor = Color(0xFFFFEBEE),
								contentColor = PrincipalRed
							),
							contentPadding = PaddingValues(0.dp)
						) {
							Icon(
								imageVector = Icons.Outlined.Delete,
								contentDescription = stringResource(R.string.feature_comments_delete_content_description),
								tint = PrincipalRed,
								modifier = Modifier.size(18.dp)
							)
						}
					}
				}
			}
		}
	}

	if (deletingCommentId != null) {
		CompactDestructiveConfirmDialog(
			title = stringResource(R.string.feature_comments_delete_dialog_title),
			message = stringResource(R.string.feature_comments_delete_dialog_message),
			confirmText = stringResource(R.string.feature_comments_delete_dialog_confirm),
			dismissText = stringResource(R.string.feature_comments_delete_dialog_cancel),
			onDismissRequest = { deletingCommentId = null },
			onConfirm = {
				viewModel.deleteComment(publicationId, deletingCommentId!!, currentUserId)
				deletingCommentId = null
			}
		)
	}
}

