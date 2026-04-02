package com.example.triplink.features.filters

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class FiltersViewModel: ViewModel() {
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

    fun cambiarOpcion(opcion: String) {
        selectedCategories = if (selectedCategories.contains(opcion)) {
            selectedCategories - opcion
        } else {
            selectedCategories + opcion
        }
    }



}