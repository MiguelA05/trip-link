package com.example.triplink.domain.repository.publication

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.EstadoPublicacion
import kotlinx.coroutines.flow.StateFlow

interface PublicationRepository {
    val publications: StateFlow<List<PuntoInteres>>

    fun homePublications(): List<PuntoInteres>
    fun explorePublications(): List<PuntoInteres>
    fun getPublicationById(publicationId: String): PuntoInteres?
    fun savePuntoInteres(publication: PuntoInteres): Boolean
    fun updatePuntoInteres(publication: PuntoInteres): Boolean
    fun deletePublicationById(publicationId: String): Boolean
    fun getUserPublications(userId: String): List<PuntoInteres>
    fun getPublicationsByState(estado: EstadoPublicacion): List<PuntoInteres>
}

