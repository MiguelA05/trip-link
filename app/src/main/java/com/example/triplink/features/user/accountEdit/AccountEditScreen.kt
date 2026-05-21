package com.example.triplink.features.user.accountEdit

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.R
import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.DestructiveConfirmDialog
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.core.components.imagepicker.ImagePickerBottomSheet
import com.example.triplink.core.components.imagepicker.ProfileImage
import com.example.triplink.core.components.map.LocationPickerMapField
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.messageText
import com.example.triplink.core.utils.createTempImageUri
import com.example.triplink.ui.theme.TextColors
import com.example.triplink.ui.theme.TextTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEditScreen(
    accountEditViewModel: AccountEditViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onAppHomeClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val updateResult by accountEditViewModel.updateResult.collectAsState()
    val deleteResult by accountEditViewModel.deleteResult.collectAsState()
    val isLoading by accountEditViewModel.isLoading.collectAsState()
    val isDeleting by accountEditViewModel.isDeleting.collectAsState()
    val photoUri by accountEditViewModel.photoUri.collectAsState()
    val showChangePasswordDialog by accountEditViewModel.showChangePasswordDialog.collectAsState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showCameraPermissionError by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val bottomSheetState = rememberModalBottomSheetState()

    // Galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let { accountEditViewModel.onPhotoUriChange(it) }
        showBottomSheet = false
    }

    // Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            tempCameraUri?.let { accountEditViewModel.onPhotoUriChange(it) }
        }
        showBottomSheet = false
    }

    // Permiso de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            tempCameraUri = createTempImageUri(context)
            tempCameraUri?.let { cameraLauncher.launch(it) }
        } else {
            showCameraPermissionError = true
        }
    }

    LaunchedEffect(showCameraPermissionError) {
        if (showCameraPermissionError) {
            snackbarHostState.showSnackbar(context.getString(R.string.permissions_camera_permission_denied))
            showCameraPermissionError = false
        }
    }

    LaunchedEffect(updateResult) {
        updateResult?.let { result ->
            snackbarHostState.showSnackbar(result.messageText())
            accountEditViewModel.clearUpdateResult()
        }
    }

    LaunchedEffect(deleteResult) {
        deleteResult?.let { result ->
            snackbarHostState.showSnackbar(result.messageText())
            if (result is RequestResult.Success) {
                onAppHomeClick()
            }
            accountEditViewModel.clearDeleteResult()
        }
    }

    // Change password dialog state
    var currentPasswordInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }
    var confirmNewPasswordInput by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmNewPasswordVisible by remember { mutableStateOf(false) }
    // Inline validation errors
    var currentPasswordError by remember { mutableStateOf<String?>(null) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            GeneralTopBar(
                title = stringResource(R.string.feature_account_edit_title),
                onBack = onBackClick,
                showBackButton = true
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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

            // User Avatar - Profileimage con soporte para cambio de foto
            ProfileImage(
                photoUri = photoUri,
                isEditMode = true,
                onEditClick = { showBottomSheet = true }
            )

            Spacer(modifier = Modifier.height(28.dp))

            AccountSectionContainer(
                title = stringResource(R.string.feature_account_edit_personal_info_section)
            ) {
                FormField(
                    label = stringResource(R.string.feature_account_edit_full_name_label),
                    value = accountEditViewModel.fullName,
                    onValueChange = { accountEditViewModel.onFullNameChange(it) },
                    placeholder = stringResource(R.string.feature_account_edit_full_name_placeholder),
                    modifier = Modifier.fillMaxWidth(),
                    isError = accountEditViewModel.fullNameError != null,
                    errorText = accountEditViewModel.fullNameError
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormField(
                    label = stringResource(R.string.feature_account_edit_phone_label),
                    value = accountEditViewModel.phone,
                    onValueChange = { accountEditViewModel.onPhoneChange(it) },
                    placeholder = stringResource(R.string.feature_account_edit_phone_placeholder),
                    modifier = Modifier.fillMaxWidth(),
                    isError = accountEditViewModel.phoneError != null,
                    errorText = accountEditViewModel.phoneError
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AccountSectionContainer(
                title = stringResource(R.string.feature_account_edit_residence_section)
            ) {
                FormField(
                    label = stringResource(R.string.feature_account_edit_address_label),
                    value = accountEditViewModel.address,
                    onValueChange = { accountEditViewModel.onAddressChange(it) },
                    placeholder = stringResource(R.string.feature_account_edit_address_placeholder),
                    modifier = Modifier.fillMaxWidth(),
                    isError = accountEditViewModel.addressError != null,
                    errorText = accountEditViewModel.addressError
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = buildRequiredLabel(stringResource(R.string.feature_account_edit_location_label)).toString(),
                    style = TextTokens.title(),
                    color = TextColors.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                LocationPickerMapField(
                    currentLatitude = accountEditViewModel.selectedLatitude,
                    currentLongitude = accountEditViewModel.selectedLongitude,
                    showMyLocationButton = true,
                    onLocationConfirmed = accountEditViewModel::onLocationSelected
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AccountSectionContainer(
                title = stringResource(R.string.feature_account_edit_access_data_section)
            ) {
                Text(
                    text = stringResource(R.string.feature_account_edit_email_label),
                    style = TextTokens.title(),
                    color = TextColors.Secondary,
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
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = TextTokens.body().fontSize
                    ),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.feature_account_edit_email_helper),
                    style = TextTokens.body(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = stringResource(R.string.feature_account_edit_password_label),
                    style = TextTokens.title(),
                    color = TextColors.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        accountEditViewModel.changePassword()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(76.dp),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text(
                        text = stringResource(R.string.feature_account_edit_change_password_action),
                        style = TextTokens.button()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.feature_account_edit_password_helper),
                    style = TextTokens.body(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Changes Button
            Box(modifier = Modifier.fillMaxWidth()) {
                GeneralButton(
                    text = stringResource(R.string.feature_account_edit_save_changes_action),
                    primary = true,
                    onClick = {
                        accountEditViewModel.saveChanges()
                    },
                    enabled = accountEditViewModel.canSaveChanges,
                    isLoading = isLoading
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
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = stringResource(R.string.feature_account_edit_delete_account_action),
                    style = TextTokens.title()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showDeleteConfirmation) {
        DestructiveConfirmDialog(
            title = stringResource(R.string.feature_account_edit_delete_account_dialog_title),
            message = stringResource(R.string.feature_account_edit_delete_account_dialog_message),
            confirmText = stringResource(R.string.feature_account_edit_delete_account_dialog_confirm),
            dismissText = stringResource(R.string.feature_account_edit_delete_account_dialog_cancel),
            onDismissRequest = { showDeleteConfirmation = false },
            onConfirm = {
                showDeleteConfirmation = false
                accountEditViewModel.deleteAccount()
            }
        )
    }

    // Change password dialog
    // Clear inputs when dialog closed
    LaunchedEffect(showChangePasswordDialog) {
        if (!showChangePasswordDialog) {
            currentPasswordInput = ""
            newPasswordInput = ""
            confirmNewPasswordInput = ""
            currentPasswordVisible = false
            newPasswordVisible = false
            confirmNewPasswordVisible = false
        }
    }

    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { accountEditViewModel.closeChangePasswordDialog() },
            title = { Text(text = stringResource(R.string.feature_account_edit_change_password_dialog_title)) },
            text = {
                Column {
                    FormField(
                        label = stringResource(R.string.feature_account_edit_current_password_label),
                        value = currentPasswordInput,
                        onValueChange = {
                            currentPasswordInput = it
                            if (!it.isNullOrBlank()) currentPasswordError = null
                        },
                        placeholder = "",
                        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = currentPasswordError != null,
                        errorText = currentPasswordError,
                        trailingIcon = {
                            val img = if (currentPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                Icon(imageVector = img, contentDescription = null)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FormField(
                        label = stringResource(R.string.feature_account_edit_new_password_label),
                        value = newPasswordInput,
                        onValueChange = {
                            newPasswordInput = it
                            if (it.length >= 6) newPasswordError = null
                        },
                        placeholder = "",
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = newPasswordError != null,
                        errorText = newPasswordError,
                        trailingIcon = {
                            val img = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(imageVector = img, contentDescription = null)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FormField(
                        label = stringResource(R.string.feature_account_edit_confirm_new_password_label),
                        value = confirmNewPasswordInput,
                        onValueChange = {
                            confirmNewPasswordInput = it
                            if (it == newPasswordInput) confirmPasswordError = null
                        },
                        placeholder = "",
                        visualTransformation = if (confirmNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = confirmPasswordError != null,
                        errorText = confirmPasswordError,
                        trailingIcon = {
                            val img = if (confirmNewPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { confirmNewPasswordVisible = !confirmNewPasswordVisible }) {
                                Icon(imageVector = img, contentDescription = null)
                            }
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Local validations before calling ViewModel
                        currentPasswordError = if (currentPasswordInput.isBlank()) context.getString(R.string.feature_account_edit_current_password_required) else null
                        newPasswordError = if (newPasswordInput.length < 6) context.getString(R.string.vm_account_edit_change_password_too_short) else null
                        confirmPasswordError = if (newPasswordInput != confirmNewPasswordInput) context.getString(R.string.vm_account_edit_change_password_mismatch) else null

                        if (currentPasswordError == null && newPasswordError == null && confirmPasswordError == null) {
                            accountEditViewModel.performChangePassword(currentPasswordInput, newPasswordInput, confirmNewPasswordInput)
                        }
                    },
                    enabled = !isLoading
                ) {
                    Text(text = stringResource(R.string.feature_account_edit_change_password_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { accountEditViewModel.closeChangePasswordDialog() }) {
                    Text(text = stringResource(R.string.feature_account_edit_change_password_cancel))
                }
            }
        )
    }

    // Image Picker Bottom Sheet
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState
        ) {
            ImagePickerBottomSheet(
                onCameraClick = {
                    showBottomSheet = false
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onGalleryClick = {
                    showBottomSheet = false
                    galleryLauncher.launch("image/*")
                },
                onDismiss = { showBottomSheet = false }
            )
        }
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = TextTokens.label(),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountEditScreenPreview() {
    AccountEditScreen()
}

fun buildRequiredLabel(text: String) = buildAnnotatedString {
    append(text)
    withStyle(style = SpanStyle(color = Color.Red)) {
        append(" *")
    }
}
