package com.example.triplink.features.user.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserHomeViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    var selectedTabIndex by mutableIntStateOf(0)
        private set

    val puntoInteres: List<PuntoInteres>
        get() = repository.homePublications()

    fun selectTab(index: Int) {
        selectedTabIndex = index
    }

    fun toggleFavorite(publicationId: String) {
        // Placeholder visual: el modelo de dominio actual no expone estado de favoritos.
    }
}
