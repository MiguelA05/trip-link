package com.example.triplink.domain.repository.user

import com.example.triplink.domain.model.PuntoInteres

interface FavoriteRepository {
    suspend fun toggleFavorite(userId: String, publicationId: String): Boolean
    fun getFavoritePublications(userId: String): List<PuntoInteres>
    fun isFavorite(userId: String, publicationId: String): Boolean
}