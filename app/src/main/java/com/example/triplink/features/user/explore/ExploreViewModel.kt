package com.example.triplink.features.user.explore

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.core.utils.toRatingLabel
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.UbicacionFiltro
import com.example.triplink.features.filters.FiltersStore
import com.example.triplink.features.filters.publicationMatchesFilters
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.repository.user.UserProfileRepository
import com.example.triplink.core.utils.FirebaseAuthPersistenceManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.example.triplink.domain.repository.user.PublicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val TAG = "ExploreViewModel"

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val filtersStore: FiltersStore,
    publicationRepository: PublicationRepository,
    private val userProfileRepository: UserProfileRepository,
    private val authPersistence: FirebaseAuthPersistenceManager
) : ViewModel() {

    private val _userLocation = MutableStateFlow<Ubicacion?>(null)
    val userLocation = _userLocation.asStateFlow()

    val publications: StateFlow<List<PuntoInteres>> = publicationRepository.publications

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Categoria?>(null)
    val selectedCategory: StateFlow<Categoria?> = _selectedCategory.asStateFlow()

    var selectedTabIndex by mutableIntStateOf(1)
        private set

    val appliedFilters = filtersStore.appliedFilters

    val categories = listOf(
        Categoria.GASTRONOMIA,
        Categoria.ENTRETENIMIENTO,
        Categoria.NATURALEZA,
        Categoria.CULTURA
    )

    // Flujo reactivo que combina publications, appliedFilters, selectedCategory, query y userLocation
    val filteredPublications: StateFlow<List<PuntoInteres>> = combine(
        publications,
        appliedFilters,
        _selectedCategory,
        _query,
        _userLocation
    ) { pubs, filters, category, query, userLoc ->
        pubs.filter { publication ->
            val isVisible = publication.estado == EstadoPublicacion.VERIFICADA
            val categoryMatches = category == null || publication.categoria == category
            isVisible && categoryMatches && publicationMatchesFilters(publication, filters, query, userLoc)
        }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, emptyList())

    fun onQueryChange(newValue: String) {
        _query.value = newValue
    }

    fun onCategorySelected(category: Categoria?) {
        _selectedCategory.value = category
    }

    init {
        Log.d(TAG, "ExploreViewModel init: Starting user location resolution")
        // Try to resolve the registered user's location from cached auth + user profile
        viewModelScope.launch {
            try {
                var userIdentifier: String? = null

                // 1) Try cached auth from DataStore
                val cachedState = authPersistence.authCacheFlow.first()
                Log.d(TAG, "authCacheFlow result: $cachedState")
                if (cachedState != null) {
                    userIdentifier = cachedState.email.ifBlank { cachedState.uid }
                }

                // 2) Try FirebaseAuth current state
                val currentState = authPersistence.getCurrentAuthState()
                Log.d(TAG, "getCurrentAuthState result: $currentState")
                if (userIdentifier.isNullOrBlank() && currentState != null) {
                    userIdentifier = currentState.email.ifBlank { currentState.uid }
                }

                // 3) Fallback to FirebaseAuth directly
                if (userIdentifier.isNullOrBlank()) {
                    Log.d(TAG, "Fallback: checking FirebaseAuth.currentUser directly")
                    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        Log.d(TAG, "FirebaseAuth.currentUser found: ${currentUser.email}")
                        userIdentifier = currentUser.email ?: currentUser.uid
                    }
                }

                if (!userIdentifier.isNullOrBlank()) {
                    Log.d(TAG, "Fetching user with identifier: $userIdentifier")
                    val user = userProfileRepository.getUserById(userIdentifier)
                    Log.d(TAG, "User fetched: ${user?.email}, ubicacion: ${user?.ubicacion}")
                    _userLocation.value = user?.ubicacion
                    if (user?.ubicacion != null) {
                        Log.i(TAG, "User location resolved: lat=${user.ubicacion.latitud}, lng=${user.ubicacion.longitud}, ciudad=${user.ubicacion.ciudad}")
                    } else {
                        Log.w(TAG, "User location is null")
                    }
                } else {
                    Log.w(TAG, "No auth state or Firebase user found")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error resolving user location: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }

    fun ratingLabelForPublication(publication: PuntoInteres): String {
        val average = publication.comments.map { it.rating }.average().takeIf { !it.isNaN() } ?: 0.0
        return average.toRatingLabel()
    }


    fun removeAppliedCategory(category: Categoria) {
        filtersStore.removeCategory(category)
    }

    fun removeAppliedLocation(location: UbicacionFiltro) {
        filtersStore.removeLocation(location)
    }

    fun removeAppliedPrice(price: RangoPrecios) {
        filtersStore.removePrice(price)
    }

    fun removeAppliedRating(rating: Int) {
        filtersStore.removeRating(rating)
    }
}
