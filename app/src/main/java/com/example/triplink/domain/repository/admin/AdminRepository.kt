package com.example.triplink.domain.repository.admin

import com.example.triplink.domain.model.enums.moderator.DecisionModerador
import com.example.triplink.domain.model.enums.moderator.ModerationFilter
import com.example.triplink.domain.model.moderator.ModerationPublication
import com.example.triplink.features.admin.reports.AdminReportUi

interface AdminRepository {
    val pendingModerationCount: Int
    val verifiedModerationCount: Int
    val rejectedModerationCount: Int

    val pendingReportsCount: Int
    val reportCards: List<AdminReportUi>

    fun getModerationPublicationById(publicationId: String): ModerationPublication?
    fun moderationPublicationsFor(filter: ModerationFilter): List<ModerationPublication>
    fun applyModerationDecision(
        publicationId: String,
        decision: DecisionModerador,
        reason: String? = null
    )

    fun getReportById(reportId: String): AdminReportUi?
    fun confirmReport(reportId: String)
    fun invalidateReport(reportId: String)
}

