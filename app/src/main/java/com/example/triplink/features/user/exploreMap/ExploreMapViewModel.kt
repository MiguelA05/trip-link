package com.example.triplink.features.user.exploreMap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.core.utils.toRatingLabel
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.UbicacionFiltro
import com.example.triplink.domain.repository.publication.PublicationRepository
import com.example.triplink.features.filters.FiltersStore
import com.example.triplink.features.filters.publicationMatchesFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapMarkerUi(
    val id: String,
    val xFraction: Float,
    val yFraction: Float,
    val ratingLabel: String,
    val highlighted: Boolean = false
)

@HiltViewModel
class ExploreMapViewModel @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val filtersStore: FiltersStore
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Categoria?>(null)
    val selectedCategory: StateFlow<Categoria?> = _selectedCategory.asStateFlow()

    private val publications = publicationRepository.publications

    val appliedFilters: StateFlow<com.example.triplink.features.filters.AppliedFilters> = filtersStore.appliedFilters

    val categories = listOf(
        Categoria.GASTRONOMIA,
        Categoria.ENTRETENIMIENTO,
        Categoria.NATURALEZA,
        Categoria.CULTURA
    )

    // Flujo reactivo que combina publicaciones, appliedFilters, selectedCategory y query
    val filteredPublications: StateFlow<List<PuntoInteres>> = combine(
        publications,
        filtersStore.appliedFilters,
        _selectedCategory,
        _query
    ) { pubs, filters, category, query ->
        pubs.filter { publication ->
            val isVisible = publication.estado == EstadoPublicacion.VERIFICADA
            val categoryMatches = category == null || publication.categoria == category
            isVisible && categoryMatches && publicationMatchesFilters(publication, filters, query)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _selectedPublicationId = MutableStateFlow("")

    val selectedPublication: StateFlow<PuntoInteres?> = combine(
        filteredPublications,
        _selectedPublicationId,
        publications
    ) { filtered, selectedId, allPublications ->
        filtered.firstOrNull { it.id == selectedId }
            ?: filtered.firstOrNull()
            ?: allPublications.firstOrNull { it.estado == EstadoPublicacion.VERIFICADA }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val markers: StateFlow<List<MapMarkerUi>> = combine(
        filteredPublications,
        _selectedPublicationId
    ) { filtered, selectedId ->
        filtered.mapIndexed { index, publication ->
            val x = 0.18f + ((index % 3) * 0.26f)
            val y = 0.28f + ((index / 3) * 0.22f)
            MapMarkerUi(
                id = publication.id,
                xFraction = x.coerceIn(0.12f, 0.86f),
                yFraction = y.coerceIn(0.20f, 0.78f),
                ratingLabel = averageRatingLabelFor(publication),
                highlighted = publication.id == selectedId
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val selectedMarkerRatingLabel: String
        get() = selectedPublication.value?.let(::averageRatingLabelFor) ?: "0.0"

    val selectedPublicationReviewCount: Int
        get() = selectedPublication.value?.commentCount ?: 0

    fun onQueryChange(newValue: String) {
        _query.value = newValue
        keepValidSelection()
    }

    fun onCategorySelected(category: Categoria?) {
        _selectedCategory.value = category
        keepValidSelection()
    }

    fun onMarkerSelected(markerId: String) {
        _selectedPublicationId.value = markerId
    }

    private fun keepValidSelection(filtered: List<PuntoInteres> = filteredPublications.value) {
        val nextSelectionId = filtered.firstOrNull { it.id == _selectedPublicationId.value }?.id
            ?: filtered.firstOrNull()?.id
            ?: ""

        if (nextSelectionId != _selectedPublicationId.value) {
            _selectedPublicationId.value = nextSelectionId
        }
    }

    private fun averageRatingLabelFor(publication: PuntoInteres): String {
        val average = publication.comments.map { it.rating }.average().takeIf { !it.isNaN() } ?: 0.0
        return average.toRatingLabel()
    }

    fun removeAppliedCategory(category: Categoria) {
        filtersStore.removeCategory(category)
        keepValidSelection()
    }

    fun removeAppliedLocation(location: UbicacionFiltro) {
        filtersStore.removeLocation(location)
        keepValidSelection()
    }

    fun removeAppliedPrice(price: RangoPrecios) {
        filtersStore.removePrice(price)
        keepValidSelection()
    }

    fun removeAppliedRating(rating: Int) {
        filtersStore.removeRating(rating)
        keepValidSelection()
    }

    init {
        viewModelScope.launch {
            filteredPublications.collect { filtered ->
                keepValidSelection(filtered)
            }
        }
    }
}