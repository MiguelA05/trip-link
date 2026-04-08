package com.example.triplink.features.user.accountEdit

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.PrincipalRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEditScreen(
    accountEditViewModel: AccountEditViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onAppHomeClick: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val updateResult by accountEditViewModel.updateResult.collectAsState()
    val deleteResult by accountEditViewModel.deleteResult.collectAsState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(updateResult) {
        updateResult?.let { result ->
            when (result) {
                is RequestResult.Success -> {
                    snackbarHostState.showSnackbar(result.message)
                }
                is RequestResult.Failure -> {
                    snackbarHostState.showSnackbar(result.errorMessage)
                }
            }
            accountEditViewModel.clearUpdateResult()
        }
    }

    LaunchedEffect(deleteResult) {
        deleteResult?.let { result ->
            when (result) {
                is RequestResult.Success -> {
                    snackbarHostState.showSnackbar(result.message)
                }
                is RequestResult.Failure -> {
                    snackbarHostState.showSnackbar(result.errorMessage)
                }
            }
            accountEditViewModel.clearDeleteResult()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            GeneralTopBar(
                title = "Gestión de Cuenta",
                onBack = onBackClick,
                showBackButton = true
            )
        },
        containerColor = Color(0xFFF0F2F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // User Avatar with Initials
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFFE8EEF7), CircleShape)
                    .border(2.dp, Color(0xFFD0DCF0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = accountEditViewModel.getUserInitials(),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7B9BDB),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            AccountSectionContainer(
                title = "INFORMACIÓN PERSONAL"
            ) {
                FormField(
                    label = "Nombre completo",
                    value = accountEditViewModel.fullName,
                    onValueChange = { accountEditViewModel.onFullNameChange(it) },
                    placeholder = "John Doe",
                    modifier = Modifier.fillMaxWidth(),
                    isError = accountEditViewModel.fullNameError != null,
                    errorText = accountEditViewModel.fullNameError
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormField(
                    label = "Teléfono",
                    value = accountEditViewModel.phone,
                    onValueChange = { accountEditViewModel.onPhoneChange(it) },
                    placeholder = "Ingresa tu número de teléfono",
                    modifier = Modifier.fillMaxWidth(),
                    isError = accountEditViewModel.phoneError != null,
                    errorText = accountEditViewModel.phoneError
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AccountSectionContainer(
                title = "UBICACIÓN DE RESIDENCIA"
            ) {
                FormField(
                    label = "Barrio, ciudad o referencia",
                    value = accountEditViewModel.address,
                    onValueChange = { accountEditViewModel.onAddressChange(it) },
                    placeholder = "Ej. Barrio La Candelaria, Bogotá",
                    modifier = Modifier.fillMaxWidth(),
                    isError = accountEditViewModel.addressError != null,
                    errorText = accountEditViewModel.addressError
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SelectableDropdown(
                        label = "Dpto",
                        selectedValue = accountEditViewModel.selectedDepartment,
                        options = accountEditViewModel.departments,
                        onOptionSelected = { accountEditViewModel.onDepartmentChange(it) },
                        modifier = Modifier.weight(1f)
                    )

                    SelectableDropdown(
                        label = "Ciudad",
                        selectedValue = accountEditViewModel.selectedCity,
                        options = accountEditViewModel.citiesMap[accountEditViewModel.selectedDepartment]
                            ?: emptyList(),
                        onOptionSelected = { accountEditViewModel.onCityChange(it) },
                        modifier = Modifier.weight(1.5f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = accountEditViewModel.addExactLocation,
                        onCheckedChange = { accountEditViewModel.addExactLocation = it },
                        colors = CheckboxDefaults.colors(checkedColor = PrincipalBlue)
                    )
                    Text(
                        text = "¿Desea añadir ubicación exacta?",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                if (accountEditViewModel.addExactLocation) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Map Placeholder",
                                tint = Color.LightGray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Mapa (Próximamente)", color = Color.LightGray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AccountSectionContainer(
                title = "DATOS DE ACCESO"
            ) {
                Text(
                    text = "Correo electrónico",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color(0xFF5F6166),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = accountEditViewModel.email,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = null,
                            tint = Color(0xFFB1B4C1)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = Color(0xFFB1B4C1)
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(color = Color(0xFFA0A3B1), fontSize = 18.sp),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFEEF0F5),
                        unfocusedContainerColor = Color(0xFFEEF0F5),
                        disabledContainerColor = Color(0xFFEEF0F5),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        focusedTextColor = Color(0xFFA0A3B1),
                        unfocusedTextColor = Color(0xFFA0A3B1)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "El correo electrónico no se puede modificar por seguridad.",
                    fontSize = 15.sp,
                    color = Color(0xFF9C9EA5),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Contraseña",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color(0xFF5F6166),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        accountEditViewModel.changePassword()
                        onChangePasswordClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(76.dp),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Color(0xFFBBD5FA)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFFEAF1FF),
                        contentColor = PrincipalBlue
                    )
                ) {
                    Text(
                        text = "Cambiar contraseña",
                        fontWeight = FontWeight.Bold,
                        fontSize = 33.sp * 0.5f
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "La contraseña se gestiona mediante el proceso de recuperación.",
                    fontSize = 15.sp,
                    color = Color(0xFF9C9EA5),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Changes Button
            Box(modifier = Modifier.fillMaxWidth()) {
                GeneralButton(
                    text = "Guardar cambios",
                    primary = true,
                    onClick = {
                        accountEditViewModel.saveChanges()
                    },
                    enabled = accountEditViewModel.isFormValid
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Delete Account Button
            Button(
                onClick = {
                    showDeleteConfirmation = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = PrincipalRed
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Eliminar cuenta",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                Text(
                    text = "¿Eliminar cuenta?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Text(
                    text = "Esta acción es irreversible y se perderán todos tus datos. ¿Deseas continuar?",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        accountEditViewModel.deleteAccount()
                        onAppHomeClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrincipalRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text(
                        "Cancelar",
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun AccountSectionContainer(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F8FA)),
        border = BorderStroke(1.dp, Color(0xFFE5EAF1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = PrincipalBlue,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(12.dp))
            content()
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
                color = if (selectedValue.isEmpty()) Color(0xFF9E9E9E) else Color.Black,
                fontSize = 14.sp,
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
            modifier = Modifier.background(Color.White)
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
fun AccountEditScreenPreview() {
    AccountEditScreen()
}
