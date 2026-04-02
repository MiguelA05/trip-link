package com.example.triplink.features.admin.moderation.ModerationPublicationDetails

import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.domain.model.moderator.ModerationPublication
import com.example.triplink.domain.repository.admin.moderation.AdminModerationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ModerationPublicationDetailsViewModel @Inject constructor(
    private val repository: AdminModerationRepository
) : ViewModel() {

    fun getPublicationById(publicationId: String): ModerationPublication? = repository.getPublicationById(publicationId)

    fun applyDecision(
        publicationId: String,
        decision: DecisionModerador,
        reason: String? = null
    ) {
        repository.applyDecision(publicationId, decision, reason)
    }
}