package com.example.triplink.domain.repository.user

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.EstadoPublicacion
import kotlinx.coroutines.flow.StateFlow

interface PublicationRepository {
    val publications: StateFlow<List<PuntoInteres>>

    suspend fun homePublications(): List<PuntoInteres>
    suspend fun explorePublications(): List<PuntoInteres>
    suspend fun getPublicationById(publicationId: String): PuntoInteres?
    suspend fun savePuntoInteres(publication: PuntoInteres): Boolean
    suspend fun updatePuntoInteres(publication: PuntoInteres): Boolean
    suspend fun deletePublicationById(publicationId: String): Boolean
    suspend fun getUserPublications(userId: String): List<PuntoInteres>
    suspend fun getPublicationsByState(estado: EstadoPublicacion): List<PuntoInteres>
    // Recalculate denormalized counts (favorites) by querying subcollections remotely.
    // Returns true if the remote document was updated successfully.
    suspend fun recalculateFavoriteCount(publicationId: String): Boolean
}