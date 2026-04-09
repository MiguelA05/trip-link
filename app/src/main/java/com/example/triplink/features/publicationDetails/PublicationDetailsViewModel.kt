package com.example.triplink.features.publicationDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.domain.model.Comentario
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.repository.comment.CommentRepository
import com.example.triplink.domain.repository.favorite.FavoriteRepository
import com.example.triplink.domain.repository.publication.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.UUID

@HiltViewModel
class PublicationDetailsViewModel @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val favoriteRepository: FavoriteRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {

    var isFavorite by mutableStateOf(false)
        private set

    var comments by mutableStateOf<List<Comentario>>(emptyList())
        private set

    private val _favoriteToggleResult = MutableStateFlow<RequestResult?>(null)
    val favoriteToggleResult: StateFlow<RequestResult?> = _favoriteToggleResult.asStateFlow()

    private val _commentResult = MutableStateFlow<RequestResult?>(null)
    val commentResult: StateFlow<RequestResult?> = _commentResult.asStateFlow()

    private val _publicationActionResult = MutableStateFlow<RequestResult?>(null)
    val publicationActionResult: StateFlow<RequestResult?> = _publicationActionResult.asStateFlow()

    fun getPublicationById(publicationId: String): PuntoInteres? {
        return publicationRepository.getPublicationById(publicationId)
    }

    fun loadCommentsForPublication(publicationId: String) {
        comments = commentRepository.getCommentsByPublicationId(publicationId)
    }

    fun toggleFavorite(userId: String, publicationId: String) {
        viewModelScope.launch {
            try {
                val wasToggled = favoriteRepository.toggleFavorite(userId, publicationId)
                if (wasToggled) {
                    isFavorite = !isFavorite
                    val message = if (isFavorite) "Añadido a favoritos" else "Removido de favoritos"
                    _favoriteToggleResult.value = RequestResult.Success(message)
                } else {
                    _favoriteToggleResult.value = RequestResult.Failure("No se pudo actualizar favorito")
                }
            } catch (e: Exception) {
                _favoriteToggleResult.value = RequestResult.Failure("Error: ${e.message}")
            }
        }
    }

    fun checkIsFavorite(userId: String, publicationId: String) {
        isFavorite = favoriteRepository.isFavorite(userId, publicationId)
    }

    fun saveComment(publicationId: String, userId: String, userName: String, rating: Float, text: String) {
        if (userId.isBlank()) {
            _commentResult.value = RequestResult.Failure("Debes iniciar sesión para comentar")
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
                    rating = rating,
                    text = text.trim()
                )

                val wasSaved = commentRepository.saveComment(publicationId, comment)
                if (wasSaved) {
                    comments = commentRepository.getCommentsByPublicationId(publicationId)
                    _commentResult.value = RequestResult.Success("Comentario guardado exitosamente")
                } else {
                    _commentResult.value = RequestResult.Failure("No se pudo guardar el comentario")
                }
            } catch (e: Exception) {
                _commentResult.value = RequestResult.Failure("Error al guardar: ${e.message}")
            }
        }
    }

    fun getAverageRating(publicationId: String): Double {
        return commentRepository.getAverageRating(publicationId)
    }

    fun updatePublication(updatedPublication: PuntoInteres) {
        viewModelScope.launch {
            try {
                val wasUpdated = publicationRepository.updatePuntoInteres(updatedPublication)
                _publicationActionResult.value = if (wasUpdated) {
                    RequestResult.Success("Publicación actualizada")
                } else {
                    RequestResult.Failure("No se pudo actualizar la publicación")
                }
            } catch (e: Exception) {
                _publicationActionResult.value = RequestResult.Failure("Error al actualizar: ${e.message}")
            }
        }
    }

    fun deletePublication(publicationId: String) {
        viewModelScope.launch {
            try {
                val wasDeleted = publicationRepository.deletePublicationById(publicationId)
                _publicationActionResult.value = if (wasDeleted) {
                    RequestResult.Success("Publicación eliminada")
                } else {
                    RequestResult.Failure("No se pudo eliminar la publicación")
                }
            } catch (e: Exception) {
                _publicationActionResult.value = RequestResult.Failure("Error al eliminar: ${e.message}")
            }
        }
    }

    fun clearFavoriteResult() {
        _favoriteToggleResult.value = null
    }

    fun clearCommentResult() {
        _commentResult.value = null
    }

    fun clearPublicationActionResult() {
        _publicationActionResult.value = null
    }
}