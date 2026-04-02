package com.example.triplink.features.admin.reports.AdminReportDetails

import androidx.lifecycle.ViewModel
import com.example.triplink.domain.repository.admin.reports.AdminReportsRepository
import com.example.triplink.features.admin.reports.AdminReportUi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminReportsDetailsViewModel @Inject constructor(
    private val repository: AdminReportsRepository
) : ViewModel() {

	fun getReportById(reportId: String): AdminReportUi? = repository.getReportById(reportId)

	fun confirmReport(reportId: String) {
		repository.confirmReport(reportId)
	}

	fun invalidateReport(reportId: String) {
		repository.invalidateReport(reportId)
	}
}