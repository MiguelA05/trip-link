package com.example.triplink.features.user.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triplink.core.components.GeneralAlertDialog
import com.example.triplink.core.components.profile.EmptyState
import com.example.triplink.core.components.profile.ProfileHeader
import com.example.triplink.core.components.profile.SectionCard
import com.example.triplink.core.components.profile.StatsRow
import com.example.triplink.core.components.profile.StatusTabs
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.PrincipalWhite

@Composable
fun UserInfoScreen(
	viewModel: UserInfoViewModel = hiltViewModel(),
	contentPadding: PaddingValues = PaddingValues(),
	onLogoutClick: () -> Unit = {},
	onEditClick: () -> Unit = {},
	onBagdesClick: () -> Unit = {},
	onPostCreationClick: () -> Unit = {}
) {
	val state = viewModel.uiState

	Scaffold(
		modifier = Modifier
			.fillMaxSize()
			.padding(contentPadding),
		containerColor = Color(0xFFF0F2F5),
		contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
					onEditClick = onEditClick,
					modifier = Modifier
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
					title = "Mis Insignias",
					actionLabel = "Ver todas",
					onActionClick = onBagdesClick,
					modifier = Modifier.padding(horizontal = 10.dp)
				) {
					EmptyState(message = "Aun no tienes insignias ganadas")
				}
			}

			item {
				Card(
					modifier = Modifier.fillMaxWidth(),
					colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F8FA)),
					elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
				) {
					StatusTabs(
						selectedTab = state.selectedContributionTab,
						onTabSelected = viewModel::onContributionTabSelected,
						modifier = Modifier.padding(horizontal = 4.dp)
					)
				}
			}

			item {
				Card(
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 10.dp),
					colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F4F7)),
					shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
					elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
				) {
					Column(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 42.dp, horizontal = 20.dp),
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.spacedBy(18.dp)
					) {
						val tabLabel = when (state.selectedContributionTab) {
							com.example.triplink.domain.model.enums.EstadoPublicacion.PENDIENTE -> "pendientes"
							com.example.triplink.domain.model.enums.EstadoPublicacion.VERIFICADA -> "verificadas"
							com.example.triplink.domain.model.enums.EstadoPublicacion.RECHAZADA -> "rechazadas"
						}
						Text(
							text = if (state.contributionsInSelectedTab == 0) {
								"No tienes contribuciones\n$tabLabel"
							} else {
								"Tienes ${state.contributionsInSelectedTab}\ncontribuciones $tabLabel"
							},
							color = Color(0xFF8FA1BA),
							style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
							fontWeight = FontWeight.Medium
						)

						Button(
							onClick = onPostCreationClick,
							colors = ButtonDefaults.buttonColors(
								containerColor = PrincipalBlue,
								contentColor = PrincipalWhite
							)
						) {
							Text(
								text = "Crear POI",
								style = androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(
									fontWeight = FontWeight.Bold
								),
								modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
							)
						}
					}
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
					title = "¿Cerrar sesión?",
					message = "Se cerrará tu sesión en este dispositivo.\nPodrás volver a ingresar en cualquier momento.",
					icon = Icons.AutoMirrored.Outlined.Logout,
					buttonText = "Cerrar sesión",
					dismissButtonText = "Cancelar",
					onDismissAction = viewModel::dismissLogoutDialog
				)
			}
}


