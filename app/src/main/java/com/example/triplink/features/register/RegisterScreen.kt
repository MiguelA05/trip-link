package com.example.triplink.features.register

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.core.components.common.AppTitle
import com.example.triplink.ui.theme.AppTitleVariant

import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.LinkTextRow
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.TextColors
import com.example.triplink.ui.theme.TextTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val registerResult by registerViewModel.registerResult.collectAsState()

    LaunchedEffect(registerResult) {
        registerResult?.let { result ->
            when (result) {
                is RequestResult.Success -> {
                    snackbarHostState.showSnackbar(result.message)
                    // Optional: Navigate or clear state here
                }
                is RequestResult.Failure -> {
                    snackbarHostState.showSnackbar(result.errorMessage)
                }
            }
            // Clear result after showing snackbar to avoid repeated triggers on recomposition
            registerViewModel.clearResult()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Registrarse",
                        style = TextTokens.screenTitle()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrincipalBlue
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

            AppTitle(variant = AppTitleVariant.Hero, modifier = Modifier.padding(bottom = 0.dp))

            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextColors.Primary
            )

            Text(
                text = "Encuentra lugares únicos, conecta con tu comunidad.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextColors.Secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(bottom = 24.dp), thickness = 1.dp, color = Color(0xFFEEEEEE))

            FormField(
                label = "Nombre Completo",
                value = registerViewModel.name,
                onValueChange = { registerViewModel.onNameChange(it) },
                placeholder = "John Doe",
                modifier = Modifier.fillMaxWidth(),
                isError = registerViewModel.nameError != null,
                errorText = registerViewModel.nameError
            )

            Spacer(modifier = Modifier.height(16.dp))

            FormField(
                label = "Correo electrónico",
                value = registerViewModel.email,
                onValueChange = { registerViewModel.onEmailChange(it) },
                placeholder = "tu@email.com",
                modifier = Modifier.fillMaxWidth(),
                isError = registerViewModel.emailError != null,
                errorText = registerViewModel.emailError
            )

            Spacer(modifier = Modifier.height(16.dp))

            FormField(
                label = "Contraseña",
                value = registerViewModel.password,
                onValueChange = { registerViewModel.onPasswordChange(it) },
                placeholder = "••••••••••••",
                modifier = Modifier.fillMaxWidth(),
                isError = registerViewModel.passwordError != null,
                errorText = registerViewModel.passwordError
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Ubicación de Residencia",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SelectableDropdown(
                    label = "Dpto",
                    selectedValue = registerViewModel.selectedDepartment,
                    options = registerViewModel.departments,
                    onOptionSelected = { registerViewModel.onDepartmentChange(it) },
                    modifier = Modifier.weight(1f)
                )

                SelectableDropdown(
                    label = "Ciudad",
                    selectedValue = registerViewModel.selectedCity,
                    options = registerViewModel.citiesMap[registerViewModel.selectedDepartment] ?: emptyList(),
                    onOptionSelected = { registerViewModel.onCityChange(it) },
                    modifier = Modifier.weight(1.5f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = registerViewModel.addExactLocation,
                    onCheckedChange = { registerViewModel.addExactLocation = it },
                    colors = CheckboxDefaults.colors(checkedColor = PrincipalBlue)
                )
                Text(
                    text = "¿Desea añadir ubicacion exacta?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextColors.Secondary
                )
            }

            if (registerViewModel.addExactLocation) {
                Spacer(modifier = Modifier.height(12.dp))
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

            // Result message display as per your example
            registerResult?.let { result ->
                when (result) {
                    is RequestResult.Success -> {
                        Text(
                            text = result.message,
                            color = Color(0xFF4CAF50),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    is RequestResult.Failure -> {
                        Text(
                            text = result.errorMessage,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }

            GeneralButton(
                text = "Crear Cuenta",
                enabled = registerViewModel.isFormValid,
                onClick = {
                    registerViewModel.register()
                }
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
fun SelectableDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), RoundedCornerShape(24.dp))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(24.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedValue.ifEmpty { label },
                color = if (selectedValue.isEmpty()) TextColors.Muted else TextColors.Primary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = PrincipalBlue,
                modifier = Modifier.size(20.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}
