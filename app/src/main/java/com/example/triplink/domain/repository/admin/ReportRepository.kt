package com.example.triplink.domain.repository.admin

import com.example.triplink.domain.model.admin.AdminReportCase

interface ReportRepository {
    val pendingReportsCount: Int
    val reportCases: List<AdminReportCase>

    fun getReportById(reportId: String): AdminReportCase?
    fun confirmReport(reportId: String)
    fun invalidateReport(reportId: String)
}

