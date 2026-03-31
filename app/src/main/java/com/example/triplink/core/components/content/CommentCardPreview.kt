package com.example.triplink.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.triplink.domain.model.Comentario


@Preview(showBackground = true)
@Composable
fun CommentCardPreview() {
    val sampleComment = Comentario(
        id = "1",
        usuarioId = "u1",
        puntoInteresId = "poi1",
        userName = "Camila Torres",
        date = System.currentTimeMillis(),
        rating = 5.0f,
        text = "Un lugar con mucha magia, supera todas las expectativas y te hace emocionar por su belleza y tranquilidad."
    )
    Box(modifier = Modifier.padding(16.dp)) {
        CommentCard(comment = sampleComment)
    }
}

