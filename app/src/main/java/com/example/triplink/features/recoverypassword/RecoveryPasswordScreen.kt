package com.example.triplink.features.recoverypassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Label
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triplink.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryPasswordScreen(

    viewModel: RecoveryPasswordViewModel = viewModel()
){

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text(
                        text = "Recuperar Contraseña"
                    )
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier.clickable{

                        },
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Icono de navegacion hacia atras para volver"
                    )
                }
            )

        }
    ) { paddingValues ->

        Column(
            modifier = Modifier.padding(paddingValues)
        ) {

            Image(
                painter = painterResource(R.drawable.logo),
                "Icono de la aplicacion"
            )
            Row() {
                Text(
                    text = "Trip"
                )
                Text(
                    text = "Link"
                )

            }
            Text(
                text = "Introduce el correo electrónico de tu cuenta y te enviaremos un correo electrónico " +
                        "con un enlace para recuperar tu contraseña"
            )

            Text(
                text = "correo electronico"
            )

            OutlinedTextField(
                value = viewModel.email.value,
                onValueChange = {viewModel.email.onChange(it)},
                placeholder = {Text(text = "tu@email.com")}
            )
        }







    }
}