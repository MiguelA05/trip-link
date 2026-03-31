package com.example.triplink.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.enums.Categoria

@Composable
fun PublicationList(
    modifier: Modifier = Modifier,
    onFavoriteToggle: (String) -> Unit = {},
    onCommentsClick: (String) -> Unit = {}
) {
    val puntoInteres = listOf(
        PuntoInteres(
            id = "1",
            titulo = "Valle del Cocora",
            informacion = "Paisajes de palmas y senderos en el Quindio",
            usuarioAutorId = "Laura Gomez",
            categoria = Categoria.NATURALEZA,
            ubicacion = Ubicacion(4.6383, -75.4964, "Salento, Quindio"),
            fotos = listOf("https://images.unsplash.com/photo-1599408162165-276634c0e351?q=80&w=1000&auto=format&fit=crop")
        ),
        PuntoInteres(
            id = "2",
            titulo = "Museo del Oro",
            informacion = "Coleccion historica y cultural",
            usuarioAutorId = "Carlos Ruiz",
            categoria = Categoria.CULTURA,
            ubicacion = Ubicacion(4.6017, -74.0721, "Bogota, Colombia"),
            fotos = listOf("https://images.unsplash.com/photo-1582650800082-9366367793b8?q=80&w=1000&auto=format&fit=crop")
        ),
        PuntoInteres(
            id = "3",
            titulo = "Restaurante El Mirador",
            informacion = "Comida local con vista al valle",
            usuarioAutorId = "Ana Maria",
            categoria = Categoria.GASTRONOMIA,
            ubicacion = Ubicacion(4.6375, -75.5723, "Salento, Quindio"),
            fotos = listOf("https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?q=80&w=1000&auto=format&fit=crop")
        )
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(puntoInteres) { publication ->
            PublicationCard(
                puntoInteres = publication,
                onFavoriteToggle = { onFavoriteToggle(publication.id) },
                onCommentsClick = { onCommentsClick(publication.id) }
            )
        }
    }
}

