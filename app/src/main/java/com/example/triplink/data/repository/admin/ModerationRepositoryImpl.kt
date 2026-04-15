package com.example.triplink.data.repository.admin

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.domain.model.enums.moderator.ModerationFilter
import com.example.triplink.domain.model.moderator.ModerationPublication
import com.example.triplink.domain.repository.admin.ModerationRepository
import com.example.triplink.domain.repository.publication.PublicationRepository
import com.example.triplink.domain.repository.user.UserProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModerationRepositoryImpl @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val userProfileRepository: UserProfileRepository
) : ModerationRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val publicationOrderIds = mutableListOf<String>()

    private val _moderationPublications = MutableStateFlow<List<ModerationPublication>>(emptyList())
    override val moderationPublications: StateFlow<List<ModerationPublication>> = _moderationPublications.asStateFlow()

    init {
        scope.launch {
            publicationRepository.publications.collectLatest { publications ->
                refreshModerationPublications(publications)
            }
        }
    }

    override val pendingModerationCount: Int
        get() = _moderationPublications.value.count { it.pointOfInterest.estado == EstadoPublicacion.PENDIENTE }

    override val verifiedModerationCount: Int
        get() = _moderationPublications.value.count { it.pointOfInterest.estado == EstadoPublicacion.VERIFICADA }

    override val rejectedModerationCount: Int
        get() = _moderationPublications.value.count { it.pointOfInterest.estado == EstadoPublicacion.RECHAZADA }

    override fun getModerationPublicationById(publicationId: String): ModerationPublication? {
        return publicationRepository.getPublicationById(publicationId)?.toModerationPublication()
    }

    override fun moderationPublicationsFor(filter: ModerationFilter): List<ModerationPublication> = when (filter) {
        ModerationFilter.ALL -> {
            val pending = _moderationPublications.value
                .filter { it.pointOfInterest.estado == EstadoPublicacion.PENDIENTE }
            val reviewed = _moderationPublications.value
                .filter { it.pointOfInterest.estado != EstadoPublicacion.PENDIENTE }
            pending + reviewed
        }

        ModerationFilter.PENDING -> _moderationPublications.value
            .filter { it.pointOfInterest.estado == EstadoPublicacion.PENDIENTE }

        ModerationFilter.VERIFIED -> _moderationPublications.value
            .filter { it.pointOfInterest.estado == EstadoPublicacion.VERIFICADA }

        ModerationFilter.REJECTED -> _moderationPublications.value
            .filter { it.pointOfInterest.estado == EstadoPublicacion.RECHAZADA }
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

        val updated = publicationRepository.updatePuntoInteres(
            publication.copy(
                estado = updatedStatus,
                motivoRechazo = if (decision == DecisionModerador.RECHAZADA) reason else null
            )
        )

        if (updated) {
            movePublicationToEnd(publicationId)
            refreshModerationPublications(publicationRepository.publications.value)
        }
    }

    private fun refreshModerationPublications(publications: List<PuntoInteres>) {
        val incomingIds = publications.map { it.id }
        publicationOrderIds.retainAll(incomingIds.toSet())
        incomingIds.forEach { id ->
            if (id !in publicationOrderIds) publicationOrderIds.add(id)
        }

        val orderIndex = publicationOrderIds.withIndex().associate { it.value to it.index }
        _moderationPublications.value = publications
            .sortedBy { orderIndex[it.id] ?: Int.MAX_VALUE }
            .map { it.toModerationPublication() }
    }

    private fun movePublicationToEnd(publicationId: String) {
        publicationOrderIds.remove(publicationId)
        publicationOrderIds.add(publicationId)
    }

    private fun PuntoInteres.toModerationPublication(): ModerationPublication {
        val authorName = userProfileRepository.findUserNameById(usuarioAutorId)
            ?: usuarioAutorId.substringBefore('@')

        return ModerationPublication(
            id = id,
            pointOfInterest = this,
            authorName = authorName,
            createdAtMillis = fechaCreacion,
            moderationReason = motivoRechazo,
            rejectReason = motivoRechazo
        )
    }
}

