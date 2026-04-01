package com.example.triplink.features.publicationDetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.ui.theme.*

data class DaySchedule(
    val day: String,
    val hours: String,
    val isClosed: Boolean = false
)

data class Review(
    val username: String,
    val rating: Int,
    val comment: String
)

fun esInapropiado(): Boolean = (0..1).random() == 1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationDetailsScreen(
    publicationId: String,
    onBackClick: () -> Unit,
    onSeeAllReviewsClick: (String) -> Unit
) {
    var showReportModal by remember { mutableStateOf(false) }
    var showRatingModal by remember { mutableStateOf(false) }

    val schedules = listOf(
        DaySchedule("Lunes", "8:00 am - 5:00 pm"),
        DaySchedule("Martes", "8:00 am - 5:00 pm"),
        DaySchedule("Miércoles", "8:00 am - 5:00 pm"),
        DaySchedule("Jueves", "8:00 am - 5:00 pm"),
        DaySchedule("Viernes", "8:00 am - 6:00 pm"),
        DaySchedule("Sábado", "7:00 am - 6:00 pm"),
        DaySchedule("Domingo", "Cerrado", isClosed = true)
    )
    val today = "Jueves"
    
    val selectedPriceLevel = "Económico"

    val reviews = listOf(
        Review("carlos_montoya", 5, "¡Increíble lugar! La vista es maravillosa."),
        Review("carlos_montoya", 4, "Me encanto la historia del lugar.")
    )
    val generalRating = 4.8

    Scaffold(
        topBar = {
            Column {
                GeneralTopBar(
                    title = "Detalle del Lugar",
                    onBack = onBackClick
                )
                ImageHeader(
                    onReportClick = { showReportModal = true },
                    onBackClick = onBackClick
                )
            }
        },
        bottomBar = {
            BottomActionsBar(onVisitedClick = { showRatingModal = true })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            item {
                DescriptionSection()
            }
            item {
                PriceRangeSection(selectedLevel = selectedPriceLevel)
            }
            item {
                LocationSection()
            }
            item {
                ScheduleSection(schedules = schedules, today = today)
            }
            item {
                ReviewsSection(
                    publicationId = publicationId,
                    reviews = reviews,
                    generalRating = generalRating,
                    onSeeAllReviewsClick = onSeeAllReviewsClick
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showReportModal) {
        ReportModal(onDismiss = { showReportModal = false })
    }

    if (showRatingModal) {
        RatingModal(onDismiss = { showRatingModal = false })
    }
}

@Composable
fun ImageHeader(onReportClick: () -> Unit, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) 
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray) 
        )
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = "NATURALEZA",
                color = PrincipalGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Valle de Cocora",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .size(44.dp),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.4f)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .size(44.dp),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.4f)
        ) {
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
            }
        }

        IconButton(
            onClick = onReportClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = "Reportar",
                tint = PrincipalRed,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingModal(onDismiss: () -> Unit) {
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var showInappropriateModal by remember { mutableStateOf(false) }
    val maxChars = 300

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
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Qué te pareció este lugar?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tu opinión ayuda a otros usuarios",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CALIFICACIÓN",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "REQUERIDA",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = Color(0xFFEF5350),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(5) { index ->
                    val starIndex = index + 1
                    val isSelected = starIndex <= rating
                    Icon(
                        imageVector = if (isSelected) Icons.Default.Star else Icons.Outlined.StarOutline,
                        contentDescription = null,
                        tint = if (isSelected) PrincipalOrange else Color(0xFFCBD5E1),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { rating = starIndex }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Toca para calificar",
                fontSize = 14.sp,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "COMENTARIO",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "OPCIONAL",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color.Gray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "${comment.length}/$maxChars",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = { if (it.length <= maxChars) comment = it },
                placeholder = { Text("Cuéntanos más sobre tu experiencia...", color = Color.LightGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE2E8F0),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color(0xFFF8FAFC),
                    unfocusedContainerColor = Color(0xFFF8FAFC)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            GeneralButton(
                text = "Publicar reseña",
                onClick = {
                    if (esInapropiado()) {
                        showInappropriateModal = true
                    } else {
                        onDismiss()
                    }
                },
                icon = Icons.AutoMirrored.Filled.Send,
                enabled = rating > 0
            )

            TextButton(onClick = onDismiss) {
                Text("Cancelar", fontWeight = FontWeight.Bold, color = PrincipalBlue)
            }
        }
    }

    if (showInappropriateModal) {
        InappropriateContentModal(
            onDismiss = { showInappropriateModal = false },
            onReplace = {
                showInappropriateModal = false
                onDismiss()
            }
        )
    }
}

@Composable
fun InappropriateContentModal(onDismiss: () -> Unit, onReplace: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(28.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Red indicator bar at the top
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(Color(0xFFEF5350))
                )

                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Close button
                    Box(modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(Color(0xFFF1F5F9), CircleShape)
                                .size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", modifier = Modifier.size(18.dp), tint = Color.Gray)
                        }
                    }

                    // Angry Face Icon
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFEBEE),
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SentimentVeryDissatisfied,
                                contentDescription = null,
                                tint = Color(0xFFEF5350),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "¡Se ha detectado contenido inapropiado!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "El comentario que estas realizando contiene lenguaje obseno. Si quieres crear un comentario puedes reemplazarlo por:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Suggestion Box
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "El lugar no fue de mi agrado, mi expeciencia fue mala.",
                            modifier = Modifier.padding(16.dp),
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, PrincipalBlue)
                        ) {
                            Text("Cancelar", color = PrincipalBlue, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onReplace,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E))
                        ) {
                            Text("Reemplazar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportModal(onDismiss: () -> Unit) {
    var selectedOption by remember { mutableStateOf("") }
    var otherReason by remember { mutableStateOf("") }

    val options = listOf(
        ReportOptionData("Información incorrecta", "Datos desactualizados o inexactos", Icons.AutoMirrored.Outlined.LibraryBooks),
        ReportOptionData("Ubicación errónea", "El punto en el mapa no coincide con el lugar real", Icons.Outlined.LocationOn),
        ReportOptionData("Contenido inapropiado", "Lenguaje ofensivo, imágenes inadecuadas o spam", Icons.Outlined.Block),
        ReportOptionData("Otro", "Describe el motivo específico a continuación", Icons.Outlined.Edit)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(28.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = PrincipalBlue,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Reportar contenido",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .background(Color(0xFFF1F5F9), CircleShape)
                            .size(32.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", modifier = Modifier.size(18.dp), tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Selecciona el motivo del reporte",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth()
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF1F5F9))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    options.forEach { option ->
                        val isSelected = selectedOption == option.title
                        ReportOptionItem(
                            option = option,
                            isSelected = isSelected,
                            onClick = { selectedOption = option.title }
                        )
                    }
                }

                if (selectedOption == "Otro") {
                    Spacer(modifier = Modifier.height(16.dp))
                    FormField(
                        label = "Descripción del motivo",
                        value = otherReason,
                        onValueChange = { otherReason = it },
                        placeholder = "Escribe aquí el motivo..."
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.VerifiedUser, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Tu reporte será revisado por el equipo de moderación.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                GeneralButton(
                    text = "Enviar reporte",
                    onClick = { /* Enviar */ },
                    icon = Icons.AutoMirrored.Filled.Send,
                    enabled = selectedOption.isNotEmpty()
                )

                TextButton(onClick = onDismiss) {
                    Text("Cancelar", fontWeight = FontWeight.Bold, color = PrincipalBlue)
                }
            }
        }
    }
}

data class ReportOptionData(val title: String, val subtitle: String, val icon: ImageVector)

@Composable
fun ReportOptionItem(option: ReportOptionData, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isSelected) PrincipalBlue else Color(0xFFF1F5F9)),
        color = if (isSelected) Color(0xFFF8FAFF) else Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                tint = if (isSelected) PrincipalBlue else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isSelected) PrincipalBlue else Color.Black
                )
                Text(
                    text = option.subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 14.sp
                )
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = PrincipalBlue,
                    unselectedColor = Color.LightGray
                )
            )
        }
    }
}

@Composable
fun DescriptionSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Descripción",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "El hogar de la palma de cera del Quindío, el árbol nacional de Colombia. Un paisaje surrealista de verdes montañas y niebla.",
            style = MaterialTheme.typography.bodyLarge,
            color = DarkGray,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun PriceRangeSection(selectedLevel: String) {
    val levels = listOf("Gratuito", "Económico", "Moderado", "Costoso")
    val selectedIndex = levels.indexOf(selectedLevel).coerceAtLeast(0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Rango de precios",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        Surface(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Selector de precios basado en el índice
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PriceTag(text = "$", isSelected = selectedIndex == 0)
                    PriceTag(text = "$$", isSelected = selectedIndex == 1)
                    PriceTag(text = "$$$", isSelected = selectedIndex == 2)
                    PriceTag(text = "$$$$", isSelected = selectedIndex == 3)
                }

                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = selectedLevel,
                        color = PrincipalBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Rango de precio\nestimado",
                        color = PrincipalGray,
                        fontSize = 12.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PriceTag(text: String, isSelected: Boolean) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) PrincipalBlue else Color(0xFFF1F5F9),
        modifier = Modifier.size(width = 50.dp, height = 45.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (isSelected) Color.White else PrincipalGray,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun ScheduleSection(schedules: List<DaySchedule>, today: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Horarios",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        Surface(
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp)
            ) {
                schedules.forEach { item ->
                    val isToday = item.day == today
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isToday) SoftBlue else Color.Transparent)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isToday) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(PrincipalBlue, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            } else {
                                Spacer(modifier = Modifier.width(14.dp))
                            }
                            
                            Text(
                                text = item.day,
                                color = if (isToday) PrincipalBlue else if (item.isClosed) PrincipalGray else Color.Black,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 16.sp
                            )
                            
                            if (isToday) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = PastelBlue,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Hoy",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        color = PrincipalBlue,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        
                        Text(
                            text = item.hours,
                            color = if (isToday) PrincipalBlue else if (item.isClosed) PrincipalGray.copy(alpha = 0.6f) else DarkGray,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LocationSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Ubicación",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        // Tarjeta de Mapa personalizada según la imagen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE2E8F0)) // Color base de fondo de mapa
                .clickable { /* Abrir mapas */ },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono de Mapa en círculo blanco
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(70.dp),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = PrincipalBlue,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Ver en Mapas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
                
                Text(
                    text = "4.6650, -75.5751",
                    fontSize = 14.sp,
                    color = DarkGray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ReviewsSection(
    publicationId: String,
    reviews: List<Review>,
    generalRating: Double,
    onSeeAllReviewsClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reseñas",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = PrincipalOrange,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = generalRating.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        reviews.forEach { review ->
            ReviewCard(review)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        GeneralButton(
            onClick = { onSeeAllReviewsClick(publicationId) },
            text = "Ver todas las reseñas"
        )
    }
}

@Composable
fun ReviewCard(review: Review) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.username,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < review.rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = PrincipalOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.comment,
                color = DarkGray,
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun BottomActionsBar(onVisitedClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 16.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, PrincipalBlue),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrincipalBlue)
            ) {
                Icon(Icons.Outlined.FavoriteBorder, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Me interesa",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            OutlinedButton(
                onClick = onVisitedClick,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, PrincipalGreen),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrincipalGreen)
            ) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Visitado",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
