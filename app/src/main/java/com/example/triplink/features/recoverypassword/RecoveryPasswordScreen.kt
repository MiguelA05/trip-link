package com.example.triplink.features.recoverypassword

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
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triplink.R
import com.example.triplink.core.components.AppTitle
import com.example.triplink.core.components.FormField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryPasswordScreen(
    buttonText: String = "Enviar Correo",
    descriptionText: String = "Introduce el correo electrónico de tu cuenta y te enviaremos un correo electrónico " +
            "con un enlace para recuperar tu contraseña" ,
    viewModel: RecoveryPasswordViewModel = viewModel()
){

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(

                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal,

                        text = "Recuperar Contraseña"
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
                text = descriptionText
            )

            FormField(
                label = "Correo Electrónico",
                value = viewModel.email.value,
                onValueChange = { viewModel.email.onChange(it) },
                placeholder = "tu@email.com",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )


            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                onClick = {
                    viewModel.sendPasswordResetEmail()
                },
                enabled = viewModel.isFormValid,
                content = {
                    Text(text = buttonText)
                }
            )
        }
    }
}