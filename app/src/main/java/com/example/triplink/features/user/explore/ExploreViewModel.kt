package com.example.triplink.features.user.explore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.core.utils.toRatingLabel
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.UbicacionFiltro
import com.example.triplink.features.filters.FiltersStore
import com.example.triplink.features.filters.publicationMatchesFilters
import com.example.triplink.domain.repository.publication.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val filtersStore: FiltersStore
) : ViewModel() {

    val publications: StateFlow<List<PuntoInteres>> = publicationRepository.publications

    var query by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf<Categoria?>(null)
        private set

    var selectedTabIndex by mutableIntStateOf(1)
        private set

    val appliedFilters = filtersStore.appliedFilters

    val categories = listOf(
        Categoria.GASTRONOMIA,
        Categoria.ENTRETENIMIENTO,
        Categoria.NATURALEZA,
        Categoria.CULTURA
    )

    fun filteredPuntoInteres(source: List<PuntoInteres>): List<PuntoInteres> {
        val applied = appliedFilters.value
        return source.filter { publication ->
            val isVisible = publication.estado == EstadoPublicacion.VERIFICADA
            val categoryMatches = selectedCategory == null || publication.categoria == selectedCategory

            isVisible && categoryMatches && publicationMatchesFilters(publication, applied, query)
        }
    }

    fun onQueryChange(newValue: String) {
        query = newValue
    }

    fun onCategorySelected(category: Categoria?) {
        selectedCategory = category
    }

    fun ratingLabelForPublication(publication: PuntoInteres): String {
        val average = publication.comments.map { it.rating }.average().takeIf { !it.isNaN() } ?: 0.0
        return average.toRatingLabel()
    }

    fun onBottomTabSelected(index: Int) {
        selectedTabIndex = index
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
