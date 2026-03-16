package com.example.triplink.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.triplink.features.appHome.HomeScreen
import com.example.triplink.features.login.LoginScreen
import com.example.triplink.features.recoverypassword.RecoveryPasswordScreen
import com.example.triplink.features.register.RegisterScreen
import com.example.triplink.features.userHome.UserHomeScreen

@Composable
fun AppNavigation() {
    // Estado de la navegación, permite controlar la navegación entre pantallas
    val navController = rememberNavController()

    // Un Surface que ocupa toda la pantalla y se adapta al tema de la aplicación
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController, // Controlador de navegación
            startDestination = MainRoutes.Home // Pantalla de inicio, esta es la primer pantalla que se muestra al iniciar la aplicación
        ) {

            // Definición de las rutas y sus composables asociados (se puede agregar más rutas según sea necesario)

            composable<MainRoutes.Home> {
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
                    onNavigateToUsers = {
                        navController.navigate(MainRoutes.UserHome) {
                            // Limpiar el stack de navegación para que no se pueda volver al login con el botón atrás
                            popUpTo(MainRoutes.Home) { inclusive = true }
                        }
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
                        navController.navigate(MainRoutes.Login) {
                            popUpTo(MainRoutes.Home)
                        }
                    }
                )
            }

            composable<MainRoutes.UserHome> {
                UserHomeScreen()
            }

            composable<MainRoutes.RecoveryPassword>{
                RecoveryPasswordScreen(
                    onBack = {
                        navController.popBackStack()
                    }

                )
            }

        }
    }
}
