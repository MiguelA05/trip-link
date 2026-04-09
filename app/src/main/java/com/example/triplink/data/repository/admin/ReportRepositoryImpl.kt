package com.example.triplink.data.repository.admin

import com.example.triplink.data.seed.AdminReportSeedEntry
import com.example.triplink.domain.model.admin.AdminReportCase
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.repository.admin.ReportRepository
import com.example.triplink.domain.repository.publication.PublicationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val store: AdminReportsStore
) : ReportRepository {

    override val pendingReportsCount: Int
        get() = store.seedState.pendingReports.size

    override val reportCases: List<AdminReportCase>
        get() = store.seedState.pendingReports
            .map { it.toDomain(store.seedState.acceptedReportsCountByPublication[it.pointOfInterest.id] ?: 0) }
            .sortedByDescending { it.report.fechaCreacion }

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
        val publicationId = entry.pointOfInterest.id
        val updatedCount = (store.seedState.acceptedReportsCountByPublication[publicationId] ?: 0) + 1
        store.seedState.acceptedReportsCountByPublication[publicationId] = updatedCount

        store.seedState.pendingReports.removeAt(index)

        if (updatedCount >= store.acceptedReportThreshold) {
            val publication = publicationRepository.getPublicationById(publicationId)
            if (publication != null) {
                publicationRepository.updatePuntoInteres(
                    publication.copy(
                        estado = EstadoPublicacion.RECHAZADA,
                        motivoRechazo = "Publicación rechazada por acumulación de reportes confirmados"
                    )
                )
            }
            store.seedState.pendingReports.removeAll { it.pointOfInterest.id == publicationId }
        }
    }

    override fun invalidateReport(reportId: String) {
        val index = store.seedState.pendingReports.indexOfFirst { it.report.id == reportId }
        if (index == -1) return

        store.seedState.pendingReports.removeAt(index)
    }

    private fun AdminReportSeedEntry.toDomain(acceptedReportsCount: Int): AdminReportCase = AdminReportCase(
        report = report,
        pointOfInterest = pointOfInterest,
        reporterName = reporterName,
        acceptedReportsCount = acceptedReportsCount
    )
}



