package com.example.triplink.features.recoverypassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triplink.R
import com.example.triplink.core.components.AppTitle
import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.GeneralAlertDialog
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.PrincipalRed
import com.example.triplink.ui.theme.PrincipalWhite
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryPasswordScreen(
    onBack: () -> Unit,
    viewModel: RecoveryPasswordViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val recoveryMessage = "Introduce el correo electrónico de tu cuenta y te enviaremos un " +
            "correo electrónico con un enlace para recuperar tu contraseña"
    val recoveryResendMessage = "¿No recibiste el correo electrónico o el enlace ya ha caducado? " +
            "Revisa el correo electrónico que introdujiste y te reenviaremos el correo de recuperación"
    val recoveryResult by viewModel.recoveryResult.collectAsState()

    LaunchedEffect(recoveryResult) {
        recoveryResult?.let { result ->
            val message = when (result) {
                is RequestResult.Success -> result.message
                is RequestResult.Failure -> result.errorMessage
            }
            snackbarHostState.showSnackbar(message)

            if (result is RequestResult.Success) {
                delay(1000)
            }
            viewModel.resetRecoveryResult()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val isError = recoveryResult is RequestResult.Failure
                Snackbar(
                    containerColor = if (isError) PrincipalRed else PrincipalBlue,
                    contentColor = PrincipalWhite
                ) {
                    Text(
                        text = data.visuals.message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        topBar = {
            GeneralTopBar(
                title = "Recuperar Contraseña",
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 32.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            Image(
                modifier = Modifier.width(74.dp),
                painter = painterResource(R.drawable.logo),
                contentDescription = "Icono de la aplicacion"
            )
            AppTitle()
            Text(
                textAlign = TextAlign.Center,
                text = if (!viewModel.isEmailSent) recoveryMessage else recoveryResendMessage,
                style = MaterialTheme.typography.bodyLarge
            )

            FormField(
                label = "Correo Electrónico",
                value = viewModel.email.value,
                onValueChange = { viewModel.email.onChange(it) },
                placeholder = "tu@email.com",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = viewModel.email.error != null,
                errorText = viewModel.email.error
            )

            GeneralButton(
                primary = true,
                onClick = {
                    viewModel.sendPasswordResetEmail()
                },
                enabled = viewModel.isFormValid,
                text = if (!viewModel.isEmailSent) "Enviar Correo" else "Reenviar Correo"
            )
        }
    }

    if (viewModel.showSuccessDialog) {
        GeneralAlertDialog(
            onDismissRequest = { viewModel.dismissDialog() },
            onConfirm = { viewModel.dismissDialog() },
            title = "Revisa tu correo",
            message = "Comprueba tu bandeja de entrada y sigue el enlace para reestablecer tu contraseña de forma segura",
            icon = Icons.Default.Email
        )
    }
}