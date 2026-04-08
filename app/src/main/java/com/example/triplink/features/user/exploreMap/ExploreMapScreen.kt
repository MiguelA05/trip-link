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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntSize
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.R
import com.example.triplink.core.components.ExploreMapPublicationCard
import com.example.triplink.core.components.common.CategoryChips
import com.example.triplink.core.components.common.SearchBar
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.PrincipalWhite
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreMapScreen(
	viewModel: ExploreMapViewModel = hiltViewModel(),
	contentPadding: PaddingValues = PaddingValues(),
	onBackToExplore: () -> Unit = {},
	onFiltersClick: () -> Unit = {},
	onPublicationDetailsClick: (String) -> Unit = {}
) {
	val categoryOptions = listOf<Categoria?>(null) + Categoria.entries
	val categoryItems = categoryOptions.map { category ->
		MapCategoryChip(
			category = category,
			label = stringResource(id = categoryLabelRes(category))
		)
	}
	val selectedCategoryLabel = categoryItems
		.firstOrNull { it.category == viewModel.selectedCategory }
		?.label
		.orEmpty()

	val sheetState = rememberStandardBottomSheetState(
		initialValue = SheetValue.PartiallyExpanded,
		skipHiddenState = true
	)
	val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
	val coroutineScope = rememberCoroutineScope()
	var mapSize by remember { mutableStateOf(IntSize.Zero) }

	BottomSheetScaffold(
		modifier = Modifier
			.fillMaxSize(),
		scaffoldState = scaffoldState,
		sheetPeekHeight = 170.dp,
		sheetContainerColor = Color(0xFFF4F5F7),
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
							.background(Color(0xFFD8DCE3), shape = RoundedCornerShape(999.dp))
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

					ExploreMapPublicationCard(
						publication = viewModel.selectedPublication,
						ratingLabel = viewModel.selectedMarkerRatingLabel,
						expanded = sheetState.targetValue == SheetValue.Expanded || sheetState.currentValue == SheetValue.Expanded,
						onOpenPublication = {onPublicationDetailsClick("")}
					)
				}

				FloatingActionButton(
					onClick = onBackToExplore,
					modifier = Modifier
						.align(Alignment.TopEnd)
						.padding(top = 18.dp, end = 16.dp),
					containerColor = PrincipalBlue,
					contentColor = PrincipalWhite,
					shape = CircleShape
				) {
					Icon(
						imageVector = Icons.AutoMirrored.Outlined.FormatListBulleted,
						contentDescription = "Volver a lista"
					)
				}
			}
		}
	) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(Color(0xFFE6E9EE))
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
					query = viewModel.query,
					onQueryChange = viewModel::onQueryChange,
					onFilterClick = onFiltersClick
				)
				CategoryChips(
					categories = categoryItems.map { it.label },
					selectedCategory = selectedCategoryLabel,
					onCategorySelected = { selectedLabel ->
						val selectedCategory = categoryItems.firstOrNull { it.label == selectedLabel }?.category
						viewModel.onCategorySelected(selectedCategory)
					}
				)
			}

			Box(
				modifier = Modifier
					.fillMaxSize()
					.onSizeChanged { newSize -> mapSize = newSize }
			) {
				viewModel.markers.forEach { marker ->
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

private data class MapCategoryChip(
	val category: Categoria?,
	val label: String
)

private fun categoryLabelRes(category: Categoria?): Int = when (category) {
	null -> R.string.component_explore_map_category_all
	Categoria.GASTRONOMIA -> R.string.component_explore_map_category_gastronomia
	Categoria.CULTURA -> R.string.component_explore_map_category_cultura
	Categoria.NATURALEZA -> R.string.component_explore_map_category_naturaleza
	Categoria.ENTRETENIMIENTO -> R.string.component_explore_map_category_entretenimiento
	Categoria.HISTORIA -> R.string.component_explore_map_category_historia
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
			color = Color(0xFFF6F7FA),
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
					tint = Color(0xFFF4AA1E),
					modifier = Modifier.size(13.dp)
				)
				Text(
					text = marker.ratingLabel,
					style = MaterialTheme.typography.bodyMedium,
					fontWeight = FontWeight.Bold,
					color = Color(0xFF2B3748)
				)
			}
		}

		Surface(
			modifier = Modifier.size(if (marker.highlighted) 54.dp else 46.dp),
			shape = CircleShape,
			color = if (marker.highlighted) Color(0xFF2E7D32) else PrincipalBlue,
			onClick = onClick,
			shadowElevation = 6.dp
		) {
			Box(contentAlignment = Alignment.Center) {
				Icon(
					imageVector = if (marker.highlighted) Icons.Outlined.Eco else Icons.Outlined.LocationOn,
					contentDescription = null,
					tint = Color.White,
					modifier = Modifier.size(24.dp)
				)
			}
		}
	}
}

@Composable
private fun MapPlaceholderLayer(modifier: Modifier = Modifier) {
	Canvas(modifier = modifier) {
		drawRect(Color(0xFFE3E7ED))

		val roadColor = Color(0xFFF7F8FB)
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
			color = Color(0xFFB8DDA5),
			topLeft = Offset(size.width * 0.07f, size.height * 0.18f),
			size = androidx.compose.ui.geometry.Size(size.width * 0.16f, size.height * 0.10f),
			cornerRadius = CornerRadius(32f, 32f)
		)
		drawRoundRect(
			color = Color(0xFFB8DDA5),
			topLeft = Offset(size.width * 0.72f, size.height * 0.42f),
			size = androidx.compose.ui.geometry.Size(size.width * 0.2f, size.height * 0.12f),
			cornerRadius = CornerRadius(32f, 32f)
		)

		drawRoundRect(
			color = Color(0xFF91C8F6),
			topLeft = Offset(size.width * 0.42f, size.height * 0.04f),
			size = androidx.compose.ui.geometry.Size(size.width * 0.18f, size.height * 0.72f),
			cornerRadius = CornerRadius(100f, 100f)
		)
	}
}
