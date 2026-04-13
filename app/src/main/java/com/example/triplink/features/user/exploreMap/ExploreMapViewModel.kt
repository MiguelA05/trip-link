package com.example.triplink.features.user.exploreMap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.core.utils.toRatingLabel
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.UbicacionFiltro
import com.example.triplink.domain.repository.publication.PublicationRepository
import com.example.triplink.features.filters.FiltersStore
import com.example.triplink.features.filters.publicationMatchesFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    var query by mutableStateOf("")
        private set

    private val _selectedCategory = MutableStateFlow<Categoria?>(null)
    val selectedCategory: StateFlow<Categoria?> = _selectedCategory.asStateFlow()

    val appliedFilters: StateFlow<com.example.triplink.features.filters.AppliedFilters> = filtersStore.appliedFilters

    val categories = listOf(
        Categoria.GASTRONOMIA,
        Categoria.ENTRETENIMIENTO,
        Categoria.NATURALEZA,
        Categoria.CULTURA
    )

    private val allPublications: List<PuntoInteres>
        get() = publicationRepository.explorePublications()

    // Flujo reactivo que combina appliedFilters, selectedCategory y query
    val filteredPublications: StateFlow<List<PuntoInteres>> = combine(
        filtersStore.appliedFilters,
        _selectedCategory
    ) { filters, category ->
        allPublications.filter { publication ->
            val categoryMatches = category == null || publication.categoria == category
            categoryMatches && publicationMatchesFilters(publication, filters, query)
        }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, emptyList())

    var selectedPublicationId by mutableStateOf(allPublications.firstOrNull()?.id.orEmpty())
        private set

    val selectedPublication: PuntoInteres?
        get() = filteredPublications.value.firstOrNull { it.id == selectedPublicationId }
            ?: filteredPublications.value.firstOrNull()
            ?: allPublications.firstOrNull()

    val markers: List<MapMarkerUi>
        get() = filteredPublications.value.mapIndexed { index, publication ->
            val x = 0.18f + ((index % 3) * 0.26f)
            val y = 0.28f + ((index / 3) * 0.22f)
            MapMarkerUi(
                id = publication.id,
                xFraction = x.coerceIn(0.12f, 0.86f),
                yFraction = y.coerceIn(0.20f, 0.78f),
                ratingLabel = averageRatingLabelFor(publication),
                highlighted = publication.id == selectedPublicationId
            )
        }

    val selectedMarkerRatingLabel: String
        get() = selectedPublication?.let(::averageRatingLabelFor) ?: "0.0"

    val selectedPublicationReviewCount: Int
        get() = selectedPublication?.commentCount ?: 0

    fun onQueryChange(newValue: String) {
        query = newValue
        keepValidSelection()
    }

    fun onCategorySelected(category: Categoria?) {
        _selectedCategory.value = category
        keepValidSelection()
    }

    fun onMarkerSelected(markerId: String) {
        selectedPublicationId = markerId
    }

    private fun keepValidSelection() {
        if (filteredPublications.value.none { it.id == selectedPublicationId }) {
            selectedPublicationId = filteredPublications.value.firstOrNull()?.id.orEmpty()
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
}