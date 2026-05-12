package com.example.triplink.features.badges

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.R
import com.example.triplink.core.localization.localizedLabel
import com.example.triplink.core.localization.localizedDescription
import com.example.triplink.core.localization.localizedName
import com.example.triplink.data.datastore.SessionDataStore
import com.example.triplink.domain.model.InsigniaIconKey
import com.example.triplink.domain.model.UserInsigniaProgress
import com.example.triplink.domain.repository.user.BadgeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BadgeUi(
    val id: String,
    val name: String,
    val description: String,
    val requirement: String,
    val points: Int,
    val iconKey: InsigniaIconKey,
    val isUnlocked: Boolean,
    val unlockedAtMillis: Long?
)

data class BadgeUnlockUi(
    val badge: BadgeUi,
    val totalPoints: Int,
    val currentLevel: String
)

data class BadgesUiState(
    val currentLevel: String,
    val points: Int,
    val contributions: Int,
    val unlockedBadges: List<BadgeUi>,
    val lockedBadges: List<BadgeUi>,
    val selectedBadge: BadgeUi? = null,
    val unlockDialog: BadgeUnlockUi? = null
)

@HiltViewModel
class BadgesViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val badgeRepository: BadgeRepository,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        BadgesUiState(
            currentLevel = appContext.getString(R.string.enum_nivel_turista),
            points = 0,
            contributions = 0,
            unlockedBadges = emptyList(),
            lockedBadges = emptyList()
        )
    )
    val uiState: StateFlow<BadgesUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val session = sessionDataStore.sessionFlow.first()
            val userId = session?.userId ?: return@launch
            syncForUser(userId)
        }
    }

    fun onBadgeClick(badge: BadgeUi) {
        _uiState.value = _uiState.value.copy(selectedBadge = badge)
    }

    fun dismissBadgeDetail() {
        _uiState.value = _uiState.value.copy(selectedBadge = null)
    }

    fun dismissUnlockDialog() {
        _uiState.value = _uiState.value.copy(unlockDialog = null)
    }

    private fun syncForUser(userId: String) {
        viewModelScope.launch {
            val sync = badgeRepository.syncUserProgress(userId)
            val progress = badgeRepository.userBadgeProgress(userId)
                .map { it.toUi() }
                .sortedByDescending { it.unlockedAtMillis ?: Long.MIN_VALUE }

            val unlocked = progress.filter { it.isUnlocked }
            val locked = progress.filterNot { it.isUnlocked }

            val latestUnlocked = sync.newlyUnlockedBadgeIds.firstOrNull()
                ?.let { badgeId ->
                    unlocked.firstOrNull { it.id == badgeId }?.let { badgeUi ->
                        BadgeUnlockUi(
                            badge = badgeUi,
                            totalPoints = sync.points,
                            currentLevel = sync.level.localizedLabel(appContext)
                        )
                    }
                }

            _uiState.value = _uiState.value.copy(
                currentLevel = sync.level.localizedLabel(appContext),
                points = sync.points,
                contributions = sync.contributions,
                unlockedBadges = unlocked,
                lockedBadges = locked,
                unlockDialog = latestUnlocked
            )
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