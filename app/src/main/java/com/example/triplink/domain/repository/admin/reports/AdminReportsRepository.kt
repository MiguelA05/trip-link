package com.example.triplink.domain.repository.admin.reports

import com.example.triplink.features.admin.reports.AdminReportUi

interface AdminReportsRepository {
    val pendingCount: Int
    val reportCards: List<AdminReportUi>
    fun getReportById(reportId: String): AdminReportUi?
    fun confirmReport(reportId: String)
    fun invalidateReport(reportId: String)
}






