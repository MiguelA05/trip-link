package com.example.triplink.core.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.triplink.domain.model.PuntoInteres

@Preview(showBackground = true)
@Composable
fun PublicationCardPreview() {
    val samplePuntoInteres = PuntoInteres(
        id = "1",
        authorName = "Laura Gomez",
        authorInitials = "LG",
        timeAgo = "1 hora",
        distance = "3.9 km",
        category = "Naturaleza y Parques",
        rating = 4.8,
        title = "Valle del Cocora",
        location = "Salento, Quindio",
        imageUrl = "https://images.unsplash.com/photo-1599408162165-276634c0e351?q=80&w=1000&auto=format&fit=crop",
        commentsCount = 34,
        likesCount = 247,
        isFavorite = true
    )

    Column {
        PublicationCard(
            puntoInteres = samplePuntoInteres,
            onCommentsClick = {}
        )
        Spacer(modifier = Modifier.height(16.dp))
        PublicationCard(
            puntoInteres = samplePuntoInteres.copy(isFavorite = false),
            showFooter = true,
            onCommentsClick = {}
        )
    }
}

