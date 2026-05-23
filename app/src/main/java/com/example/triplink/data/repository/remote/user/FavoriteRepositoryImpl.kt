package com.example.triplink.data.repository.remote.user

import android.util.Log
import com.example.triplink.data.repository.remote.FAVORITES_COLLECTION
import com.example.triplink.data.repository.remote.PUBLICATIONS_COLLECTION
import com.example.triplink.data.repository.remote.FirestoreFavoriteDto
import com.example.triplink.data.repository.remote.toDomain
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.repository.user.FavoriteRepository
import com.example.triplink.domain.repository.user.PublicationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val publicationRepository: PublicationRepository
) : FavoriteRepository {

    companion object {
        private const val TAG = "FavRepo"
    }

    private val _favoriteIdsByUser = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
    override val favoriteIdsByUser: StateFlow<Map<String, Set<String>>> = _favoriteIdsByUser.asStateFlow()

    private var favoritesListener: ListenerRegistration? = null

    init {
        observeFavorites()
    }

    override suspend fun toggleFavorite(userId: String, publicationId: String): Boolean {
        val normalizedUserId = normalize(userId)
        val favoriteRef = favoritesCollection(normalizedUserId).document(publicationId)
        if (publicationRepository.getPublicationById(publicationId) == null) return false
        val exists = favoriteRef.get().await().exists()
        val added = !exists

        if (added) {
            favoriteRef.set(
                FirestoreFavoriteDto(
                    publicationId = publicationId,
                    createdAt = System.currentTimeMillis()
                )
            ).await()
            Log.d(TAG, "favorite doc created for user=$normalizedUserId pub=$publicationId")

            // Optimistic local update for UI
            try {
                publicationRepository.adjustFavoriteCountLocal(publicationId, 1)
                Log.d(TAG, "optimistic local +1 for pub=$publicationId")
            } catch (e: Exception) {
                Log.e(TAG, "optimistic local +1 failed for pub=$publicationId", e)
            }

            // Try atomic increment in publication doc as fast path (no index required)
            try {
                firestore.collection(PUBLICATIONS_COLLECTION)
                    .document(publicationId)
                    .set(mapOf("favoriteCount" to FieldValue.increment(1)), SetOptions.merge())
                    .await()
                Log.d(TAG, "firestore increment +1 applied for pub=$publicationId")
            } catch (e: Exception) {
                Log.e(TAG, "firestore increment +1 failed for pub=$publicationId", e)
            }
        } else {
            favoriteRef.delete().await()
            Log.d(TAG, "favorite doc deleted for user=$normalizedUserId pub=$publicationId")

            // Optimistic local update for UI
            try {
                publicationRepository.adjustFavoriteCountLocal(publicationId, -1)
                Log.d(TAG, "optimistic local -1 for pub=$publicationId")
            } catch (e: Exception) {
                Log.e(TAG, "optimistic local -1 failed for pub=$publicationId", e)
            }

            // Try atomic decrement in publication doc as fast path (no index required)
            try {
                firestore.collection(PUBLICATIONS_COLLECTION)
                    .document(publicationId)
                    .set(mapOf("favoriteCount" to FieldValue.increment(-1)), SetOptions.merge())
                    .await()
                Log.d(TAG, "firestore increment -1 applied for pub=$publicationId")
            } catch (e: Exception) {
                Log.e(TAG, "firestore increment -1 failed for pub=$publicationId", e)
            }
        }

        // Recalculate favorite count remotely to keep denormalized count consistent (may fail if index missing)
        try {
            val recalc = publicationRepository.recalculateFavoriteCount(publicationId)
            Log.d(TAG, "recalculateFavoriteCount result for pub=$publicationId: $recalc")
        } catch (e: Exception) {
            Log.e(TAG, "recalculateFavoriteCount failed for pub=$publicationId", e)
        }

        updateFavoriteCache(normalizedUserId, publicationId, added)
        return added
    }

    override suspend fun getFavoritePublications(userId: String): List<PuntoInteres> {
        val normalizedUserId = normalize(userId)
        val favoriteIds = favoritesCollection(normalizedUserId)
            .get()
            .await()
            .documents
            .mapNotNull { document -> document.toObject(FirestoreFavoriteDto::class.java)?.toDomain() }

        return favoriteIds.mapNotNull { publicationRepository.getPublicationById(it) }
    }

    override suspend fun isFavorite(userId: String, publicationId: String): Boolean {
        if (userId.isBlank()) return false
        val normalizedUserId = normalize(userId)
        return favoritesCollection(normalizedUserId)
            .document(publicationId)
            .get()
            .await()
            .exists()
    }

    private fun observeFavorites() {
        try {
            favoritesListener?.remove()
            favoritesListener = firestore.collectionGroup(FAVORITES_COLLECTION)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        error.printStackTrace()
                        return@addSnapshotListener
                    }

                    if (snapshot == null) return@addSnapshotListener

                    val grouped = mutableMapOf<String, MutableSet<String>>()
                    snapshot.documents.forEach { document ->
                        val userId = document.reference.parent.parent?.id?.trim()?.lowercase().orEmpty()
                        if (userId.isBlank()) return@forEach

                        val publicationId = document.toObject(FirestoreFavoriteDto::class.java)?.toDomain()
                            ?: document.id
                        grouped.getOrPut(userId) { mutableSetOf() }.add(publicationId)
                    }

                    _favoriteIdsByUser.value = grouped.mapValues { it.value.toSet() }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateFavoriteCache(userId: String, publicationId: String, added: Boolean) {
        val current = _favoriteIdsByUser.value.toMutableMap()
        val updated = current[userId].orEmpty().toMutableSet()

        if (added) {
            updated.add(publicationId)
        } else {
            updated.remove(publicationId)
        }

        if (updated.isEmpty()) {
            current.remove(userId)
        } else {
            current[userId] = updated
        }

        _favoriteIdsByUser.value = current.mapValues { it.value.toSet() }
    }

    private fun favoritesCollection(userId: String) = firestore.collection("users")
        .document(userId)
        .collection(FAVORITES_COLLECTION)

    private fun normalize(value: String): String = value.trim().lowercase()
}

