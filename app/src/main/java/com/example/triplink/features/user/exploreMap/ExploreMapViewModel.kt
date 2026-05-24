package com.example.triplink.features.user.exploreMap

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.core.utils.toRatingLabel
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.UbicacionFiltro
import com.example.triplink.domain.repository.user.PublicationRepository
import com.example.triplink.features.filters.FiltersStore
import com.example.triplink.features.filters.publicationMatchesFilters
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.repository.user.UserProfileRepository
import com.example.triplink.core.utils.FirebaseAuthPersistenceManager
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val TAG = "ExploreMapViewModel"

data class MapMarkerUi(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val ratingLabel: String,
    val highlighted: Boolean = false
)

@HiltViewModel
class ExploreMapViewModel @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val filtersStore: FiltersStore,
    private val userProfileRepository: UserProfileRepository,
    private val authPersistence: FirebaseAuthPersistenceManager
) : ViewModel() {

    private val _userLocation = MutableStateFlow<Ubicacion?>(null)
    val userLocation = _userLocation.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Categoria?>(null)
    val selectedCategory: StateFlow<Categoria?> = _selectedCategory.asStateFlow()

    private val publications = publicationRepository.publications

    val appliedFilters: StateFlow<com.example.triplink.features.filters.AppliedFilters> = filtersStore.appliedFilters

    val categories = listOf(
        Categoria.GASTRONOMIA,
        Categoria.ENTRETENIMIENTO,
        Categoria.NATURALEZA,
        Categoria.CULTURA
    )

    // Flujo reactivo que combina publicaciones, appliedFilters, selectedCategory, query y userLocation
    val filteredPublications: StateFlow<List<PuntoInteres>> = combine(
        publications,
        filtersStore.appliedFilters,
        _selectedCategory,
        _query,
        _userLocation
    ) { pubs, filters, category, query, userLoc ->
        pubs.filter { publication ->
            val isVisible = publication.estado == EstadoPublicacion.VERIFICADA
            val categoryMatches = category == null || publication.categoria == category
            isVisible && categoryMatches && publicationMatchesFilters(publication, filters, query, userLoc)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _selectedPublicationId = MutableStateFlow("")

    val selectedPublication: StateFlow<PuntoInteres?> = combine(
        filteredPublications,
        _selectedPublicationId
    ) { filtered, selectedId ->
        filtered.firstOrNull { it.id == selectedId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val markers: StateFlow<List<MapMarkerUi>> = combine(
        filteredPublications,
        _selectedPublicationId
    ) { filtered, selectedId ->
        filtered.map { publication ->
            MapMarkerUi(
                id = publication.id,
                latitude = publication.ubicacion.latitud,
                longitude = publication.ubicacion.longitud,
                ratingLabel = averageRatingLabelFor(publication),
                highlighted = publication.id == selectedId
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val selectedMarkerRatingLabel: String
        get() = selectedPublication.value?.let(::averageRatingLabelFor) ?: "0.0"

    val selectedPublicationReviewCount: Int
        get() = selectedPublication.value?.commentCount ?: 0

    fun onQueryChange(newValue: String) {
        _query.value = newValue
        keepValidSelection()
    }

    fun onCategorySelected(category: Categoria?) {
        _selectedCategory.value = category
        keepValidSelection()
    }

    fun onMarkerSelected(markerId: String) {
        _selectedPublicationId.value = markerId
    }

    private fun keepValidSelection(filtered: List<PuntoInteres> = filteredPublications.value) {
        val selectedId = _selectedPublicationId.value
        if (selectedId.isNotBlank() && filtered.none { it.id == selectedId }) {
            _selectedPublicationId.value = ""
        }
    }

    private fun averageRatingLabelFor(publication: PuntoInteres): String {
        val average = publication.comments.map { it.rating }.average().takeIf { !it.isNaN() } ?: 0.0
        return average.toRatingLabel()
    }

    fun removeAppliedCategory(category: Categoria) {
        filtersStore.removeCategory(category)
        keepValidSelection()
    }

    fun removeAppliedLocation(location: UbicacionFiltro) {
        filtersStore.removeLocation(location)
        keepValidSelection()
    }

    fun removeAppliedPrice(price: RangoPrecios) {
        filtersStore.removePrice(price)
        keepValidSelection()
    }

    fun removeAppliedRating(rating: Int) {
        filtersStore.removeRating(rating)
        keepValidSelection()
    }

    init {
        Log.d(TAG, "ExploreMapViewModel init: Starting user location resolution")
        viewModelScope.launch {
            try {
                var userEmail: String? = null

                // Try cached auth state first
                var cached = authPersistence.getCurrentAuthState()
                Log.d(TAG, "getCurrentAuthState result: $cached")
                if (cached != null) {
                    userEmail = cached.email
                } else {
                    // Fallback to Firebase Auth directly
                    Log.d(TAG, "Fallback: checking FirebaseAuth.currentUser directly")
                    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        Log.d(TAG, "FirebaseAuth.currentUser found: ${currentUser.email}")
                        userEmail = currentUser.email
                    }
                }

                if (!userEmail.isNullOrBlank()) {
                    Log.d(TAG, "Fetching user with email: $userEmail")
                    val user = userProfileRepository.getUserById(userEmail)
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

            filteredPublications.collect { filtered ->
                keepValidSelection(filtered)
            }
        }
    }
}
