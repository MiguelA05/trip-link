package com.example.triplink.features.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.core.components.RatingCount
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.domain.model.Comentario
import com.example.triplink.domain.repository.comment.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    private val _saveCommentResult = MutableStateFlow<RequestResult?>(null)
    val saveCommentResult: StateFlow<RequestResult?> = _saveCommentResult.asStateFlow()

    private val _refreshTick = MutableStateFlow(0)
    val refreshTick: StateFlow<Int> = _refreshTick.asStateFlow()


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

        val totalReviews = reviews.size
        val averageRating = if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else 0.0

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
