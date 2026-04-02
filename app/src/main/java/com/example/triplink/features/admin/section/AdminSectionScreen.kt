package com.example.triplink.features.admin.section

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.triplink.core.components.GeneralAlertDialog
import com.example.triplink.core.components.navigation.BottomBar
import com.example.triplink.core.components.navigation.adminNavItems
import com.example.triplink.core.navigation.AdminNavigation
import com.example.triplink.core.navigation.AdminRoutes

@Composable
fun AdminSectionScreen(
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navItems = adminNavItems()
    val routesForTabs = listOf(AdminRoutes.Moderation, AdminRoutes.Reports)
    var showLogoutDialog by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val selectedIndex = when {
        currentDestination?.hasRoute(AdminRoutes.Moderation::class) == true -> 1
        currentDestination?.hasRoute(AdminRoutes.Reports::class) == true -> 2
        else -> 1
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            BottomBar(
                items = navItems,
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    when (index) {
                        0 -> showLogoutDialog = true
                        1 -> navController.navigate(routesForTabs[0]) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        2 -> navController.navigate(routesForTabs[1]) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        AdminNavigation(
            navController = navController,
            padding = paddingValues
        )
    }

    if (showLogoutDialog) {
        GeneralAlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            title = "¿Cerrar sesión?",
            message = "Se cerrará tu sesión en este dispositivo.\nPodrás volver a ingresar en cualquier momento.",
            icon = Icons.AutoMirrored.Outlined.Logout,
            buttonText = "Cerrar sesión",
            dismissButtonText = "Cancelar",
            onDismissAction = { showLogoutDialog = false }
        )
    }
}
