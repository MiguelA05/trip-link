package com.example.triplink.features.user.section

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.toRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.triplink.core.components.navigation.BottomBar
import com.example.triplink.core.components.navigation.defaultNavItems
import com.example.triplink.core.navigation.UserSectionRoutes
import com.example.triplink.features.comments.CommentsScreen
import com.example.triplink.features.publicationDetails.PublicationDetailsScreen
import com.example.triplink.features.user.accountEdit.AccountEditScreen
import com.example.triplink.features.user.explore.ExploreScreen
import com.example.triplink.features.user.home.UserHomeScreen
import com.example.triplink.features.user.info.UserInfoScreen

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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isTabRoute = tabRoutes.any { route ->
        currentDestination?.hasRoute(route::class) == true
    }
    val selectedIndex = tabRoutes.indexOfFirst { route ->
        currentDestination?.hasRoute(route::class) == true
    }.takeIf { it >= 0 } ?: 0

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
        NavHost(
            navController = navController,
            startDestination = UserSectionRoutes.UserHome,
            modifier = Modifier
                .fillMaxSize()
        ) {
            composable<UserSectionRoutes.UserHome> {
                UserHomeScreen(
                    contentPadding = paddingValues,
                    onCommentsClick = { publicationId ->
                        navController.navigate(UserSectionRoutes.PublicationDetails(publicationId))
                    }
                )
            }
            composable<UserSectionRoutes.Explore> {
                ExploreScreen(contentPadding = paddingValues)
            }
            composable<UserSectionRoutes.UserInfo> {
                UserInfoScreen(
                    contentPadding = paddingValues,
                    onLogoutClick = onLogout,
                    onEditClick = {
                        navController.navigate(UserSectionRoutes.AccountEdit)
                    }
                )
            }
            composable<UserSectionRoutes.AccountEdit> {
                AccountEditScreen(
                    onBackClick = {
                        val navigatedBack = navController.popBackStack()
                        if (!navigatedBack) {
                            navController.navigate(UserSectionRoutes.UserInfo) {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
            composable<UserSectionRoutes.PublicationDetails> { backStackEntry ->
                val route = backStackEntry.toRoute<UserSectionRoutes.PublicationDetails>()
                PublicationDetailsScreen(
                    publicationId = route.publicationId,
                    onBackClick = { navController.popBackStack() },
                    onSeeAllReviewsClick = { publicationId ->
                        navController.navigate(UserSectionRoutes.Comments(publicationId))
                    }
                )
            }
            composable<UserSectionRoutes.Comments> { backStackEntry ->
                val route = backStackEntry.toRoute<UserSectionRoutes.Comments>()
                CommentsScreen(
                    publicationId = route.publicationId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}


