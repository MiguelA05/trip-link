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
import com.example.triplink.core.components.common.AppliedFilterChipUi
import com.example.triplink.core.components.common.CategoryChips
import com.example.triplink.core.components.common.SearchBar
import com.example.triplink.core.localization.localizedLabel
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.UbicacionFiltro

@Composable
fun ExploreScreen(
	contentPadding: PaddingValues = PaddingValues(),
	onPublicationClick: (String) -> Unit = {},
	onMapClick: () -> Unit = {},
	onFiltersClick: () -> Unit = {}
) {
	val viewModel: ExploreViewModel = hiltViewModel()
	val filteredPublications by viewModel.filteredPublications.collectAsState()
	val appliedFilters by viewModel.appliedFilters.collectAsState()
	val selectedCategory by viewModel.selectedCategory.collectAsState()
	val query by viewModel.query.collectAsState()
	val appliedChips = buildList {
		appliedFilters.categories.forEach { category ->
			add(
				AppliedFilterChipUi(
					key = "cat-${category.name}",
					label = category.localizedLabel(),
					onRemove = { viewModel.removeAppliedCategory(category) }
				)
			)
		}
		appliedFilters.locations.forEach { location ->
			val label = when (location) {
				UbicacionFiltro.CERCANOS -> stringResource(R.string.vm_filters_location_nearby)
				UbicacionFiltro.CIUDAD -> stringResource(R.string.vm_filters_location_city)
				UbicacionFiltro.DEPARTAMENTO -> stringResource(R.string.vm_filters_location_department)
				UbicacionFiltro.PAIS -> stringResource(R.string.vm_filters_location_country)
			}
			add(
				AppliedFilterChipUi(
					key = "loc-${location.name}",
					label = label,
					onRemove = { viewModel.removeAppliedLocation(location) }
				)
			)
		}
		appliedFilters.prices.forEach { price ->
			val label = when (price) {
				RangoPrecios.GRATUITO -> stringResource(R.string.component_publication_price_range_free)
				RangoPrecios.ECONOMICO -> stringResource(R.string.component_publication_price_range_economic)
				RangoPrecios.MODERADO -> stringResource(R.string.component_publication_price_range_moderate)
				RangoPrecios.COSTOSO -> stringResource(R.string.component_publication_price_range_expensive)
			}
			add(
				AppliedFilterChipUi(
					key = "price-${price.name}",
					label = label,
					onRemove = { viewModel.removeAppliedPrice(price) }
				)
			)
		}
		appliedFilters.ratings.forEach { rating ->
			add(
				AppliedFilterChipUi(
					key = "rating-$rating",
					label = "$rating★",
					onRemove = { viewModel.removeAppliedRating(rating) }
				)
			)
		}
	}

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
					query = query,
					onQueryChange = viewModel::onQueryChange,
					onFilterClick = onFiltersClick
				)
			CategoryChips(
				categories = viewModel.categories,
				selectedCategory = selectedCategory,
				onCategorySelected = viewModel::onCategorySelected,
				appliedChips = appliedChips
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
			items(filteredPublications, key = { it.id }) { publication ->
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
