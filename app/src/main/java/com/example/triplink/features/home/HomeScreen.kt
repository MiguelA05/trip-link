package com.example.triplink.features.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.R
import com.example.triplink.core.components.AppTitle
import com.example.triplink.core.components.LinkTextRow


@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit, // Función para navegar a la pantalla de Login
    onNavigateToRegister: () -> Unit // Función para navegar a la pantalla de Registro
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Overlay con Gradiente (de transparente a azul oscuro)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,          // Comienzo (arriba)
                            Color(0xFF001A33).copy(alpha = 0.9f) // Final (abajo) - Azul muy oscuro
                        )
                    )
                )
        )

        // 3. Contenido encima del fondo
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.width(300.dp).size(150.dp)
            )
            AppTitle(fontSize = 40)
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Encuentra lugares únicos,",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = "conecta con tu comunidad",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }


        }

        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
            horizontalAlignment = Alignment.End
        ) {
            GeneralButton(
                icon = Icons.Filled.Add,
                contentDescription = "Crear cuenta",
                onClick = onNavigateToRegister  ,
                text = "Register"
            )

            LinkTextRow(
                text = "¿Tienes una cuenta?",
                buttonText = "Inicia sesión",
                textColor = Color.White,
                onClick = onNavigateToLogin

            )

        }


    }


}
