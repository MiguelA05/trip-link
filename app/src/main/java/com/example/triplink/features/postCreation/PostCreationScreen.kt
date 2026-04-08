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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.core.utils.RequestResult
import com.example.triplink.ui.theme.PrincipalBlue
import kotlinx.coroutines.launch

data class DayScheduleData(
    val day: String,
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
                title = "Nueva Publicación",
                onBack = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF8F9FA)
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
                    label = buildRequiredLabel("Nombre del lugar").toString(),
                    value = viewModel.placeName.value,
                    onValueChange = { viewModel.placeName.onChange(it) },
                    placeholder = "Ej. Valle del Cocora",
                    modifier = Modifier.fillMaxWidth(),
                    isError = viewModel.placeName.error != null,
                    errorText = viewModel.placeName.error,
                    trailingIcon = {
                        Text(
                            text = "${viewModel.placeName.value.length}/80",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                )
            }

            // Descripcion
            PostCreationCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = viewModel.description,
                        onValueChange = { if (it.length <= 300) viewModel.description = it },
                        placeholder = {
                            Text(
                                "Describe este lugar, qué lo hace especial...",
                                color = Color.LightGray
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
                                    color = Color.LightGray
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
                        text = buildRequiredLabel("Categoría"),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = viewModel.selectedCategory.value,
                                onValueChange = {},
                                readOnly = true,
                                placeholder = { Text("Seleccionar categoría", color = Color.LightGray) },
                                modifier = Modifier
                                    .menuAnchor()
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
                                        text = { Text(text = category) },
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
                        text = buildRequiredLabel("Fotos"),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
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
                                contentDescription = "Añadir foto",
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
                        text = "Añade hasta 5 fotografías para mostrar en tu publicación",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            // Ubicacion exacta
            PostCreationCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = buildRequiredLabel("Ubicación exacta"),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Toca el mapa para marcar la ubicación exacta del lugar",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
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

                        // Label "Armenia, Quindío"
                        Text(
                            text = "Armenia, Quindío",
                            modifier = Modifier.padding(8.dp),
                            color = Color.Gray,
                            fontSize = 12.sp
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
                            Text("Toca para marcar", fontWeight = FontWeight.SemiBold)
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
                            Text("La dirección aparecerá aquí", color = Color.LightGray, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Horarios de atención
            Text(
                text = "Horarios de atención",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
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
                            Text("Abierto todos los días", fontWeight = FontWeight.Medium)
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
                                    snackbarHostState.showSnackbar("Se debe activar el switch del día para poder hacerlo")
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
                        text = "Rango de precios",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PriceOption(icon = "$", label = "Gratuito", isSelected = viewModel.selectedPriceRange == "Gratuito") {
                            viewModel.selectedPriceRange = "Gratuito"
                        }
                        PriceOption(icon = "$$", label = "Economico", isSelected = viewModel.selectedPriceRange == "Economico") {
                            viewModel.selectedPriceRange = "Economico"
                        }
                        PriceOption(icon = "$$$", label = "Moderado", isSelected = viewModel.selectedPriceRange == "Moderado") {
                            viewModel.selectedPriceRange = "Moderado"
                        }
                        PriceOption(icon = "$$$$", label = "Costoso", isSelected = viewModel.selectedPriceRange == "Costoso") {
                            viewModel.selectedPriceRange = "Costoso"
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
                                append("Campos obligatorios: ")
                            }
                            append("Nombre, Categoría y Ubicación en mapa para poder publicar.")
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
                        text = "¡Publicación enviada!",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Está en manos del equipo de moderación",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
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
                    text = "PROCESO DE PUBLICACIÓN",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )

                // Paso 1: Enviada
                StepRow(
                    icon = Icons.Default.Check,
                    title = "Publicación enviada",
                    subtitle = "Recibida correctamente",
                    status = StepStatus.COMPLETED
                )

                // Paso 2: En revisión
                StepRow(
                    icon = Icons.Default.AccessTime,
                    title = "En revisión",
                    subtitle = "Moderadores revisarán en ~24 h",
                    status = StepStatus.ACTIVE
                )

                // Paso 3: Publicada
                StepRow(
                    icon = Icons.Default.Circle,
                    title = "Publicada",
                    subtitle = "Visible para la comunidad",
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
                    Text("Aceptar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Ver mis publicaciones", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (status == StepStatus.INACTIVE) Color.LightGray else Color.Black
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = if (status == StepStatus.ACTIVE) Color(0xFFFF9800) else Color.Gray
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
            text = schedule.day,
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
        Text("-", color = Color.Gray)
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
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (enabled) Color.Black else Color.Gray
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            cursorBrush = SolidColor(PrincipalBlue),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        "00:00",
                        fontSize = 14.sp,
                        color = Color.LightGray,
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
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isSelected) PrincipalBlue else Color.Black
                )
            }
        }
        Text(
            text = label,
            fontSize = 12.sp,
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
