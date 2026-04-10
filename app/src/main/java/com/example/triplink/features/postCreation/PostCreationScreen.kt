package com.example.triplink.features.postCreation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.R
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.core.localization.localizedLabel
import com.example.triplink.core.localization.localizedShortLabel
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.DiaSemana
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.ui.theme.PrincipalBlue
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
    val dayDisabledSnackbar = stringResource(R.string.feature_post_creation_day_disabled_snackbar)

    LaunchedEffect(publicationIdToEdit) {
        viewModel.loadPublicationForEdit(publicationIdToEdit)
    }

    LaunchedEffect(createResult) {
        when (val result = createResult) {
            is RequestResult.Failure -> {
                snackbarHostState.showSnackbar(result.errorMessage)
                viewModel.clearResult()
            }

            is RequestResult.Success -> {
                viewModel.clearResult()
            }

            null -> Unit
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
                            text = "${viewModel.placeName.value.length}/80",
                            style = MaterialTheme.typography.bodySmall,
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
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
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
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedBorderColor = Color(0xFFE0E0E0),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        supportingText = {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                                Text(
                                    text = "${viewModel.description.length}/300",
                                    style = MaterialTheme.typography.bodySmall,
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
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
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
                                    focusedContainerColor = Color(0xFFF5F5F5),
                                    unfocusedContainerColor = Color(0xFFF5F5F5),
                                    focusedBorderColor = if (viewModel.selectedCategory.error != null) Color.Red else Color(0xFFE0E0E0),
                                    unfocusedBorderColor = if (viewModel.selectedCategory.error != null) Color.Red else Color(0xFFE0E0E0)
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(Color.White)
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
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Fotos
            PostCreationCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = buildRequiredLabel(stringResource(R.string.feature_post_creation_photos_label)),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(R.string.feature_post_creation_photos_helper),
                        style = TextTokens.helperText(),
                        color = TextColors.Muted
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {

                        Surface(
                            modifier = Modifier.size(60.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = PrincipalBlue
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.feature_post_creation_photos_add_content_description),
                                tint = Color.White,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        repeat(4) {
                            Surface(
                                modifier = Modifier.size(60.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF5F5F5),
                                border = borderStroke()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = stringResource(R.string.feature_post_creation_photos_helper),
                        style = TextTokens.helperText(),
                        color = TextColors.Secondary
                    )
                }
            }

            // Ubicacion exacta
            PostCreationCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = buildRequiredLabel(stringResource(R.string.feature_post_creation_location_label)),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(R.string.feature_post_creation_location_helper),
                        style = TextTokens.helperText(),
                        color = TextColors.Secondary
                    )
                    // Placeholder mapa
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color(0xFFD1E7D1), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val gridSpacing = 40.dp.toPx()
                            for (x in 0..size.width.toInt() step gridSpacing.toInt()) {
                                drawLine(
                                    Color.White.copy(alpha = 0.5f),
                                    start = androidx.compose.ui.geometry.Offset(x.toFloat(), 0f),
                                    end = androidx.compose.ui.geometry.Offset(x.toFloat(), size.height)
                                )
                            }
                            for (y in 0..size.height.toInt() step gridSpacing.toInt()) {
                                drawLine(
                                    Color.White.copy(alpha = 0.5f),
                                    start = androidx.compose.ui.geometry.Offset(0f, y.toFloat()),
                                    end = androidx.compose.ui.geometry.Offset(size.width, y.toFloat())
                                )
                            }
                        }

                        // Label externo
                        Text(
                            text = stringResource(R.string.feature_post_creation_location_city),
                            modifier = Modifier.padding(8.dp),
                            color = TextColors.Secondary,
                            style = TextTokens.helperText()
                        )

                        // Button Overlay
                        Button(
                            onClick = { /* Mark on map */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = PrincipalBlue
                            ),
                            shape = RoundedCornerShape(24.dp),
                            elevation = ButtonDefaults.buttonElevation(4.dp),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            ClarifierSpacer(Modifier.width(8.dp))
                                Text(text = stringResource(R.string.feature_post_creation_location_mark_action), style = TextTokens.buttonLabel())
                        }
                    }


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(24.dp))
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(24.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                            ClarifierSpacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.feature_post_creation_location_address_placeholder),
                                color = TextColors.Muted,
                                style = TextTokens.inputText()
                            )
                        }
                    }
                }
            }

            // Horarios de atención
            Text(
                text = stringResource(R.string.feature_post_creation_schedule_title),
                style = TextTokens.sectionAction(),
                modifier = Modifier.padding(top = 8.dp)
            )

            PostCreationCard {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = PrincipalBlue)
                            ClarifierSpacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.feature_post_creation_open_every_day),
                                style = TextTokens.inputText()
                            )
                        }
                        Switch(
                            checked = viewModel.isOpenEveryDay,
                            onCheckedChange = { viewModel.onOpenEveryDayChange(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrincipalBlue,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.LightGray
                            )
                        )
                    }

                    // Dias
                    viewModel.daySchedules.forEachIndexed { index, schedule ->
                        DayScheduleRow(
                            schedule = schedule,
                            onToggle = { viewModel.onDayToggle(index, it) },
                            onOpenTimeChange = { viewModel.onOpenTimeChange(index, it) },
                            onCloseTimeChange = { viewModel.onCloseTimeChange(index, it) },
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
                color = Color(0xFFFFF9C4).copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F)),
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
                        tint = Color(0xFFF57F17)
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(R.string.feature_post_creation_required_fields_warning_prefix))
                            }
                            append(" ")
                            append(stringResource(R.string.feature_post_creation_required_fields_warning_body))
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5D4037)
                    )
                }
            }


            GeneralButton(
                text = viewModel.submitButtonLabel,
                onClick = { viewModel.createPost() },
                enabled = viewModel.isFormValid
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
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color(0xFFE2E8F0), CircleShape)
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
                    .background(PrincipalBlue)
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.3f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                                text = stringResource(R.string.feature_post_creation_success_title),
                        color = Color.White,
                                style = TextTokens.sectionTitle()
                    )
                    Text(
                                text = stringResource(R.string.feature_post_creation_success_subtitle),
                        color = Color.White.copy(alpha = 0.9f),
                                style = TextTokens.inputText()
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
                    style = TextTokens.counterLabel(),
                    color = TextColors.Secondary,
                    letterSpacing = 1.sp
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
                    colors = ButtonDefaults.buttonColors(containerColor = PrincipalBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.feature_post_creation_accept_action),
                        style = TextTokens.buttonLabel(),
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToPosts,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrincipalBlue.copy(alpha = 0.1f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PrincipalBlue,
                        containerColor = Color(0xFFF5F8FF)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.feature_post_creation_view_publications_action),
                            style = TextTokens.buttonLabel(),
                            fontWeight = FontWeight.Bold
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
                    StepStatus.COMPLETED -> Color(0xFF4CAF50)
                    StepStatus.ACTIVE -> Color(0xFFFF9800)
                    StepStatus.INACTIVE -> Color(0xFFE0E0E0)
                }
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(6.dp)
                )
            }

            if (status != StepStatus.INACTIVE) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(16.dp)
                        .background(if (status == StepStatus.COMPLETED) Color(0xFF4CAF50) else Color(0xFFE0E0E0))
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (status == StepStatus.INACTIVE) TextColors.Muted else TextColors.Primary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (status == StepStatus.ACTIVE) Color(0xFFFF9800) else TextColors.Secondary
            )
        }
    }
}

@Composable
fun DayScheduleRow(
    schedule: DayScheduleData,
    onToggle: (Boolean) -> Unit,
    onOpenTimeChange: (String) -> Unit,
    onCloseTimeChange: (String) -> Unit,
    onDisabledClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = schedule.day.localizedShortLabel(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = PrincipalBlue,
            modifier = Modifier.width(35.dp)
        )

        TimeInputBox(
            value = schedule.openTime,
            onValueChange = onOpenTimeChange,
            enabled = schedule.isEnabled,
            onDisabledClick = onDisabledClick
        )
        Text(stringResource(R.string.feature_post_creation_time_separator), color = TextColors.Secondary)
        TimeInputBox(
            value = schedule.closeTime,
            onValueChange = onCloseTimeChange,
            enabled = schedule.isEnabled,
            onDisabledClick = onDisabledClick
        )

        ClarifierSpacer(Modifier.weight(1f))

        Switch(
            checked = schedule.isEnabled,
            onCheckedChange = onToggle,
            modifier = Modifier.scale(0.8f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrincipalBlue,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}

@Composable
fun TimeInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    onDisabledClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(36.dp)
            .background(
                if (enabled) Color.White else Color(0xFFF5F5F5),
                RoundedCornerShape(8.dp)
            )
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
            .clickable(enabled = !enabled, onClick = onDisabledClick),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = { if (it.length <= 5) onValueChange(it) },
            enabled = enabled,
            singleLine = true,
            textStyle = TextStyle(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                color = if (enabled) TextColors.Primary else TextColors.Secondary
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            cursorBrush = SolidColor(PrincipalBlue),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        stringResource(R.string.feature_post_creation_time_placeholder),
                        style = TextTokens.inputText(),
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
            color = if (isSelected) PrincipalBlue.copy(alpha = 0.1f) else Color.White,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isSelected) PrincipalBlue else Color(0xFFE0E0E0)
            )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) PrincipalBlue else Color.Black
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) PrincipalBlue else Color.Gray
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
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))

fun buildRequiredLabel(text: String) = buildAnnotatedString {
    append(text)
    withStyle(style = SpanStyle(color = Color.Red)) {
        append(" *")
    }
}


@Preview(showBackground = true)
@Composable
fun PostCreationScreenPreview() {
    PostCreationScreen()
}
