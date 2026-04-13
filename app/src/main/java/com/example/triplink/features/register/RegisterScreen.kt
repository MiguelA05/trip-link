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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.R
import com.example.triplink.core.components.common.AppTitle
import com.example.triplink.ui.theme.AppTitleVariant

import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.LinkTextRow
import com.example.triplink.core.utils.RequestResult
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
                isError = registerViewModel.passwordError != null,
                errorText = registerViewModel.passwordError
            )

            Spacer(modifier = Modifier.height(24.dp))

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

            // Result message display as per your example
            registerResult?.let { result ->
                when (result) {
                    is RequestResult.Success -> {
                        Text(
                            text = result.message,
                            color = MaterialTheme.colorScheme.primary,
                            style = TextTokens.body(),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    is RequestResult.Failure -> {
                        Text(
                            text = result.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = TextTokens.body(),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }

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
