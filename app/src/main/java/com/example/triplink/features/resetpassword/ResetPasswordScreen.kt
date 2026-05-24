package com.example.triplink.features.resetpassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.R
import com.example.triplink.core.components.common.AppTitle
import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.core.utils.isErrorResult
import com.example.triplink.core.utils.messageText
import com.example.triplink.ui.theme.TextTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    oobCode: String,
    onBack: () -> Unit = {},
    onPasswordChanged: () -> Unit = {},
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val recoveryResult by viewModel.recoveryResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var isRedirecting by remember { mutableStateOf(false) }
    val isSubmitBlocked = isLoading || isRedirecting

    BackHandler {
        onBack()
    }

    LaunchedEffect(recoveryResult) {
        recoveryResult?.let { result ->
            val message = result.messageText()
            if (!result.isErrorResult) {
                isRedirecting = true
            }
            snackbarHostState.showSnackbar(message)
            viewModel.resetRecoveryResult()
            if (!result.isErrorResult) {
                onPasswordChanged()
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val isError = recoveryResult?.isErrorResult == true
                Snackbar(
                    containerColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = if (isError) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(text = data.visuals.message, style = TextTokens.body())
                }
            }
        },
        topBar = {
            GeneralTopBar(
                title = stringResource(R.string.feature_reset_password_title),
                onBack = onBack
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
                contentDescription = stringResource(R.string.feature_reset_password_logo_content_description)
            )
            AppTitle()
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(R.string.feature_reset_password_message),
                style = TextTokens.input()
            )

            FormField(
                label = stringResource(R.string.feature_reset_password_new_password_label),
                value = viewModel.password.value,
                onValueChange = { viewModel.password.onChange(it) },
                placeholder = stringResource(R.string.feature_reset_password_new_password_placeholder),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (viewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = viewModel.password.error != null,
                errorText = viewModel.password.error,
                trailingIcon = {
                    val icon = if (viewModel.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (viewModel.passwordVisible) {
                        stringResource(R.string.feature_login_hide_password)
                    } else {
                        stringResource(R.string.feature_login_show_password)
                    }
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Icon(imageVector = icon, contentDescription = description)
                    }
                }
            )

            FormField(
                label = stringResource(R.string.feature_reset_password_confirm_password_label),
                value = viewModel.confirmPassword.value,
                onValueChange = { viewModel.confirmPassword.onChange(it) },
                placeholder = stringResource(R.string.feature_reset_password_new_password_placeholder),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (viewModel.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = viewModel.confirmPassword.error != null,
                errorText = viewModel.confirmPassword.error,
                trailingIcon = {
                    val icon = if (viewModel.confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (viewModel.confirmPasswordVisible) {
                        stringResource(R.string.feature_login_hide_password)
                    } else {
                        stringResource(R.string.feature_login_show_password)
                    }
                    IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                        Icon(imageVector = icon, contentDescription = description)
                    }
                }
            )

            GeneralButton(
                primary = true,
                onClick = { viewModel.saveNewPassword(oobCode) },
                enabled = viewModel.isFormValid && !isSubmitBlocked,
                isLoading = isSubmitBlocked,
                text = stringResource(R.string.feature_reset_password_submit_action)
            )
        }
    }
}
