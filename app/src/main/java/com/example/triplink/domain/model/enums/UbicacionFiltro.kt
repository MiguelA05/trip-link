package com.example.triplink.domain.model.enums

import kotlinx.serialization.Serializable

@Serializable
enum class UbicacionFiltro {
    CERCANOS,
    CIUDAD,
    DEPARTAMENTO,
    PAIS
}
