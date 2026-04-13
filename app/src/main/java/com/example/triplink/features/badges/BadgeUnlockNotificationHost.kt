package com.example.triplink.features.badges

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.core.components.feedback.BadgeDetailModal

@Composable
fun BadgeUnlockNotificationHost(
	userId: String,
	onViewBadges: () -> Unit,
	viewModel: BadgeUnlockNotifierViewModel = hiltViewModel()
) {
	LaunchedEffect(userId) {
		viewModel.bindUser(userId)
	}

	val unlockDialog by viewModel.currentUnlockDialog.collectAsState()

	unlockDialog?.let { unlock ->
		BadgeDetailModal(
			badge = unlock.badge,
			totalPoints = unlock.totalPoints,
			levelLabel = unlock.currentLevel,
			onDismiss = viewModel::dismissUnlockDialog,
			onViewBadges = onViewBadges
		)
	}
}


