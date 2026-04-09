package com.example.triplink.features.user.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.repository.favorite.FavoriteRepository
import com.example.triplink.domain.repository.publication.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserHomeViewModel @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    val publications: StateFlow<List<PuntoInteres>> = publicationRepository.publications

    var selectedTabIndex by mutableIntStateOf(0)
        private set

    private val _favoriteToggleResult = MutableStateFlow<RequestResult?>(null)
    val favoriteToggleResult: StateFlow<RequestResult?> = _favoriteToggleResult.asStateFlow()

    fun toHomePublications(source: List<PuntoInteres>): List<PuntoInteres> =
        source.filter { it.estado == EstadoPublicacion.VERIFICADA }

    fun selectTab(index: Int) {
        selectedTabIndex = index
    }

    fun toggleFavorite(userId: String, publicationId: String) {
        viewModelScope.launch {
            try {
                val wasSaved = favoriteRepository.toggleFavorite(userId, publicationId)
                if (wasSaved) {
                    val isFavorite = favoriteRepository.isFavorite(userId, publicationId)
                    val message = if (isFavorite) "Añadido a favoritos" else "Removido de favoritos"
                    _favoriteToggleResult.value = RequestResult.Success(message)
                } else {
                    _favoriteToggleResult.value = RequestResult.Failure("No se pudo actualizar favorito")
                }
            } catch (e: Exception) {
                _favoriteToggleResult.value = RequestResult.Failure("Error: ${e.message}")
            }
        }
    }

    fun isFavorite(userId: String, publicationId: String): Boolean {
        return favoriteRepository.isFavorite(userId, publicationId)
    }

    fun clearFavoriteResult() {
        _favoriteToggleResult.value = null
    }
}
