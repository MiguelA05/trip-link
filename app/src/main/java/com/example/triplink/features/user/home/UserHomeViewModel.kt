package com.example.triplink.features.user.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.enums.Categoria

class UserHomeViewModel : ViewModel() {

    var selectedTabIndex by mutableIntStateOf(0)
        private set

    private val _puntoInteres = mutableStateListOf(
        PuntoInteres(
            id = "1",
            titulo = "Valle del Cocora",
            informacion = "Paisajes de palmas y senderos en el Quindio",
            usuarioAutorId = "Laura Gomez",
            categoria = Categoria.NATURALEZA,
            ubicacion = Ubicacion(4.6383, -75.4964, "Salento, Quindio"),
            fotos = listOf("https://visitmycolombia.com/wp-content/uploads/2024/01/bosque-de-palmas-valle-de-cocora-1536x864.jpg")
        ),
        PuntoInteres(
            id = "2",
            titulo = "Cafe de Origen Quindio",
            informacion = "Cafe especial de la region",
            usuarioAutorId = "Martin Ruiz",
            categoria = Categoria.GASTRONOMIA,
            ubicacion = Ubicacion(4.5339, -75.6811, "Armenia, Quindio"),
            fotos = listOf("https://images.unsplash.com/photo-1509042239860-f550ce710b93?q=80&w=1200&auto=format&fit=crop")
        )
    )
    val puntoInteres: List<PuntoInteres> get() = _puntoInteres

    fun selectTab(index: Int) {
        selectedTabIndex = index
    }

    fun toggleFavorite(publicationId: String) {
        // Placeholder visual: el modelo de dominio actual no expone estado de favoritos.
    }
}

