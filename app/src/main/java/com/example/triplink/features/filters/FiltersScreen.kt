package com.example.triplink.features.filters

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.example.triplink.R
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.core.localization.localizedLabel
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.UbicacionFiltro
import com.example.triplink.ui.theme.TextTokens

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersScreen(
    onBackClick: () -> Unit,
    viewModel: FiltersViewModel = hiltViewModel()
) {

    Scaffold(
        topBar = {
            GeneralTopBar(
                title = stringResource(R.string.feature_filters_title),
                onBack = onBackClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                FilterSection(
                    title = stringResource(R.string.feature_filters_categories),
                    options = viewModel.categories,
                    selectedOptions = viewModel.selectedCategories,
                    onOptionToggle = viewModel::toggleCategory,
                    optionLabel = { category -> category.localizedLabel() }
                )
            }

            item { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) }

            item {
                FilterSection(
                    title = stringResource(R.string.feature_filters_location),
                    options = viewModel.locations,
                    selectedOptions = viewModel.selectedLocations,
                    onOptionToggle = viewModel::toggleLocation,
                    optionLabel = { option ->
                        when (option) {
                            UbicacionFiltro.CERCANOS -> stringResource(R.string.vm_filters_location_nearby)
                            UbicacionFiltro.CIUDAD -> stringResource(R.string.vm_filters_location_city)
                            UbicacionFiltro.DEPARTAMENTO -> stringResource(R.string.vm_filters_location_department)
                            UbicacionFiltro.PAIS -> stringResource(R.string.vm_filters_location_country)
                        }
                    }
                )
            }

            item { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) }

            item {
                FilterSection(
                    title = stringResource(R.string.feature_filters_price_range),
                    options = viewModel.priceRanges,
                    selectedOptions = viewModel.selectedPrices,
                    onOptionToggle = viewModel::togglePrice,
                    optionLabel = { option ->
                        when (option) {
                            RangoPrecios.GRATUITO -> stringResource(R.string.component_publication_price_range_free)
                            RangoPrecios.ECONOMICO -> stringResource(R.string.component_publication_price_range_economic)
                            RangoPrecios.MODERADO -> stringResource(R.string.component_publication_price_range_moderate)
                            RangoPrecios.COSTOSO -> stringResource(R.string.component_publication_price_range_expensive)
                        }
                    }
                )
            }

            item { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) }

            item {
                FilterSection(
                    title = stringResource(R.string.feature_filters_minimum_rating),
                    options = viewModel.ratings,
                    selectedOptions = viewModel.selectedRatings,
                    onOptionToggle = viewModel::toggleRating,
                    optionLabel = { "$it★" }
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> FilterSection(
    title: String,
    options: List<T>,
    selectedOptions: Set<T>,
    onOptionToggle: (T) -> Unit,
    optionLabel: @Composable (T) -> String
) {
    Column {
        Text(
            text = title,
            style = TextTokens.sectionTitle(),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { option ->
                val isSelected = selectedOptions.contains(option)
                FilterChipItem(
                    text = optionLabel(option),
                    isSelected = isSelected,
                    onClick = { onOptionToggle(option) }
                )
            }
        }
    }
}

@Composable
fun FilterChipItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = TextTokens.emphasized(TextTokens.body(), FontWeight.Medium),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
