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
        store.setPublications(store.publications.value + publication)
        store.comments[publication.id] = mutableListOf()
        return true
    }

    override fun updatePuntoInteres(publication: PuntoInteres): Boolean {
        val index = store.publications.value.indexOfFirst { it.id == publication.id }
        if (index == -1) return false

        val updated = store.publications.value.toMutableList()
        updated[index] = publication
        store.setPublications(updated)
        return true
    }

    override fun deletePublicationById(publicationId: String): Boolean {
        val initialSize = store.publications.value.size
        store.setPublications(store.publications.value.filter { it.id != publicationId })
        store.comments.remove(publicationId)
        return store.publications.value.size < initialSize
    }

    override fun getUserPublications(userId: String): List<PuntoInteres> {
        return store.publications.value.filter { it.usuarioAutorId.equals(userId, ignoreCase = true) }
    }

    override fun getPublicationsByState(estado: EstadoPublicacion): List<PuntoInteres> {
        return store.publications.value.filter { it.estado == estado }
    }
}

