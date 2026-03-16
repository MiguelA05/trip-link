package com.example.triplink.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.triplink.features.home.HomeScreen
import com.example.triplink.features.login.LoginScreen
import com.example.triplink.features.register.RegisterScreen

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

                )
            }

            composable<MainRoutes.Register> {
                RegisterScreen()
            }

        }
    }
}