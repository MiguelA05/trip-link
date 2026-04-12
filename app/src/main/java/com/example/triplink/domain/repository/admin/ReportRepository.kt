package com.example.triplink.domain.repository.admin

import com.example.triplink.domain.model.admin.AdminReportCase
import com.example.triplink.domain.model.Reporte

interface ReportRepository {
    val pendingReportsCount: Int
    val reportCases: List<AdminReportCase>

    fun hasUserReportedPublication(userId: String, publicationId: String): Boolean
    fun submitReport(report: Reporte): Boolean
    fun getReportById(reportId: String): AdminReportCase?
    fun confirmReport(reportId: String)
    fun invalidateReport(reportId: String)
}

