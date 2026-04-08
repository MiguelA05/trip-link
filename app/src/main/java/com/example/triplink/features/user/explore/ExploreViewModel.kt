package com.example.triplink.features.user.explore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    val publications: StateFlow<List<PuntoInteres>> = repository.publications

    var query by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf("Todos")
        private set

    var selectedTabIndex by mutableIntStateOf(1)
        private set

    val categories = listOf(
        "Todos",
        "Gastronomia",
        "Entretenimiento",
        "Naturaleza",
        "Cultura"
    )

    fun filteredPuntoInteres(source: List<PuntoInteres>): List<PuntoInteres> {
            val normalizedQuery = query.trim().lowercase(Locale.ROOT)
            return source.filter { publication ->
                val isVisible = publication.estado == EstadoPublicacion.VERIFICADA
                val categoryMatches = selectedCategory == "Todos" ||
                    publication.categoria.name.equals(selectedCategory, ignoreCase = true)

                val queryMatches = normalizedQuery.isBlank() ||
                    publication.titulo.lowercase(Locale.ROOT).contains(normalizedQuery) ||
                    publication.ubicacion.ciudad.lowercase(Locale.ROOT).contains(normalizedQuery) ||
                    publication.categoria.name.lowercase(Locale.ROOT).contains(normalizedQuery)

                isVisible && categoryMatches && queryMatches
            }
        }

    fun onQueryChange(newValue: String) {
        query = newValue
    }

    fun onCategorySelected(category: String) {
        selectedCategory = category
    }

    fun onBottomTabSelected(index: Int) {
        selectedTabIndex = index
    }
}
