package com.example.triplink.features.user.info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.data.datastore.SessionDataStore
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class UserInfoUiState(
	val userName: String,
	val userInitials: String,
	val roleLabel: String,
	val points: Int,
	val contributions: Int,
	val activeDays: Int,
	val selectedContributionTab: EstadoPublicacion,
	val selectedBottomTabIndex: Int
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
			activeDays = 1,
			selectedContributionTab = EstadoPublicacion.PENDIENTE,
			selectedBottomTabIndex = 2
		)
	)
		private set

	init {
		loadUserData()
	}

	fun onContributionTabSelected(tab: EstadoPublicacion) {
		uiState = uiState.copy(selectedContributionTab = tab)
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

	private fun loadUserData() {
		viewModelScope.launch {
			val session = sessionDataStore.sessionFlow.first()
			val user = session?.userId?.let { userRepository.findByEmail(it) }
				?: userRepository.users.value.firstOrNull()

			user?.let { mappedUser ->
				// Filtrar publicaciones del usuario por email (userId = email)
				val contributionsByUser = userRepository.explorePublications().filter {
					it.usuarioAutorId.equals(mappedUser.email, ignoreCase = true)
				}

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
			}
		}
	}

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
