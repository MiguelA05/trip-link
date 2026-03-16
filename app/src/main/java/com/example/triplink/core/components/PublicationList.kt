package com.example.triplink.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.triplink.domain.model.Publication

@Composable
fun PublicationList(modifier: Modifier = Modifier) {
    val publications = listOf(
        Publication(
            id = "1",
            authorName = "Laura Gómez",
            authorInitials = "LG",
            timeAgo = "1 hora",
            distance = "3.2 km",
            category = "Naturaleza y Parques",
            rating = 4.8,
            title = "Valle del Cocora",
            location = "Salento, Quindío",
            imageUrl = "https://images.unsplash.com/photo-1599408162165-276634c0e351?q=80&w=1000&auto=format&fit=crop",
            commentsCount = 34,
            likesCount = 247
        ),
        Publication(
            id = "2",
            authorName = "Carlos Ruiz",
            authorInitials = "CR",
            timeAgo = "2 horas",
            distance = "5.0 km",
            category = "Cultura",
            rating = 4.5,
            title = "Museo del Oro",
            location = "Bogotá, Colombia",
            imageUrl = "https://images.unsplash.com/photo-1582650800082-9366367793b8?q=80&w=1000&auto=format&fit=crop",
            commentsCount = 12,
            likesCount = 150
        ),
        Publication(
            id = "3",
            authorName = "Ana Maria",
            authorInitials = "AM",
            timeAgo = "3 horas",
            distance = "1.5 km",
            category = "Gastronomía",
            rating = 4.9,
            title = "Restaurante El Mirador",
            location = "Salento, Quindío",
            imageUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?q=80&w=1000&auto=format&fit=crop",
            commentsCount = 45,
            likesCount = 312
        )
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(publications) { publication ->
            PublicationCard(publication = publication)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PublicationListPreview() {
    PublicationList()
}
