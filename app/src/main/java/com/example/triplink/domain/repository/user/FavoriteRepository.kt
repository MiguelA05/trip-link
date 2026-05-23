package com.example.triplink.domain.repository.user

import com.example.triplink.domain.model.PuntoInteres
import kotlinx.coroutines.flow.StateFlow

interface FavoriteRepository {
    // Expose a flow of favorite ids grouped by userId (normalized)
    val favoriteIdsByUser: StateFlow<Map<String, Set<String>>>
    suspend fun toggleFavorite(userId: String, publicationId: String): Boolean
    suspend fun getFavoritePublications(userId: String): List<PuntoInteres>
    suspend fun isFavorite(userId: String, publicationId: String): Boolean
}