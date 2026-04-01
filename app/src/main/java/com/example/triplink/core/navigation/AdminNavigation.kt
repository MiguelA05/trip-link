package com.example.triplink.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.triplink.features.admin.moderation.ModerationScreen
import com.example.triplink.features.admin.reports.AdminReportsScreen
import com.example.triplink.features.admin.reports.AdminReportDetails.AdminReportDetailsScreen
import com.example.triplink.data.repository.admin.reports.AdminReportsRepository

@Composable
fun AdminNavigation(
    navController: NavHostController,
    padding: PaddingValues
) {
    val reportsRepository = remember { AdminReportsRepository() }

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
                repository = reportsRepository,
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
                repository = reportsRepository,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
