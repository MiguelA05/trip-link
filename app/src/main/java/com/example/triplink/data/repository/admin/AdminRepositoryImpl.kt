package com.example.triplink.data.repository.admin

import com.example.triplink.data.repository.admin.moderation.createAdminModerationSeedState
import com.example.triplink.data.repository.admin.reports.AdminReportSeedEntry
import com.example.triplink.data.repository.admin.reports.createAdminReportsSeedState
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.domain.model.enums.moderator.ModerationFilter
import com.example.triplink.domain.model.moderator.ModerationPublication
import com.example.triplink.domain.repository.admin.AdminRepository
import com.example.triplink.features.admin.reports.AdminReportUi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepositoryImpl @Inject constructor() : AdminRepository {

    private val moderationSeedState = createAdminModerationSeedState()
    private val reportsSeedState = createAdminReportsSeedState()
    private val acceptedReportThreshold = 3

    override val pendingModerationCount: Int
        get() = moderationSeedState.pendingPublications.size

    override val verifiedModerationCount: Int
        get() = moderationSeedState.reviewedPublications.count {
            it.pointOfInterest.estado == EstadoPublicacion.VERIFICADA
        }

    override val rejectedModerationCount: Int
        get() = moderationSeedState.reviewedPublications.count {
            it.pointOfInterest.estado == EstadoPublicacion.RECHAZADA
        }

    override val pendingReportsCount: Int
        get() = reportsSeedState.pendingReports.size

    override val reportCards: List<AdminReportUi>
        get() = reportsSeedState.pendingReports
            .map { it.toUi(reportsSeedState.acceptedReportsCountByPublication[it.pointOfInterest.id] ?: 0) }
            .sortedByDescending { it.report.fechaCreacion }

    override fun getModerationPublicationById(publicationId: String): ModerationPublication? {
        return moderationSeedState.pendingPublications.firstOrNull { it.id == publicationId }
            ?: moderationSeedState.reviewedPublications.firstOrNull { it.id == publicationId }
    }

    override fun moderationPublicationsFor(filter: ModerationFilter): List<ModerationPublication> = when (filter) {
        ModerationFilter.ALL -> buildList {
            addAll(moderationSeedState.pendingPublications)
            addAll(moderationSeedState.reviewedPublications)
        }

        ModerationFilter.PENDING -> moderationSeedState.pendingPublications.toList()
        ModerationFilter.VERIFIED -> moderationSeedState.reviewedPublications.filter {
            it.pointOfInterest.estado == EstadoPublicacion.VERIFICADA
        }

        ModerationFilter.REJECTED -> moderationSeedState.reviewedPublications.filter {
            it.pointOfInterest.estado == EstadoPublicacion.RECHAZADA
        }
    }

    override fun applyModerationDecision(
        publicationId: String,
        decision: DecisionModerador,
        reason: String?
    ) {
        val publication = moderationSeedState.pendingPublications.firstOrNull { it.id == publicationId } ?: return
        moderationSeedState.pendingPublications.remove(publication)

        val updatedStatus = if (decision == DecisionModerador.APROBADA) {
            EstadoPublicacion.VERIFICADA
        } else {
            EstadoPublicacion.RECHAZADA
        }

        moderationSeedState.reviewedPublications.add(
            moderationSeedState.reviewedPublications.size,
            publication.copy(
                pointOfInterest = publication.pointOfInterest.copy(estado = updatedStatus),
                moderationReason = if (decision == DecisionModerador.RECHAZADA) reason else publication.moderationReason,
                rejectReason = if (decision == DecisionModerador.RECHAZADA) reason else null
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
            reportsSeedState.publicationsById[publicationId] = reportsSeedState.publicationsById.getValue(publicationId)
                .copy(estado = EstadoPublicacion.RECHAZADA)
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
}

