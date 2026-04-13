package com.example.triplink.domain.model.enums

import kotlinx.serialization.Serializable

@Serializable
enum class Categoria(val label: String) {
    GASTRONOMIA("Gastronomía"),
    CULTURA("Cultura"),
    NATURALEZA("Naturaleza"),
    ENTRETENIMIENTO("Entretenimiento"),
    HISTORIA("Historia")
}