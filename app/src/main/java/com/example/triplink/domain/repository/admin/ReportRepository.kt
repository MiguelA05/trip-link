package com.example.triplink.domain.repository.admin

import com.example.triplink.domain.model.admin.AdminReportCase
import com.example.triplink.domain.model.Reporte
import kotlinx.coroutines.flow.StateFlow

interface ReportRepository {
    val pendingReportsCount: Int
    val reportCases: StateFlow<List<AdminReportCase>>

    fun hasUserReportedPublication(userId: String, publicationId: String): Boolean
    fun submitReport(report: Reporte): Boolean
    fun getReportById(reportId: String): AdminReportCase?
    fun confirmReport(reportId: String)
    fun invalidateReport(reportId: String)
}

