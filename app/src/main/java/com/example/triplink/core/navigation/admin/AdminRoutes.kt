package com.example.triplink.core.navigation.admin

import kotlinx.serialization.Serializable

@Serializable
sealed class AdminRoutes {

    @Serializable
    data object Moderation : AdminRoutes()

    @Serializable
    data object Reports : AdminRoutes()

    @Serializable
    data class ReportDetails(val reportId: String) : AdminRoutes()

    @Serializable
    data class ModerationPublicationDetails(val publicationId: String) : AdminRoutes()
}