package com.example.triplink.features.user.exploreMap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.repository.publication.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
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
    private val publicationRepository: PublicationRepository
) : ViewModel() {

    var query by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf<Categoria?>(null)
        private set

    val categories = listOf(
        Categoria.GASTRONOMIA,
        Categoria.ENTRETENIMIENTO,
        Categoria.NATURALEZA,
        Categoria.CULTURA
    )

    private val allPublications: List<PuntoInteres>
        get() = publicationRepository.explorePublications()

    private val filteredPublications: List<PuntoInteres>
        get() {
            val normalizedQuery = query.trim().lowercase(Locale.ROOT)
            return allPublications.filter { publication ->
                val categoryMatches = selectedCategory == null || publication.categoria == selectedCategory

                val queryMatches = normalizedQuery.isBlank() ||
                    publication.titulo.lowercase(Locale.ROOT).contains(normalizedQuery) ||
                    publication.ubicacion.ciudad.lowercase(Locale.ROOT).contains(normalizedQuery) ||
                    publication.categoria.label.lowercase(Locale.ROOT).contains(normalizedQuery)

                categoryMatches && queryMatches
            }
        }

    var selectedPublicationId by mutableStateOf(allPublications.firstOrNull()?.id.orEmpty())
        private set

    val selectedPublication: PuntoInteres
        get() = filteredPublications.firstOrNull { it.id == selectedPublicationId }
            ?: filteredPublications.firstOrNull()
            ?: allPublications.first()

    val markers: List<MapMarkerUi>
        get() = filteredPublications.mapIndexed { index, publication ->
            val x = 0.18f + ((index % 3) * 0.26f)
            val y = 0.28f + ((index / 3) * 0.22f)
            MapMarkerUi(
                id = publication.id,
                xFraction = x.coerceIn(0.12f, 0.86f),
                yFraction = y.coerceIn(0.20f, 0.78f),
                ratingLabel = ratingLabelFor(index),
                highlighted = publication.id == selectedPublicationId
            )
        }

    val selectedMarkerRatingLabel: String
        get() = markers.firstOrNull { it.id == selectedPublicationId }?.ratingLabel ?: "4.5"

    fun onQueryChange(newValue: String) {
        query = newValue
        keepValidSelection()
    }

    fun onCategorySelected(category: Categoria?) {
        selectedCategory = category
        keepValidSelection()
    }

    fun onMarkerSelected(markerId: String) {
        selectedPublicationId = markerId
    }

    private fun keepValidSelection() {
        if (filteredPublications.none { it.id == selectedPublicationId }) {
            selectedPublicationId = filteredPublications.firstOrNull()?.id.orEmpty()
        }
    }

    private fun ratingLabelFor(index: Int): String {
        val ratings = listOf("4.5", "4.8", "4.2", "4.0", "3.9")
        return ratings[index % ratings.size]
    }
}