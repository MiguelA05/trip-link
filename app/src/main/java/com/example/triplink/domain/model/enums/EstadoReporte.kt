package com.example.triplink.domain.model.enums

import kotlinx.serialization.Serializable

@Serializable
enum class EstadoReporte {
    PENDIENTE,
    REVISADO,
    RECHAZADO,
    APROBADO
}