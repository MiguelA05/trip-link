package com.example.triplink.domain.repository.user

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.EstadoPublicacion
import kotlinx.coroutines.flow.StateFlow

interface PublicationRepository {
    val publications: StateFlow<List<PuntoInteres>>

    fun homePublications(): List<PuntoInteres>
    fun explorePublications(): List<PuntoInteres>
    fun getPublicationById(publicationId: String): PuntoInteres?
    suspend fun savePuntoInteres(publication: PuntoInteres): Boolean
    suspend fun updatePuntoInteres(publication: PuntoInteres): Boolean
    suspend fun deletePublicationById(publicationId: String): Boolean
    fun getUserPublications(userId: String): List<PuntoInteres>
    fun getPublicationsByState(estado: EstadoPublicacion): List<PuntoInteres>
}