package com.example.triplink.features.admin.reports

import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.admin.AdminReportCase
import com.example.triplink.domain.repository.admin.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminReportsViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    val pendingCount: Int
        get() = repository.pendingReportsCount

    val reportCards: List<AdminReportUi>
        get() = repository.reportCases.map { it.toUiModel() }

    fun getReportById(reportId: String): AdminReportUi? = repository.getReportById(reportId)?.toUiModel()

    fun confirmReport(reportId: String) {
        repository.confirmReport(reportId)
    }

    fun invalidateReport(reportId: String) {
        repository.invalidateReport(reportId)
    }

    private fun AdminReportCase.toUiModel(): AdminReportUi = AdminReportUi(
        report = report,
        pointOfInterest = pointOfInterest,
        reporterName = reporterName,
        acceptedReportsCount = acceptedReportsCount
    )
}
