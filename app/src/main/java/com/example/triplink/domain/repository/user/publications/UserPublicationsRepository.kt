package com.example.triplink.domain.repository.user.publications

import com.example.triplink.domain.model.PuntoInteres

interface UserPublicationsRepository {
    val publications: List<PuntoInteres>
    fun homePublications(): List<PuntoInteres>
    fun explorePublications(): List<PuntoInteres>
    fun getPublicationById(publicationId: String): PuntoInteres?
}

