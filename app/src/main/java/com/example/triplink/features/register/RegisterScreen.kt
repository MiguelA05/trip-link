package com.example.triplink.features.register

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.R
import com.example.triplink.core.components.common.AppTitle
import com.example.triplink.ui.theme.AppTitleVariant

import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.GeneralAlertDialog
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.LinkTextRow
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.ui.theme.TextColors
import com.example.triplink.ui.theme.TextTokens
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val registerResult by registerViewModel.registerResult.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    LaunchedEffect(registerResult) {
        registerResult?.let { result ->
            when (result) {
                is RequestResult.Success -> {
                    successMessage = result.message
                    showSuccessDialog = true
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
                        text = stringResource(R.string.feature_register_title),
                        style = TextTokens.screenTitle()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.component_general_top_bar_back_content_description),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                text = stringResource(R.string.feature_register_create_account_heading),
                style = TextTokens.sectionTitle(),
                color = TextColors.Primary
            )

            Text(
                text = stringResource(R.string.feature_register_subtitle),
                style = TextTokens.body(),
                color = TextColors.Secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(bottom = 24.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            FormField(
                label = stringResource(R.string.feature_register_full_name_label),
                value = registerViewModel.name,
                onValueChange = { registerViewModel.onNameChange(it) },
                placeholder = stringResource(R.string.feature_register_full_name_placeholder),
                modifier = Modifier.fillMaxWidth(),
                isError = registerViewModel.nameError != null,
                errorText = registerViewModel.nameError
            )

            Spacer(modifier = Modifier.height(16.dp))

            FormField(
                label = stringResource(R.string.feature_register_email_label),
                value = registerViewModel.email,
                onValueChange = { registerViewModel.onEmailChange(it) },
                placeholder = stringResource(R.string.feature_register_email_placeholder),
                modifier = Modifier.fillMaxWidth(),
                isError = registerViewModel.emailError != null,
                errorText = registerViewModel.emailError
            )

            Spacer(modifier = Modifier.height(16.dp))

            FormField(
                label = stringResource(R.string.feature_register_password_label),
                value = registerViewModel.password,
                onValueChange = { registerViewModel.onPasswordChange(it) },
                placeholder = stringResource(R.string.feature_register_password_placeholder),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (registerViewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = registerViewModel.passwordError != null,
                errorText = registerViewModel.passwordError,
                trailingIcon = {
                    val icon = if (registerViewModel.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (registerViewModel.passwordVisible) {
                        stringResource(R.string.feature_login_hide_password)
                    } else {
                        stringResource(R.string.feature_login_show_password)
                    }

                    IconButton(onClick = { registerViewModel.togglePasswordVisibility() }) {
                        Icon(imageVector = icon, contentDescription = description)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            FormField(
                label = stringResource(R.string.feature_register_phone_label),
                value = registerViewModel.phone,
                onValueChange = { registerViewModel.onPhoneChange(it) },
                placeholder = stringResource(R.string.feature_register_phone_placeholder),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = registerViewModel.phoneError != null,
                errorText = registerViewModel.phoneError
            )

            Spacer(modifier = Modifier.height(16.dp))

            FormField(
                label = stringResource(R.string.feature_register_address_label),
                value = registerViewModel.address,
                onValueChange = { registerViewModel.onAddressChange(it) },
                placeholder = stringResource(R.string.feature_register_address_placeholder),
                modifier = Modifier.fillMaxWidth(),
                isError = registerViewModel.addressError != null,
                errorText = registerViewModel.addressError
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.feature_register_residence_title),
                style = TextTokens.title(),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SelectableDropdown(
                    label = stringResource(R.string.feature_register_department_label),
                    selectedValue = registerViewModel.selectedDepartment,
                    options = registerViewModel.departments,
                    onOptionSelected = { registerViewModel.onDepartmentChange(it) },
                    modifier = Modifier.weight(1f)
                )

                SelectableDropdown(
                    label = stringResource(R.string.feature_register_city_label),
                    selectedValue = registerViewModel.selectedCity,
                    options = registerViewModel.getCitiesForDepartment(registerViewModel.selectedDepartment),
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
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = stringResource(R.string.feature_register_add_exact_location),
                    style = TextTokens.body(),
                    color = TextColors.Secondary
                )
            }

            if (registerViewModel.addExactLocation) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = stringResource(R.string.feature_register_map_placeholder_content_description),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = stringResource(R.string.feature_register_map_placeholder_label),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))


            GeneralButton(
                text = stringResource(R.string.feature_register_create_account_heading),
                enabled = registerViewModel.isFormValid,
                onClick = {
                    registerViewModel.register()
                }
            )

            LinkTextRow(
                text = stringResource(R.string.feature_register_have_account),
                buttonText = stringResource(R.string.feature_register_login_action),
                onClick = onLoginClick,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }

    if (showSuccessDialog) {
        GeneralAlertDialog(
            onDismissRequest = {},
            onConfirm = {
                showSuccessDialog = false
                onRegisterSuccess()
            },
            title = stringResource(R.string.feature_register_success_dialog_title),
            message = successMessage,
            icon = Icons.Default.CheckCircle,
            buttonText = stringResource(R.string.feature_register_login_action),
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            showCloseButton = false
        )
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
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedValue.ifEmpty { label },
                color = if (selectedValue.isEmpty()) TextColors.Muted else TextColors.Primary,
                style = TextTokens.body(),
                maxLines = 1
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
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
