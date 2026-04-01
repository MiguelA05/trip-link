package com.example.triplink.features.admin.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.triplink.data.repository.admin.reports.AdminReportsRepository

class AdminReportsViewModel(
    private val repository: AdminReportsRepository
) : ViewModel() {

    val pendingCount: Int
        get() = repository.pendingCount

    val reportCards: List<AdminReportUi>
        get() = repository.reportCards

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
                    return AdminReportsViewModel(repository) as T
                }
            }
    }
}


