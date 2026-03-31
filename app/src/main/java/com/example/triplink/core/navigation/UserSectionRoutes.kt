package com.example.triplink.core.navigation

import kotlinx.serialization.Serializable

sealed class UserSectionRoutes {

    @Serializable
    data object UserHome : UserSectionRoutes()

    @Serializable
    data object Explore : UserSectionRoutes()

    @Serializable
    data object UserInfo : UserSectionRoutes()

    @Serializable
    data object AccountEdit : UserSectionRoutes()
}

