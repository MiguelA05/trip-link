package com.example.triplink.features.user.info

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.HeartBroken
import androidx.compose.material3.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.R
import com.example.triplink.core.components.GeneralAlertDialog
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.profile.EmptyState
import com.example.triplink.core.components.profile.ProfileHeader
import com.example.triplink.core.components.profile.RecentBadgeChip
import com.example.triplink.core.components.profile.SectionCard
import com.example.triplink.core.components.profile.StatsRow
import com.example.triplink.core.components.profile.StatusTabs
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.ui.theme.TextColors
import com.example.triplink.ui.theme.TextTokens
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UserInfoScreen(
	viewModel: UserInfoViewModel = hiltViewModel(),
	contentPadding: PaddingValues = PaddingValues(),
	onLogoutClick: () -> Unit = {},
	onEditClick: () -> Unit = {},
	onBagdesClick: () -> Unit = {},
	onPostCreationClick: () -> Unit = {},
	onEditRejectedPublication: (String) -> Unit = {},
	onViewVerifiedPublication: (String) -> Unit = {}
) {
	LaunchedEffect(Unit) {
		viewModel.refreshData()
	}

	val stateFlow = viewModel.uiState.collectAsState()
	val state = stateFlow.value

	Scaffold(
		modifier = Modifier
			.fillMaxSize()
			.padding(contentPadding),
		containerColor = MaterialTheme.colorScheme.background,
		contentWindowInsets = WindowInsets(0, 0, 0, 0)
	) { paddingValues ->
	LazyColumn(
		modifier = Modifier
			.fillMaxSize()
			.padding(paddingValues),
		contentPadding = PaddingValues(bottom = 10.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		item {
			ProfileHeader(
				userName = state.userName,
				initials = state.userInitials,
				roleLabel = state.roleLabel,
				onBackClick = viewModel::onLogoutRequested,
				onEditClick = onEditClick
			)
		}

		item {
			StatsRow(
				points = state.points,
				contributions = state.contributions,
				activeDays = state.activeDays,
				modifier = Modifier.padding(horizontal = 10.dp)
			)
		}

		item {
				SectionCard(
					title = stringResource(R.string.feature_user_info_badges_title),
					actionLabel = stringResource(R.string.feature_user_info_badges_action),
					onActionClick = onBagdesClick,
					modifier = Modifier.padding(horizontal = 10.dp)
				) {
				if (state.recentBadges.isEmpty()) {
					EmptyState(message = stringResource(R.string.feature_user_info_badges_empty))
				} else {
					LazyRow(
						horizontalArrangement = Arrangement.spacedBy(10.dp)
					) {
						items(state.recentBadges) { badge ->
							RecentBadgeChip(badge = badge)
						}
					}
				}
			}
		}

		item {
			Card(
				modifier = Modifier.fillMaxWidth(),
				colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
				elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
			) {
				StatusTabs(
					selectedIndex = state.selectedContributionIndex,
					onTabSelectedIndex = viewModel::onContributionTabSelected,
					favoritesCount = state.favoritesCount,
					verifiedCount = state.verifiedCount,
					pendingCount = state.pendingCount,
					rejectedCount = state.rejectedCount,
					modifier = Modifier.padding(horizontal = 4.dp)
				)
			}
		}

		if (state.isContributionsLoading) {
			item {
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 32.dp),
					contentAlignment = Alignment.Center
				) {
					CircularProgressIndicator()
				}
			}
		} else if (state.selectedContributionItems.isEmpty()) {
			item {
				val emptyMessage = when (state.selectedContributionIndex) {
					0 -> stringResource(R.string.feature_user_info_empty_favorites)
					1 -> stringResource(R.string.feature_user_info_empty_verified)
					2 -> stringResource(R.string.feature_user_info_empty_pending)
					3 -> stringResource(R.string.feature_user_info_empty_rejected)
					else -> stringResource(R.string.feature_user_info_empty_verified)
				}

				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 10.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text = emptyMessage,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						style = TextTokens.sectionTitle(),
						textAlign = TextAlign.Center,
						modifier = Modifier.fillMaxWidth()
					)

					Spacer(modifier = Modifier.height(12.dp))

					// Para la sección de Favoritos (índice 0) mostramos un icono de "corazón" en lugar
					// del botón de crear publicación. Para las otras secciones mantenemos el botón.
					if (state.selectedContributionIndex == 0) {
						Icon(
							imageVector = Icons.Outlined.HeartBroken,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.onSurfaceVariant,
							modifier = Modifier.size(48.dp)
						)
					} else {
						GeneralButton(
							onClick = onPostCreationClick,
							text = stringResource(R.string.feature_user_info_create_poi_action)
						)
					}
				}
			}
		} else {
			items(state.selectedContributionItems) { contribution ->
				val contributionAction = {
					if (state.selectedContributionIndex == 0) {
						// Favorites - open like verified
						onViewVerifiedPublication(contribution.id)
					} else {
						when (contribution.status) {
							EstadoPublicacion.RECHAZADA -> onEditRejectedPublication(contribution.id)
							EstadoPublicacion.VERIFICADA,
							EstadoPublicacion.PENDIENTE -> onViewVerifiedPublication(contribution.id)
						}
					}
				}

				ContributionCard(
					contribution = contribution,
					onActionClick = contributionAction
				)
			}
		}
	}
	}

	if (viewModel.showLogoutDialog) {
		GeneralAlertDialog(
			onDismissRequest = viewModel::dismissLogoutDialog,
			onConfirm = {
				viewModel.dismissLogoutDialog()
				onLogoutClick()
			},
			title = stringResource(R.string.feature_user_info_logout_title),
			message = stringResource(R.string.feature_user_info_logout_message),
			icon = Icons.AutoMirrored.Outlined.Logout,
			buttonText = stringResource(R.string.feature_user_info_logout_confirm),
			dismissButtonText = stringResource(R.string.feature_user_info_logout_cancel),
			onDismissAction = viewModel::dismissLogoutDialog
		)
	}
}

@Composable
fun ContributionCard(
	contribution: UserContributionItem,
	onActionClick: () -> Unit
) {
	val leftBarColor = when (contribution.status) {
		EstadoPublicacion.VERIFICADA -> MaterialTheme.colorScheme.primary
		EstadoPublicacion.PENDIENTE -> MaterialTheme.colorScheme.tertiary
		EstadoPublicacion.RECHAZADA -> MaterialTheme.colorScheme.error
	}

	val badgeColor = when (contribution.status) {
		EstadoPublicacion.VERIFICADA -> MaterialTheme.colorScheme.primaryContainer
		EstadoPublicacion.PENDIENTE -> MaterialTheme.colorScheme.tertiaryContainer
		EstadoPublicacion.RECHAZADA -> MaterialTheme.colorScheme.errorContainer
	}

	val badgeTextColor = when (contribution.status) {
		EstadoPublicacion.VERIFICADA -> MaterialTheme.colorScheme.onPrimaryContainer
		EstadoPublicacion.PENDIENTE -> MaterialTheme.colorScheme.onTertiaryContainer
		EstadoPublicacion.RECHAZADA -> MaterialTheme.colorScheme.onErrorContainer
	}

	val statusLabel = when (contribution.status) {
		EstadoPublicacion.VERIFICADA -> stringResource(R.string.feature_user_info_status_verified)
		EstadoPublicacion.PENDIENTE -> stringResource(R.string.feature_user_info_status_pending)
		EstadoPublicacion.RECHAZADA -> stringResource(R.string.feature_user_info_status_rejected)
	}

	val dateFormatter = SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("es-ES"))
	val dateString = dateFormatter.format(Date(contribution.createdAt))

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 10.dp),
		onClick = onActionClick,
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
		shape = RoundedCornerShape(16.dp),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.height(IntrinsicSize.Min)
		) {
			Box(
				modifier = Modifier
					.width(6.dp)
					.fillMaxHeight()
					.background(leftBarColor)
			)

			Column(
				modifier = Modifier
					.padding(16.dp)
					.fillMaxWidth()
			) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = contribution.title,
						style = TextTokens.title(),
						color = TextColors.Primary,
						modifier = Modifier.weight(1f)
					)

					Surface(
						color = badgeColor,
						shape = RoundedCornerShape(12.dp)
					) {
						Text(
							text = statusLabel,
							modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
							color = badgeTextColor,
							style = TextTokens.caption()
						)
					}
				}

				Text(
					text = dateString,
					color = TextColors.Secondary,
					style = TextTokens.body()
				)

			if (contribution.status == EstadoPublicacion.RECHAZADA && !contribution.rejectReason.isNullOrBlank()) {
				Text(
					text = stringResource(R.string.feature_user_info_reject_reason_prefix, contribution.rejectReason ?: ""),
					color = TextColors.Secondary,
					style = TextTokens.body()
				)
			}

				if (contribution.status != EstadoPublicacion.PENDIENTE) {
					Spacer(modifier = Modifier.height(16.dp))
					GeneralButton(
						onClick = onActionClick,
						text = if (contribution.status == EstadoPublicacion.RECHAZADA) {
							stringResource(R.string.feature_user_info_edit_resend_action)
						} else {
							stringResource(R.string.feature_user_info_view_publication_action)
						}
					)
				}
			}
		}
	}
}
