package com.example.triplink.domain.repository.admin

import com.example.triplink.domain.model.admin.AdminReportCase
import com.example.triplink.domain.model.Reporte
import kotlinx.coroutines.flow.StateFlow

interface ReportRepository {
    val pendingReportsCount: Int
    val reportCases: StateFlow<List<AdminReportCase>>

    suspend fun hasUserReportedPublication(userId: String, publicationId: String): Boolean
    suspend fun submitReport(report: Reporte): Boolean
    suspend fun getReportById(reportId: String): AdminReportCase?
    suspend fun confirmReport(reportId: String)
    suspend fun invalidateReport(reportId: String)
}

