package com.example.triplink.features.admin.reports.AdminReportDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.triplink.features.admin.reports.AdminReportUi
import com.example.triplink.data.repository.admin.reports.AdminReportsRepository

class AdminReportsDetailsViewModel(
    private val repository: AdminReportsRepository
) : ViewModel() {

	fun getReportById(reportId: String): AdminReportUi? = repository.getReportById(reportId)

	fun confirmReport(reportId: String) {
		repository.confirmReport(reportId)
	}

	fun invalidateReport(reportId: String) {
		repository.invalidateReport(reportId)
	}

	companion object {
		fun factory(repository: AdminReportsRepository): ViewModelProvider.Factory =
			object : ViewModelProvider.Factory {
				@Suppress("UNCHECKED_CAST")
				override fun <T : ViewModel> create(modelClass: Class<T>): T {
					return AdminReportsDetailsViewModel(repository) as T
				}
			}
	}
}