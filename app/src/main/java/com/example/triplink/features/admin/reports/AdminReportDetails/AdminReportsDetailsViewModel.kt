package com.example.triplink.features.admin.reports.AdminReportDetails

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.triplink.domain.model.admin.AdminReportCase
import com.example.triplink.domain.repository.admin.ReportRepository
import com.example.triplink.features.admin.reports.AdminReportUi
import com.example.triplink.features.admin.reports.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class AdminReportsDetailsViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val repository: ReportRepository
) : ViewModel() {

    fun getReportById(reportId: String): AdminReportUi? = repository.getReportById(reportId)?.toUiModel()

    fun confirmReport(reportId: String) {
        repository.confirmReport(reportId)
    }

    fun invalidateReport(reportId: String) {
        repository.invalidateReport(reportId)
    }

    private fun AdminReportCase.toUiModel(): AdminReportUi = toUi(appContext)
}