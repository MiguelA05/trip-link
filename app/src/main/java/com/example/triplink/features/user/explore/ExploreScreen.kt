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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.R
import com.example.triplink.core.components.PublicationCard
import com.example.triplink.core.components.common.CategoryChips
import com.example.triplink.core.components.common.SearchBar

@Composable
fun ExploreScreen(
	contentPadding: PaddingValues = PaddingValues(),
	onPublicationClick: (String) -> Unit = {},
	onMapClick: () -> Unit = {},
	onFiltersClick: () -> Unit = {}
) {
	val viewModel: ExploreViewModel = hiltViewModel()
	val publications by viewModel.publications.collectAsState()

	Scaffold(
		modifier = Modifier
			.fillMaxSize()
			.padding(contentPadding),
		containerColor = MaterialTheme.colorScheme.background,
		contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
		topBar = {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.background(MaterialTheme.colorScheme.background)
					.statusBarsPadding()
					.padding(horizontal = 12.dp, vertical = 8.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				SearchBar(
					query = viewModel.query,
					onQueryChange = viewModel::onQueryChange,
					onFilterClick = onFiltersClick
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
				containerColor = MaterialTheme.colorScheme.primary,
				contentColor = MaterialTheme.colorScheme.onPrimary
			) {
				Icon(
					imageVector = Icons.Outlined.Map,
					contentDescription = stringResource(R.string.feature_explore_view_map_content_description)
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
			items(viewModel.filteredPuntoInteres(publications), key = { it.id }) { publication ->
				PublicationCard(
					puntoInteres = publication,
					ratingLabel = viewModel.ratingLabelForPublication(publication),
					onCardClick = { onPublicationClick(publication.id) },
					showFooter = false
				)
			}
		}
	}
}
