package com.example.triplink.data.repository.admin.moderation

import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.domain.model.enums.moderator.ModerationFilter
import com.example.triplink.domain.model.moderator.ModerationPublication

class AdminModerationRepository {

    private val seedState = createAdminModerationSeedState()

    val pendingCount: Int
        get() = seedState.pendingPublications.size

    val verifiedCount: Int
        get() = seedState.reviewedPublications.count { it.pointOfInterest.estado == EstadoPublicacion.VERIFICADA }

    val rejectedCount: Int
        get() = seedState.reviewedPublications.count { it.pointOfInterest.estado == EstadoPublicacion.RECHAZADA }

    fun getPublicationById(publicationId: String): ModerationPublication? {
        return seedState.pendingPublications.firstOrNull { it.id == publicationId }
            ?: seedState.reviewedPublications.firstOrNull { it.id == publicationId }
    }

    fun publicationsFor(filter: ModerationFilter): List<ModerationPublication> = when (filter) {
        ModerationFilter.ALL -> buildList {
            addAll(seedState.pendingPublications)
            addAll(seedState.reviewedPublications)
        }
        ModerationFilter.PENDING -> seedState.pendingPublications.toList()
        ModerationFilter.VERIFIED -> seedState.reviewedPublications.filter { it.pointOfInterest.estado == EstadoPublicacion.VERIFICADA }
        ModerationFilter.REJECTED -> seedState.reviewedPublications.filter { it.pointOfInterest.estado == EstadoPublicacion.RECHAZADA }
    }

    fun applyDecision(
        publicationId: String,
        decision: DecisionModerador,
        reason: String? = null
    ) {
        val publication = seedState.pendingPublications.firstOrNull { it.id == publicationId } ?: return
        seedState.pendingPublications.remove(publication)

        val updatedStatus = if (decision == DecisionModerador.APROBADA) {
            EstadoPublicacion.VERIFICADA
        } else {
            EstadoPublicacion.RECHAZADA
        }

        seedState.reviewedPublications.add(
            seedState.reviewedPublications.size,
            publication.copy(
                pointOfInterest = publication.pointOfInterest.copy(estado = updatedStatus),
                moderationReason = if (decision == DecisionModerador.RECHAZADA) reason else publication.moderationReason,
                rejectReason = if (decision == DecisionModerador.RECHAZADA) reason else null
            )
        )
    }
}

