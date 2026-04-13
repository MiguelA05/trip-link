package com.example.triplink.features.badges

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.R
import com.example.triplink.core.localization.localizedDescription
import com.example.triplink.core.localization.localizedLabel
import com.example.triplink.core.localization.localizedName
import com.example.triplink.domain.model.Insignia
import com.example.triplink.domain.model.UserInsigniaProgress
import com.example.triplink.domain.repository.badge.BadgeRepository
import com.example.triplink.domain.repository.publication.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BadgeUnlockNotifierViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val badgeRepository: BadgeRepository,
    private val publicationRepository: PublicationRepository
) : ViewModel() {

    private val _currentUnlockDialog = MutableStateFlow<BadgeUnlockUi?>(null)
    val currentUnlockDialog: StateFlow<BadgeUnlockUi?> = _currentUnlockDialog.asStateFlow()

    private val pendingUnlocks = ArrayDeque<BadgeUnlockUi>()
    private var observedUserId: String? = null
    private var publicationsObserverJob: Job? = null

    fun bindUser(userId: String) {
        val normalizedUserId = userId.trim().lowercase()
        if (normalizedUserId.isBlank()) return
        if (observedUserId == normalizedUserId && publicationsObserverJob != null) return

        observedUserId = normalizedUserId
        pendingUnlocks.clear()
        _currentUnlockDialog.value = null

        publicationsObserverJob?.cancel()
        syncAndQueueUnlocks(normalizedUserId)

        publicationsObserverJob = viewModelScope.launch {
            publicationRepository.publications.collect {
                syncAndQueueUnlocks(normalizedUserId)
            }
        }
    }

    fun dismissUnlockDialog() {
        _currentUnlockDialog.value = pendingUnlocks.removeFirstOrNull()
    }

    fun dismissAllUnlockDialogs() {
        pendingUnlocks.clear()
        _currentUnlockDialog.value = null
    }

    override fun onCleared() {
        publicationsObserverJob?.cancel()
        super.onCleared()
    }

    private fun syncAndQueueUnlocks(userId: String) {
        val sync = badgeRepository.syncUserProgress(userId)
        if (sync.newlyUnlockedBadgeIds.isEmpty()) return

        val progressByBadgeId = badgeRepository
            .userBadgeProgress(userId)
            .associateBy { it.insignia.id }

        val currentBadgeId = _currentUnlockDialog.value?.badge?.id

        sync.newlyUnlockedBadgeIds.forEach { badgeId ->
            val progress = progressByBadgeId[badgeId] ?: return@forEach
            val candidate = BadgeUnlockUi(
                badge = progress.toUi(),
                totalPoints = sync.points,
                currentLevel = sync.level.localizedLabel(appContext)
            )

            val isAlreadyVisible = currentBadgeId == candidate.badge.id
            val isAlreadyQueued = pendingUnlocks.any { it.badge.id == candidate.badge.id }
            if (!isAlreadyVisible && !isAlreadyQueued) {
                pendingUnlocks.addLast(candidate)
            }
        }

        if (_currentUnlockDialog.value == null) {
            _currentUnlockDialog.value = pendingUnlocks.removeFirstOrNull()
        }
    }

    private fun UserInsigniaProgress.toUi(): BadgeUi {
        val definition = insignia
        return BadgeUi(
            id = definition.id,
            name = definition.localizedName(appContext),
            description = definition.localizedDescription(appContext),
            requirement = definition.requirementLabel(appContext),
            points = definition.puntos,
            iconKey = definition.iconKey,
            isUnlocked = isUnlocked,
            unlockedAtMillis = unlockedAtMillis
        )
    }
}


