package com.example.triplink.core.navigation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.triplink.data.model.UserSession
import com.example.triplink.domain.model.enums.Rol
import com.example.triplink.features.admin.section.AdminSectionScreen
import com.example.triplink.features.user.section.UserSectionScreen

@Composable
fun MainNavigation(
    session: UserSession,
    onLogout: () -> Unit,
    pendingPublicationId: String? = null,
    onPendingPublicationConsumed: () -> Unit = {}
) {
    val navController = rememberNavController()

    // Determina la pantalla de inicio según el rol del usuario
    val startDestination: Any = when (session.role) {
        Rol.MODERADOR -> MainRoutes.AdminSection
        Rol.USUARIO -> MainRoutes.UserSection
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable<MainRoutes.UserSection> {
                // Se pasa el callback de logout para cerrar sesión, úselo dentro de UserScreen
                UserSectionScreen(
                    userId = session.userId,
                    onLogout = onLogout,
                    openPublicationId = pendingPublicationId,
                    onOpenPublicationConsumed = onPendingPublicationConsumed
                )
            }

            composable<MainRoutes.AdminSection> {
                // Si no tiene AdminScreen debe crearlo, se pasa el callback de logout para cerrar sesión, úselo dentro de AdminScreen
                AdminSectionScreen(
                    onLogout = onLogout
                )
            }
        }
    }
}