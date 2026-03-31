package com.example.triplink.features.user.explore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.enums.Categoria
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
            titulo = "Valle del Cocora",
            informacion = "Paisajes de palmas en el Quindio",
            usuarioAutorId = "Laura Gomez",
            categoria = Categoria.NATURALEZA,
            ubicacion = Ubicacion(4.6383, -75.4964, "Salento, Quindio"),
            fotos = listOf("https://visitmycolombia.com/wp-content/uploads/2024/01/bosque-de-palmas-valle-de-cocora-1536x864.jpg")
        ),
		PuntoInteres(
			id = "2",
			titulo = "Parque del Cafe",
			informacion = "Parque tematico con atracciones",
			usuarioAutorId = "Miguel Mira",
			categoria = Categoria.ENTRETENIMIENTO,
			ubicacion = Ubicacion(4.5666, -75.7519, "Montenegro, Quindio"),
			fotos = listOf("https://images.unsplash.com/photo-1554118811-1e0d58224f24?q=80&w=1200&auto=format&fit=crop")
		),
		PuntoInteres(
			id = "3",
			titulo = "Cafe de Origen Quindio",
			informacion = "Experiencia de cafe local",
			usuarioAutorId = "Pedro Sanchez",
			categoria = Categoria.GASTRONOMIA,
			ubicacion = Ubicacion(4.5339, -75.6811, "Armenia, Quindio"),
			fotos = listOf("https://images.unsplash.com/photo-1509042239860-f550ce710b93?q=80&w=1200&auto=format&fit=crop")
		)
	)

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
