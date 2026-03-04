package com.example.triplink.features.register

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triplink.core.components.AppTitle
import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.LinkTextRow
import com.example.triplink.features.login.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Registrarse",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF2563EB)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            AppTitle(fontSize = 40, modifier = Modifier.padding(bottom = 0.dp))

            Text(
                text = "Crear Cuenta",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "Encuentra lugares únicos, conecta con tu comunidad.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(bottom = 24.dp), thickness = 1.dp, color = Color(0xFFEEEEEE))

            FormField(
                label = "Nombre Completo",
                value = registerViewModel.name,
                onValueChange = { registerViewModel.name = it },
                placeholder = "John Doe",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            FormField(
                label = "Correo electrónico",
                value = registerViewModel.email,
                onValueChange = { registerViewModel.email = it },
                placeholder = "tu@email.com",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            FormField(
                label = "Contraseña",
                value = registerViewModel.password,
                onValueChange = { registerViewModel.password = it },
                placeholder = "••••••••••••",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Ubicación de Residencia",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DropdownPlaceholder(text = "Ciudad", modifier = Modifier.weight(1f))
                DropdownPlaceholder(text = "Departamento", modifier = Modifier.weight(1.5f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = registerViewModel.addExactLocation,
                    onCheckedChange = { registerViewModel.addExactLocation = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2563EB))
                )
                Text(
                    text = "¿Desea añadir ubicacion exacta?",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            if (registerViewModel.addExactLocation) {
                Spacer(modifier = Modifier.height(12.dp))
                // Map Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Map Placeholder",
                            tint = Color.LightGray,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(text = "Mapa (Próximamente)", color = Color.LightGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            GeneralButton(
                text = "Crear Cuenta",
                onClick = { /* Handle registration */ }
            )

            LinkTextRow(
                text = "¿Tienes una cuenta?",
                buttonText = "Iniciar sesión",
                onClick = onLoginClick,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}

@Composable
fun DropdownPlaceholder(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(Color(0xFFF5F5F5), RoundedCornerShape(24.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = text, color = Color(0xFF9E9E9E), fontSize = 14.sp)
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = Color(0xFF2563EB),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}
