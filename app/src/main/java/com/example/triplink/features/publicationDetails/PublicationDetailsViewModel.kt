package com.example.triplink.features.publicationDetails

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.R
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.domain.model.Comentario
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.repository.comment.CommentRepository
import com.example.triplink.domain.repository.favorite.FavoriteRepository
import com.example.triplink.domain.repository.publication.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.UUID

@HiltViewModel
class PublicationDetailsViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
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
                    val message = if (isFavorite) {
                        appContext.getString(R.string.vm_publication_details_favorite_added)
                    } else {
                        appContext.getString(R.string.vm_publication_details_favorite_removed)
                    }
                    _favoriteToggleResult.value = RequestResult.Success(message)
                } else {
                    _favoriteToggleResult.value = RequestResult.Failure(
                        appContext.getString(R.string.vm_publication_details_favorite_update_failed)
                    )
                }
            } catch (e: Exception) {
                _favoriteToggleResult.value = RequestResult.Failure(
                    appContext.getString(R.string.vm_publication_details_generic_error, e.message ?: "")
                )
            }
        }
    }

    fun checkIsFavorite(userId: String, publicationId: String) {
        isFavorite = favoriteRepository.isFavorite(userId, publicationId)
    }

    fun saveComment(publicationId: String, userId: String, userName: String, rating: Float, text: String) {
        if (userId.isBlank()) {
            _commentResult.value = RequestResult.Failure(appContext.getString(R.string.vm_publication_details_login_required))
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
                    _commentResult.value = RequestResult.Success(appContext.getString(R.string.vm_publication_details_comment_saved))
                } else {
                    _commentResult.value = RequestResult.Failure(appContext.getString(R.string.vm_publication_details_comment_save_failed))
                }
            } catch (e: Exception) {
                _commentResult.value = RequestResult.Failure(
                    appContext.getString(R.string.vm_publication_details_save_error, e.message ?: "")
                )
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
                    RequestResult.Success(appContext.getString(R.string.vm_publication_details_publication_updated))
                } else {
                    RequestResult.Failure(appContext.getString(R.string.vm_publication_details_publication_update_failed))
                }
            } catch (e: Exception) {
                _publicationActionResult.value = RequestResult.Failure(
                    appContext.getString(R.string.vm_publication_details_update_error, e.message ?: "")
                )
            }
        }
    }

    fun deletePublication(publicationId: String) {
        viewModelScope.launch {
            try {
                val wasDeleted = publicationRepository.deletePublicationById(publicationId)
                _publicationActionResult.value = if (wasDeleted) {
                    RequestResult.Success(appContext.getString(R.string.vm_publication_details_publication_deleted))
                } else {
                    RequestResult.Failure(appContext.getString(R.string.vm_publication_details_publication_delete_failed))
                }
            } catch (e: Exception) {
                _publicationActionResult.value = RequestResult.Failure(
                    appContext.getString(R.string.vm_publication_details_delete_error, e.message ?: "")
                )
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