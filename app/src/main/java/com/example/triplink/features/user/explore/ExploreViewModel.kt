package com.example.triplink.features.user.explore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.core.utils.toRatingLabel
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.UbicacionFiltro
import com.example.triplink.features.filters.FiltersStore
import com.example.triplink.features.filters.publicationMatchesFilters
import com.example.triplink.domain.repository.user.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val filtersStore: FiltersStore,
    publicationRepository: PublicationRepository
) : ViewModel() {

    val publications: StateFlow<List<PuntoInteres>> = publicationRepository.publications

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Categoria?>(null)
    val selectedCategory: StateFlow<Categoria?> = _selectedCategory.asStateFlow()

    var selectedTabIndex by mutableIntStateOf(1)
        private set

    val appliedFilters = filtersStore.appliedFilters

    val categories = listOf(
        Categoria.GASTRONOMIA,
        Categoria.ENTRETENIMIENTO,
        Categoria.NATURALEZA,
        Categoria.CULTURA
    )

    // Flujo reactivo que combina publications, appliedFilters y selectedCategory
    val filteredPublications: StateFlow<List<PuntoInteres>> = combine(
        publications,
        appliedFilters,
        _selectedCategory,
        _query
    ) { pubs, filters, category, query ->
        pubs.filter { publication ->
            val isVisible = publication.estado == EstadoPublicacion.VERIFICADA
            val categoryMatches = category == null || publication.categoria == category
            isVisible && categoryMatches && publicationMatchesFilters(publication, filters, query)
        }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, emptyList())

    fun onQueryChange(newValue: String) {
        _query.value = newValue
    }

    fun onCategorySelected(category: Categoria?) {
        _selectedCategory.value = category
    }

    fun ratingLabelForPublication(publication: PuntoInteres): String {
        val average = publication.comments.map { it.rating }.average().takeIf { !it.isNaN() } ?: 0.0
        return average.toRatingLabel()
    }


    fun removeAppliedCategory(category: Categoria) {
        filtersStore.removeCategory(category)
    }

    fun removeAppliedLocation(location: UbicacionFiltro) {
        filtersStore.removeLocation(location)
    }

    fun removeAppliedPrice(price: RangoPrecios) {
        filtersStore.removePrice(price)
    }

    fun removeAppliedRating(rating: Int) {
        filtersStore.removeRating(rating)
    }
}
