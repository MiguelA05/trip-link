package com.example.triplink.core.navigation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.triplink.core.navigation.main.MainRoutes
import com.example.triplink.features.appHome.HomeScreen
import com.example.triplink.features.login.LoginScreen
import com.example.triplink.features.recoverypassword.RecoveryPasswordScreen
import com.example.triplink.features.register.RegisterScreen
import com.example.triplink.features.resetpassword.ResetPasswordScreen

@Composable
fun AuthNavigation(
    deepLink: android.net.Uri? = null,
    onResetPasswordSuccess: () -> Unit = {}
) {
    val navController = rememberNavController()

    LaunchedEffect(deepLink) {
        deepLink?.let { uri ->
            val resetPasswordDeepLink = uri.toResetPasswordDeepLink()
            if (resetPasswordDeepLink != null) {
                navController.navigate(MainRoutes.ResetPassword(resetPasswordDeepLink.oobCode))
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = MainRoutes.Home
    ) {

        composable<MainRoutes.Home> {
            // Tal como antes, se pasan los callbacks de navegación
            HomeScreen(
                onNavigateToLogin = {
                    navController.navigate(MainRoutes.Login)
                },
                onNavigateToRegister = {
                    navController.navigate(MainRoutes.Register)
                }
            )
        }

        composable<MainRoutes.Login> {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToRegister = {
                    navController.navigate(MainRoutes.Register)
                },
                onNavigateToRecovery = {
                    navController.navigate(MainRoutes.RecoveryPassword)
                }
            )
        }

        composable<MainRoutes.Register> {
            RegisterScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginClick = {
                    navController.navigate(MainRoutes.Login)
                },
                onRegisterSuccess = {
                    navController.navigate(MainRoutes.Login) {
                        popUpTo(MainRoutes.Register) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<MainRoutes.RecoveryPassword> {
            RecoveryPasswordScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<MainRoutes.ResetPassword> { backStackEntry ->
            val route = backStackEntry.toRoute <MainRoutes.ResetPassword>()
            // Aquí iría la pantalla de restablecimiento de contraseña, si es diferente a la de recuperación
            ResetPasswordScreen(
                oobCode = route.oobCode,
                onPasswordChanged = {
                    navController.navigate(MainRoutes.Login) {
                        popUpTo(MainRoutes.ResetPassword(route.oobCode)) { inclusive = true }
                        launchSingleTop = true
                    }
                    onResetPasswordSuccess()
                }
            )
        }

        // Otras pantallas que no requieren autenticación pueden ir aquí
    }
}
