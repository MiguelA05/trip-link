package com.example.triplink.features.filters

import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.UbicacionFiltro
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class FiltersStore @Inject constructor() {
    private val _appliedFilters = MutableStateFlow(AppliedFilters())
    val appliedFilters: StateFlow<AppliedFilters> = _appliedFilters.asStateFlow()

    fun apply(filters: AppliedFilters) {
        _appliedFilters.value = filters
    }

    fun removeCategory(category: Categoria) {
        _appliedFilters.value = _appliedFilters.value.copy(
            categories = _appliedFilters.value.categories - category
        )
    }

    fun removeLocation(location: UbicacionFiltro) {
        _appliedFilters.value = _appliedFilters.value.copy(
            locations = _appliedFilters.value.locations - location
        )
    }

    fun removePrice(price: RangoPrecios) {
        _appliedFilters.value = _appliedFilters.value.copy(
            prices = _appliedFilters.value.prices - price
        )
    }

    fun removeRating(rating: Int) {
        _appliedFilters.value = _appliedFilters.value.copy(
            ratings = _appliedFilters.value.ratings - rating
        )
    }

    fun clearAll() {
        _appliedFilters.value = AppliedFilters()
    }
}

