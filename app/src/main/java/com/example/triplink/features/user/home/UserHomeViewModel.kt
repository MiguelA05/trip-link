package com.example.triplink.features.user.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.R
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.toRatingLabel
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.repository.user.FavoriteRepository
import com.example.triplink.domain.repository.user.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserHomeViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val publicationRepository: PublicationRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    val publications: StateFlow<List<PuntoInteres>> = publicationRepository.publications

    private val _favoritePublicationIds = MutableStateFlow<Set<String>>(emptySet())
    val favoritePublicationIds: StateFlow<Set<String>> = _favoritePublicationIds.asStateFlow()

    var selectedTabIndex by mutableIntStateOf(0)
        private set

    private val _favoriteToggleResult = MutableStateFlow<RequestResult?>(null)
    val favoriteToggleResult: StateFlow<RequestResult?> = _favoriteToggleResult.asStateFlow()

    fun toHomePublications(source: List<PuntoInteres>): List<PuntoInteres> =
        source.filter { it.estado == EstadoPublicacion.VERIFICADA }

    fun selectTab(index: Int) {
        selectedTabIndex = index
    }

    fun loadFavoriteIds(userId: String) {
        viewModelScope.launch {
            _favoritePublicationIds.value = favoriteRepository.getFavoritePublications(userId).map { it.id }.toSet()
        }
    }

    fun toggleFavorite(userId: String, publicationId: String) {
        viewModelScope.launch {
            _favoriteToggleResult.value = RequestResult.Loading
            try {
                val wasSaved = favoriteRepository.toggleFavorite(userId, publicationId)
                if (wasSaved) {
                    loadFavoriteIds(userId)
                    val isFavorite = favoriteRepository.isFavorite(userId, publicationId)
                    val message = if (isFavorite) {
                        appContext.getString(R.string.vm_user_home_favorite_added)
                    } else {
                        appContext.getString(R.string.vm_user_home_favorite_removed)
                    }
                    _favoriteToggleResult.value = RequestResult.Success(message)
                } else {
                    _favoriteToggleResult.value = RequestResult.Failure(
                        appContext.getString(R.string.vm_user_home_favorite_update_failed)
                    )
                }
            } catch (e: Exception) {
                _favoriteToggleResult.value = RequestResult.Failure(
                    appContext.getString(R.string.vm_user_home_favorite_error, e.message ?: "")
                )
            }
        }
    }

    fun isFavorite(publicationId: String): Boolean {
        return favoritePublicationIds.value.contains(publicationId)
    }

    fun ratingLabelForPublication(publication: PuntoInteres): String {
        val average = publication.comments.map { it.rating }.average().takeIf { !it.isNaN() } ?: 0.0
        return average.toRatingLabel()
    }

    fun clearFavoriteResult() {
        _favoriteToggleResult.value = null
    }
}
