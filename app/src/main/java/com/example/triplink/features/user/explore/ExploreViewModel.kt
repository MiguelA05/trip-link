package com.example.triplink.features.user.explore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.repository.publication.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val publicationRepository: PublicationRepository
) : ViewModel() {

    val publications: StateFlow<List<PuntoInteres>> = publicationRepository.publications

    var query by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf<Categoria?>(null)
        private set

    var selectedTabIndex by mutableIntStateOf(1)
        private set

    val categories = listOf(
        Categoria.GASTRONOMIA,
        Categoria.ENTRETENIMIENTO,
        Categoria.NATURALEZA,
        Categoria.CULTURA
    )

    fun filteredPuntoInteres(source: List<PuntoInteres>): List<PuntoInteres> {
        val normalizedQuery = query.trim().lowercase(Locale.ROOT)
        return source.filter { publication ->
            val isVisible = publication.estado == EstadoPublicacion.VERIFICADA
            val categoryMatches = selectedCategory == null || publication.categoria == selectedCategory

            val queryMatches = normalizedQuery.isBlank() ||
                publication.titulo.lowercase(Locale.ROOT).contains(normalizedQuery) ||
                publication.ubicacion.ciudad.lowercase(Locale.ROOT).contains(normalizedQuery) ||
                publication.categoria.label.lowercase(Locale.ROOT).contains(normalizedQuery)

            isVisible && categoryMatches && queryMatches
        }
    }

    fun onQueryChange(newValue: String) {
        query = newValue
    }

    fun onCategorySelected(category: Categoria?) {
        selectedCategory = category
    }

    fun onBottomTabSelected(index: Int) {
        selectedTabIndex = index
    }
}
