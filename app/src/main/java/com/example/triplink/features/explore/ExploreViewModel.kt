package com.example.triplink.features.explore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.PuntoInteres
import java.util.Locale

class ExploreViewModel : ViewModel() {

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

	private val allPuntoInteres = listOf(
        PuntoInteres(
            id = "1",
            authorName = "Laura Gomez",
            authorInitials = "LG",
            timeAgo = "1 hora",
            distance = "3.2 km",
            category = "Naturaleza y Parques",
            rating = 4.8,
            title = "Valle del Cocora",
            location = "Salento, Quindio",
            imageUrl = "https://visitmycolombia.com/wp-content/uploads/2024/01/bosque-de-palmas-valle-de-cocora-1536x864.jpg",
            commentsCount = 0,
            likesCount = 0,
        ),
		PuntoInteres(
			id = "2",
			title = "Parque del Cafe",
			authorName = "Miguel Mira",
			authorInitials = "MM",
			timeAgo = "3 horas",
			distance = "3.8 Km",
			category = "ENTRETENIMIENTO",
			rating = 4.8,
			location = "Montenegro, Quindio",
			imageUrl = "https://images.unsplash.com/photo-1554118811-1e0d58224f24?q=80&w=1200&auto=format&fit=crop",
			commentsCount = 0,
			likesCount = 0
		),
		PuntoInteres(
			id = "3",
			title = "Cafe de Origen Quindio",
			authorName = "Pedro Sanchez",
			authorInitials = "PS",
			timeAgo = "9 horas",
			distance = "9 Km",
			category = "GASTRONOMIA",
			rating = 4.7,
			location = "Armenia, Quindio",
			imageUrl = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?q=80&w=1200&auto=format&fit=crop",
			commentsCount = 0,
			likesCount = 0
		)
	)

	val filteredPuntoInteres: List<PuntoInteres>
		get() {
			val normalizedQuery = query.trim().lowercase(Locale.ROOT)
			return allPuntoInteres.filter { publication ->
				val categoryMatches = selectedCategory == "Todos" ||
					publication.category.equals(selectedCategory, ignoreCase = true)

				val queryMatches = normalizedQuery.isBlank() ||
					publication.title.lowercase(Locale.ROOT).contains(normalizedQuery) ||
					publication.location.lowercase(Locale.ROOT).contains(normalizedQuery) ||
					publication.category.lowercase(Locale.ROOT).contains(normalizedQuery)

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