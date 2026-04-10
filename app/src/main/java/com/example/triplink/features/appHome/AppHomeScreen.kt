package com.example.triplink.features.appHome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.R
import com.example.triplink.core.components.common.AppTitle
import com.example.triplink.core.components.LinkTextRow
import com.example.triplink.ui.theme.AppTitleVariant
import com.example.triplink.ui.theme.TextTokens


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
                contentDescription = stringResource(R.string.component_brand_header_logo_content_description),
                modifier = Modifier.width(300.dp).size(150.dp)
            )
            AppTitle(variant = AppTitleVariant.Hero)
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.feature_app_home_hero_line_1),
                    style = TextTokens.sectionTitle(),
                    color = Color.White
                )
                Text(
                    text = stringResource(R.string.feature_app_home_hero_line_2),
                    style = TextTokens.sectionTitle(),
                    color = Color.White
                )
            }


        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GeneralButton(
                icon = Icons.Filled.Add,
                contentDescription = stringResource(R.string.feature_app_home_create_account_content_description),
                onClick = onNavigateToRegister,
                text = stringResource(R.string.feature_app_home_register_action)
            )

            LinkTextRow(
                text = stringResource(R.string.feature_app_home_have_account),
                buttonText = stringResource(R.string.feature_app_home_login_action),
                textColor = Color.White,
                onClick = onNavigateToLogin

            )

        }


    }


}
