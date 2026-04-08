package com.example.triplink.features.publicationDetails

import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PublicationDetailsViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    fun getPublicationById(publicationId: String): PuntoInteres? {
        return repository.getPublicationById(publicationId)
    }
}