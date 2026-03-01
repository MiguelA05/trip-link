package com.example.triplink.features.login

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel() // Se crea o se obtiene el ViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 16.dp, alignment = CenterVertically)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.email.value, // Estado del campo de email desde el ViewModel
            onValueChange = { viewModel.email.onChange(it) }, // Actualiza el estado en el ViewModel
            label = {
                Text(text = "Email")
            },
            isError = viewModel.email.error != null, // Se muestra el borde rojo si hay error
            supportingText = viewModel.email.error?.let { error ->
                { Text(text = error) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.password.value, // Estado del campo de password desde el ViewModel
            onValueChange = { viewModel.password.onChange(it) }, // Actualiza el estado en el ViewModel
            visualTransformation = PasswordVisualTransformation(),
            label = {
                Text(text = "Password")
            },
            isError = viewModel.password.error != null, // Se muestra el borde rojo si hay error
            supportingText = viewModel.password.error?.let { error ->
                { Text(text = error) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Button(
            onClick = {
                // Se imprime el email y password en el logcat por ahora
                Log.d(
                    "Login",
                    "Email: ${viewModel.email.value}, Password: ${viewModel.password.value}"
                )
            },
            enabled = viewModel.isFormValid,
            content = {
                Text(text = "Iniciar Sesión")
            }
        )

    }
}