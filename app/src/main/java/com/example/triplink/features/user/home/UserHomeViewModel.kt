package com.example.triplink.features.user.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserHomeViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    var selectedTabIndex by mutableIntStateOf(0)
        private set

    private val _favoriteToggleResult = MutableStateFlow<RequestResult?>(null)
    val favoriteToggleResult: StateFlow<RequestResult?> = _favoriteToggleResult.asStateFlow()

    val puntoInteres: List<PuntoInteres>
        get() = repository.homePublications()

    fun selectTab(index: Int) {
        selectedTabIndex = index
    }

    fun toggleFavorite(userId: String, publicationId: String) {
        viewModelScope.launch {
            try {
                val wasSaved = repository.toggleFavorite(userId, publicationId)
                if (wasSaved) {
                    val isFavorite = repository.isFavorite(userId, publicationId)
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
        return repository.isFavorite(userId, publicationId)
    }

    fun clearFavoriteResult() {
        _favoriteToggleResult.value = null
    }
}
