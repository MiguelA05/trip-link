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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.core.components.CommentCard
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.core.components.RatingSummaryCard
import com.example.triplink.core.navigation.SessionState
import com.example.triplink.core.navigation.SessionViewModel
import com.example.triplink.core.utils.RequestResult

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
	val currentUserName = currentUserId.substringBefore('@').ifBlank { "Usuario" }
	val snackbarHostState = remember { SnackbarHostState() }
	var editingCommentId by remember { mutableStateOf<String?>(null) }
	var editingText by remember { mutableStateOf("") }
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
				title = "Reseñas y calificaciones",
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
					text = "${uiState.totalReviews} reseñas",
					modifier = Modifier.fillMaxWidth(),
					color = Color(0xFF63758E),
					fontWeight = FontWeight.SemiBold
				)
			}

			items(uiState.reviews, key = { it.id }) { review ->
				Column {
					CommentCard(comment = review)
					Spacer(modifier = Modifier.height(8.dp))
					val canManage = review.usuarioId.equals(currentUserId, ignoreCase = true)
					if (canManage) {
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
									viewModel.updateComment(publicationId, review.id, currentUserId, editingText)
									editingCommentId = null
								}) { Text("Guardar") }
								TextButton(onClick = { editingCommentId = null }) { Text("Cancelar") }
							}
						} else {
							Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
								TextButton(onClick = {
									editingCommentId = review.id
									editingText = review.text
								}) { Text("Editar") }
								TextButton(onClick = { deletingCommentId = review.id }) { Text("Eliminar") }
							}
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
				if (currentUserId.isBlank()) {
					Text(
						text = "Inicia sesión para publicar una reseña.",
						color = Color(0xFF63758E)
					)
				} else {
					OutlinedTextField(
						value = viewModel.commentText,
						onValueChange = viewModel::updateCommentText,
						modifier = Modifier.fillMaxWidth(),
						label = { Text("Comentario") }
					)
					Spacer(modifier = Modifier.height(8.dp))
					GeneralButton(
						text = "Publicar",
						enabled = viewModel.commentText.isNotBlank(),
						onClick = {
							viewModel.saveComment(
								publicationId = publicationId,
								userId = currentUserId,
								userName = currentUserName
							)
						}
					)
				}
			}
		}
	}

	if (deletingCommentId != null) {
		AlertDialog(
			onDismissRequest = { deletingCommentId = null },
			title = { Text("Eliminar comentario") },
			text = { Text("¿Seguro que quieres eliminar este comentario?") },
			confirmButton = {
				TextButton(onClick = {
					viewModel.deleteComment(publicationId, deletingCommentId!!, currentUserId)
					deletingCommentId = null
				}) { Text("Eliminar") }
			},
			dismissButton = {
				TextButton(onClick = { deletingCommentId = null }) { Text("Cancelar") }
			}
		)
	}
}

