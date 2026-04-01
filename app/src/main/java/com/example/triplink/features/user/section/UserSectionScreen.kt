package com.example.triplink.features.user.section

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.triplink.core.components.navigation.BottomBar
import com.example.triplink.core.components.navigation.defaultNavItems
import com.example.triplink.core.navigation.UserNavigation
import com.example.triplink.core.navigation.UserSectionRoutes

@Composable
fun UserSectionScreen(
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navItems = defaultNavItems()
    val tabRoutes = listOf(
        UserSectionRoutes.UserHome,
        UserSectionRoutes.Explore,
        UserSectionRoutes.UserInfo
    )
    val routesWithBottomBar = tabRoutes + UserSectionRoutes.ExploreMap

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isTabRoute = routesWithBottomBar.any { route ->
        currentDestination?.hasRoute(route::class) == true
    }
    val selectedIndex = when {
        currentDestination?.hasRoute(UserSectionRoutes.ExploreMap::class) == true -> 1
        else -> tabRoutes.indexOfFirst { route ->
            currentDestination?.hasRoute(route::class) == true
        }.takeIf { it >= 0 } ?: 0
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (isTabRoute) {
                BottomBar(
                    items = navItems,
                    selectedIndex = selectedIndex,
                    onItemSelected = { index ->
                        navController.navigate(tabRoutes[index]) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        UserNavigation(
            navController = navController,
            padding = paddingValues,
            onLogout = onLogout
        )
    }
}
