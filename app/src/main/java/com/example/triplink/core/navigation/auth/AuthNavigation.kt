package com.example.triplink.core.navigation.auth

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.triplink.core.navigation.main.MainRoutes
import com.example.triplink.features.appHome.HomeScreen
import com.example.triplink.features.login.LoginScreen
import com.example.triplink.features.recoverypassword.RecoveryPasswordScreen
import com.example.triplink.features.register.RegisterScreen

@Composable
fun AuthNavigation() {
    val navController = rememberNavController()

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

        // Otras pantallas que no requieren autenticación pueden ir aquí
    }
}