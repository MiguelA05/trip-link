package com.example.triplink.core.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.enums.Categoria

@Preview(showBackground = true)
@Composable
fun PublicationCardPreview() {
    val samplePuntoInteres = PuntoInteres(
        id = "1",
        titulo = "Valle del Cocora",
        informacion = "Paisajes de palmas y senderos",
        usuarioAutorId = "Laura Gomez",
        categoria = Categoria.NATURALEZA,
        ubicacion = Ubicacion(4.6383, -75.4964, "Salento, Quindio"),
        fotos = listOf("https://images.unsplash.com/photo-1599408162165-276634c0e351?q=80&w=1000&auto=format&fit=crop")
    )

    Column {
        PublicationCard(
            puntoInteres = samplePuntoInteres,
            onCommentsClick = {}
        )
        Spacer(modifier = Modifier.height(16.dp))
        PublicationCard(
            puntoInteres = samplePuntoInteres,
            showFooter = true,
            onCommentsClick = {}
        )
    }
}

