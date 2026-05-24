package com.example.triplink.features.publicationDetails

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.R
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.domain.model.Comentario
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Reporte
import com.example.triplink.domain.model.enums.RazonReporte
import com.example.triplink.domain.repository.admin.ReportRepository
import com.example.triplink.domain.repository.user.CommentModerationRepository
import com.example.triplink.domain.repository.user.CommentRepository
import com.example.triplink.domain.repository.user.FavoriteRepository
import com.example.triplink.domain.repository.user.PublicationRepository
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
    private val commentModerationRepository: CommentModerationRepository,
    private val commentRepository: CommentRepository,
    private val reportRepository: ReportRepository
) : ViewModel() {

    data class InappropriateCommentSuggestion(
        val suggestedComment: String,
        val reason: String
    )

    private data class PendingCommentSubmission(
        val publicationId: String,
        val userId: String,
        val userName: String,
        val rating: Float,
        val suggestedText: String
    )

    private val _publication = MutableStateFlow<PuntoInteres?>(null)
    val publication: StateFlow<PuntoInteres?> = _publication.asStateFlow()

    var isFavorite by mutableStateOf(false)
        private set

    var comments by mutableStateOf<List<Comentario>>(emptyList())
        private set

    var publicationLoaded by mutableStateOf(false)
        private set

    private val _favoriteToggleResult = MutableStateFlow<RequestResult?>(null)
    val favoriteToggleResult: StateFlow<RequestResult?> = _favoriteToggleResult.asStateFlow()

    private val _commentResult = MutableStateFlow<RequestResult?>(null)
    val commentResult: StateFlow<RequestResult?> = _commentResult.asStateFlow()

    private val _publicationActionResult = MutableStateFlow<RequestResult?>(null)
    val publicationActionResult: StateFlow<RequestResult?> = _publicationActionResult.asStateFlow()

    private val _reportResult = MutableStateFlow<RequestResult?>(null)
    val reportResult: StateFlow<RequestResult?> = _reportResult.asStateFlow()

    private val _isSavingComment = MutableStateFlow(false)
    val isSavingComment: StateFlow<Boolean> = _isSavingComment.asStateFlow()

    private val _isPublishingSuggestedComment = MutableStateFlow(false)
    val isPublishingSuggestedComment: StateFlow<Boolean> = _isPublishingSuggestedComment.asStateFlow()

    private val _inappropriateCommentSuggestion = MutableStateFlow<InappropriateCommentSuggestion?>(null)
    val inappropriateCommentSuggestion: StateFlow<InappropriateCommentSuggestion?> =
        _inappropriateCommentSuggestion.asStateFlow()

    private var pendingCommentSubmission: PendingCommentSubmission? = null

    private val _isSubmittingReport = MutableStateFlow(false)
    val isSubmittingReport: StateFlow<Boolean> = _isSubmittingReport.asStateFlow()

    fun loadPublication(publicationId: String) {
        viewModelScope.launch {
            publicationLoaded = false
            _publication.value = publicationRepository.getPublicationById(publicationId)
            publicationLoaded = true
        }
    }

    fun loadCommentsForPublication(publicationId: String) {
        viewModelScope.launch {
            comments = commentRepository.getCommentsByPublicationId(publicationId)
        }
    }

    fun toggleFavorite(userId: String, publicationId: String) {
        viewModelScope.launch {
            _favoriteToggleResult.value = RequestResult.Loading
            try {
                val wasToggled = favoriteRepository.toggleFavorite(userId, publicationId)
                if (wasToggled) {
                    isFavorite = favoriteRepository.isFavorite(userId, publicationId)
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
        viewModelScope.launch {
            isFavorite = favoriteRepository.isFavorite(userId, publicationId)
        }
    }

    fun saveComment(publicationId: String, userId: String, userName: String, rating: Float, text: String) {
        if (userId.isBlank()) {
            Log.w("PublicationDetails", "saveComment aborted: userId blank for publicationId=$publicationId")
            _commentResult.value = RequestResult.Failure(appContext.getString(R.string.vm_publication_details_login_required))
            return
        }

        Log.d(
            "PublicationDetails",
            "saveComment requested: publicationId=$publicationId userId=$userId rating=$rating textLength=${text.trim().length}"
        )

        viewModelScope.launch {
            _isSavingComment.value = true
            _commentResult.value = RequestResult.Loading
            try {
                val trimmedText = text.trim()

                if (trimmedText.isNotBlank()) {
                    Log.d("PublicationDetails", "Calling comment moderation for publicationId=$publicationId")
                    val moderationResult = commentModerationRepository
                        .moderateComment(comment = trimmedText, publicationId = publicationId)

                    if (moderationResult.isFailure) {
                        Log.e("PublicationDetails", "Comment moderation failed: ${moderationResult.exceptionOrNull()?.message}")
                        _commentResult.value = RequestResult.Failure(
                            appContext.getString(R.string.vm_publication_details_moderation_failed)
                        )
                        _isSavingComment.value = false
                        return@launch
                    }

                    val moderation = moderationResult.getOrNull()
                    Log.d("PublicationDetails", "Moderation response received: isInappropriate=${moderation?.isInappropriate}")
                    if (moderation?.isInappropriate == true) {
                        val suggestedText = moderation.safeAlternative
                            .takeIf { it.isNotBlank() }
                            ?: appContext.getString(R.string.feature_publication_details_inappropriate_suggestion)

                        pendingCommentSubmission = PendingCommentSubmission(
                            publicationId = publicationId,
                            userId = userId,
                            userName = userName,
                            rating = rating,
                            suggestedText = suggestedText
                        )
                        _inappropriateCommentSuggestion.value = InappropriateCommentSuggestion(
                            suggestedComment = suggestedText,
                            reason = moderation.reason
                        )
                        _commentResult.value = null
                        _isSavingComment.value = false
                        return@launch
                    }
                }

                Log.d("PublicationDetails", "Publishing approved comment for publicationId=$publicationId")
                publishComment(
                    publicationId = publicationId,
                    userId = userId,
                    userName = userName,
                    rating = rating,
                    text = trimmedText
                )
            } catch (e: Exception) {
                Log.e("PublicationDetails", "Unexpected error saving comment", e)
                _commentResult.value = RequestResult.Failure(
                    appContext.getString(R.string.vm_publication_details_save_error, e.message ?: "")
                )
                _isSavingComment.value = false
            }
        }
    }

    fun acceptSuggestedCommentAndPublish() {
        val pendingSubmission = pendingCommentSubmission ?: return
        Log.d("PublicationDetails", "acceptSuggestedCommentAndPublish for publicationId=${pendingSubmission.publicationId}")

        viewModelScope.launch {
            _isPublishingSuggestedComment.value = true
            _commentResult.value = RequestResult.Loading
            try {
                publishComment(
                    publicationId = pendingSubmission.publicationId,
                    userId = pendingSubmission.userId,
                    userName = pendingSubmission.userName,
                    rating = pendingSubmission.rating,
                    text = pendingSubmission.suggestedText
                )
            } catch (e: Exception) {
                Log.e("PublicationDetails", "Unexpected error publishing suggested comment", e)
                _commentResult.value = RequestResult.Failure(
                    appContext.getString(R.string.vm_publication_details_save_error, e.message ?: "")
                )
                _isPublishingSuggestedComment.value = false
            }
        }
    }

    fun dismissInappropriateCommentSuggestion() {
        pendingCommentSubmission = null
        _inappropriateCommentSuggestion.value = null
    }

    private suspend fun publishComment(
        publicationId: String,
        userId: String,
        userName: String,
        rating: Float,
        text: String
    ) {
        Log.d(
            "PublicationDetails",
            "publishComment start: publicationId=$publicationId userId=$userId rating=$rating textLength=${text.length}"
        )
        val comment = Comentario(
            id = UUID.randomUUID().toString(),
            usuarioId = userId,
            puntoInteresId = publicationId,
            userName = userName,
            date = System.currentTimeMillis(),
            rating = rating,
            text = text
        )

        val wasSaved = commentRepository.saveComment(publicationId, comment)
        Log.d("PublicationDetails", "publishComment save result: wasSaved=$wasSaved publicationId=$publicationId")
        if (wasSaved) {
            comments = commentRepository.getCommentsByPublicationId(publicationId)
            Log.d("PublicationDetails", "publishComment completed successfully: comments=${comments.size}")
            _commentResult.value = RequestResult.Success(appContext.getString(R.string.vm_publication_details_comment_saved))
        } else {
            Log.w("PublicationDetails", "publishComment failed to persist comment for publicationId=$publicationId")
            _commentResult.value = RequestResult.Failure(appContext.getString(R.string.vm_publication_details_comment_save_failed))
        }
    }

    fun getAverageRating(): Double {
        return comments.map { it.rating }.average().takeIf { !it.isNaN() } ?: 0.0
    }

    fun submitReport(
        publicationId: String,
        userId: String,
        reason: RazonReporte,
        description: String?
    ) {
        if (userId.isBlank()) {
            _reportResult.value = RequestResult.Failure(
                appContext.getString(R.string.vm_publication_details_report_login_required)
            )
            return
        }

        if (reason == RazonReporte.OTRO && description.isNullOrBlank()) {
            _reportResult.value = RequestResult.Failure(
                appContext.getString(R.string.vm_publication_details_report_other_reason_required)
            )
            return
        }

        viewModelScope.launch {
            _isSubmittingReport.value = true
            _reportResult.value = RequestResult.Loading
            val hasReported = reportRepository.hasUserReportedPublication(userId, publicationId)
            if (hasReported) {
                _reportResult.value = RequestResult.Failure(
                    appContext.getString(R.string.vm_publication_details_report_duplicate)
                )
                _isSubmittingReport.value = false
                return@launch
            }

            try {
                val report = Reporte(
                    id = UUID.randomUUID().toString(),
                    reportadorId = userId,
                    puntoInteresId = publicationId,
                    motivo = reason,
                    descripcion = description?.trim()?.takeIf { it.isNotBlank() }
                )

                val wasSaved = reportRepository.submitReport(report)
                if (wasSaved) {
                    // Refresh publication so UI receives the updated report list
                    try {
                        _publication.value = publicationRepository.getPublicationById(publicationId)
                    } catch (e: Exception) {
                        Log.w("PublicationDetails", "Failed to refresh publication after report: ${e.message}")
                    }
                    _reportResult.value = RequestResult.Success(appContext.getString(R.string.vm_publication_details_report_sent))
                } else {
                    _reportResult.value = RequestResult.Failure(appContext.getString(R.string.vm_publication_details_report_send_failed))
                }
            } catch (e: Exception) {
                _reportResult.value = RequestResult.Failure(
                    appContext.getString(R.string.vm_publication_details_report_error, e.message ?: "")
                )
            } finally {
                _isSubmittingReport.value = false
            }
        }
    }


    fun deletePublication(publicationId: String) {
        viewModelScope.launch {
            _publicationActionResult.value = RequestResult.Loading
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

    fun clearSavingComment() {
        _isSavingComment.value = false
    }

    fun clearPublishingSuggestedComment() {
        _isPublishingSuggestedComment.value = false
    }

    fun clearPublicationActionResult() {
        _publicationActionResult.value = null
    }

    fun clearReportResult() {
        _reportResult.value = null
    }
}