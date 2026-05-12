package com.example.triplink.features.user.info

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.R
import com.example.triplink.core.localization.localizedLabel
import com.example.triplink.core.localization.localizedName
import com.example.triplink.data.datastore.SessionDataStore
import com.example.triplink.domain.model.InsigniaIconKey
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.repository.user.BadgeRepository
import com.example.triplink.domain.repository.user.FavoriteRepository
import com.example.triplink.domain.repository.user.PublicationRepository
import com.example.triplink.domain.repository.user.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class UserContributionItem(
	val id: String,
	val title: String,
	val status: EstadoPublicacion,
	val rejectReason: String?,
	val createdAt: Long
)

data class UserRecentBadgeItem(
	val id: String,
	val name: String,
	val iconKey: InsigniaIconKey,
	val points: Int
)

data class UserInfoUiState(
	val userName: String,
	val userInitials: String,
	val roleLabel: String,
	val points: Int,
	val contributions: Int,
	val activeDays: Int,
	// selectedContributionIndex: 0 = Favoritos, 1 = Verificadas, 2 = Pendientes, 3 = Rechazadas
	val selectedContributionIndex: Int,
	val selectedBottomTabIndex: Int,
	val selectedContributionItems: List<UserContributionItem>,
	val recentBadges: List<UserRecentBadgeItem> = emptyList(),
	val favoritesCount: Int = 0,
	val verifiedCount: Int = 0,
	val pendingCount: Int = 0,
	val rejectedCount: Int = 0
)

@HiltViewModel
class UserInfoViewModel @Inject constructor(
	@param:ApplicationContext private val appContext: Context,
	private val userProfileRepository: UserProfileRepository,
	private val publicationRepository: PublicationRepository,
	private val badgeRepository: BadgeRepository,
	private val favoriteRepository: FavoriteRepository,
	private val sessionDataStore: SessionDataStore
) : ViewModel() {

	var showLogoutDialog by mutableStateOf(false)
		private set

	private val _uiState = MutableStateFlow(
		UserInfoUiState(
			userName = appContext.getString(R.string.vm_user_info_default_user_name),
			userInitials = buildInitials(appContext.getString(R.string.vm_user_info_default_user_name)),
			roleLabel = appContext.getString(R.string.vm_user_info_default_role_label),
			points = 0,
			contributions = 0,
			activeDays = 1,
			selectedContributionIndex = 2, // keep previous default (Pendiente)
			selectedBottomTabIndex = 2,
			selectedContributionItems = emptyList()
		)
	)
	val uiState: StateFlow<UserInfoUiState> = _uiState.asStateFlow()

	private var currentUserId: String? = null

	init {
		observePublications()
		loadUserData()
	}

	private fun observePublications() {
		viewModelScope.launch {
			publicationRepository.publications.collectLatest {
				loadUserData()
			}
		}
	}

	fun onContributionTabSelected(index: Int) {
		_uiState.value = _uiState.value.copy(selectedContributionIndex = index)
		refreshSelectedContributions()
	}

	fun onLogoutRequested() {
		showLogoutDialog = true
	}

	fun dismissLogoutDialog() {
		showLogoutDialog = false
	}

	fun refreshData() {
		loadUserData()
	}

	private fun loadUserData() {
		viewModelScope.launch {
			val session = sessionDataStore.sessionFlow.first()
			val user = session?.userId?.let { userProfileRepository.getUserById(it) }
				?: userProfileRepository.users.value.firstOrNull()

			user?.let { mappedUser ->
				currentUserId = mappedUser.email

				val sync = badgeRepository.syncUserProgress(mappedUser.email)
				val recentBadges = badgeRepository
					.userBadgeProgress(mappedUser.email)
					.filter { it.isUnlocked }
					.sortedByDescending { it.unlockedAtMillis ?: Long.MIN_VALUE }
					.take(3)
					.map {
						UserRecentBadgeItem(
							id = it.insignia.id,
							name = it.insignia.localizedName(appContext),
							iconKey = it.insignia.iconKey,
							points = it.insignia.puntos
						)
					}

				_uiState.value = _uiState.value.copy(
					userName = mappedUser.nombre,
					userInitials = buildInitials(mappedUser.nombre),
					roleLabel = sync.level.localizedLabel(appContext),
					points = sync.points,
					contributions = sync.contributions,
					activeDays = maxOf(sync.contributions, 1),
					recentBadges = recentBadges
				)
				refreshSelectedContributions()
			}
		}
	}

	private fun refreshSelectedContributions() {
		try {
			val userId = currentUserId ?: return
			val allUserPublications = publicationRepository.getUserPublications(userId)

			val favorites = favoriteRepository.getFavoritePublications(userId)

			val filtered = if (_uiState.value.selectedContributionIndex == 0) {
				// Favorites: show publications the user marked as favorite
				favorites.map { it.toContributionItem() }
			} else {
				val targetEstado = when (_uiState.value.selectedContributionIndex) {
					1 -> EstadoPublicacion.VERIFICADA
					2 -> EstadoPublicacion.PENDIENTE
					3 -> EstadoPublicacion.RECHAZADA
					else -> EstadoPublicacion.PENDIENTE
				}
				allUserPublications
					.filter { it.estado == targetEstado }
					.map { it.toContributionItem() }
			}

			_uiState.value = _uiState.value.copy(
				selectedContributionItems = filtered,
				favoritesCount = favorites.size,
				verifiedCount = allUserPublications.count { it.estado == EstadoPublicacion.VERIFICADA },
				pendingCount = allUserPublications.count { it.estado == EstadoPublicacion.PENDIENTE },
				rejectedCount = allUserPublications.count { it.estado == EstadoPublicacion.RECHAZADA }
			)
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	private fun PuntoInteres.toContributionItem(): UserContributionItem = UserContributionItem(
		id = id,
		title = titulo,
		status = estado,
		rejectReason = motivoRechazo,
		createdAt = fechaCreacion
	)

	private companion object {
		fun buildInitials(name: String): String {
			val parts = name.trim().split(" ").filter { it.isNotBlank() }
			if (parts.isEmpty()) return "U"

			return parts
				.take(2)
				.joinToString(separator = "") { part -> part.first().toString() }
				.uppercase(Locale.ROOT)
		}
	}
}
