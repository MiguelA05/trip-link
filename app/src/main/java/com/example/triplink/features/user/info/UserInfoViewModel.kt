package com.example.triplink.features.user.info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.data.datastore.SessionDataStore
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class UserContributionItem(
	val id: String,
	val title: String,
	val status: EstadoPublicacion,
	val rejectReason: String?
)

data class UserInfoUiState(
	val userName: String,
	val userInitials: String,
	val roleLabel: String,
	val points: Int,
	val contributions: Int,
	val contributionsInSelectedTab: Int,
	val activeDays: Int,
	val selectedContributionTab: EstadoPublicacion,
	val selectedBottomTabIndex: Int,
	val selectedContributionItems: List<UserContributionItem>
)

@HiltViewModel
class UserInfoViewModel @Inject constructor(
	private val userRepository: UserRepository,
	private val sessionDataStore: SessionDataStore
) : ViewModel() {

	var showLogoutDialog by mutableStateOf(false)
		private set

	var uiState by mutableStateOf(
		UserInfoUiState(
			userName = "Usuario",
			userInitials = buildInitials("Usuario"),
			roleLabel = "TURISTA",
			points = 0,
			contributions = 0,
			contributionsInSelectedTab = 0,
			activeDays = 1,
			selectedContributionTab = EstadoPublicacion.PENDIENTE,
			selectedBottomTabIndex = 2,
			selectedContributionItems = emptyList()
		)
	)
		private set

	private var currentUserId: String? = null

	init {
		loadUserData()
	}

	fun onContributionTabSelected(tab: EstadoPublicacion) {
		uiState = uiState.copy(selectedContributionTab = tab)
		refreshSelectedContributions()
	}

	fun onBottomTabSelected(index: Int) {
		uiState = uiState.copy(selectedBottomTabIndex = index)
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
			val user = session?.userId?.let { userRepository.findByEmail(it) }
				?: userRepository.users.value.firstOrNull()

			user?.let { mappedUser ->
				currentUserId = mappedUser.email
				val contributionsByUser = userRepository.getUserPublications(mappedUser.email)
				val contributionCount = contributionsByUser.count {
					it.estado == EstadoPublicacion.VERIFICADA ||
						it.estado == EstadoPublicacion.PENDIENTE ||
						it.estado == EstadoPublicacion.RECHAZADA
				}

				uiState = uiState.copy(
					userName = mappedUser.nombre,
					userInitials = buildInitials(mappedUser.nombre),
					roleLabel = mappedUser.rol.name,
					points = mappedUser.puntos,
					contributions = contributionCount,
					activeDays = maxOf(contributionCount, 1)
				)
				refreshSelectedContributions()
			}
		}
	}

	private fun refreshSelectedContributions() {
		val userId = currentUserId ?: return
		val filtered = userRepository.getUserPublications(userId)
			.filter { it.estado == uiState.selectedContributionTab }
			.map { it.toContributionItem() }

		uiState = uiState.copy(
			contributionsInSelectedTab = filtered.size,
			selectedContributionItems = filtered
		)
	}

	private fun PuntoInteres.toContributionItem(): UserContributionItem = UserContributionItem(
		id = id,
		title = titulo,
		status = estado,
		rejectReason = motivoRechazo
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
