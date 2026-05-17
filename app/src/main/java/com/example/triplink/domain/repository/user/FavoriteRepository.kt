package com.example.triplink.domain.repository.user

import com.example.triplink.domain.model.PuntoInteres

interface FavoriteRepository {
    suspend fun toggleFavorite(userId: String, publicationId: String): Boolean
    suspend fun getFavoritePublications(userId: String): List<PuntoInteres>
    suspend fun isFavorite(userId: String, publicationId: String): Boolean
}