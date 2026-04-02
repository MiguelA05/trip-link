package com.example.triplink.features.admin.moderation.ModerationPublicationDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.triplink.data.repository.admin.moderation.AdminModerationRepository
import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.domain.model.moderator.ModerationPublication

class ModerationPublicationDetailsViewModel(
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

    companion object {
        fun factory(repository: AdminModerationRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ModerationPublicationDetailsViewModel(repository) as T
                }
            }
    }
}