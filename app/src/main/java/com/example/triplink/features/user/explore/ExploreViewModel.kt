package com.example.triplink.features.user.explore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.repository.user.publications.UserPublicationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
	private val repository: UserPublicationsRepository
) : ViewModel() {

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

	private val allPuntoInteres: List<PuntoInteres>
		get() = repository.explorePublications()

	val filteredPuntoInteres: List<PuntoInteres>
		get() {
			val normalizedQuery = query.trim().lowercase(Locale.ROOT)
			return allPuntoInteres.filter { publication ->
				val categoryMatches = selectedCategory == "Todos" ||
									publication.categoria.name.equals(selectedCategory, ignoreCase = true)

				val queryMatches = normalizedQuery.isBlank() ||
									publication.titulo.lowercase(Locale.ROOT).contains(normalizedQuery) ||
									publication.ubicacion.ciudad.lowercase(Locale.ROOT).contains(normalizedQuery) ||
									publication.categoria.name.lowercase(Locale.ROOT).contains(normalizedQuery)

				categoryMatches && queryMatches
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
