package com.example.triplink.data.repository.admin

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.domain.model.enums.moderator.ModerationFilter
import com.example.triplink.domain.model.moderator.ModerationPublication
import com.example.triplink.domain.repository.admin.ModerationRepository
import com.example.triplink.domain.repository.publication.PublicationRepository
import com.example.triplink.domain.repository.user.UserProfileRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.absoluteValue

@Singleton
class ModerationRepositoryImpl @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val userProfileRepository: UserProfileRepository
) : ModerationRepository {

    override val pendingModerationCount: Int
        get() = publicationRepository.publications.value.count { it.estado == EstadoPublicacion.PENDIENTE }

    override val verifiedModerationCount: Int
        get() = publicationRepository.publications.value.count { it.estado == EstadoPublicacion.VERIFICADA }

    override val rejectedModerationCount: Int
        get() = publicationRepository.publications.value.count { it.estado == EstadoPublicacion.RECHAZADA }

    override fun getModerationPublicationById(publicationId: String): ModerationPublication? {
        return publicationRepository.getPublicationById(publicationId)?.toModerationPublication()
    }

    override fun moderationPublicationsFor(filter: ModerationFilter): List<ModerationPublication> = when (filter) {
        ModerationFilter.ALL -> {
            val pending = publicationRepository.publications.value
                .filter { it.estado == EstadoPublicacion.PENDIENTE }
                .map { it.toModerationPublication() }
            val reviewed = publicationRepository.publications.value
                .filter { it.estado != EstadoPublicacion.PENDIENTE }
                .map { it.toModerationPublication() }
            pending + reviewed
        }

        ModerationFilter.PENDING -> publicationRepository.publications.value
            .filter { it.estado == EstadoPublicacion.PENDIENTE }
            .map { it.toModerationPublication() }

        ModerationFilter.VERIFIED -> publicationRepository.publications.value
            .filter { it.estado == EstadoPublicacion.VERIFICADA }
            .map { it.toModerationPublication() }

        ModerationFilter.REJECTED -> publicationRepository.publications.value
            .filter { it.estado == EstadoPublicacion.RECHAZADA }
            .map { it.toModerationPublication() }
    }

    override fun applyModerationDecision(
        publicationId: String,
        decision: DecisionModerador,
        reason: String?
    ) {
        val publication = publicationRepository.getPublicationById(publicationId) ?: return

        val updatedStatus = if (decision == DecisionModerador.APROBADA) {
            EstadoPublicacion.VERIFICADA
        } else {
            EstadoPublicacion.RECHAZADA
        }

        publicationRepository.updatePuntoInteres(
            publication.copy(
                estado = updatedStatus,
                motivoRechazo = if (decision == DecisionModerador.RECHAZADA) reason else null
            )
        )
    }

    private fun PuntoInteres.toModerationPublication(): ModerationPublication {
        val authorName = userProfileRepository.findUserNameById(usuarioAutorId)
            ?: usuarioAutorId.substringBefore('@')

        return ModerationPublication(
            id = id,
            pointOfInterest = this,
            authorName = authorName,
            createdAtMillis = System.currentTimeMillis() -
                (id.hashCode().toLong().absoluteValue % 96L) * 60L * 60L * 1000L,
            moderationReason = motivoRechazo,
            rejectReason = motivoRechazo
        )
    }
}

