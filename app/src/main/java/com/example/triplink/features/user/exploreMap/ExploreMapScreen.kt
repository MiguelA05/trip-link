package com.example.triplink.features.user.exploreMap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.R
import com.example.triplink.core.components.ExploreMapPublicationCard
import com.example.triplink.core.components.common.AppliedFilterChipUi
import com.example.triplink.core.components.common.CategoryChips
import com.example.triplink.core.components.common.SearchBar
import com.example.triplink.core.localization.localizedLabel
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.UbicacionFiltro
import com.example.triplink.ui.theme.TextColors
import com.example.triplink.ui.theme.TextTokens
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreMapScreen(
	viewModel: ExploreMapViewModel = hiltViewModel(),
	contentPadding: PaddingValues = PaddingValues(),
	onBackToExplore: () -> Unit = {},
	onFiltersClick: () -> Unit = {},
	onPublicationDetailsClick: (String) -> Unit = {}
) {
	val sheetState = rememberStandardBottomSheetState(
		initialValue = SheetValue.PartiallyExpanded,
		skipHiddenState = true
	)
	val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
	val coroutineScope = rememberCoroutineScope()
	var mapSize by remember { mutableStateOf(IntSize.Zero) }
	val appliedFilters by viewModel.appliedFilters.collectAsState()
	val selectedPublication by viewModel.selectedPublication.collectAsState()
	val markers by viewModel.markers.collectAsState()
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

	BottomSheetScaffold(
		modifier = Modifier
			.fillMaxSize()
			.padding(contentPadding),
		scaffoldState = scaffoldState,
		sheetPeekHeight = 170.dp,
		sheetContainerColor = MaterialTheme.colorScheme.surface,
		sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
		sheetShadowElevation = 8.dp,
		sheetTonalElevation = 4.dp,
		sheetSwipeEnabled = true,
		sheetDragHandle = null,
			sheetContent = {
			Box(modifier = Modifier.fillMaxWidth()) {
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.navigationBarsPadding()
						.padding(horizontal = 12.dp, vertical = 8.dp)
						.padding(bottom = 45.dp)
				) {
					Box(
						modifier = Modifier
							.align(Alignment.CenterHorizontally)
							.padding(top = 4.dp, bottom = 10.dp)
							.size(width = 56.dp, height = 6.dp)
							.background(MaterialTheme.colorScheme.outlineVariant, shape = RoundedCornerShape(999.dp))
							.clickable {
								coroutineScope.launch {
									if (sheetState.currentValue == SheetValue.PartiallyExpanded) {
										sheetState.expand()
									} else {
										sheetState.partialExpand()
									}
								}
							}
					)

					selectedPublication?.let { selected ->
						ExploreMapPublicationCard(
							publication = selected,
							ratingLabel = viewModel.selectedMarkerRatingLabel,
							reviewCount = viewModel.selectedPublicationReviewCount,
							expanded = sheetState.targetValue == SheetValue.Expanded || sheetState.currentValue == SheetValue.Expanded,
							onOpenPublication = { onPublicationDetailsClick(selected.id) }
						)
					} ?: Text(
						text = stringResource(R.string.feature_filters_empty_filtered_results),
						style = TextTokens.body(),
						color = TextColors.Secondary,
						modifier = Modifier.padding(12.dp)
					)
				}

				FloatingActionButton(
					onClick = onBackToExplore,
					modifier = Modifier
						.align(Alignment.TopEnd)
						.padding(top = 18.dp, end = 16.dp),
					containerColor = MaterialTheme.colorScheme.primary,
					contentColor = MaterialTheme.colorScheme.onPrimary,
					shape = CircleShape
				) {
					Icon(
						imageVector = Icons.AutoMirrored.Outlined.FormatListBulleted,
						contentDescription = stringResource(R.string.feature_explore_map_back_to_list_content_description)
					)
				}
			}
		}
	) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background)
		) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.clickable {
						coroutineScope.launch { sheetState.partialExpand() }
					}
			) {
				MapPlaceholderLayer(modifier = Modifier.fillMaxSize())
			}

			Column(
				modifier = Modifier
					.fillMaxWidth()
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

			Box(
				modifier = Modifier
					.fillMaxSize()
					.onSizeChanged { newSize -> mapSize = newSize }
			) {
				markers.forEach { marker ->
					MarkerPin(
						marker = marker,
						onClick = {
							viewModel.onMarkerSelected(marker.id)
							coroutineScope.launch { sheetState.partialExpand() }
						},
						modifier = Modifier
							.offset {
								IntOffset(
									x = (mapSize.width * marker.xFraction).roundToInt() - 24.dp.roundToPx(),
									y = (mapSize.height * marker.yFraction).roundToInt() - 54.dp.roundToPx()
								)
							}
					)
				}
			}

		}
	}
}

@Composable
private fun MarkerPin(
	marker: MapMarkerUi,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(4.dp)
	) {
		Surface(
			shape = RoundedCornerShape(999.dp),
			color = MaterialTheme.colorScheme.surface,
			tonalElevation = 2.dp,
			onClick = onClick
		) {
			Row(
				modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(2.dp)
			) {
				Icon(
					imageVector = Icons.Outlined.Star,
					contentDescription = null,
					tint = MaterialTheme.colorScheme.tertiary,
					modifier = Modifier.size(13.dp)
				)
				Text(
					text = marker.ratingLabel,
					style = TextTokens.body(),
					color = TextColors.Primary
				)
			}
		}

		Surface(
			modifier = Modifier.size(if (marker.highlighted) 54.dp else 46.dp),
			shape = CircleShape,
			color = if (marker.highlighted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
			onClick = onClick,
			shadowElevation = 6.dp
		) {
			Box(contentAlignment = Alignment.Center) {
				Icon(
					imageVector = if (marker.highlighted) Icons.Outlined.Eco else Icons.Outlined.LocationOn,
					contentDescription = null,
					tint = if (marker.highlighted) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary,
					modifier = Modifier.size(24.dp)
				)
			}
		}
	}
}

@Composable
private fun MapPlaceholderLayer(modifier: Modifier = Modifier) {
	val mapBackground = MaterialTheme.colorScheme.surfaceVariant
	val roadColor = MaterialTheme.colorScheme.surface
	val parkColor = MaterialTheme.colorScheme.secondaryContainer
	val riverColor = MaterialTheme.colorScheme.primaryContainer

	Canvas(modifier = modifier) {
		drawRect(mapBackground)

		val pathEffect = PathEffect.cornerPathEffect(20f)

		for (index in 0..8) {
			val y = size.height * (index / 8f)
			drawLine(
				color = roadColor,
				start = Offset(0f, y),
				end = Offset(size.width, y + 32f),
				strokeWidth = if (index % 3 == 0) 28f else 16f,
				pathEffect = pathEffect
			)
		}

		for (index in 0..6) {
			val x = size.width * (index / 6f)
			drawLine(
				color = roadColor,
				start = Offset(x, 0f),
				end = Offset(x - 48f, size.height),
				strokeWidth = if (index % 2 == 0) 22f else 14f,
				pathEffect = pathEffect
			)
		}

		drawRoundRect(
			color = parkColor,
			topLeft = Offset(size.width * 0.07f, size.height * 0.18f),
			size = androidx.compose.ui.geometry.Size(size.width * 0.16f, size.height * 0.10f),
			cornerRadius = CornerRadius(32f, 32f)
		)
		drawRoundRect(
			color = parkColor,
			topLeft = Offset(size.width * 0.72f, size.height * 0.42f),
			size = androidx.compose.ui.geometry.Size(size.width * 0.2f, size.height * 0.12f),
			cornerRadius = CornerRadius(32f, 32f)
		)

		drawRoundRect(
			color = riverColor,
			topLeft = Offset(size.width * 0.42f, size.height * 0.04f),
			size = androidx.compose.ui.geometry.Size(size.width * 0.18f, size.height * 0.72f),
			cornerRadius = CornerRadius(100f, 100f)
		)
	}
}
