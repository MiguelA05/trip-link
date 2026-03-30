package com.example.triplink.features.postCreation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplink.core.components.FormField
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.ui.theme.PrincipalBlue

@Composable
fun PostCreationScreen(
    onBack: () -> Unit = {}
) {
    var placeName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isOpenEveryDay by remember { mutableStateOf(false) }
    var selectedPriceRange by remember { mutableStateOf("Gratuito") }

    Scaffold(
        topBar = {
            GeneralTopBar(
                title = "Nueva Publicación",
                onBack = onBack
            )
        },
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
                    value = placeName,
                    onValueChange = { if (it.length <= 80) placeName = it },
                    placeholder = "Ej. Valle del Cocora",
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Text(
                            text = "${placeName.length}/80",
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
                        value = description,
                        onValueChange = { if (it.length <= 300) description = it },
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
                                    text = "${description.length}/300",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray
                                )
                            }
                        }
                    )
                }
            }

            // Categoria
            PostCreationCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = buildRequiredLabel("Categoría"),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                            .clickable { /* Show category picker */ }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Seleccionar categoría", color = Color.LightGray)
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
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
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
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
                    // Map Placeholder with Button Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color(0xFFD1E7D1), RoundedCornerShape(12.dp)) // Light green for map feel
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                    ) {
                        // Grid effect to simulate map
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val gridSpacing = 40.dp.toPx()
                            for (x in 0..size.width.toInt() step gridSpacing.toInt()) {
                                drawLine(Color.White.copy(alpha = 0.5f), start = androidx.compose.ui.geometry.Offset(x.toFloat(), 0f), end = androidx.compose.ui.geometry.Offset(x.toFloat(), size.height))
                            }
                            for (y in 0..size.height.toInt() step gridSpacing.toInt()) {
                                drawLine(Color.White.copy(alpha = 0.5f), start = androidx.compose.ui.geometry.Offset(0f, y.toFloat()), end = androidx.compose.ui.geometry.Offset(size.width, y.toFloat()))
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
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
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
                            checked = isOpenEveryDay,
                            onCheckedChange = { isOpenEveryDay = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrincipalBlue,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.LightGray
                            )
                        )
                    }

                    // Dias
                    val days = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
                    days.forEach { day ->
                        DayScheduleRow(day = day)
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
                        PriceOption(icon = "$", label = "Gratuito", isSelected = selectedPriceRange == "Gratuito") {
                            selectedPriceRange = "Gratuito"
                        }
                        PriceOption(icon = "$$", label = "Economico", isSelected = selectedPriceRange == "Economico") {
                            selectedPriceRange = "Economico"
                        }
                        PriceOption(icon = "$$$", label = "Moderado", isSelected = selectedPriceRange == "Moderado") {
                            selectedPriceRange = "Moderado"
                        }
                        PriceOption(icon = "$$$$", label = "Costoso", isSelected = selectedPriceRange == "Costoso") {
                            selectedPriceRange = "Costoso"
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
                text = "Publicar",
                onClick = { /* Handle publish */ }
            )
        }
    }
}

@Composable
fun DayScheduleRow(day: String) {
    var isEnabled by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = day,
            fontWeight = FontWeight.Bold,
            color = PrincipalBlue,
            modifier = Modifier.width(35.dp)
        )

        TimeInputBox()
        Text("-", color = Color.Gray)
        TimeInputBox()

        ClarifierSpacer(Modifier.weight(1f))

        Switch(
            checked = isEnabled,
            onCheckedChange = { isEnabled = it },
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
fun TimeInputBox() {
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(36.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
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
