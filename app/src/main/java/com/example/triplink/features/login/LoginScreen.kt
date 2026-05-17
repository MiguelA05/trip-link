package com.example.triplink.features.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.R
import com.example.triplink.core.components.common.AppTitle
import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.core.components.LinkTextRow
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.isErrorResult
import com.example.triplink.core.utils.messageText
import com.example.triplink.ui.theme.TextTokens
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToRecovery: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val loginResult by viewModel.loginResult.collectAsState()

    LaunchedEffect(loginResult) {
        loginResult?.let { result ->
            val message = result.messageText()

            if (result is RequestResult.Success) {
                // Mostrar feedback y navegar sin bloquear la transición.
                launch { snackbarHostState.showSnackbar(message) }
            } else {
                snackbarHostState.showSnackbar(message)
            }
            viewModel.resetLoginResult()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                val isError = loginResult?.isErrorResult == true
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
                title = stringResource(R.string.feature_login_title),
                onBack = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 16.dp, alignment = CenterVertically)
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.feature_login_logo_content_description),
                Modifier.size(100.dp)
            )

            AppTitle()

            Text(
                text = stringResource(R.string.feature_login_welcome_back),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 26.dp),
                textAlign = TextAlign.Center,
                style = TextTokens.sectionTitle()
            )

            FormField(
                label = stringResource(R.string.feature_login_email_label),
                value = viewModel.email.value,
                onValueChange = { viewModel.email.onChange(it) },
                placeholder = stringResource(R.string.feature_login_email_placeholder),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = viewModel.email.error != null,
                errorText = viewModel.email.error
            )

            FormField(
                label = stringResource(R.string.feature_login_password_label),
                value = viewModel.password.value,
                onValueChange = { viewModel.password.onChange(it) },
                placeholder = stringResource(R.string.feature_login_password_placeholder),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (viewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = viewModel.password.error != null,
                errorText = viewModel.password.error,
                trailingIcon = {
                    val icon =
                        if (viewModel.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description =
                        if (viewModel.passwordVisible) stringResource(R.string.feature_login_hide_password) else stringResource(R.string.feature_login_show_password)

                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Icon(imageVector = icon, contentDescription = description)
                    }
                })

            TextButton(
                onClick = onNavigateToRecovery,
                interactionSource = viewModel.forgotPasswordInteractionSource,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Text(
                    text = stringResource(R.string.feature_login_forgot_password),
                    style = TextTokens.emphasized(TextTokens.button(), FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            GeneralButton(
                primary = true,
                onClick = {
                    viewModel.login()
                },
                enabled = viewModel.isFormValid,
                text = stringResource(R.string.feature_login_primary_action)
            )

            LinkTextRow(
                text = stringResource(R.string.feature_login_no_account),
                buttonText = stringResource(R.string.feature_login_create_account),
                textColor = MaterialTheme.colorScheme.onSurface,
                onClick = onNavigateToRegister

            )
        }
    }
}
