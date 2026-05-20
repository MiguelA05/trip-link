package com.example.triplink.core.navigation.main

import kotlinx.serialization.Serializable

sealed class MainRoutes {

    @Serializable
    data object Home : MainRoutes()

    @Serializable
    data object Login : MainRoutes()

    @Serializable
    data object Register : MainRoutes()

    @Serializable
    data object UserSection : MainRoutes()

    @Serializable
    data object AdminSection : MainRoutes()

    @Serializable
    data object RecoveryPassword: MainRoutes()
    @Serializable
    data class ResetPassword ( val oobCode: String) : MainRoutes()
}