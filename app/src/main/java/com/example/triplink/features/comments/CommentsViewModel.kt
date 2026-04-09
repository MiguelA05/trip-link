package com.example.triplink.features.comments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.core.components.RatingCount
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.data.seed.seedCommentsFor
import com.example.triplink.domain.model.Comentario
import com.example.triplink.domain.repository.comment.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.UUID

data class CommentsUiState(
    val publicationId: String,
    val averageRating: Double,
    val totalReviews: Int,
    val distribution: List<RatingCount>,
    val reviews: List<Comentario>
)

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val commentRepository: CommentRepository
) : ViewModel() {

    var commentText by mutableStateOf("")
        private set

    var selectedRating by mutableStateOf(5f)
        private set

    private val _saveCommentResult = MutableStateFlow<RequestResult?>(null)
    val saveCommentResult: StateFlow<RequestResult?> = _saveCommentResult.asStateFlow()

    private val _refreshTick = MutableStateFlow(0)
    val refreshTick: StateFlow<Int> = _refreshTick.asStateFlow()

    fun updateCommentText(text: String) {
        commentText = text
    }

    fun updateRating(rating: Float) {
        selectedRating = rating
    }

    fun saveComment(publicationId: String, userId: String, userName: String) {
        if (commentText.isBlank()) {
            _saveCommentResult.value = RequestResult.Failure("El comentario no puede estar vacío")
            return
        }

        viewModelScope.launch {
            try {
                val comment = Comentario(
                    id = UUID.randomUUID().toString(),
                    usuarioId = userId,
                    puntoInteresId = publicationId,
                    userName = userName,
                    date = System.currentTimeMillis(),
                    rating = selectedRating,
                    text = commentText
                )

                val wasSaved = commentRepository.saveComment(publicationId, comment)
                if (wasSaved) {
                    _saveCommentResult.value = RequestResult.Success("Comentario guardado exitosamente")
                    // Limpiar campos después de guardar exitosamente
                    commentText = ""
                    selectedRating = 5f
                    _refreshTick.value += 1
                } else {
                    _saveCommentResult.value = RequestResult.Failure("No se pudo guardar el comentario")
                }
            } catch (e: Exception) {
                _saveCommentResult.value = RequestResult.Failure("Error al guardar: ${e.message}")
            }
        }
    }

    fun updateComment(publicationId: String, commentId: String, currentUserId: String, newText: String) {
        if (newText.isBlank()) {
            _saveCommentResult.value = RequestResult.Failure("El comentario no puede estar vacío")
            return
        }

        viewModelScope.launch {
            try {
                val existing = commentRepository.getCommentsByPublicationId(publicationId)
                    .firstOrNull { it.id == commentId }
                    ?: run {
                        _saveCommentResult.value = RequestResult.Failure("Comentario no encontrado")
                        return@launch
                    }

                if (!existing.usuarioId.equals(currentUserId, ignoreCase = true)) {
                    _saveCommentResult.value = RequestResult.Failure("Solo puedes editar tus propios comentarios")
                    return@launch
                }

                val wasUpdated = commentRepository.updateComment(
                    publicationId = publicationId,
                    comment = existing.copy(text = newText)
                )

                _saveCommentResult.value = if (wasUpdated) {
                    _refreshTick.value += 1
                    RequestResult.Success("Comentario actualizado")
                } else {
                    RequestResult.Failure("No se pudo actualizar el comentario")
                }
            } catch (e: Exception) {
                _saveCommentResult.value = RequestResult.Failure("Error al actualizar: ${e.message}")
            }
        }
    }

    fun deleteComment(publicationId: String, commentId: String, currentUserId: String) {
        viewModelScope.launch {
            try {
                val existing = commentRepository.getCommentsByPublicationId(publicationId)
                    .firstOrNull { it.id == commentId }
                    ?: run {
                        _saveCommentResult.value = RequestResult.Failure("Comentario no encontrado")
                        return@launch
                    }

                if (!existing.usuarioId.equals(currentUserId, ignoreCase = true)) {
                    _saveCommentResult.value = RequestResult.Failure("Solo puedes eliminar tus propios comentarios")
                    return@launch
                }

                val wasDeleted = commentRepository.deleteComment(publicationId, commentId)
                _saveCommentResult.value = if (wasDeleted) {
                    _refreshTick.value += 1
                    RequestResult.Success("Comentario eliminado")
                } else {
                    RequestResult.Failure("No se pudo eliminar el comentario")
                }
            } catch (e: Exception) {
                _saveCommentResult.value = RequestResult.Failure("Error al eliminar: ${e.message}")
            }
        }
    }

    fun buildUiState(publicationId: String): CommentsUiState {
        val reviews = commentRepository.getCommentsByPublicationId(publicationId)
            .takeIf { it.isNotEmpty() }
            ?: seedCommentsFor(publicationId)

        val totalReviews = reviews.size
        val averageRating = commentRepository.getAverageRating(publicationId)
            .takeIf { it > 0.0 }
            ?: if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else 0.0

        val distribution = listOf(
            RatingCount(stars = 5, count = reviews.count { it.rating >= 5f }),
            RatingCount(stars = 4, count = reviews.count { it.rating in 4f..4.9f }),
            RatingCount(stars = 3, count = reviews.count { it.rating in 3f..3.9f }),
            RatingCount(stars = 2, count = reviews.count { it.rating in 2f..2.9f }),
            RatingCount(stars = 1, count = reviews.count { it.rating < 2f })
        )

        return CommentsUiState(
            publicationId = publicationId,
            averageRating = averageRating,
            totalReviews = totalReviews,
            distribution = distribution,
            reviews = reviews.sortedByDescending { it.date }
        )
    }

    fun clearSaveResult() {
        _saveCommentResult.value = null
    }
}
