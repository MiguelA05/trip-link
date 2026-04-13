package com.example.triplink.core.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.triplink.R
import com.example.triplink.core.localization.localizedLabelOrAll
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.ui.theme.TextTokens


@Composable
fun <T> CategoryChips(
    categories: List<T>,
    selectedCategory: T,
    onCategorySelected: (T) -> Unit,
    label: @Composable (T) -> String,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = label(category),
                        style = TextTokens.chip()
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}

@Composable
fun CategoryChips(
    categories: List<Categoria>,
    selectedCategory: Categoria?,
    onCategorySelected: (Categoria?) -> Unit,
    modifier: Modifier = Modifier,
    appliedChips: List<AppliedFilterChipUi> = emptyList()
) {
    // "Todos" debe estar deseleccionado si hay filtros aplicados
    val shouldTodosBeSelected = appliedChips.isEmpty() && selectedCategory == null

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        // Mostrar chips aplicados primero
        items(appliedChips, key = { it.key }) { chip ->
            FilterChip(
                selected = false,
                onClick = chip.onRemove,
                label = {
                    Text(
                        text = chip.label,
                        style = TextTokens.chip()
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(R.string.feature_filters_remove_chip_content_description),
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

        // Mostrar chip "Todos" + categorías regulares
        items(listOf<Categoria?>(null) + categories, key = { it?.name ?: "all" }) { category ->
            val isSelected = if (category == null) shouldTodosBeSelected else category == selectedCategory

            FilterChip(
                selected = isSelected,
                onClick = {
                    when {
                        category == null && appliedChips.isNotEmpty() -> {
                            // Si clickean "Todos" con filtros aplicados, limpiar todos los filtros
                            appliedChips.forEach { it.onRemove() }
                        }
                        else -> onCategorySelected(category)
                    }
                },
                label = {
                    Text(
                        text = category.localizedLabelOrAll(),
                        style = TextTokens.chip()
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}

