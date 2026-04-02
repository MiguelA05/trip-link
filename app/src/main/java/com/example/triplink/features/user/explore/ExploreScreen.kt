package com.example.triplink.features.user.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.example.triplink.core.components.PublicationCard
import com.example.triplink.core.components.common.CategoryChips
import com.example.triplink.core.components.common.SearchBar
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.PrincipalWhite

@Composable
fun ExploreScreen(
	viewModel: ExploreViewModel = viewModel(),
	contentPadding: PaddingValues = PaddingValues(),
	onPublicationClick: (String) -> Unit = {},
	onMapClick: () -> Unit = {}
) {
	Scaffold(
		modifier = Modifier
			.fillMaxSize()
			.padding(contentPadding),
		containerColor = Color(0xFFF5F6F8),
		contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
		topBar = {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.background(Color(0xFFF5F6F8))
					.statusBarsPadding()
					.padding(horizontal = 12.dp, vertical = 8.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				SearchBar(
					query = viewModel.query,
					onQueryChange = viewModel::onQueryChange,
					onFilterClick = {}
				)
				CategoryChips(
					categories = viewModel.categories,
					selectedCategory = viewModel.selectedCategory,
					onCategorySelected = viewModel::onCategorySelected
				)
			}
		},
		floatingActionButton = {
			FloatingActionButton(
				onClick = onMapClick,
				containerColor = PrincipalBlue,
				contentColor = PrincipalWhite
			) {
				Icon(
					imageVector = Icons.Outlined.Map,
					contentDescription = "Ver mapa"
				)
			}
		}
	) { scaffoldPaddingValues ->
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.padding(scaffoldPaddingValues),
			contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp)
		) {
			items(viewModel.filteredPuntoInteres, key = { it.id }) { publication ->
				PublicationCard(
					puntoInteres = publication,
					onCardClick = { onPublicationClick(publication.id) },
					showFooter = false
				)
			}
		}
	}
}

