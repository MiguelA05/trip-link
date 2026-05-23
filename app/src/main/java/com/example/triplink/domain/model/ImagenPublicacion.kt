package com.example.triplink.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ImagenPublicacion(
    val id: String,
    val publicacionId: String,
    val urlRemota: String,
    val marcaLocal: Long = System.currentTimeMillis(),
    val estado: EstadoImagen = EstadoImagen.PENDIENTE_SUBIDA
)

@Serializable
enum class EstadoImagen {
    PENDIENTE_SUBIDA,
    SUBIENDO,
    SUBIDA,
    ERROR
}
