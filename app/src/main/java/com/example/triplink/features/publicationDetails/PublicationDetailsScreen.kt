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
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun PublicationDetailsScreen(
    publicationId: String = "",
    onBackClick: () -> Unit = {},
    onSeeAllReviewsClick: (String) -> Unit = {}
) {
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
    
    // Variable para el rango de precio: "Gratuito", "Económico", "Moderado" o "Costoso"
    val selectedPriceLevel = "Económico"

    val reviews = listOf(
        Review("carlos_montoya", 5, "¡Increíble lugar! La vista es maravillosa."),
        Review("carlos_montoya", 4, "Me encanto la historia del lugar.")
    )
    val generalRating = 4.8

    Scaffold(
        topBar = {
            Column {
                // Barra superior fija
                GeneralTopBar(
                    title = "Detalle del Lugar",
                    onBack = onBackClick
                )
                // Imagen y texto fijados (no se mueven al hacer scroll)
                ImageHeader()
            }
        },
        bottomBar = {
            // El bloque que contiene "Me interesa" y "Visitado" fijado abajo
            BottomActionsBar()
        }
    ) { paddingValues ->
        // Solo el contenido de abajo (Descripción, Precios, Horarios, Ubicación, Reseñas) tendrá scroll
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
                    reviews = reviews,
                    generalRating = generalRating,
                    onSeeAllReviewsClick = {
                        onSeeAllReviewsClick(publicationId)
                    }
                )
            }
            // Espacio extra al final
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ImageHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) 
    ) {
        // Marcador de posición para la imagen (Valle de Cocora)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray) 
        )
        
        // Texto sobre la imagen
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

        // Flecha Izquierda
        Surface(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .size(44.dp),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.4f)
        ) {
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
            }
        }

        // Flecha Derecha
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

        // Icono de reporte/error
        IconButton(
            onClick = { /* TODO */ },
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
    reviews: List<Review>,
    generalRating: Double,
    onSeeAllReviewsClick: () -> Unit
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
            onClick = onSeeAllReviewsClick,
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
fun BottomActionsBar() {
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
                onClick = { /* TODO */ },
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
