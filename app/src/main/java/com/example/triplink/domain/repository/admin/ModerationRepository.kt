package com.example.triplink.domain.repository.admin

import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.domain.model.enums.moderator.ModerationFilter
import com.example.triplink.domain.model.moderator.ModerationPublication
import kotlinx.coroutines.flow.StateFlow

interface ModerationRepository {
    val moderationPublications: StateFlow<List<ModerationPublication>>
    val pendingModerationCount: Int
    val verifiedModerationCount: Int
    val rejectedModerationCount: Int

    suspend fun getModerationPublicationById(publicationId: String): ModerationPublication?
    suspend fun moderationPublicationsFor(filter: ModerationFilter): List<ModerationPublication>
    suspend fun applyModerationDecision(
        publicationId: String,
        decision: DecisionModerador,
        reason: String? = null
    )
}

