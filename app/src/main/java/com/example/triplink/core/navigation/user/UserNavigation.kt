package com.example.triplink.core.navigation.user

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.triplink.core.navigation.main.MainRoutes
import com.example.triplink.core.navigation.user.UserSectionRoutes
import com.example.triplink.features.badges.BadgesScreen
import com.example.triplink.features.comments.CommentsScreen
import com.example.triplink.features.filters.FiltersScreen
import com.example.triplink.features.notifications.NotificationsScreen
import com.example.triplink.features.postCreation.PostCreationScreen
import com.example.triplink.features.publicationDetails.PublicationDetailsScreen
import com.example.triplink.features.recoverypassword.RecoveryPasswordScreen
import com.example.triplink.features.user.accountEdit.AccountEditScreen
import com.example.triplink.features.user.explore.ExploreScreen
import com.example.triplink.features.user.exploreMap.ExploreMapScreen
import com.example.triplink.features.user.home.UserHomeScreen
import com.example.triplink.features.user.info.UserInfoScreen

@Composable
fun UserNavigation(
    navController: NavHostController,
    padding: PaddingValues,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = UserSectionRoutes.UserHome
    ) {
        composable<UserSectionRoutes.UserHome> {
            UserHomeScreen(
                contentPadding = padding,
                onPublicationClick = { publicationId ->
                    navController.navigate(UserSectionRoutes.PublicationDetails(publicationId))
                },
                onCommentsClick = { publicationId ->
                    navController.navigate(UserSectionRoutes.Comments(publicationId))
                },
                onNotificationsClick = {
                    navController.navigate(UserSectionRoutes.Notifications)
                },
                onPostCreationClick = {
                    navController.navigate(UserSectionRoutes.PostCreation)
                }
            )
        }
        composable<UserSectionRoutes.Explore> {
            ExploreScreen(
                contentPadding = padding,
                onMapClick = {
                    navController.navigate(UserSectionRoutes.ExploreMap)
                },
                onPublicationClick = { publicationId ->
                    navController.navigate(UserSectionRoutes.PublicationDetails(publicationId))
                },
                onFiltersClick = {
                    navController.navigate(UserSectionRoutes.Filters)
                }
            )
        }
        composable<UserSectionRoutes.ExploreMap> {
            ExploreMapScreen(
                contentPadding = padding,
                onBackToExplore = {
                    if (!navController.popBackStack()) {
                        navController.navigate(UserSectionRoutes.Explore) {
                            launchSingleTop = true
                        }
                    }
                },
                onFiltersClick = {
                    navController.navigate(UserSectionRoutes.Filters)
                },
                onPublicationDetailsClick = { publicationId ->
                    navController.navigate(UserSectionRoutes.PublicationDetails(publicationId))
                }
            )
        }
        composable<UserSectionRoutes.UserInfo> {
            UserInfoScreen(
                contentPadding = padding,
                onLogoutClick = onLogout,
                onEditClick = {
                    navController.navigate(UserSectionRoutes.AccountEdit)
                },
                onBagdesClick = {
                    navController.navigate(UserSectionRoutes.Bagdes)
                },
                onPostCreationClick = {
                    navController.navigate(UserSectionRoutes.PostCreation)
                }
            )
        }
        composable<UserSectionRoutes.AccountEdit> {
            AccountEditScreen(
                onBackClick = {
                    if (!navController.popBackStack()) {
                        navController.navigate(UserSectionRoutes.UserInfo) {
                            launchSingleTop = true
                        }
                    }
                },
                onChangePasswordClick = {navController.navigate(MainRoutes.RecoveryPassword)},
                onAppHomeClick = {
                    navController.navigate(MainRoutes.Home) {
                        popUpTo(MainRoutes.Home) { inclusive = true }
                    }
                }
            )
        }
        composable<UserSectionRoutes.PublicationDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<UserSectionRoutes.PublicationDetails>()
            PublicationDetailsScreen(
                route.publicationId,
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
        composable<UserSectionRoutes.Notifications> {
            NotificationsScreen({ navController.popBackStack() })
        }
        composable<UserSectionRoutes.Filters> {
            FiltersScreen({ navController.popBackStack() })
        }
        composable<UserSectionRoutes.PostCreation> {
            PostCreationScreen(
                onBack = { navController.popBackStack() },
                onUserHomeClick = {
                    navController.navigate(UserSectionRoutes.UserHome)
                },
                onUserInfoClick = {
                    navController.navigate(UserSectionRoutes.UserInfo)
                }
            )
        }
        composable<MainRoutes.RecoveryPassword> {
            RecoveryPasswordScreen({ navController.popBackStack() })
        }
        composable<UserSectionRoutes.Bagdes> {
            BadgesScreen({ navController.popBackStack() })
        }

    }
}
