package com.example.triplink.data.repository.user

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.repository.favorite.FavoriteRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val store: UserRepositoryStore
) : FavoriteRepository {

    override fun toggleFavorite(userId: String, publicationId: String): Boolean {
        if (store.publications.value.none { it.id == publicationId }) return false

        val toggled = store.toggleFavorite(userId, publicationId)
        val favoriteCount = store.favoriteCountForPublication(publicationId)
        val updatedPublications = store.publications.value.map { publication ->
            if (publication.id == publicationId) publication.copy(favoriteCount = favoriteCount) else publication
        }
        store.setPublications(updatedPublications)
        return toggled
    }

    override fun getFavoritePublications(userId: String): List<PuntoInteres> {
        val favorites = store.favoritePublicationIds(userId)
        return store.publications.value.filter { it.id in favorites }
    }

    override fun isFavorite(userId: String, publicationId: String): Boolean {
        return store.isFavorite(userId, publicationId)
    }
}

