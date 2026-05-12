package com.example.triplink.data.repository.remote.user

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.repository.user.PublicationRepository
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
        // Prefer in-memory snapshot for fast access. The store keeps a real-time StateFlow synced with Firestore.
        return store.publications.value.firstOrNull { it.id == publicationId }
    }

    override suspend fun savePuntoInteres(publication: PuntoInteres): Boolean {
        // ensure counts reflect current state: comments length and persisted favorite count (likely 0 on new)
        val computed = publication.copy(
            commentCount = publication.comments.size,
            favoriteCount = store.favoriteCountForPublication(publication.id)
        )
        return store.savePublication(computed)
    }

    override suspend fun updatePuntoInteres(publication: PuntoInteres): Boolean {
        val computed = publication.copy(
            commentCount = if (publication.comments.isEmpty()) publication.commentCount else publication.comments.size,
            favoriteCount = store.favoriteCountForPublication(publication.id)
        )
        return store.updatePublication(computed)
    }

    override suspend fun deletePublicationById(publicationId: String): Boolean {
        val deleted = store.deletePublication(publicationId)
        // remove favorites references (store operation)
        store.removePublicationFromFavorites(publicationId)
        return deleted
    }

    override fun getUserPublications(userId: String): List<PuntoInteres> {
        return store.userPublications(userId)
    }

    override fun getPublicationsByState(estado: EstadoPublicacion): List<PuntoInteres> {
        return store.publicationsByState(estado)
    }
}

