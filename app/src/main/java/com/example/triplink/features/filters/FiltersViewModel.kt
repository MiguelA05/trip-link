package com.example.triplink.features.filters

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
 class FiltersViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context
) : ViewModel() {
    var selectedCategories by mutableStateOf(setOf<String>())
    var selectedLocations by mutableStateOf(setOf<String>())
    var selectedPrices by mutableStateOf(setOf<String>())
    var selectedRatings by mutableStateOf(setOf<String>())

    val categories = listOf(
        appContext.getString(R.string.vm_filters_category_nature_parks),
        appContext.getString(R.string.vm_filters_category_specialty_coffee),
        appContext.getString(R.string.vm_filters_category_museums_culture),
        appContext.getString(R.string.vm_filters_category_viewpoints),
        appContext.getString(R.string.vm_filters_category_colonial_architecture),
        appContext.getString(R.string.vm_filters_category_typical_restaurants),
        appContext.getString(R.string.vm_filters_category_nightlife),
        appContext.getString(R.string.vm_filters_category_adventure_sports)
    )

    val locations = listOf(
        appContext.getString(R.string.vm_filters_location_nearby),
        appContext.getString(R.string.vm_filters_location_city),
        appContext.getString(R.string.vm_filters_location_department),
        appContext.getString(R.string.vm_filters_location_country)
    )

    val priceRanges = listOf(
        appContext.getString(R.string.vm_filters_price_free),
        appContext.getString(R.string.vm_filters_price_economic),
        appContext.getString(R.string.vm_filters_price_moderate),
        appContext.getString(R.string.vm_filters_price_expensive)
    )

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