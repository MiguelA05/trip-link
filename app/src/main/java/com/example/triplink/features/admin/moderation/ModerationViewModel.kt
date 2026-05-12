package com.example.triplink.features.admin.moderation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.domain.model.enums.moderator.ModerationFilter
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.moderator.ModerationPublication
import com.example.triplink.domain.repository.admin.ModerationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModerationViewModel @Inject constructor(
    private val repository: ModerationRepository
) : ViewModel() {

    private val selectedFilterState = MutableStateFlow(ModerationFilter.ALL)
    val selectedFilter: StateFlow<ModerationFilter> = selectedFilterState

    private val _filteredPublications = MutableStateFlow<List<ModerationPublication>>(emptyList())
    val filteredPublications: StateFlow<List<ModerationPublication>> = _filteredPublications

    init {
        viewModelScope.launch {
            combine(
                repository.moderationPublications,
                selectedFilterState
            ) { _, filter ->
                repository.moderationPublicationsFor(filter)
            }.collect { publications ->
                _filteredPublications.value = publications
            }
        }
    }

    val pendingCount: StateFlow<Int> = repository.moderationPublications
        .map { publications -> publications.count { it.pointOfInterest.estado == EstadoPublicacion.PENDIENTE } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), repository.pendingModerationCount)

    val verifiedCount: StateFlow<Int> = repository.moderationPublications
        .map { publications -> publications.count { it.pointOfInterest.estado == EstadoPublicacion.VERIFICADA } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), repository.verifiedModerationCount)

    val rejectedCount: StateFlow<Int> = repository.moderationPublications
        .map { publications -> publications.count { it.pointOfInterest.estado == EstadoPublicacion.RECHAZADA } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), repository.rejectedModerationCount)

    fun onFilterSelected(filter: ModerationFilter) {
        selectedFilterState.value = filter
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

    fun getPublicationById(publicationId: String) {
        viewModelScope.launch {
            repository.getModerationPublicationById(publicationId)
        }
    }
}
