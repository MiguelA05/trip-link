package com.example.triplink.features.resetpassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triplink.R
import com.example.triplink.core.components.AppTitle
import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.GeneralButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    viewModel: ResetPasswordViewModel = viewModel()
){


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(

                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal,

                        text = "Restablecer Contraseña"
                    )
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier.clickable {
                            // Acción para volver
                        },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Icono de navegacion hacia atras para volver"
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
                text = "Es momento de restablecer tu contraseña! Introduce una nueva contraseña para " +
                        "tu cuenta"
            )

            //TODO: Poner dos campos para contraseña con las variables viewmodel correspondientes
            /*
            FormField(
                label = "Correo Electrónico",
                value = viewModel.email.value,
                onValueChange = { viewModel.email.onChange(it) },
                placeholder = "tu@email.com",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )*/

            FormField(
                label = "Contraseña",
                value = viewModel.password.value,
                onValueChange = { viewModel.password.onChange(it) },
                placeholder = "Escribe tu nueva contraseña",
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (viewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = viewModel.password.error != null,
                errorText = viewModel.password.error,
                trailingIcon = {
                    val icon = if (viewModel.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (viewModel.passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Icon(imageVector = icon, contentDescription = description)
                    }
                }
            )

            FormField(
                label = "Confirmar nueva contraseña",
                value = viewModel.confirmPassword.value,
                onValueChange = { viewModel.confirmPassword.onChange(it) },
                placeholder = "Escribe tu nueva contraseña",
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (viewModel.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = viewModel.confirmPassword.error != null,
                errorText = viewModel.confirmPassword.error,
                trailingIcon = {
                    val icon = if (viewModel.confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (viewModel.confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                    IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                        Icon(imageVector = icon, contentDescription = description)
                    }
                }
            )

            GeneralButton(
                primary = true,
                onClick = {
                    viewModel.resetPassword()
                },
                enabled = viewModel.isFormValid,
                text = "Restablecer Contraseña"
            )
        }
    }
}