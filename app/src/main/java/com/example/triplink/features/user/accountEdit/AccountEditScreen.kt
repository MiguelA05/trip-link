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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.createTempImageUri
import com.example.triplink.ui.theme.TextColors
import com.example.triplink.ui.theme.TextTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEditScreen(
    accountEditViewModel: AccountEditViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onAppHomeClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val updateResult by accountEditViewModel.updateResult.collectAsState()
    val deleteResult by accountEditViewModel.deleteResult.collectAsState()
    val photoUri by accountEditViewModel.photoUri.collectAsState()
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
                    onAppHomeClick()
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SelectableDropdown(
                        label = stringResource(R.string.feature_account_edit_department_label),
                        selectedValue = accountEditViewModel.selectedDepartment,
                        options = accountEditViewModel.departments,
                        onOptionSelected = { accountEditViewModel.onDepartmentChange(it) },
                        modifier = Modifier.weight(1f)
                    )

                    SelectableDropdown(
                        label = stringResource(R.string.feature_account_edit_city_label),
                        selectedValue = accountEditViewModel.selectedCity,
                        options = accountEditViewModel.getCitiesForDepartment(accountEditViewModel.selectedDepartment),
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
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = stringResource(R.string.feature_account_edit_add_exact_location),
                        style = TextTokens.body(),
                        color = TextColors.Secondary
                    )
                }

                if (accountEditViewModel.addExactLocation) {
                    Spacer(modifier = Modifier.height(12.dp))
                    // Small MapBox to pick exact location. Clicking on map will set coordinates in ViewModel.
                    com.example.triplink.core.components.map.MapBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        markers = listOfNotNull(
                            accountEditViewModel.selectedLatitude?.let { lat ->
                                accountEditViewModel.selectedLongitude?.let { lon ->
                                    com.example.triplink.core.components.map.MapMarker(id = "selected", latitude = lat, longitude = lon)
                                }
                            }
                        ),
                        showMyLocationButton = false,
                        activateClick = true,
                        onMapClickListener = { lon, lat ->
                            accountEditViewModel.onLocationSelected(lon, lat)
                        }
                    )
                }
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
                        onChangePasswordClick()
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
                    enabled = accountEditViewModel.canSaveChanges
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
                color = if (selectedValue.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
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
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
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
