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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
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
import com.example.triplink.core.components.map.LocationPickerMapField
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.messageText
import com.example.triplink.ui.theme.TextColors
import com.example.triplink.ui.theme.TextTokens
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import kotlinx.coroutines.delay

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
    val isLoading by registerViewModel.isLoading.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var pendingSuccessNavigation by remember { mutableStateOf(false) }

    LaunchedEffect(registerResult) {
        registerResult?.let { result ->
            when (result) {
                is RequestResult.Success -> {
                    successMessage = result.messageText()
                    showSuccessDialog = true
                }

                is RequestResult.Failure -> {
                    snackbarHostState.showSnackbar(result.messageText())
                }

                is RequestResult.Loading -> Unit
            }
            // Clear result after showing snackbar to avoid repeated triggers on recomposition
            registerViewModel.clearResult()
        }
    }

    LaunchedEffect(pendingSuccessNavigation) {
        if (pendingSuccessNavigation) {
            delay(150)
            pendingSuccessNavigation = false
            onRegisterSuccess()
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
                text = buildRequiredLabel(stringResource(R.string.feature_register_residence_title)).toString(),
                style = TextTokens.title(),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(12.dp))

            LocationPickerMapField(
                currentLatitude = registerViewModel.selectedLatitude,
                currentLongitude = registerViewModel.selectedLongitude,
                onLocationConfirmed = registerViewModel::onExactLocationSelected
            )

            Spacer(modifier = Modifier.height(32.dp))


            GeneralButton(
                text = stringResource(R.string.feature_register_create_account_heading),
                enabled = registerViewModel.isFormValid,
                isLoading = isLoading,
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
                pendingSuccessNavigation = true
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

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}

fun buildRequiredLabel(text: String) = buildAnnotatedString {
    append(text)
    withStyle(style = SpanStyle(color = Color.Red)) {
        append(" *")
    }
}
