package com.example.triplink.features.admin.reports

import androidx.lifecycle.ViewModel
import com.example.triplink.domain.repository.admin.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminReportsViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    val pendingCount: Int
        get() = repository.pendingReportsCount

    val reportCards: List<AdminReportUi>
        get() = repository.reportCards

    fun getReportById(reportId: String): AdminReportUi? = repository.getReportById(reportId)

    fun confirmReport(reportId: String) {
        repository.confirmReport(reportId)
    }

    fun invalidateReport(reportId: String) {
        repository.invalidateReport(reportId)
    }
}
