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
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.ui.theme.DarkGray
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.PrincipalGray

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersScreen(
    onBackClick: () -> Unit,
    viewModel: FiltersViewModel = hiltViewModel()
) {

    Scaffold(
        topBar = {
            GeneralTopBar(
                title = "Filtros",
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
                    title = "Categorías",
                    options = viewModel.categories,
                    selectedOptions = viewModel.selectedCategories,
                    onOptionToggle = { option -> viewModel.cambiarOpcion(option)}
                )
            }

            item { HorizontalDivider(color = Color(0xFFEEEEEE)) }

            item {
                FilterSection(
                    title = "Ubicación",
                    options = viewModel.locations,
                    selectedOptions = viewModel.selectedLocations,
                    onOptionToggle = { option -> viewModel.cambiarOpcion(option)
                    }
                )
            }

            item { HorizontalDivider(color = Color(0xFFEEEEEE)) }

            item {
                FilterSection(
                    title = "Rango de Precios",
                    options = viewModel.priceRanges,
                    selectedOptions = viewModel.selectedPrices,
                    onOptionToggle = { option -> viewModel.cambiarOpcion(option) }
                )
            }

            item { HorizontalDivider(color = Color(0xFFEEEEEE)) }

            item {
                FilterSection(
                    title = "Calificación Mínima",
                    options = viewModel.ratings,
                    selectedOptions = viewModel.selectedRatings,
                    onOptionToggle = { option ->viewModel.cambiarOpcion(option) }
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterSection(
    title: String,
    options: List<String>,
    selectedOptions: Set<String>,
    onOptionToggle: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = Color.Black
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
                    text = option,
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
            color = if (isSelected) PrincipalBlue else Color(0xFFD1D5DB)
        ),
        color = if (isSelected) PrincipalBlue.copy(alpha = 0.1f) else Color.Transparent
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = if (isSelected) PrincipalBlue else DarkGray
            )
        )
    }
}
