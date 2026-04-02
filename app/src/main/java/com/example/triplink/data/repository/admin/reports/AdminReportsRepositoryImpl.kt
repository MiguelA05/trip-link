package com.example.triplink.data.repository.admin.reports

import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.repository.admin.reports.AdminReportsRepository
import com.example.triplink.features.admin.reports.AdminReportUi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminReportsRepositoryImpl @Inject constructor() : AdminReportsRepository {

    private val acceptedReportThreshold = 3
    private val seedState = createAdminReportsSeedState()

    override val pendingCount: Int
        get() = seedState.pendingReports.size

    override val reportCards: List<AdminReportUi>
        get() = seedState.pendingReports
            .map { it.toUi(seedState.acceptedReportsCountByPublication[it.pointOfInterest.id] ?: 0) }
            .sortedByDescending { it.report.fechaCreacion }

    override fun getReportById(reportId: String): AdminReportUi? {
        return seedState.pendingReports
            .find { it.report.id == reportId }
            ?.let { entry ->
                entry.toUi(seedState.acceptedReportsCountByPublication[entry.pointOfInterest.id] ?: 0)
            }
    }

    override fun confirmReport(reportId: String) {
        val index = seedState.pendingReports.indexOfFirst { it.report.id == reportId }
        if (index == -1) return

        val entry = seedState.pendingReports[index]
        val publicationId = entry.pointOfInterest.id
        val updatedCount = (seedState.acceptedReportsCountByPublication[publicationId] ?: 0) + 1
        seedState.acceptedReportsCountByPublication[publicationId] = updatedCount

        seedState.pendingReports.removeAt(index)

        if (updatedCount >= acceptedReportThreshold) {
            seedState.publicationsById[publicationId] = seedState.publicationsById.getValue(publicationId)
                .copy(estado = EstadoPublicacion.RECHAZADA)
            seedState.pendingReports.removeAll { it.pointOfInterest.id == publicationId }
        }
    }

    override fun invalidateReport(reportId: String) {
        val index = seedState.pendingReports.indexOfFirst { it.report.id == reportId }
        if (index == -1) return

        seedState.pendingReports.removeAt(index)
    }

    private fun AdminReportSeedEntry.toUi(acceptedReportsCount: Int): AdminReportUi = AdminReportUi(
        report = report,
        pointOfInterest = pointOfInterest,
        reporterName = reporterName,
        acceptedReportsCount = acceptedReportsCount
    )
}

