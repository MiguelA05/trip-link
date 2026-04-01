package com.example.triplink.core.navigation

import kotlinx.serialization.Serializable

sealed class AdminRoutes {

    @Serializable
    data object Moderation : AdminRoutes()

    @Serializable
    data object Reports : AdminRoutes()

    @Serializable
    data class ReportDetails(val reportId: String) : AdminRoutes()
}

