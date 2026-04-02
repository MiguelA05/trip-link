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
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.ui.theme.DarkGray
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.PrincipalGray

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersScreen(
    onBackClick: () -> Unit
) {
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    var selectedLocations by remember { mutableStateOf(setOf<String>()) }
    var selectedPrices by remember { mutableStateOf(setOf<String>()) }
    var selectedRatings by remember { mutableStateOf(setOf<String>()) }

    val categories = listOf(
        "Naturaleza y Parques", "Cafes Especiales", "Museos y Cultura",
        "Miradores", "Arquitectura Colonial", "Restaurantes Típicos",
        "Vida Nocturna", "Aventura y Deporte"
    )

    val locations = listOf(
        "Cercanos", "En la ciudad", "En el departamento", "En el país"
    )

    val priceRanges = listOf("Gratuito", "Economico", "Moderado", "Costoso")

    val ratings = listOf("1★", "2★", "3★", "4★", "5★")

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
                    options = categories,
                    selectedOptions = selectedCategories,
                    onOptionToggle = { option ->
                        selectedCategories = if (selectedCategories.contains(option)) {
                            selectedCategories - option
                        } else {
                            selectedCategories + option
                        }
                    }
                )
            }

            item { HorizontalDivider(color = Color(0xFFEEEEEE)) }

            item {
                FilterSection(
                    title = "Ubicación",
                    options = locations,
                    selectedOptions = selectedLocations,
                    onOptionToggle = { option ->
                        selectedLocations = if (selectedLocations.contains(option)) {
                            selectedLocations - option
                        } else {
                            selectedLocations + option
                        }
                    }
                )
            }

            item { HorizontalDivider(color = Color(0xFFEEEEEE)) }

            item {
                FilterSection(
                    title = "Rango de Precios",
                    options = priceRanges,
                    selectedOptions = selectedPrices,
                    onOptionToggle = { option ->
                        selectedPrices = if (selectedPrices.contains(option)) {
                            selectedPrices - option
                        } else {
                            selectedPrices + option
                        }
                    }
                )
            }

            item { HorizontalDivider(color = Color(0xFFEEEEEE)) }

            item {
                FilterSection(
                    title = "Calificación Mínima",
                    options = ratings,
                    selectedOptions = selectedRatings,
                    onOptionToggle = { option ->
                        selectedRatings = if (selectedRatings.contains(option)) {
                            selectedRatings - option
                        } else {
                            selectedRatings + option
                        }
                    }
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
