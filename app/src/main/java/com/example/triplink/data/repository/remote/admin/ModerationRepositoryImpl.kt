package com.example.triplink.data.repository.remote.admin

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.domain.model.enums.moderator.ModerationFilter
import com.example.triplink.domain.model.moderator.ModerationPublication
import com.example.triplink.domain.repository.admin.ModerationRepository
import com.example.triplink.domain.repository.user.PublicationRepository
import com.example.triplink.data.repository.remote.PUBLICATIONS_COLLECTION
import com.example.triplink.data.repository.remote.FirestorePuntoInteresDto
import com.example.triplink.data.repository.remote.toDomain
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.triplink.domain.repository.user.UserProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModerationRepositoryImpl @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val userProfileRepository: UserProfileRepository,
    private val firestore: FirebaseFirestore
) : ModerationRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _moderationPublications = MutableStateFlow<List<ModerationPublication>>(emptyList())
    override val moderationPublications: StateFlow<List<ModerationPublication>> = _moderationPublications.asStateFlow()

    init {
        scope.launch {
            refreshModerationPublications()
        }
    }

    override val pendingModerationCount: Int
        get() = _moderationPublications.value.count { it.pointOfInterest.estado == EstadoPublicacion.PENDIENTE }

    override val verifiedModerationCount: Int
        get() = _moderationPublications.value.count { it.pointOfInterest.estado == EstadoPublicacion.VERIFICADA }

    override val rejectedModerationCount: Int
        get() = _moderationPublications.value.count { it.pointOfInterest.estado == EstadoPublicacion.RECHAZADA }

    override suspend fun getModerationPublicationById(publicationId: String): ModerationPublication? {
        return publicationRepository.getPublicationById(publicationId)?.toModerationPublication()
    }

    override suspend fun moderationPublicationsFor(filter: ModerationFilter): List<ModerationPublication> = when (filter) {
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

    override suspend fun applyModerationDecision(
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
            // refresh from remote to avoid relying on cached central list
            refreshModerationPublications()
        }
    }
    private suspend fun refreshModerationPublications() {
        try {
            val publications = firestore.collection(PUBLICATIONS_COLLECTION)
                .orderBy("fechaCreacion")
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    doc.toObject(FirestorePuntoInteresDto::class.java)?.toDomain()
                }

            _moderationPublications.value = publications
                .sortedByDescending { it.fechaCreacion }
                .map { it.toModerationPublication() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun PuntoInteres.toModerationPublication(): ModerationPublication {
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

