package com.example.triplink.features.comments

import androidx.lifecycle.ViewModel
import com.example.triplink.core.components.RatingCount
import com.example.triplink.domain.model.Comentario
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class CommentsUiState(
    val publicationId: String,
    val averageRating: Double,
    val totalReviews: Int,
    val distribution: List<RatingCount>,
    val reviews: List<Comentario>
)

@HiltViewModel
class CommentsViewModel @Inject constructor() : ViewModel() {

    fun buildUiState(publicationId: String): CommentsUiState {
        val reviews = sampleReviews(publicationId)
        val distribution = listOf(
            RatingCount(stars = 5, count = 95),
            RatingCount(stars = 4, count = 22),
            RatingCount(stars = 3, count = 6),
            RatingCount(stars = 2, count = 3),
            RatingCount(stars = 1, count = 2)
        )

        return CommentsUiState(
            publicationId = publicationId,
            averageRating = 4.8,
            totalReviews = 128,
            distribution = distribution,
            reviews = reviews
        )
    }

    // Estado visual mock mientras se conecta la data real de reseñas.
    private fun sampleReviews(publicationId: String): List<Comentario> = listOf(
        Comentario(
            id = "c1",
            usuarioId = "u1",
            puntoInteresId = publicationId,
            userName = "Camila Torres",
            date = 1778025600000,
            rating = 5f,
            text = "Un lugar con mucha magia, supera todas las expectativas y te hace emocionar por su belleza y tranquilidad. Sus altas palmeras de cera y el paisaje te transportan a otra dimension."
        ),
        Comentario(
            id = "c2",
            usuarioId = "u2",
            puntoInteresId = publicationId,
            userName = "Valentina Rios",
            date = 1777075200000,
            rating = 5f,
            text = "Para llegar al valle es mas facil desde el pueblo Salento, desde alli salen los famosos jeep camino al Valle. Hay varios senderos o trekking con diferentes precios, depende del recorrido que desees hacer."
        ),
        Comentario(
            id = "c3",
            usuarioId = "u3",
            puntoInteresId = publicationId,
            userName = "Luis Herrera",
            date = 1776207600000,
            rating = 4f,
            text = "Muy recomendado para ir con tiempo y disfrutar del recorrido completo. Lleva hidratacion y bloqueador porque el sol puede pegar fuerte en algunas horas."
        )
    )
}
