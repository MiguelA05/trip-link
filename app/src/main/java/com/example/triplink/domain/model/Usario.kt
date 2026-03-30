package com.example.triplink.domain.model

data class Usario (
    //El email es la clave principal de esta clase no se puede repetir
    val email: String,

    val nombre: String,
    val password: String,
    val puntos: Int,
    val ubicacion: String
)