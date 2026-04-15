package com.example.triplink.data.repository.admin

import com.example.triplink.domain.model.Reporte
import com.example.triplink.domain.model.admin.AdminReportCase
import com.example.triplink.domain.model.enums.EstadoReporte
import com.example.triplink.domain.repository.admin.ReportRepository
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
class ReportRepositoryImpl @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val userProfileRepository: UserProfileRepository
) : ReportRepository {

    private val acceptedReportThreshold: Int = 3
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _reportCases = MutableStateFlow<List<AdminReportCase>>(emptyList())
    override val reportCases: StateFlow<List<AdminReportCase>> = _reportCases.asStateFlow()

    override val pendingReportsCount: Int
        get() = _reportCases.value.size

    init {
        scope.launch {
            publicationRepository.publications.collectLatest {
                refreshReportCases()
            }
        }
    }

    override fun hasUserReportedPublication(userId: String, publicationId: String): Boolean {
        return publicationRepository.getPublicationById(publicationId)
            ?.reportes
            ?.any { report ->
                report.reportadorId.equals(userId, ignoreCase = true) &&
                    report.puntoInteresId == publicationId
            } == true
    }

    override fun submitReport(report: Reporte): Boolean {
        val publication = publicationRepository.getPublicationById(report.puntoInteresId) ?: return false
        if (hasUserReportedPublication(report.reportadorId, report.puntoInteresId)) return false

        val updatedReports = publication.reportes + report.copy(estado = EstadoReporte.PENDIENTE)
        return publicationRepository.updatePuntoInteres(
            publication.copy(reportes = updatedReports)
        )
    }

    override fun getReportById(reportId: String): AdminReportCase? {
        return _reportCases.value.find { it.report.id == reportId }
    }

    override fun confirmReport(reportId: String) {
        val case = findReportCase(reportId) ?: return

        // Primero actualizamos el estado del reporte
        updatePublicationReportStatus(
            publicationId = case.pointOfInterest.id,
            reportId = case.report.id,
            newStatus = EstadoReporte.APROBADO
        )

        // Luego obtenemos la publicación actualizada y verificamos el conteo
        val updatedPublication = publicationRepository.getPublicationById(case.pointOfInterest.id) ?: return
        val approvedCount = updatedPublication.reportes.count { it.estado == EstadoReporte.APROBADO }

        // Si hay 3 o más reportes aprobados, eliminamos la publicación
        if (approvedCount >= acceptedReportThreshold) {
            publicationRepository.deletePublicationById(case.pointOfInterest.id)
        }
    }

    override fun invalidateReport(reportId: String) {
        val case = findReportCase(reportId) ?: return
        updatePublicationReportStatus(
            publicationId = case.pointOfInterest.id,
            reportId = case.report.id,
            newStatus = EstadoReporte.RECHAZADO
        )
    }

    private fun refreshReportCases() {
        _reportCases.value = publicationRepository.publications.value
            .flatMap { publication ->
                publication.reportes
                    .filter { it.estado == EstadoReporte.PENDIENTE }
                    .map { report ->
                        AdminReportCase(
                            report = report,
                            pointOfInterest = publication,
                            reporterName = userProfileRepository.findUserNameById(report.reportadorId)
                                ?: report.reportadorId.substringBefore('@'),
                            acceptedReportsCount = publication.reportes.count { it.estado == EstadoReporte.APROBADO }
                        )
                    }
            }
            .sortedByDescending { it.report.fechaCreacion }
    }

    private fun findReportCase(reportId: String): AdminReportCase? =
        _reportCases.value.firstOrNull { it.report.id == reportId }

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
}



