package com.example.triplink.features.publicationDetails

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.R
import com.example.triplink.core.ai.ModerationOutcome
import com.example.triplink.core.ai.ReviewModerationService
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.domain.model.Comentario
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Reporte
import com.example.triplink.domain.model.enums.RazonReporte
import com.example.triplink.domain.repository.admin.ReportRepository
import com.example.triplink.domain.repository.user.CommentRepository
import com.example.triplink.domain.repository.user.FavoriteRepository
import com.example.triplink.domain.repository.user.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.UUID

sealed class CommentModerationState {
    object Idle : CommentModerationState()
    object Loading : CommentModerationState()
    data class Suggested(val pending: PendingComment, val suggestedText: String) : CommentModerationState()
}

data class PendingComment(
    val publicationId: String,
    val userId: String,
    val userName: String,
    val rating: Float,
    val originalText: String
)

@HiltViewModel
class PublicationDetailsViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val publicationRepository: PublicationRepository,
    private val favoriteRepository: FavoriteRepository,
    private val commentRepository: CommentRepository,
    private val reportRepository: ReportRepository,
    private val reviewModerationService: ReviewModerationService
) : ViewModel() {

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

    private val _isModeratingComment = MutableStateFlow(false)
    val isModeratingComment: StateFlow<Boolean> = _isModeratingComment.asStateFlow()

    private val _commentModerationState = MutableStateFlow<CommentModerationState>(CommentModerationState.Idle)
    val commentModerationState: StateFlow<CommentModerationState> = _commentModerationState.asStateFlow()

    private val _moderationError = MutableStateFlow<String?>(null)
    val moderationError: StateFlow<String?> = _moderationError.asStateFlow()

    // When the backend returns a RESOURCE_EXHAUSTED / retry suggestion, store until-when we should back off
    private val _moderationCooldownUntilMs = MutableStateFlow<Long?>(null)
    val moderationCooldownUntilMs: StateFlow<Long?> = _moderationCooldownUntilMs.asStateFlow()

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
        val trimmedText = text.trim()
        if (userId.isBlank()) {
            _commentResult.value = RequestResult.Failure(appContext.getString(R.string.vm_publication_details_login_required))
            return
        }
        if (trimmedText.isBlank()) {
            _commentResult.value = RequestResult.Failure(appContext.getString(R.string.vm_publication_details_comment_empty))
            return
        }

        // If we recently received a quota-exhausted response, block client calls until cooldown expires
        val cooldownUntil = _moderationCooldownUntilMs.value
        val now = System.currentTimeMillis()
        if (cooldownUntil != null && cooldownUntil > now) {
            val remainingSec = ((cooldownUntil - now) / 1000).coerceAtLeast(1)
            val message = "Se ha alcanzado la cuota de moderación. Intenta de nuevo en ${remainingSec}s."
            _moderationError.value = message
            android.util.Log.d("PublicationDetailsVM", "saveComment blocked due to moderation cooldown: $remainingSec s remaining")
            return
        }
        // Prevent re-entrant calls: if moderation or saving is already in progress, ignore additional requests
        if (_isModeratingComment.value || _isSavingComment.value) {
            android.util.Log.d("PublicationDetailsVM", "saveComment ignored - operation already in progress")
            return
        }

        viewModelScope.launch {
            android.util.Log.d("PublicationDetailsVM", "saveComment START. publicationId=$publicationId userId=$userId text='${trimmedText.take(80)}'")
            _isModeratingComment.value = true
            _commentModerationState.value = CommentModerationState.Loading
            try {
                android.util.Log.d("PublicationDetailsVM", "Calling reviewModerationService.moderateReview...")
                when (val outcome = reviewModerationService.moderateReview(trimmedText)) {
                    is ModerationOutcome.Clean -> {
                        android.util.Log.d("PublicationDetailsVM", "moderation result: CLEAN")
                        _commentModerationState.value = CommentModerationState.Idle
                        _isModeratingComment.value = false
                        saveCommentInternal(publicationId, userId, userName, rating, outcome.text)
                    }
                    is ModerationOutcome.Moderated -> {
                        android.util.Log.d("PublicationDetailsVM", "moderation result: MODERATED. suggesting replacement")
                        _commentModerationState.value = CommentModerationState.Suggested(
                            pending = PendingComment(
                                publicationId = publicationId,
                                userId = userId,
                                userName = userName,
                                rating = rating,
                                originalText = outcome.originalText
                            ),
                            suggestedText = outcome.suggestedText
                        )
                        _isModeratingComment.value = false
                    }
                }
            } catch (e: TimeoutCancellationException) {
                android.util.Log.d("PublicationDetailsVM", "moderation timeout")
                _commentModerationState.value = CommentModerationState.Idle
                _isModeratingComment.value = false
                _moderationError.value = appContext.getString(R.string.vm_publication_details_moderation_timeout)
            } catch (e: Exception) {
                android.util.Log.d("PublicationDetailsVM", "moderation error: ${e.message}")
                _commentModerationState.value = CommentModerationState.Idle
                _isModeratingComment.value = false

                // Detect quota-exhausted / retry suggestion and set a local cooldown to avoid hammering the API
                val msg = e.message ?: ""
                val retryRegex = Regex("Please retry in ([0-9]+(\\.[0-9]+)?)s", RegexOption.IGNORE_CASE)
                val match = retryRegex.find(msg)
                if (match != null) {
                    val seconds = match.groupValues[1].toDoubleOrNull()?.let { Math.ceil(it).toLong() } ?: 30L
                    val until = System.currentTimeMillis() + seconds * 1000
                    _moderationCooldownUntilMs.value = until
                    val userMsg = "Se ha alcanzado la cuota de moderación. Intenta de nuevo en ${seconds}s."
                    _moderationError.value = userMsg
                    android.util.Log.d("PublicationDetailsVM", "Detected quota-exhausted. Backing off for ${seconds}s")
                } else {
                    _moderationError.value = appContext.getString(
                        R.string.vm_publication_details_moderation_error,
                        msg
                    )
                }
            }
            android.util.Log.d("PublicationDetailsVM", "saveComment END for publicationId=$publicationId")
        }
    }

    fun resolveModeratedComment(replaceWithSuggestion: Boolean) {
        val currentState = _commentModerationState.value as? CommentModerationState.Suggested ?: return
        val pending = currentState.pending
        val resolvedText = if (replaceWithSuggestion) {
            currentState.suggestedText
        } else {
            pending.originalText
        }
        _commentModerationState.value = CommentModerationState.Idle
        viewModelScope.launch {
            saveCommentInternal(
                publicationId = pending.publicationId,
                userId = pending.userId,
                userName = pending.userName,
                rating = pending.rating,
                text = resolvedText
            )
        }
    }

    fun dismissModerationSuggestion() {
        _commentModerationState.value = CommentModerationState.Idle
    }

    fun clearModerationError() {
        _moderationError.value = null
    }

    private suspend fun saveCommentInternal(
        publicationId: String,
        userId: String,
        userName: String,
        rating: Float,
        text: String
    ) {
        android.util.Log.d("PublicationDetailsVM", "saveCommentInternal START. publicationId=$publicationId userId=$userId text='${text.take(80)}'")
        _isSavingComment.value = true
        _commentResult.value = RequestResult.Loading
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
                android.util.Log.d("PublicationDetailsVM", "saveCommentInternal: comment saved successfully")
            } else {
                _commentResult.value = RequestResult.Failure(appContext.getString(R.string.vm_publication_details_comment_save_failed))
            }
        } catch (e: Exception) {
            _commentResult.value = RequestResult.Failure(
                appContext.getString(R.string.vm_publication_details_save_error, e.message ?: "")
            )
        } finally {
            _isSavingComment.value = false
            android.util.Log.d("PublicationDetailsVM", "saveCommentInternal END for publicationId=$publicationId")
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
                _reportResult.value = if (wasSaved) {
                    RequestResult.Success(appContext.getString(R.string.vm_publication_details_report_sent))
                } else {
                    RequestResult.Failure(appContext.getString(R.string.vm_publication_details_report_send_failed))
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

    fun clearPublicationActionResult() {
        _publicationActionResult.value = null
    }

    fun clearReportResult() {
        _reportResult.value = null
    }
}