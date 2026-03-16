package com.example.triplink.core.navigation

import kotlinx.serialization.Serializable

sealed class MainRoutes {

    @Serializable
    data object Home : MainRoutes()

    @Serializable
    data object Login : MainRoutes()

    @Serializable
    data object Register : MainRoutes()

    @Serializable
    data object UserHome : MainRoutes()

    @Serializable
    data object RecoveryPassword: MainRoutes()
}