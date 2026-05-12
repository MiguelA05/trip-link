package com.example.triplink.domain.repository.user

import com.example.triplink.domain.model.Insignia
import com.example.triplink.domain.model.UserInsigniaProgress
import com.example.triplink.domain.model.enums.Nivel

data class BadgeSyncResult(
    val unlockedBadgeIds: Set<String>,
    val newlyUnlockedBadgeIds: List<String>,
    val points: Int,
    val level: Nivel,
    val contributions: Int,
    val verifiedContributions: Int
)

interface BadgeRepository {
    fun badgeDefinitions(): List<Insignia>
    suspend fun syncUserProgress(userId: String): BadgeSyncResult
    suspend fun userBadgeProgress(userId: String): List<UserInsigniaProgress>
    suspend fun recentUnlockedBadgeIds(userId: String, limit: Int = 3): List<String>
}

