package com.example.triplink.data.repository.admin

import com.example.triplink.data.seed.AdminReportsSeedState
import com.example.triplink.data.seed.createAdminReportsSeedState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminReportsStore @Inject constructor() {
    internal val seedState: AdminReportsSeedState = createAdminReportsSeedState()
    val acceptedReportThreshold: Int = 1
}



