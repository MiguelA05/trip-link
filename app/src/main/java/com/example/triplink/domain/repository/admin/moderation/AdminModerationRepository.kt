package com.example.triplink.domain.repository.admin.moderation

import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.domain.model.enums.moderator.ModerationFilter
import com.example.triplink.domain.model.moderator.ModerationPublication

interface AdminModerationRepository {



    val pendingCount: Int


    val verifiedCount: Int


    val rejectedCount: Int


    fun getPublicationById(publicationId: String): ModerationPublication?

    fun publicationsFor(filter: ModerationFilter): List<ModerationPublication>

    fun applyDecision(
        publicationId: String,
        decision: DecisionModerador,
        reason: String? = null
    )
}

