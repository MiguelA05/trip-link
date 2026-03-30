package com.example.triplink.features.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triplink.core.components.ExploreCategoryChips
import com.example.triplink.core.components.ExploreSearchBar
import com.example.triplink.core.components.PublicationCard
import com.example.triplink.core.components.UserHomeBottomBar
import com.example.triplink.core.components.defaultUserHomeNavItems
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.PrincipalWhite

@Composable
fun ExploreScreen(viewModel: ExploreViewModel = viewModel()) {
	val navItems = defaultUserHomeNavItems()

	Scaffold(
		modifier = Modifier.fillMaxSize(),
		containerColor = Color(0xFFF5F6F8),
		contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
		topBar = {
			Column(
				modifier = Modifier
					.systemBarsPadding()
					.padding(horizontal = 12.dp, vertical = 8.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				ExploreSearchBar(
					query = viewModel.query,
					onQueryChange = viewModel::onQueryChange,
					onFilterClick = {}
				)
				ExploreCategoryChips(
					categories = viewModel.categories,
					selectedCategory = viewModel.selectedCategory,
					onCategorySelected = viewModel::onCategorySelected
				)
			}
		},
		floatingActionButton = {
			FloatingActionButton(
				onClick = {},
				containerColor = PrincipalBlue,
				contentColor = PrincipalWhite
			) {
				Icon(
					imageVector = Icons.Outlined.Map,
					contentDescription = "Ver mapa"
				)
			}
		},
		bottomBar = {
			UserHomeBottomBar(
				items = navItems,
				selectedIndex = viewModel.selectedTabIndex,
				onItemSelected = viewModel::onBottomTabSelected
			)
		}
	) { paddingValues ->
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues),
			contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			items(viewModel.filteredPublications, key = { it.id }) { publication ->
				PublicationCard(
					publication = publication,
					showFooter = false
				)
			}
		}
	}
}

