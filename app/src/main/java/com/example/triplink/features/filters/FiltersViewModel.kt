package com.example.triplink.features.filters

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.UbicacionFiltro
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FiltersViewModel @Inject constructor() : ViewModel() {
    var selectedCategories by mutableStateOf(setOf<Categoria>())
    var selectedLocations by mutableStateOf(setOf<UbicacionFiltro>())
    var selectedPrices by mutableStateOf(setOf<RangoPrecios>())
    var selectedRatings by mutableStateOf(setOf<Int>())

    val categories = Categoria.entries

    val locations = UbicacionFiltro.entries

    val priceRanges = RangoPrecios.entries

    val ratings = (1..5).toList()

    fun toggleCategory(category: Categoria) {
        selectedCategories = if (selectedCategories.contains(category)) {
            selectedCategories - category
        } else {
            selectedCategories + category
        }
    }

    fun toggleLocation(location: UbicacionFiltro) {
        selectedLocations = if (selectedLocations.contains(location)) {
            selectedLocations - location
        } else {
            selectedLocations + location
        }
    }

    fun togglePrice(price: RangoPrecios) {
        selectedPrices = if (selectedPrices.contains(price)) {
            selectedPrices - price
        } else {
            selectedPrices + price
        }
    }

    fun toggleRating(rating: Int) {
        selectedRatings = if (selectedRatings.contains(rating)) {
            selectedRatings - rating
        } else {
            selectedRatings + rating
        }
    }

    fun clearFilters() {
        selectedCategories = setOf()
        selectedLocations = setOf()
        selectedPrices = setOf()
        selectedRatings = setOf()
    }

    fun hasActiveFilters(): Boolean {
        return selectedCategories.isNotEmpty() ||
               selectedLocations.isNotEmpty() ||
               selectedPrices.isNotEmpty() ||
               selectedRatings.isNotEmpty()
    }
}