package com.example.triplink.data.repository.admin

import com.example.triplink.data.repository.admin.reports.AdminReportSeedEntry
import com.example.triplink.data.repository.admin.reports.createAdminReportsSeedState
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.domain.model.enums.moderator.ModerationFilter
import com.example.triplink.domain.model.moderator.ModerationPublication
import com.example.triplink.domain.repository.admin.AdminRepository
import com.example.triplink.domain.repository.user.UserRepository
import com.example.triplink.features.admin.reports.AdminReportUi
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.absoluteValue

@Singleton
class AdminRepositoryImpl @Inject constructor(
    private val userRepository: UserRepository
) : AdminRepository {

    private val reportsSeedState = createAdminReportsSeedState()
    private val acceptedReportThreshold = 3

    override val pendingModerationCount: Int
        get() = userRepository.publications.value.count { it.estado == EstadoPublicacion.PENDIENTE }

    override val verifiedModerationCount: Int
        get() = userRepository.publications.value.count { it.estado == EstadoPublicacion.VERIFICADA }

    override val rejectedModerationCount: Int
        get() = userRepository.publications.value.count { it.estado == EstadoPublicacion.RECHAZADA }

    override val pendingReportsCount: Int
        get() = reportsSeedState.pendingReports.size

    override val reportCards: List<AdminReportUi>
        get() = reportsSeedState.pendingReports
            .map { it.toUi(reportsSeedState.acceptedReportsCountByPublication[it.pointOfInterest.id] ?: 0) }
            .sortedByDescending { it.report.fechaCreacion }

    override fun getModerationPublicationById(publicationId: String): ModerationPublication? {
        return userRepository.getPublicationById(publicationId)?.toModerationPublication()
    }

    override fun moderationPublicationsFor(filter: ModerationFilter): List<ModerationPublication> = when (filter) {
        ModerationFilter.ALL -> {
            val pending = userRepository.publications.value
                .filter { it.estado == EstadoPublicacion.PENDIENTE }
                .map { it.toModerationPublication() }
            val reviewed = userRepository.publications.value
                .filter { it.estado != EstadoPublicacion.PENDIENTE }
                .map { it.toModerationPublication() }
            pending + reviewed
        }

        ModerationFilter.PENDING -> userRepository.publications.value
            .filter { it.estado == EstadoPublicacion.PENDIENTE }
            .map { it.toModerationPublication() }

        ModerationFilter.VERIFIED -> userRepository.publications.value
            .filter { it.estado == EstadoPublicacion.VERIFICADA }
            .map { it.toModerationPublication() }

        ModerationFilter.REJECTED -> userRepository.publications.value
            .filter { it.estado == EstadoPublicacion.RECHAZADA }
            .map { it.toModerationPublication() }
    }

    override fun applyModerationDecision(
        publicationId: String,
        decision: DecisionModerador,
        reason: String?
    ) {
        val publication = userRepository.getPublicationById(publicationId) ?: return

        val updatedStatus = if (decision == DecisionModerador.APROBADA) {
            EstadoPublicacion.VERIFICADA
        } else {
            EstadoPublicacion.RECHAZADA
        }

        userRepository.updatePuntoInteres(
            publication.copy(
                estado = updatedStatus,
                motivoRechazo = if (decision == DecisionModerador.RECHAZADA) reason else null
            )
        )
    }

    override fun getReportById(reportId: String): AdminReportUi? {
        return reportsSeedState.pendingReports
            .find { it.report.id == reportId }
            ?.let { entry ->
                entry.toUi(reportsSeedState.acceptedReportsCountByPublication[entry.pointOfInterest.id] ?: 0)
            }
    }

    override fun confirmReport(reportId: String) {
        val index = reportsSeedState.pendingReports.indexOfFirst { it.report.id == reportId }
        if (index == -1) return

        val entry = reportsSeedState.pendingReports[index]
        val publicationId = entry.pointOfInterest.id
        val updatedCount = (reportsSeedState.acceptedReportsCountByPublication[publicationId] ?: 0) + 1
        reportsSeedState.acceptedReportsCountByPublication[publicationId] = updatedCount

        reportsSeedState.pendingReports.removeAt(index)

        if (updatedCount >= acceptedReportThreshold) {
            val publication = userRepository.getPublicationById(publicationId)
            if (publication != null) {
                userRepository.updatePuntoInteres(
                    publication.copy(
                        estado = EstadoPublicacion.RECHAZADA,
                        motivoRechazo = "Publicación rechazada por acumulación de reportes confirmados"
                    )
                )
            }
            reportsSeedState.pendingReports.removeAll { it.pointOfInterest.id == publicationId }
        }
    }

    override fun invalidateReport(reportId: String) {
        val index = reportsSeedState.pendingReports.indexOfFirst { it.report.id == reportId }
        if (index == -1) return

        reportsSeedState.pendingReports.removeAt(index)
    }

    private fun AdminReportSeedEntry.toUi(acceptedReportsCount: Int): AdminReportUi = AdminReportUi(
        report = report,
        pointOfInterest = pointOfInterest,
        reporterName = reporterName,
        acceptedReportsCount = acceptedReportsCount
    )

    private fun PuntoInteres.toModerationPublication(): ModerationPublication {
        val authorName = userRepository.findUserNameById(usuarioAutorId)
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
