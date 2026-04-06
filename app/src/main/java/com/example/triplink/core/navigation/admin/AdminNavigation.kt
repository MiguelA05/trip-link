package com.example.triplink.core.navigation.admin

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.triplink.core.navigation.admin.AdminRoutes
import com.example.triplink.data.repository.admin.moderation.AdminModerationRepositoryImpl
import com.example.triplink.features.admin.moderation.ModerationScreen
import com.example.triplink.features.admin.moderation.ModerationPublicationDetails.ModerationPublicationDetailsScreen
import com.example.triplink.features.admin.reports.AdminReportsScreen
import com.example.triplink.features.admin.reports.AdminReportDetails.AdminReportDetailsScreen
import com.example.triplink.domain.repository.admin.reports.AdminReportsRepository

@Composable
fun AdminNavigation(
    navController: NavHostController,
    padding: PaddingValues
) {
    val moderationRepository = remember { AdminModerationRepositoryImpl() }
    val reportsRepository = remember { AdminReportsRepository() }

    NavHost(
        navController = navController,
        startDestination = AdminRoutes.Moderation
    ) {
        composable<AdminRoutes.Moderation> {
            ModerationScreen(
                contentPadding = padding,
                repository = moderationRepository,
                onPublicationDetailsClick = { publicationId ->
                    navController.navigate(AdminRoutes.ModerationPublicationDetails(publicationId))
                }
            )
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

        composable<AdminRoutes.ModerationPublicationDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<AdminRoutes.ModerationPublicationDetails>()
            ModerationPublicationDetailsScreen(
                publicationId = route.publicationId,
                contentPadding = padding,
                repository = moderationRepository,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
