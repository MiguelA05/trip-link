package com.example.triplink.data.repository.user

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.repository.publication.PublicationRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PublicationRepositoryImpl @Inject constructor(
    private val store: UserRepositoryStore
) : PublicationRepository {

    override val publications: StateFlow<List<PuntoInteres>> = store.publications

    override fun homePublications(): List<PuntoInteres> = store.publications.value
        .filter { it.estado == EstadoPublicacion.VERIFICADA }
        .take(10)

    override fun explorePublications(): List<PuntoInteres> = store.publications.value
        .filter { it.estado == EstadoPublicacion.VERIFICADA }

    override fun getPublicationById(publicationId: String): PuntoInteres? {
        return store.publications.value.firstOrNull { it.id == publicationId }
    }

    override fun savePuntoInteres(publication: PuntoInteres): Boolean {
        if (getPublicationById(publication.id) != null) return false
        val normalized = publication.copy(commentCount = publication.comments.size)
        store.setPublications(store.publications.value + normalized)
        return true
    }

    override fun updatePuntoInteres(publication: PuntoInteres): Boolean {
        val index = store.publications.value.indexOfFirst { it.id == publication.id }
        if (index == -1) return false

        val updated = store.publications.value.toMutableList()
        val current = updated[index]
        val normalized = publication.copy(
            comments = if (publication.comments.isEmpty() && current.comments.isNotEmpty()) current.comments else publication.comments,
            reportes = if (publication.reportes.isEmpty() && current.reportes.isNotEmpty()) current.reportes else publication.reportes,
            commentCount = if (publication.comments.isEmpty() && current.comments.isNotEmpty()) current.commentCount else publication.comments.size,
            favoriteCount = if (publication.favoriteCount == 0 && current.favoriteCount != 0) current.favoriteCount else publication.favoriteCount
        )
        updated[index] = normalized
        store.setPublications(updated)
        return true
    }

    override fun deletePublicationById(publicationId: String): Boolean {
        val initialSize = store.publications.value.size
        store.setPublications(store.publications.value.filter { it.id != publicationId })
        store.removePublicationFromFavorites(publicationId)
        store.persistState()
        return store.publications.value.size < initialSize
    }

    override fun getUserPublications(userId: String): List<PuntoInteres> {
        return store.publications.value.filter { it.usuarioAutorId.equals(userId, ignoreCase = true) }
    }

    override fun getPublicationsByState(estado: EstadoPublicacion): List<PuntoInteres> {
        return store.publications.value.filter { it.estado == estado }
    }
}

