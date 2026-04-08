package com.example.triplink.features.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triplink.core.components.CommentCard
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.core.components.RatingSummaryCard
import com.example.triplink.core.utils.RequestResult

@Composable
fun CommentsScreen(
	publicationId: String,
	viewModel: CommentsViewModel = hiltViewModel(),
	onBackClick: () -> Unit = {}
) {
	val saveResult by viewModel.saveCommentResult.collectAsState()
	val refreshTick by viewModel.refreshTick.collectAsState()
	val snackbarHostState = remember { SnackbarHostState() }
	var editingCommentId by remember { mutableStateOf<String?>(null) }
	var editingText by remember { mutableStateOf("") }
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
					Spacer(modifier = Modifier.height(8.dp))
					if (editingCommentId == review.id) {
						OutlinedTextField(
							value = editingText,
							onValueChange = { editingText = it },
							modifier = Modifier.fillMaxWidth(),
							label = { Text("Editar comentario") }
						)
						Spacer(modifier = Modifier.height(6.dp))
						Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
							Button(onClick = {
								viewModel.updateComment(publicationId, review.id, editingText)
								editingCommentId = null
							}) { Text("Guardar") }
							Button(onClick = { editingCommentId = null }) { Text("Cancelar") }
						}
					} else {
						Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
							Button(onClick = {
								editingCommentId = review.id
								editingText = review.text
							}) { Text("Editar") }
							Button(onClick = { viewModel.deleteComment(publicationId, review.id) }) { Text("Eliminar") }
						}
					}
				}
			}

			item {
				Spacer(modifier = Modifier.height(8.dp))
				Text(
					text = "Escribe una reseña",
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold
				)
				Spacer(modifier = Modifier.height(8.dp))
				OutlinedTextField(
					value = viewModel.commentText,
					onValueChange = viewModel::updateCommentText,
					modifier = Modifier.fillMaxWidth(),
					label = { Text("Comentario") }
				)
				Spacer(modifier = Modifier.height(8.dp))
				Button(onClick = { viewModel.saveComment(publicationId, userName = "Usuario") }) {
					Text("Publicar")
				}
			}
		}
	}
}
