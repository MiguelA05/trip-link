package com.example.triplink.data.seed

import com.example.triplink.domain.model.Comentario

fun seedCommentsFor(publicationId: String): List<Comentario> = listOf(
    Comentario(
        id = "c1",
        usuarioId = "camila@email.com",
        puntoInteresId = publicationId,
        userName = "Camila Torres",
        date = 1778025600000,
        rating = 5f,
        text = "Un lugar con mucha magia, supera todas las expectativas y te hace emocionar por su belleza y tranquilidad. Sus altas palmeras de cera y el paisaje te transportan a otra dimension."
    ),
    Comentario(
        id = "c2",
        usuarioId = "valentina@email.com",
        puntoInteresId = publicationId,
        userName = "Valentina Rios",
        date = 1777075200000,
        rating = 5f,
        text = "Para llegar al valle es mas facil desde el pueblo Salento, desde alli salen los famosos jeep camino al Valle. Hay varios senderos o trekking con diferentes precios, depende del recorrido que desees hacer."
    ),
    Comentario(
        id = "c3",
        usuarioId = "luis@email.com",
        puntoInteresId = publicationId,
        userName = "Luis Herrera",
        date = 1776207600000,
        rating = 4f,
        text = "Muy recomendado para ir con tiempo y disfrutar del recorrido completo. Lleva hidratacion y bloqueador porque el sol puede pegar fuerte en algunas horas."
    )
)

