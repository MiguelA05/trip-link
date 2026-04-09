package com.example.triplink.features.user.info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.data.datastore.SessionDataStore
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.repository.publication.PublicationRepository
import com.example.triplink.domain.repository.user.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

data class UserInfoUiState(
	val userName: String,
	val userInitials: String,
	val roleLabel: String,
	val points: Int,
	val contributions: Int,
	val activeDays: Int,
	val selectedContributionTab: EstadoPublicacion,
	val selectedBottomTabIndex: Int,
	val selectedContributionItems: List<UserContributionItem>,
	val verifiedCount: Int = 0,
	val pendingCount: Int = 0,
	val rejectedCount: Int = 0
)

@HiltViewModel
class UserInfoViewModel @Inject constructor(
	private val userProfileRepository: UserProfileRepository,
	private val publicationRepository: PublicationRepository,
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
			activeDays = 1,
			selectedContributionTab = EstadoPublicacion.PENDIENTE,
			selectedBottomTabIndex = 2,
			selectedContributionItems = emptyList()
		)
	)
		private set

	private var currentUserId: String? = null

	init {
		observePublications()
		loadUserData()
	}

	private fun observePublications() {
		viewModelScope.launch {
			publicationRepository.publications.collectLatest {
				refreshSelectedContributions()
			}
		}
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
			val user = session?.userId?.let { userProfileRepository.getUserById(it) }
				?: userProfileRepository.users.value.firstOrNull()

			user?.let { mappedUser ->
				currentUserId = mappedUser.email
				
				val contributionsByUser = publicationRepository.getUserPublications(mappedUser.email)
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
		val allUserPublications = publicationRepository.getUserPublications(userId)
		
		val filtered = allUserPublications
			.filter { it.estado == uiState.selectedContributionTab }
			.map { it.toContributionItem() }

		uiState = uiState.copy(
			selectedContributionItems = filtered,
			verifiedCount = allUserPublications.count { it.estado == EstadoPublicacion.VERIFICADA },
			pendingCount = allUserPublications.count { it.estado == EstadoPublicacion.PENDIENTE },
			rejectedCount = allUserPublications.count { it.estado == EstadoPublicacion.RECHAZADA }
		)
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
