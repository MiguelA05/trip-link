package com.example.triplink.features.filters

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FiltersViewModel @Inject constructor() : ViewModel() {
    var selectedCategories by mutableStateOf(setOf<String>())
    var selectedLocations by mutableStateOf(setOf<String>())
    var selectedPrices by mutableStateOf(setOf<String>())
    var selectedRatings by mutableStateOf(setOf<String>())

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

    fun toggleCategory(category: String) {
        selectedCategories = if (selectedCategories.contains(category)) {
            selectedCategories - category
        } else {
            selectedCategories + category
        }
    }

    fun toggleLocation(location: String) {
        selectedLocations = if (selectedLocations.contains(location)) {
            selectedLocations - location
        } else {
            selectedLocations + location
        }
    }

    fun togglePrice(price: String) {
        selectedPrices = if (selectedPrices.contains(price)) {
            selectedPrices - price
        } else {
            selectedPrices + price
        }
    }

    fun toggleRating(rating: String) {
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

    // Función heredada para mantener compatibilidad
    fun cambiarOpcion(opcion: String) {
        toggleCategory(opcion)
    }
}