package com.example.triplink.features.admin.moderation.ModerationPublicationDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.domain.model.moderator.ModerationPublication
import com.example.triplink.domain.repository.admin.ModerationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModerationPublicationDetailsViewModel @Inject constructor(
    private val repository: ModerationRepository
) : ViewModel() {

    private val _currentPublication = MutableStateFlow<ModerationPublication?>(null)
    val currentPublication: StateFlow<ModerationPublication?> = _currentPublication.asStateFlow()

    fun loadPublicationById(publicationId: String) {
        viewModelScope.launch {
            _currentPublication.value = repository.getModerationPublicationById(publicationId)
        }
    }

    fun getPublicationById(publicationId: String): ModerationPublication? {
        return _currentPublication.value
    }

    fun applyDecision(
        publicationId: String,
        decision: DecisionModerador,
        reason: String? = null
    ) {
        viewModelScope.launch {
            repository.applyModerationDecision(publicationId, decision, reason)
        }
    }
}