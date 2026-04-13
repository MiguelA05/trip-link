package com.example.triplink.domain.model.enums

import kotlinx.serialization.Serializable

@Serializable
enum class RazonReporte {
    SPAM,
    CONTENIDO_INAPROPIADO,
    INFORMACION_FALSA,
    LENGUAJE_OFENSIVO,
    VIOLENCIA,
    OTRO
}