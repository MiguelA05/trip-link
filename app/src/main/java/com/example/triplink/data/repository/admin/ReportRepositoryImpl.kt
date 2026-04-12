package com.example.triplink.data.repository.admin

import com.example.triplink.data.seed.AdminReportSeedEntry
import com.example.triplink.domain.model.Reporte
import com.example.triplink.domain.model.admin.AdminReportCase
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.EstadoReporte
import com.example.triplink.domain.repository.admin.ReportRepository
import com.example.triplink.domain.repository.publication.PublicationRepository
import com.example.triplink.domain.repository.user.UserProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val userProfileRepository: UserProfileRepository,
    private val store: AdminReportsStore
) : ReportRepository {

    override val pendingReportsCount: Int
        get() = store.seedState.pendingReports.size

    override val reportCases: List<AdminReportCase>
        get() = store.seedState.pendingReports
            .map { it.toDomain(store.seedState.acceptedReportsCountByPublication[it.pointOfInterest.id] ?: 0) }
            .sortedByDescending { it.report.fechaCreacion }

    override fun submitReport(report: Reporte): Boolean {
        val publication = publicationRepository.getPublicationById(report.puntoInteresId) ?: return false

        val reporterName = userProfileRepository.findUserNameById(report.reportadorId)
            ?: report.reportadorId.substringBefore('@')

        store.seedState.pendingReports.add(
            0,
            AdminReportSeedEntry(
                report = report.copy(estado = EstadoReporte.PENDIENTE),
                pointOfInterest = publication,
                reporterName = reporterName
            )
        )

        publicationRepository.updatePuntoInteres(
            publication.copy(reportes = publication.reportes + report.copy(estado = EstadoReporte.PENDIENTE))
        )

        return true
    }

    override fun getReportById(reportId: String): AdminReportCase? {
        return store.seedState.pendingReports
            .find { it.report.id == reportId }
            ?.let { entry ->
                entry.toDomain(store.seedState.acceptedReportsCountByPublication[entry.pointOfInterest.id] ?: 0)
            }
    }

    override fun confirmReport(reportId: String) {
        val index = store.seedState.pendingReports.indexOfFirst { it.report.id == reportId }
        if (index == -1) return

        val entry = store.seedState.pendingReports[index]
        updatePublicationReportStatus(
            publicationId = entry.pointOfInterest.id,
            reportId = entry.report.id,
            newStatus = EstadoReporte.APROBADO
        )

        val publicationId = entry.pointOfInterest.id
        val updatedCount = (store.seedState.acceptedReportsCountByPublication[publicationId] ?: 0) + 1
        store.seedState.acceptedReportsCountByPublication[publicationId] = updatedCount

        store.seedState.pendingReports.removeAt(index)

        if (updatedCount >= store.acceptedReportThreshold) {
            val publication = publicationRepository.getPublicationById(publicationId)
            if (publication != null) {
                val reportDetail = entry.report.descripcion?.trim().orEmpty()
                val specificReason = if (reportDetail.isBlank()) {
                    entry.report.motivo.name.replace('_', ' ')
                } else {
                    "${entry.report.motivo.name.replace('_', ' ')}: $reportDetail"
                }
                publicationRepository.updatePuntoInteres(
                    publication.copy(
                        estado = EstadoPublicacion.RECHAZADA,
                        motivoRechazo = "Retirada por reportes verificados ($updatedCount): $specificReason"
                    )
                )
            }
            store.seedState.pendingReports.removeAll { it.pointOfInterest.id == publicationId }
        }
    }

    override fun invalidateReport(reportId: String) {
        val index = store.seedState.pendingReports.indexOfFirst { it.report.id == reportId }
        if (index == -1) return

        val entry = store.seedState.pendingReports[index]
        updatePublicationReportStatus(
            publicationId = entry.pointOfInterest.id,
            reportId = entry.report.id,
            newStatus = EstadoReporte.RECHAZADO
        )

        store.seedState.pendingReports.removeAt(index)
    }

    private fun updatePublicationReportStatus(
        publicationId: String,
        reportId: String,
        newStatus: EstadoReporte
    ) {
        val publication = publicationRepository.getPublicationById(publicationId) ?: return
        val updatedReports = publication.reportes.map { report ->
            if (report.id == reportId) report.copy(
                estado = newStatus,
                fechaRevision = System.currentTimeMillis()
            ) else report
        }
        publicationRepository.updatePuntoInteres(publication.copy(reportes = updatedReports))
    }

    private fun AdminReportSeedEntry.toDomain(acceptedReportsCount: Int): AdminReportCase = AdminReportCase(
        report = report,
        pointOfInterest = pointOfInterest,
        reporterName = reporterName,
        acceptedReportsCount = acceptedReportsCount
    )
}



