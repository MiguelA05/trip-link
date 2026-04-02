package com.example.triplink.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.triplink.features.admin.moderation.ModerationScreen
import com.example.triplink.features.admin.moderation.ModerationPublicationDetails.ModerationPublicationDetailsScreen
import com.example.triplink.features.admin.reports.AdminReportsScreen
import com.example.triplink.features.admin.reports.AdminReportDetails.AdminReportDetailsScreen

@Composable
fun AdminNavigation(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = AdminRoutes.Moderation
    ) {
        composable<AdminRoutes.Moderation> {
            ModerationScreen(
                contentPadding = padding,
                onPublicationDetailsClick = { publicationId ->
                    navController.navigate(AdminRoutes.ModerationPublicationDetails(publicationId))
                }
            )
        }
        composable<AdminRoutes.Reports> {
            AdminReportsScreen(
                contentPadding = padding,
                onReportClick = { reportId ->
                    navController.navigate(AdminRoutes.ReportDetails(reportId))
                }
            )
        }
        composable<AdminRoutes.ReportDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<AdminRoutes.ReportDetails>()
            AdminReportDetailsScreen(
                reportId = route.reportId,
                contentPadding = padding,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<AdminRoutes.ModerationPublicationDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<AdminRoutes.ModerationPublicationDetails>()
            ModerationPublicationDetailsScreen(
                publicationId = route.publicationId,
                contentPadding = padding,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
