package com.example.triplink.data.repository.user

import com.example.triplink.data.seed.seedBadges
import com.example.triplink.domain.model.Insignia
import com.example.triplink.domain.model.UserInsigniaProgress
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.Nivel
import com.example.triplink.domain.repository.badge.BadgeRepository
import com.example.triplink.domain.repository.badge.BadgeSyncResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BadgeRepositoryImpl @Inject constructor(
    private val store: UserRepositoryStore
) : BadgeRepository {

    private val badges: List<Insignia> = seedBadges().sortedBy { it.requiredContributions }

    override fun badgeDefinitions(): List<Insignia> = badges

    override fun syncUserProgress(userId: String): BadgeSyncResult {
        val normalizedUserId = userId.trim().lowercase()
        if (normalizedUserId.isBlank()) {
            return BadgeSyncResult(emptySet(), emptyList(), 0, Nivel.TURISTA, 0, 0)
        }

        store.users.value.firstOrNull { it.email.equals(normalizedUserId, ignoreCase = true) }
            ?: return BadgeSyncResult(emptySet(), emptyList(), 0, Nivel.TURISTA, 0, 0)

        val userPublications = store.publications.value.filter {
            it.usuarioAutorId.equals(normalizedUserId, ignoreCase = true)
        }

        // Contar contribuciones (todas las publicaciones del usuario)
        val contributions = userPublications.count {
            it.estado == EstadoPublicacion.VERIFICADA ||
                it.estado == EstadoPublicacion.PENDIENTE ||
                it.estado == EstadoPublicacion.RECHAZADA
        }

        // Contar publicaciones verificadas
        val verifiedContributions = userPublications.count { it.estado == EstadoPublicacion.VERIFICADA }

        // Contar favoritos totales que ha recibido el usuario en sus publicaciones
        val totalFavoritesReceived = userPublications.sumOf { it.favoriteCount }

        // Contar comentarios totales que ha recibido el usuario en sus publicaciones
        val totalCommentsReceived = userPublications.sumOf { it.comments.size }

        val unlockMap = store.badgeUnlocksFor(normalizedUserId)
        val previousUnlocked = unlockMap.keys.toSet()

        badges.forEach { badge ->
            val meetsContributions = contributions >= badge.requiredContributions
            val meetsVerified = verifiedContributions >= badge.requiredVerifiedContributions
            val meetsFavorites = totalFavoritesReceived >= badge.requiredFavorites
            val meetsComments = totalCommentsReceived >= badge.requiredComments

            if (meetsContributions && meetsVerified && meetsFavorites && meetsComments && unlockMap[badge.id] == null) {
                store.unlockBadge(normalizedUserId, badge.id)
            }
        }

        val unlockedBadgeIds = store.badgeUnlocksFor(normalizedUserId).keys.toSet()
        val newlyUnlockedIds = unlockedBadgeIds.minus(previousUnlocked).toList()

        val points = badges
            .filter { it.id in unlockedBadgeIds }
            .sumOf { it.puntos }

        val level = points.toLevel()

        val updatedUsers = store.users.value.map { current ->
            if (!current.email.equals(normalizedUserId, ignoreCase = true)) {
                current
            } else {
                current.copy(
                    puntos = points,
                    nivel = level,
                    insignias = unlockedBadgeIds.toList()
                )
            }
        }
        store.setUsers(updatedUsers)

        return BadgeSyncResult(
            unlockedBadgeIds = unlockedBadgeIds,
            newlyUnlockedBadgeIds = newlyUnlockedIds,
            points = points,
            level = level,
            contributions = contributions,
            verifiedContributions = verifiedContributions
        )
    }

    override fun userBadgeProgress(userId: String): List<UserInsigniaProgress> {
        val unlocks = store.badgeUnlocksFor(userId).toMap()
        return badges.map { badge ->
            UserInsigniaProgress(
                insignia = badge,
                unlockedAtMillis = unlocks[badge.id]
            )
        }
    }

    override fun recentUnlockedBadgeIds(userId: String, limit: Int): List<String> {
        val unlocks = store.badgeUnlocksFor(userId).toMap()
        return unlocks.entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key }
    }

    private fun Int.toLevel(): Nivel = when {
        this >= 240 -> Nivel.EMBAJADOR_LOCAL
        this >= 140 -> Nivel.AVENTURARO
        this >= 60 -> Nivel.EXPLORADOR
        else -> Nivel.TURISTA
    }
}




