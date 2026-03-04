package com.example.triplink.features.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triplink.R
import com.example.triplink.core.components.AppTitle
import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.LinkTextRow
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.ui.theme.PastelBlue
import kotlinx.coroutines.delay
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.PrincipalRed
import com.example.triplink.ui.theme.PrincipalWhite


@Composable
fun LoginScreen(
    onNavigateToUsers: () -> Unit,
    viewModel: LoginViewModel = viewModel() // Se crea o se obtiene el ViewModel
) {

    // Estado para gestionar los snackbars
    val snackbarHostState = remember { SnackbarHostState() }
    // Observar el estado de loginResult
    val loginResult by viewModel.loginResult.collectAsState()

    // Efecto para mostrar el snackbar cuando hay resultado
    LaunchedEffect(loginResult) {
        loginResult?.let { result ->
            // Obtener el mensaje según el resultado
            val message = when (result) {
                is RequestResult.Success -> result.message
                is RequestResult.Failure -> result.errorMessage
            }
            snackbarHostState.showSnackbar(message) // Mostrar el snackbar con el mensaje

            // Navegar a la pantalla de usuarios si el login fue exitoso. Se puede agregar un delay para que el usuario alcance a ver el mensaje
            if (result is RequestResult.Success) {
                delay(1000) // 2 segundos
                onNavigateToUsers()
            }

            // Reseta el estado del loginResult en el ViewModel después de mostrar el mensaje
            viewModel.resetLoginResult()
        }
    }

    // Se envuelve el contenido dentro de un Scaffold
    Scaffold(
        snackbarHost = {
            // Mostrar el SnackbarHost para gestionar los snackbars. Un SnackbarHost es un contenedor que muestra los snackbars.
            SnackbarHost(snackbarHostState) { data ->
                val isError = loginResult is RequestResult.Failure
                // Mostrar el Snackbar con el estilo adecuado según si es error o éxito
                Snackbar(
                    containerColor = if (isError) PrincipalRed else PrincipalBlue,
                    contentColor = PrincipalWhite
                ) {
                    Text(data.visuals.message)
                }
            }
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplicar los padding del Scaffold
                .padding(horizontal = 30.dp), // Padding horizontal adicional
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 16.dp, alignment = CenterVertically)
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Icono de la aplicacion",
                Modifier.size(100.dp)
            )

            AppTitle()

            Text(
                text = "Bienvenido de nuevo",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp),
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
            )

            FormField(
                label = "Email",
                value = viewModel.email.value,
                onValueChange = { viewModel.email.onChange(it) },
                placeholder = "ejemplo@correo.com",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = viewModel.email.error != null,
                errorText = viewModel.email.error
            )

            FormField(
                label = "Password",
                value = viewModel.password.value,
                onValueChange = { viewModel.password.onChange(it) },
                placeholder = "********",
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (viewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = viewModel.password.error != null,
                errorText = viewModel.password.error,
                trailingIcon = {
                    val icon =
                        if (viewModel.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description =
                        if (viewModel.passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Icon(imageVector = icon, contentDescription = description)
                    }
                })

            TextButton(
                onClick = {
                    // Navegar a la pantalla de recuperación de contraseña
                },
                interactionSource = viewModel.forgotPasswordInteractionSource,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = PrincipalBlue
                ),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(8.dp)

                    )
            ) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    fontSize = 16.sp,
                )
            }

            GeneralButton(
                primary = true,
                onClick = {
                    viewModel.login()
                },
                enabled = viewModel.isFormValid,
                text = "Iniciar Sesión"
            )

            LinkTextRow(
                text = "¿No tienes una cuenta?", buttonText = "Crea tu cuenta", onClick = {
                    // Navegar a la pantalla de registro
                })
        }
    }
}
