package com.example.triplink.features.admin.moderation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.domain.model.enums.moderator.ModerationFilter
import com.example.triplink.domain.repository.admin.moderation.AdminModerationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ModerationViewModel @Inject constructor(
    private val repository: AdminModerationRepository
) : ViewModel() {

    var selectedFilter by mutableStateOf(ModerationFilter.ALL)
        private set

    val pendingCount: Int
        get() = repository.pendingCount

    val verifiedCount: Int
        get() = repository.verifiedCount

    val rejectedCount: Int
        get() = repository.rejectedCount

    val filteredPublications get() = repository.publicationsFor(selectedFilter)

    fun onFilterSelected(filter: ModerationFilter) {
        selectedFilter = filter
    }

    fun applyDecision(
        publicationId: String,
        decision: DecisionModerador,
        reason: String? = null
    ) {
        repository.applyDecision(publicationId, decision, reason)
    }

    fun getPublicationById(publicationId: String) = repository.getPublicationById(publicationId)
}
