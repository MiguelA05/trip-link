package com.example.triplink.features.filters

import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.UbicacionFiltro

data class AppliedFilters(
    val categories: Set<Categoria> = emptySet(),
    val locations: Set<UbicacionFiltro> = emptySet(),
    val prices: Set<RangoPrecios> = emptySet(),
    val ratings: Set<Int> = emptySet()
) {
    fun hasActiveFilters(): Boolean =
        categories.isNotEmpty() ||
            locations.isNotEmpty() ||
            prices.isNotEmpty() ||
            ratings.isNotEmpty()
}

