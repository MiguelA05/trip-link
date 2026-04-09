package com.example.triplink.features.admin.moderation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.domain.model.enums.moderator.ModerationFilter
import com.example.triplink.domain.repository.admin.ModerationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ModerationViewModel @Inject constructor(
    private val repository: ModerationRepository
) : ViewModel() {

    var selectedFilter by mutableStateOf(ModerationFilter.ALL)
        private set

    val pendingCount: Int
        get() = repository.pendingModerationCount

    val verifiedCount: Int
        get() = repository.verifiedModerationCount

    val rejectedCount: Int
        get() = repository.rejectedModerationCount

    val filteredPublications get() = repository.moderationPublicationsFor(selectedFilter)

    fun onFilterSelected(filter: ModerationFilter) {
        selectedFilter = filter
    }

    fun applyDecision(
        publicationId: String,
        decision: DecisionModerador,
        reason: String? = null
    ) {
        repository.applyModerationDecision(publicationId, decision, reason)
    }

    fun getPublicationById(publicationId: String) = repository.getModerationPublicationById(publicationId)
}
