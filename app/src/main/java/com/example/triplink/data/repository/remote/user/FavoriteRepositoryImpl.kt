package com.example.triplink.data.repository.remote.user

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.repository.user.FavoriteRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val store: UserRepositoryStore
) : FavoriteRepository {

    override suspend fun toggleFavorite(userId: String, publicationId: String): Boolean {
        // Delegate entirely to the store - let it decide existence and perform the toggle with Firestore
        return store.toggleFavorite(userId, publicationId)
    }

    override fun getFavoritePublications(userId: String): List<PuntoInteres> {
        val favorites = store.favoritePublicationIds(userId)
        return store.publications.value.filter { it.id in favorites }
    }

    override fun isFavorite(userId: String, publicationId: String): Boolean {
        return store.isFavorite(userId, publicationId)
    }
}

