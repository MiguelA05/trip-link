package com.example.triplink.features.userInfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triplink.core.components.ContributionTabs
import com.example.triplink.core.components.EmptyInfoState
import com.example.triplink.core.components.UserHomeBottomBar
import com.example.triplink.core.components.UserInfoHeader
import com.example.triplink.core.components.UserInfoSectionCard
import com.example.triplink.core.components.UserStatsRow
import com.example.triplink.core.components.defaultUserHomeNavItems
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.PrincipalWhite

@Composable
fun UserInfoScreen(viewModel: UserInfoViewModel = viewModel()) {
	val state = viewModel.uiState
	val navItems = defaultUserHomeNavItems()

	Scaffold(
		modifier = Modifier.fillMaxSize(),
		containerColor = Color(0xFFF0F2F5),
		bottomBar = {
			UserHomeBottomBar(
				items = navItems,
				selectedIndex = state.selectedBottomTabIndex,
				onItemSelected = viewModel::onBottomTabSelected
			)
		}
	) { paddingValues ->
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues),
			contentPadding = PaddingValues(bottom = 10.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			item {
				UserInfoHeader(
					userName = state.userName,
					initials = state.userInitials,
					roleLabel = state.roleLabel,
					onBackClick = {},
					onEditClick = {},
					modifier = Modifier.systemBarsPadding()
				)
			}

			item {
				UserStatsRow(
					points = state.points,
					contributions = state.contributions,
					activeDays = state.activeDays,
					modifier = Modifier.padding(horizontal = 10.dp)
				)
			}

			item {
				UserInfoSectionCard(
					title = "Mis Insignias",
					actionLabel = "Ver todas",
					onActionClick = {},
					modifier = Modifier.padding(horizontal = 10.dp)
				) {
					EmptyInfoState(message = "Aun no tienes insignias ganadas")
				}
			}

			item {
				Card(
					modifier = Modifier.fillMaxWidth(),
					colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F8FA)),
					elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
				) {
					ContributionTabs(
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
						Text(
							text = "No tienes contribuciones\npendientes",
							color = Color(0xFF8FA1BA),
							style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
							fontWeight = FontWeight.Medium
						)

						Button(
							onClick = {},
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
}

