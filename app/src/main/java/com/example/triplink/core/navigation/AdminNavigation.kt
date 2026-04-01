package com.example.triplink.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.triplink.features.admin.moderation.ModerationScreen
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
            ModerationScreen(contentPadding = padding)
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
            val route = backStackEntry.destination.route ?: return@composable
            val reportId = route.split("/").lastOrNull() ?: return@composable
            AdminReportDetailsScreen(
                reportId = reportId,
                contentPadding = padding,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
