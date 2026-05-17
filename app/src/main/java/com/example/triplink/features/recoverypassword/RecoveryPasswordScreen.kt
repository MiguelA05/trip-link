package com.example.triplink.features.recoverypassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.triplink.R
import com.example.triplink.core.components.common.AppTitle
import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.GeneralAlertDialog
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.isErrorResult
import com.example.triplink.core.utils.messageText
import kotlinx.coroutines.delay
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.ui.theme.TextTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryPasswordScreen(
    viewModel: RecoveryPasswordViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onResetPassword: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val recoveryMessage = stringResource(R.string.feature_recovery_password_message)
    val recoveryResendMessage = stringResource(R.string.feature_recovery_password_resend_message)
    val recoveryResult by viewModel.recoveryResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(recoveryResult) {
        recoveryResult?.let { result ->
            val message = result.messageText()
            snackbarHostState.showSnackbar(message)

            if (result is RequestResult.Success) {
                delay(1000)
            }
            viewModel.resetRecoveryResult()
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
                    Text(
                        text = data.visuals.message,
                        style = TextTokens.body()
                    )
                }
            }
        },
        topBar = {
            GeneralTopBar(
                title = stringResource(R.string.feature_recovery_password_title),
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
            verticalArrangement = Arrangement.spacedBy(
                space = 32.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            Image(
                modifier = Modifier.width(74.dp),
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.feature_recovery_password_logo_content_description)
            )
            AppTitle()
            Text(
                textAlign = TextAlign.Center,
                text = if (!viewModel.isEmailSent) recoveryMessage else recoveryResendMessage,
                style = TextTokens.input()
            )

            FormField(
                label = stringResource(R.string.feature_recovery_password_email_label),
                value = viewModel.email.value,
                onValueChange = { viewModel.email.onChange(it) },
                placeholder = stringResource(R.string.feature_recovery_password_email_placeholder),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = viewModel.email.error != null,
                errorText = viewModel.email.error
            )

            GeneralButton(
                primary = true,
                onClick = {
                    viewModel.sendPasswordResetEmail()
                },
                enabled = viewModel.isFormValid,
                isLoading = isLoading,
                text = if (!viewModel.isEmailSent) {
                    stringResource(R.string.feature_recovery_password_send_email_action)
                } else {
                    stringResource(R.string.feature_recovery_password_resend_email_action)
                }
            )
        }
    }

    if (viewModel.showSuccessDialog) {
        GeneralAlertDialog(
            onDismissRequest = { viewModel.dismissDialog() },
            onConfirm = { viewModel.dismissDialog() },
            title = stringResource(R.string.feature_recovery_password_success_title),
            message = stringResource(R.string.feature_recovery_password_success_message),
            icon = Icons.Default.Email
        )
    }

}