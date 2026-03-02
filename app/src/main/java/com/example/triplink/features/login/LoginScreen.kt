package com.example.triplink.features.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Icono de la aplicacion",
            Modifier.size(200.dp)
        )

        AppTitle()

        Text(
            text = "Bienvenido de nuevo",
            modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp),
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
                val icon = if (viewModel.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (viewModel.passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                    Icon(imageVector = icon, contentDescription = description)
                }
            }
        )

        val isHovered by viewModel.forgotPasswordInteractionSource.collectIsHoveredAsState()
        TextButton(
            onClick = {
                // Navegar a la pantalla de recuperación de contraseña
            },
            interactionSource = viewModel.forgotPasswordInteractionSource,
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color(0xFF42A5F5)
            ),
            modifier = Modifier.padding(bottom = 8.dp).background(
                color = if (isHovered) Color(0xFF42A5F5).copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)

            )
        ) {
            Text(text = "¿Olvidaste tu contraseña?")
        }

        GeneralButton(
            primary = true,
            onClick = {
                Log.d(
                    "Login",
                    "Email: ${viewModel.email.value}, Password: ${viewModel.password.value}"
                )
            },
            enabled = viewModel.isFormValid,
            text = "Iniciar Sesión"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 0.dp, alignment = Alignment.CenterHorizontally)
        ) {
            Text(text = "¿No tienes una cuenta?")
            TextButton(
                onClick = {
                    // Navegar a la pantalla de recuperación de contraseña
                },
                interactionSource = viewModel.forgotPasswordInteractionSource,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF42A5F5)
                ),
                modifier = Modifier.background(
                    color = if (isHovered) Color(0xFF42A5F5).copy(alpha = 0.1f) else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                )
            ) {
                Text(text = "Crea tu cuenta")
            }
        }
    }
}
