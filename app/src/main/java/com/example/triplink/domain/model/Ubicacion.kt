package com.example.triplink.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Ubicacion(
    val latitud: Double,
    val longitud: Double,
    val ciudad: String
)
