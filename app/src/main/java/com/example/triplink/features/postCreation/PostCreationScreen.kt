package com.example.triplink.features.postCreation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.Manifest
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.core.components.imagepicker.ImagePickerBottomSheet
import com.example.triplink.core.components.map.LocationPickerMapField
import com.example.triplink.R
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.utils.messageText
import com.example.triplink.core.localization.localizedLabel
import com.example.triplink.core.localization.localizedShortLabel
import com.example.triplink.core.components.images.ImagenesSelectorGrid
import com.example.triplink.domain.model.enums.DiaSemana
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.ui.theme.TextColors
import com.example.triplink.ui.theme.TextTokens
import kotlinx.coroutines.launch

data class DayScheduleData(
    val day: DiaSemana,
    val isEnabled: Boolean = false,
    val openTime: String = "",
    val closeTime: String = "",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCreationScreen(
    onBack: () -> Unit = {},
    viewModel: PostCreationViewModel = hiltViewModel(),
    publicationIdToEdit: String? = null,
    onUserHomeClick: () -> Unit = {},
    onUserInfoClick: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val createResult by viewModel.createResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val dayDisabledSnackbar = stringResource(R.string.feature_post_creation_day_disabled_snackbar)
    val cameraPermissionDeniedSnackbar = stringResource(R.string.permissions_camera_permission_denied)

    LaunchedEffect(publicationIdToEdit) {
        viewModel.loadPublicationForEdit(publicationIdToEdit)
    }

    LaunchedEffect(createResult) {
        when (val result = createResult) {
            is RequestResult.Failure -> {
                snackbarHostState.showSnackbar(result.messageText())
                viewModel.clearResult()
            }

            is RequestResult.Success -> {
                snackbarHostState.showSnackbar(result.messageText())
                viewModel.clearResult()
            }

            is RequestResult.Loading -> Unit

            null -> Unit
        }
    }

    // Image selection state and launchers (alineado con AccountEdit)
    val context = LocalContext.current
    var showImageSelectionSheet by remember { mutableStateOf(false) }
    var showCameraPermissionError by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val imagePickerSheetState = rememberModalBottomSheetState()

    val galeriaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.agregarImagen(it) }
        showImageSelectionSheet = false
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            tempCameraUri?.let { viewModel.agregarImagen(it) }
        }
        showImageSelectionSheet = false
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            tempCameraUri = com.example.triplink.core.utils.createTempImageUri(context)
            tempCameraUri?.let { cameraLauncher.launch(it) }
        } else {
            showCameraPermissionError = true
        }
    }

    LaunchedEffect(showCameraPermissionError) {
        if (showCameraPermissionError) {
            snackbarHostState.showSnackbar(cameraPermissionDeniedSnackbar)
            showCameraPermissionError = false
        }
    }

    Scaffold(
        topBar = {
            GeneralTopBar(
                title = stringResource(R.string.feature_post_creation_title),
                onBack = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nombre del lugar
            PostCreationCard {
                FormField(
                    label = buildRequiredLabel(stringResource(R.string.feature_post_creation_name_label)).toString(),
                    value = viewModel.placeName.value,
                    onValueChange = { viewModel.placeName.onChange(it) },
                    placeholder = stringResource(R.string.feature_post_creation_name_placeholder),
                    modifier = Modifier.fillMaxWidth(),
                    isError = viewModel.placeName.error != null,
                    errorText = viewModel.placeName.error,
                    trailingIcon = {
                        Text(
                            text = stringResource(
                                R.string.feature_publication_details_comment_counter,
                                viewModel.placeName.value.length,
                                80
                            ),
                                style = TextTokens.caption(),
                            color = TextColors.Muted,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                )
            }

            // Descripcion
            PostCreationCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.feature_post_creation_description_label),
                        style = TextTokens.label()
                    )
                    OutlinedTextField(
                        value = viewModel.description,
                        onValueChange = { if (it.length <= 300) viewModel.description = it },
                        placeholder = {
                            Text(
                                stringResource(R.string.feature_post_creation_description_placeholder),
                                color = TextColors.Muted
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        supportingText = {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                                Text(
                                    text = stringResource(
                                        R.string.feature_publication_details_comment_counter,
                                        viewModel.description.length,
                                        300
                                    ),
                                    style = TextTokens.caption(),
                                    color = TextColors.Muted
                                )
                            }
                        }
                    )
                }
            }

            // Categoría
            PostCreationCard {
                var expanded by remember { mutableStateOf(false) }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = buildRequiredLabel(stringResource(R.string.feature_post_creation_category_label)),
                        style = TextTokens.label()
                    )
                    
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = viewModel.selectedCategory.value?.localizedLabel() ?: "",
                                onValueChange = {},
                                readOnly = true,
                                placeholder = { Text(stringResource(R.string.feature_post_creation_category_placeholder), color = TextColors.Muted) },
                                modifier = Modifier
                                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                                    .fillMaxWidth(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    focusedBorderColor = if (viewModel.selectedCategory.error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
                                    unfocusedBorderColor = if (viewModel.selectedCategory.error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                viewModel.categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(text = category.localizedLabel()) },
                                        onClick = {
                                            viewModel.selectedCategory.onChange(category)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    if (viewModel.selectedCategory.error != null) {
                        Text(
                            text = viewModel.selectedCategory.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = TextTokens.caption()
                        )
                    }
                }
            }

            // Fotos
            PostCreationCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = buildRequiredLabel(stringResource(R.string.feature_post_creation_photos_label)),
                        style = TextTokens.label()
                    )
                    Text(
                        text = stringResource(R.string.feature_post_creation_photos_helper),
                        style = TextTokens.bodySecondary(),
                        color = TextColors.Muted
                    )

                    // Imagen selector grid (composable reutilizable)
                    ImagenesSelectorGrid(
                        imagenesActuales = viewModel.imagenesTemporales,
                        indiceBotonAgregar = viewModel.indiceBotonAgregar,
                        onAgregarClick = {
                            showImageSelectionSheet = true
                        },
                        onEliminarClick = { indice -> viewModel.eliminarImagen(indice) },
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Text(
                        text = stringResource(R.string.feature_post_creation_photos_helper),
                        style = TextTokens.bodySecondary(),
                        color = TextColors.Secondary
                    )
                }
            }

            // Bottom sheet for choosing camera or gallery
            if (showImageSelectionSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showImageSelectionSheet = false },
                    sheetState = imagePickerSheetState
                ) {
                    ImagePickerBottomSheet(
                        onCameraClick = {
                            showImageSelectionSheet = false
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        onGalleryClick = {
                            showImageSelectionSheet = false
                            galeriaLauncher.launch("image/*")
                        },
                        onDismiss = { showImageSelectionSheet = false }
                    )
                }
            }

            // Ubicacion exacta
            PostCreationCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = buildRequiredLabel(stringResource(R.string.feature_post_creation_location_label)),
                        style = TextTokens.label()
                    )
                    Text(
                        text = stringResource(R.string.feature_post_creation_location_helper),
                        style = TextTokens.bodySecondary(),
                        color = TextColors.Secondary
                    )
                    LocationPickerMapField(
                        currentLatitude = viewModel.latitude,
                        currentLongitude = viewModel.longitude,
                        onLocationConfirmed = { longitude, latitude ->
                            viewModel.onLocationChange(latitude = latitude, longitude = longitude)
                        }
                    )
                }
            }

            // Horarios de atención
            Text(
                text = stringResource(R.string.feature_post_creation_schedule_title),
                style = TextTokens.title(),
                modifier = Modifier.padding(top = 8.dp)
            )

            PostCreationCard {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            ClarifierSpacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.feature_post_creation_open_every_day),
                                style = TextTokens.body()
                            )
                        }
                        Switch(
                            checked = viewModel.isOpenEveryDay,
                            onCheckedChange = { viewModel.onOpenEveryDayChange(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                                uncheckedTrackColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }

                    // Dias
                    viewModel.daySchedules.forEachIndexed { index, schedule ->
                        DayScheduleRow(
                            schedule = schedule,
                            onToggle = { viewModel.onDayToggle(index, it) },
                            onOpenTimeChange = { h, m -> viewModel.onOpenTimeChange(index, h, m) },
                            onCloseTimeChange = { h, m -> viewModel.onCloseTimeChange(index, h, m) },
                            onDisabledClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(dayDisabledSnackbar)
                                }
                            }
                        )
                    }
                }
            }

            // Rango de precios
            PostCreationCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.component_publication_price_range_title),
                        style = TextTokens.sectionTitle()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val free = RangoPrecios.GRATUITO
                        val economical = RangoPrecios.ECONOMICO
                        val moderate = RangoPrecios.MODERADO
                        val expensive = RangoPrecios.COSTOSO
                        PriceOption(icon = "$", label = free.localizedLabel(), isSelected = viewModel.selectedPriceRange == free) {
                            viewModel.selectedPriceRange = free
                        }
                        PriceOption(icon = "$$", label = economical.localizedLabel(), isSelected = viewModel.selectedPriceRange == economical) {
                            viewModel.selectedPriceRange = economical
                        }
                        PriceOption(icon = "$$$", label = moderate.localizedLabel(), isSelected = viewModel.selectedPriceRange == moderate) {
                            viewModel.selectedPriceRange = moderate
                        }
                        PriceOption(icon = "$$$$", label = expensive.localizedLabel(), isSelected = viewModel.selectedPriceRange == expensive) {
                            viewModel.selectedPriceRange = expensive
                        }
                    }
                }
            }

            // Advertencia (campos obligatorios)
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(TextTokens.boldSpanStyle()) {
                                append(stringResource(R.string.feature_post_creation_required_fields_warning_prefix))
                            }
                            append(" ")
                            append(stringResource(R.string.feature_post_creation_required_fields_warning_body))
                        },
                        style = TextTokens.caption(),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }


            GeneralButton(
                text = viewModel.submitButtonLabel,
                onClick = { viewModel.createPost() },
                enabled = viewModel.isFormValid,
                isLoading = isLoading
            )
        }

        if (viewModel.showSuccessModal) {
            PostSuccessBottomSheet(
                onDismiss = {
                    viewModel.dismissSuccessModal()
                    onUserHomeClick()
                },
                onNavigateToPosts = onUserInfoClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostSuccessBottomSheet(
    onDismiss: () -> Unit,
    onNavigateToPosts: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant, CircleShape)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                                text = stringResource(R.string.feature_post_creation_success_title),
                        color = MaterialTheme.colorScheme.onPrimary,
                                style = TextTokens.sectionTitle()
                    )
                    Text(
                                text = stringResource(R.string.feature_post_creation_success_subtitle),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                                style = TextTokens.body()
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.feature_post_creation_process_title),
                    style = TextTokens.emphasized(TextTokens.caption(), FontWeight.Bold),
                    color = TextColors.Secondary
                )

                // Paso 1: Enviada
                StepRow(
                    icon = Icons.Default.Check,
                    title = stringResource(R.string.feature_post_creation_step_sent_title),
                    subtitle = stringResource(R.string.feature_post_creation_step_sent_subtitle),
                    status = StepStatus.COMPLETED
                )

                // Paso 2: En revisión
                StepRow(
                    icon = Icons.Default.AccessTime,
                    title = stringResource(R.string.feature_post_creation_step_review_title),
                    subtitle = stringResource(R.string.feature_post_creation_step_review_subtitle),
                    status = StepStatus.ACTIVE
                )

                // Paso 3: Publicada
                StepRow(
                    icon = Icons.Default.Circle,
                    title = stringResource(R.string.feature_post_creation_step_published_title),
                    subtitle = stringResource(R.string.feature_post_creation_step_published_subtitle),
                    status = StepStatus.INACTIVE
                )

                Spacer(modifier = Modifier.height(8.dp))


                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.feature_post_creation_accept_action),
                        style = TextTokens.emphasized(TextTokens.button(), FontWeight.Bold)
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToPosts,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.feature_post_creation_view_publications_action),
                            style = TextTokens.emphasized(TextTokens.button(), FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

enum class StepStatus { COMPLETED, ACTIVE, INACTIVE }

@Composable
fun StepRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    status: StepStatus
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = when (status) {
                    StepStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                    StepStatus.ACTIVE -> MaterialTheme.colorScheme.tertiary
                    StepStatus.INACTIVE -> MaterialTheme.colorScheme.outlineVariant
                }
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = when (status) {
                        StepStatus.COMPLETED -> MaterialTheme.colorScheme.onPrimary
                        StepStatus.ACTIVE -> MaterialTheme.colorScheme.onTertiary
                        StepStatus.INACTIVE -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.padding(6.dp)
                )
            }

            if (status != StepStatus.INACTIVE) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(16.dp)
                        .background(
                            if (status == StepStatus.COMPLETED) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = TextTokens.emphasized(TextTokens.title(), FontWeight.Bold),
                color = if (status == StepStatus.INACTIVE) TextColors.Muted else TextColors.Primary
            )
            Text(
                text = subtitle,
                style = TextTokens.caption(),
                color = if (status == StepStatus.ACTIVE) MaterialTheme.colorScheme.tertiary else TextColors.Secondary
            )
        }
    }
}

@Composable
fun DayScheduleRow(
    schedule: DayScheduleData,
    onToggle: (Boolean) -> Unit,
    onOpenTimeChange: (String, String) -> Unit,
    onCloseTimeChange: (String, String) -> Unit,
    onDisabledClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = schedule.day.localizedShortLabel(),
            style = TextTokens.emphasized(TextTokens.body(), FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(35.dp)
        )

        val openParts = schedule.openTime.split(":").let { if(it.size == 2) it else listOf("", "") }
        val closeParts = schedule.closeTime.split(":").let { if(it.size == 2) it else listOf("", "") }

        TimeInputGroup(
            hours = openParts[0],
            minutes = openParts[1],
            onTimeChange = onOpenTimeChange,
            enabled = schedule.isEnabled,
            onDisabledClick = onDisabledClick
        )
        
        Text(
            text = stringResource(R.string.feature_post_creation_time_separator), 
            color = TextColors.Secondary,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
        
        TimeInputGroup(
            hours = closeParts[0],
            minutes = closeParts[1],
            onTimeChange = onCloseTimeChange,
            enabled = schedule.isEnabled,
            onDisabledClick = onDisabledClick
        )

        ClarifierSpacer(Modifier.weight(1f))

        Switch(
            checked = schedule.isEnabled,
            onCheckedChange = onToggle,
            modifier = Modifier.scale(0.8f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                uncheckedTrackColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}

@Composable
fun TimeInputGroup(
    hours: String,
    minutes: String,
    onTimeChange: (String, String) -> Unit,
    enabled: Boolean,
    onDisabledClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TimeUnitInput(
            value = hours,
            onValueChange = { onTimeChange(it, minutes) },
            placeholder = "00",
            enabled = enabled,
            onDisabledClick = onDisabledClick
        )
        Text(":", style = TextTokens.body(), color = if (enabled) TextColors.Primary else TextColors.Secondary)
        TimeUnitInput(
            value = minutes,
            onValueChange = { onTimeChange(hours, it) },
            placeholder = "00",
            enabled = enabled,
            onDisabledClick = onDisabledClick
        )
    }
}

@Composable
fun TimeUnitInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    onDisabledClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(42.dp)
            .height(36.dp)
            .background(
                if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(8.dp)
            )
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .clickable(enabled = !enabled, onClick = onDisabledClick),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = { if (it.length <= 2) onValueChange(it) },
            enabled = enabled,
            singleLine = true,
            textStyle = TextTokens.colored(
                TextTokens.centered(TextTokens.emphasized(TextTokens.body(), FontWeight.Medium)),
                if (enabled) TextColors.Primary else TextColors.Secondary
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        placeholder,
                        style = TextTokens.body(),
                        color = TextColors.Muted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                innerTextField()
            }
        )
    }
}

@Composable
fun PriceOption(icon: String, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = icon,
                    style = TextTokens.emphasized(TextTokens.title(), FontWeight.Bold),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Text(
            text = label,
            style = TextTokens.chip(),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ClarifierSpacer(modifier: Modifier) = Spacer(modifier = modifier)

@Composable
fun PostCreationCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)

@Composable
fun buildRequiredLabel(text: String) = buildAnnotatedString {
    append(text)
    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
        append(" *")
    }
}


@Preview(showBackground = true)
@Composable
fun PostCreationScreenPreview() {
    PostCreationScreen()
}
