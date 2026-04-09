package com.example.triplink.features.admin.reports.AdminReportDetails

import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.admin.AdminReportCase
import com.example.triplink.domain.repository.admin.ReportRepository
import com.example.triplink.features.admin.reports.AdminReportUi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminReportsDetailsViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

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