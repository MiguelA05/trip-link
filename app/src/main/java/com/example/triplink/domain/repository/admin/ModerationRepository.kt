package com.example.triplink.domain.repository.admin

import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.domain.model.enums.moderator.ModerationFilter
import com.example.triplink.domain.model.moderator.ModerationPublication

interface ModerationRepository {
    val pendingModerationCount: Int
    val verifiedModerationCount: Int
    val rejectedModerationCount: Int

    fun getModerationPublicationById(publicationId: String): ModerationPublication?
    fun moderationPublicationsFor(filter: ModerationFilter): List<ModerationPublication>
    fun applyModerationDecision(
        publicationId: String,
        decision: DecisionModerador,
        reason: String? = null
    )
}

