package com.example.triplink.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.core.navigation.auth.AuthNavigation
import com.example.triplink.core.navigation.main.MainNavigation
import com.example.triplink.domain.model.enums.Rol
import com.example.triplink.features.login.LoginRole

@Composable
fun AppNavigation(
    sessionViewModel: SessionViewModel = hiltViewModel()
) {
    // Observa el estado de la sesión desde el ViewModel
    val sessionState by sessionViewModel.sessionState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        when (val state = sessionState) {
            is SessionState.Loading -> {
                // Se muestra un indicador de carga mientras se determina el estado de la sesión
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SessionState.NotAuthenticated -> AuthNavigation(
                onAuthenticated = { role ->
                    when (role) {
                        LoginRole.USER -> sessionViewModel.login(userId = "user-demo", role = Rol.USUARIO)
                        LoginRole.ADMIN -> sessionViewModel.login(userId = "admin-demo", role = Rol.MODERADOR)
                    }
                }
            )
            is SessionState.Authenticated -> MainNavigation(
                session = state.session,
                onLogout = sessionViewModel::logout
            )
        }
    }
}
