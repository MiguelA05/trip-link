package com.example.triplink.features.userHome

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.PuntoInteres

class UserHomeViewModel : ViewModel() {

    var selectedTabIndex by mutableIntStateOf(0)
        private set

    private val _puntoInteres = mutableStateListOf(
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
            commentsCount = 34,
            likesCount = 247,
            isFavorite = false
        ),
        PuntoInteres(
            id = "2",
            authorName = "Martin Ruiz",
            authorInitials = "MR",
            timeAgo = "3 horas",
            distance = "1.8 km",
            category = "Cafes Especiales",
            rating = 4.7,
            title = "Cafe de Origen Quindio",
            location = "Armenia, Quindio",
            imageUrl = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?q=80&w=1200&auto=format&fit=crop",
            commentsCount = 12,
            likesCount = 198,
            isFavorite = false
        )
    )
    val puntoInteres: List<PuntoInteres> get() = _puntoInteres

    fun selectTab(index: Int) {
        selectedTabIndex = index
    }

    fun toggleFavorite(publicationId: String) {
        val index = _puntoInteres.indexOfFirst { it.id == publicationId }
        if (index != -1) {
            val publication = _puntoInteres[index]
            val newIsFavorite = !publication.isFavorite
            _puntoInteres[index] = publication.copy(
                isFavorite = newIsFavorite,
                likesCount = if (newIsFavorite) publication.likesCount + 1 else publication.likesCount - 1
            )
        }
    }
}
