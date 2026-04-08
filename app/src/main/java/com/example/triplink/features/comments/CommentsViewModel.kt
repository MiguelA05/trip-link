package com.example.triplink.features.comments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.core.components.RatingCount
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.domain.model.Comentario
import com.example.triplink.domain.repository.user.UserRepository
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
    private val userRepository: UserRepository
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

                val wasSaved = userRepository.saveComment(publicationId, comment)
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
                val existing = userRepository.getCommentsByPublicationId(publicationId)
                    .firstOrNull { it.id == commentId }
                    ?: run {
                        _saveCommentResult.value = RequestResult.Failure("Comentario no encontrado")
                        return@launch
                    }

                if (!existing.usuarioId.equals(currentUserId, ignoreCase = true)) {
                    _saveCommentResult.value = RequestResult.Failure("Solo puedes editar tus propios comentarios")
                    return@launch
                }

                val wasUpdated = userRepository.updateComment(
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
                val existing = userRepository.getCommentsByPublicationId(publicationId)
                    .firstOrNull { it.id == commentId }
                    ?: run {
                        _saveCommentResult.value = RequestResult.Failure("Comentario no encontrado")
                        return@launch
                    }

                if (!existing.usuarioId.equals(currentUserId, ignoreCase = true)) {
                    _saveCommentResult.value = RequestResult.Failure("Solo puedes eliminar tus propios comentarios")
                    return@launch
                }

                val wasDeleted = userRepository.deleteComment(publicationId, commentId)
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
        val reviews = userRepository.getCommentsByPublicationId(publicationId)
            .takeIf { it.isNotEmpty() }
            ?: sampleReviews(publicationId)

        val totalReviews = reviews.size
        val averageRating = userRepository.getAverageRating(publicationId)
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

    // Estado visual mock mientras se conecta la data real de reseñas.
    private fun sampleReviews(publicationId: String): List<Comentario> = listOf(
        Comentario(
            id = "c1",
            usuarioId = "u1",
            puntoInteresId = publicationId,
            userName = "Camila Torres",
            date = 1778025600000,
            rating = 5f,
            text = "Un lugar con mucha magia, supera todas las expectativas y te hace emocionar por su belleza y tranquilidad. Sus altas palmeras de cera y el paisaje te transportan a otra dimension."
        ),
        Comentario(
            id = "c2",
            usuarioId = "u2",
            puntoInteresId = publicationId,
            userName = "Valentina Rios",
            date = 1777075200000,
            rating = 5f,
            text = "Para llegar al valle es mas facil desde el pueblo Salento, desde alli salen los famosos jeep camino al Valle. Hay varios senderos o trekking con diferentes precios, depende del recorrido que desees hacer."
        ),
        Comentario(
            id = "c3",
            usuarioId = "u3",
            puntoInteresId = publicationId,
            userName = "Luis Herrera",
            date = 1776207600000,
            rating = 4f,
            text = "Muy recomendado para ir con tiempo y disfrutar del recorrido completo. Lleva hidratacion y bloqueador porque el sol puede pegar fuerte en algunas horas."
        )
    )
}
