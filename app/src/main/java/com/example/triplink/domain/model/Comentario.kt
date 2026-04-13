package com.example.triplink.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Comentario(
    val id: String,
    val usuarioId: String,
    val puntoInteresId: String,
    val userName: String,
    val date: Long = System.currentTimeMillis(),
    val rating: Float,
    val text: String,
)
