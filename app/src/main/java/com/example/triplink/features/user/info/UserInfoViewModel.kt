package com.example.triplink.features.user.info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject

enum class ContributionTab {
	VERIFIED,
	PENDING,
	REJECTED
}

data class UserInfoUiState(
	val userName: String,
	val userInitials: String,
	val roleLabel: String,
	val points: Int,
	val contributions: Int,
	val activeDays: Int,
	val selectedContributionTab: ContributionTab,
	val selectedBottomTabIndex: Int
)

@HiltViewModel
class UserInfoViewModel @Inject constructor() : ViewModel() {

	var showLogoutDialog by mutableStateOf(false)
		private set

	var uiState by mutableStateOf(
		UserInfoUiState(
			userName = "Usuario 1",
			userInitials = buildInitials("Usuario 1"),
			roleLabel = "TURISTA",
			points = 0,
			contributions = 0,
			activeDays = 1,
			selectedContributionTab = ContributionTab.PENDING,
			selectedBottomTabIndex = 2
		)
	)
		private set

	fun onContributionTabSelected(tab: ContributionTab) {
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
