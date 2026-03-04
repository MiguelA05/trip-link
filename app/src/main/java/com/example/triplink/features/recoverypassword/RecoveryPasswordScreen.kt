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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.utils.RequestResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryPasswordScreen(
    viewModel: RecoveryPasswordViewModel = viewModel()

){
    val recoveryMessage = "Introduce el correo electrónico de tu cuenta y te enviaremos un " +
            "correo electrónico con un enlace para recuperar tu contraseña"
    val recoveryResendMessage = "¿No recibiste el correo electrónico o el enlace ya ha caducado? " +
            "Revisa el correo electrónico que introdujiste y te reenviaremos el correo de recuperación"
    var recoveryResult = viewModel.recoveryResult.collectAsState()

    LaunchedEffect(recoveryResult) {
        when(recoveryResult){
            is RequestResult.Success -> {
                // Mostrar mensaje de éxito
            }
            is RequestResult.Failure -> {
                // Mostrar mensaje de error
            }
            else -> {}
        }

    }


    Scaffold(
        snackbarHost = {

        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Recuperar Contraseña",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton (
                        onClick = {

                        },
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    )
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 32.dp, alignment = Alignment.CenterVertically)
        ) {

            Image(
                modifier = Modifier.width(74.dp),
                painter = painterResource(R.drawable.logo),
                contentDescription = "Icono de la aplicacion"
            )
            AppTitle()
            Text(
                textAlign = TextAlign.Center,
                text = if (!viewModel.resendRecoveryPassword) recoveryMessage else recoveryResendMessage,
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
                text = if(!viewModel.resendRecoveryPassword) "Enviar Correo" else "Reenviar Correo"
            )

        }
    }


    if(viewModel.resendRecoveryPassword){
        AlertDialog(
            onDismissRequest = { viewModel.dismissDialog() },
            confirmButton = {
                Button(onClick = { viewModel.dismissDialog() }) {
                    Text("OK")
                }
            },
            title = { Text("Correo Enviado", style = MaterialTheme.typography.headlineSmall) },
            text = { Text("Revisa tu bandeja de entrada para continuar.", style = MaterialTheme.typography.bodyMedium) }
        )
    }



}