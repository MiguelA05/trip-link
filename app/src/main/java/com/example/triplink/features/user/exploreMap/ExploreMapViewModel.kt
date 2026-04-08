package com.example.triplink.features.user.exploreMap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.enums.Categoria
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class MapMarkerUi(
	val id: String,
	val xFraction: Float,
	val yFraction: Float,
	val ratingLabel: String,
	val highlighted: Boolean = false
)

@HiltViewModel
class ExploreMapViewModel @Inject constructor() : ViewModel() {

	var query by mutableStateOf("")
		private set

	var selectedCategory by mutableStateOf("Gastronomia")
		private set

	val categories = listOf("Gastronomia", "Entretenimiento", "Naturaleza", "Cultura")

	private val _publications = listOf(
		PuntoInteres(
			id = "1",
			titulo = "Valle del Cocora",
			informacion = "El hogar de la palma de cera del Quindio, el arbol nacional de Colombia. Un paisaje surrealista de verdes montanas y niebla.",
			usuarioAutorId = "Laura Gomez",
			categoria = Categoria.NATURALEZA,
			ubicacion = Ubicacion(4.6383, -75.4964, "Salento, Quindio"),
			fotos = listOf("https://visitmycolombia.com/wp-content/uploads/2024/01/bosque-de-palmas-valle-de-cocora-1536x864.jpg")
		)
	)

	var selectedPublicationId by mutableStateOf(_publications.first().id)
		private set

	private var selectedMarkerId by mutableStateOf("m1")

	val selectedPublication: PuntoInteres
		get() = _publications.firstOrNull { it.id == selectedPublicationId } ?: _publications.first()

	private val markerCatalog = listOf(
		MapMarkerUi(id = "m1", xFraction = 0.68f, yFraction = 0.32f, ratingLabel = "4.5"),
		MapMarkerUi(id = "m2", xFraction = 0.18f, yFraction = 0.42f, ratingLabel = "4.8"),
		MapMarkerUi(id = "m3", xFraction = 0.74f, yFraction = 0.58f, ratingLabel = "3.5"),
		MapMarkerUi(id = "m4", xFraction = 0.30f, yFraction = 0.74f, ratingLabel = "4.0")
	)

	val markers: List<MapMarkerUi>
		get() = markerCatalog.map { marker ->
			marker.copy(highlighted = marker.id == selectedMarkerId)
		}

	val selectedMarkerRatingLabel: String
		get() = markers.firstOrNull { it.id == selectedMarkerId }?.ratingLabel ?: "4.5"

	fun onQueryChange(newValue: String) {
		query = newValue
	}

	fun onCategorySelected(category: String) {
		selectedCategory = category
	}

	fun onMarkerSelected(markerId: String) {
		selectedMarkerId = markerId
		selectedPublicationId = "1"
	}
}