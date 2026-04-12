package com.example.triplink.data.model

data class Ciudad(
    val nombre: String
)

data class Departamento(
    val nombre: String,
    val ciudades: List<Ciudad>
)

