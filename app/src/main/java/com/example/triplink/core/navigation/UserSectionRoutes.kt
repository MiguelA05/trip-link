package com.example.triplink.core.navigation

import kotlinx.serialization.Serializable

sealed class UserSectionRoutes {

    @Serializable
    data object UserHome : UserSectionRoutes()

    @Serializable
    data object Explore : UserSectionRoutes()

    @Serializable
    data object ExploreMap : UserSectionRoutes()

    @Serializable
    data object UserInfo : UserSectionRoutes()

    @Serializable
    data object AccountEdit : UserSectionRoutes()

    @Serializable
    data class PublicationDetails(val publicationId: String) : UserSectionRoutes()

    @Serializable
    data class Comments(val publicationId: String) : UserSectionRoutes()

    @Serializable
    data object Notifications : UserSectionRoutes()

    @Serializable
    data object Filters : UserSectionRoutes()
}

