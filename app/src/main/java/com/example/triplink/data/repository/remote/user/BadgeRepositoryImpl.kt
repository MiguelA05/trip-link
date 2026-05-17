package com.example.triplink.data.repository.remote.user

import com.example.triplink.data.repository.remote.BADGE_UNLOCKS_COLLECTION
import com.example.triplink.data.repository.remote.FirestoreBadgeUnlockDto
import com.example.triplink.data.repository.remote.FirestorePuntoInteresDto
import com.example.triplink.data.repository.remote.FirestoreUsuarioDto
import com.example.triplink.data.repository.remote.PUBLICATIONS_COLLECTION
import com.example.triplink.data.repository.remote.USERS_COLLECTION
import com.example.triplink.data.repository.remote.toDomain
import com.example.triplink.data.repository.remote.toFirestoreDto
import com.example.triplink.data.seed.seedBadges
import com.example.triplink.domain.model.Insignia
import com.example.triplink.domain.model.UserInsigniaProgress
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.Nivel
import com.example.triplink.domain.repository.user.BadgeRepository
import com.example.triplink.domain.repository.user.BadgeSyncResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BadgeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BadgeRepository {

    private val badges: List<Insignia> = seedBadges().sortedBy { it.requiredContributions }
    private val badgeUnlocksByUser = mutableMapOf<String, MutableMap<String, Long>>()

    override fun badgeDefinitions(): List<Insignia> = badges

    override suspend fun syncUserProgress(userId: String): BadgeSyncResult {
        val normalizedUserId = userId.trim().lowercase()
        if (normalizedUserId.isBlank()) {
            return BadgeSyncResult(emptySet(), emptyList(), 0, Nivel.TURISTA, 0, 0)
        }

        val currentUser = fetchUserById(normalizedUserId)
            ?: return BadgeSyncResult(emptySet(), emptyList(), 0, Nivel.TURISTA, 0, 0)

        val userPublications = fetchUserPublications(currentUser.email)

        val contributions = userPublications.count {
            it.estado == EstadoPublicacion.VERIFICADA ||
                it.estado == EstadoPublicacion.PENDIENTE ||
                it.estado == EstadoPublicacion.RECHAZADA
        }

        val verifiedContributions = userPublications.count { it.estado == EstadoPublicacion.VERIFICADA }

        val totalFavoritesReceived = userPublications.sumOf { it.favoriteCount }

        val totalCommentsReceived = userPublications.sumOf { it.comments.size }

        val unlockMap = loadBadgeUnlocks(currentUser.email)
        val previousUnlocked = unlockMap.keys.toSet()

        badges.forEach { badge ->
            val meetsContributions = contributions >= badge.requiredContributions
            val meetsVerified = verifiedContributions >= badge.requiredVerifiedContributions
            val meetsFavorites = totalFavoritesReceived >= badge.requiredFavorites
            val meetsComments = totalCommentsReceived >= badge.requiredComments

            if (meetsContributions && meetsVerified && meetsFavorites && meetsComments && unlockMap[badge.id] == null) {
                unlockBadge(currentUser.email, badge.id)
            }
        }

        val unlockedBadgeIds = loadBadgeUnlocks(currentUser.email).keys.toSet()
        val newlyUnlockedIds = unlockedBadgeIds.minus(previousUnlocked).toList()

        val points = badges
            .filter { it.id in unlockedBadgeIds }
            .sumOf { it.puntos }

        val level = points.toLevel()

        firestore.collection(USERS_COLLECTION)
            .document(normalize(currentUser.email))
            .set(
                currentUser.copy(
                    puntos = points,
                    nivel = level,
                    insignias = unlockedBadgeIds.toList()
                ).toFirestoreDto(),
                SetOptions.merge()
            )
            .await()

        return BadgeSyncResult(
            unlockedBadgeIds = unlockedBadgeIds,
            newlyUnlockedBadgeIds = newlyUnlockedIds,
            points = points,
            level = level,
            contributions = contributions,
            verifiedContributions = verifiedContributions
        )
    }

    override suspend fun userBadgeProgress(userId: String): List<UserInsigniaProgress> {
        val unlocks = loadBadgeUnlocks(userId)
        return badges.map { badge ->
            UserInsigniaProgress(
                insignia = badge,
                unlockedAtMillis = unlocks[badge.id]
            )
        }
    }

    override suspend fun recentUnlockedBadgeIds(userId: String, limit: Int): List<String> {
        val unlocks = loadBadgeUnlocks(userId)
        return unlocks.entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key }
    }

    private suspend fun fetchUserById(userId: String): com.example.triplink.domain.model.Usuario? {
        val byEmailDoc = firestore.collection(USERS_COLLECTION)
            .document(normalize(userId))
            .get()
            .await()
        byEmailDoc.toObject(FirestoreUsuarioDto::class.java)?.toDomain()?.let { return it }

        val byFirebaseUid = firestore.collection(USERS_COLLECTION)
            .whereEqualTo("firebaseUid", userId)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
        return byFirebaseUid?.toObject(FirestoreUsuarioDto::class.java)?.toDomain()
    }

    private suspend fun fetchUserPublications(userId: String): List<com.example.triplink.domain.model.PuntoInteres> {
        return firestore.collection(PUBLICATIONS_COLLECTION)
            .whereEqualTo("usuarioAutorId", normalize(userId))
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                document.toObject(FirestorePuntoInteresDto::class.java)?.toDomain()
            }
    }

    private suspend fun loadBadgeUnlocks(userId: String): MutableMap<String, Long> {
        val normalized = normalize(userId)
        badgeUnlocksByUser[normalized]?.let { return it }

        val unlocks = firestore.collection(USERS_COLLECTION)
            .document(normalized)
            .collection(BADGE_UNLOCKS_COLLECTION)
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                document.toObject(FirestoreBadgeUnlockDto::class.java)?.toDomain()
            }
            .toMap()
            .toMutableMap()

        badgeUnlocksByUser[normalized] = unlocks
        return unlocks
    }

    private suspend fun unlockBadge(userId: String, badgeId: String, timestamp: Long = System.currentTimeMillis()): Boolean {
        val normalized = normalize(userId)
        val unlocks = loadBadgeUnlocks(normalized)
        if (unlocks.containsKey(badgeId)) return false

        firestore.collection(USERS_COLLECTION)
            .document(normalized)
            .collection(BADGE_UNLOCKS_COLLECTION)
            .document(badgeId)
            .set(
                FirestoreBadgeUnlockDto(
                    badgeId = badgeId,
                    unlockedAtMillis = timestamp
                )
            )
            .await()

        unlocks[badgeId] = timestamp
        return true
    }

    private fun normalize(value: String): String = value.trim().lowercase()

    private fun Int.toLevel(): Nivel = when {
        this >= 240 -> Nivel.EMBAJADOR_LOCAL
        this >= 140 -> Nivel.AVENTURARO
        this >= 60 -> Nivel.EXPLORADOR
        else -> Nivel.TURISTA
    }
}




