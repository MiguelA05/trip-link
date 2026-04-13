package com.example.triplink.domain.model

import com.example.triplink.domain.model.enums.Nivel
import com.example.triplink.domain.model.enums.Rol
import kotlinx.serialization.Serializable

@Serializable
data class Usuario (
    //El email es la clave principal de esta clase no se puede repetir
    val email: String,
    val nombre: String,
    val password: String,
    val puntos: Int,

    val ubicacion: Ubicacion? = null,

    val nivel: Nivel = Nivel.TURISTA,
    val rol: Rol = Rol.USUARIO,

    val telefono: String = "",
    val direccion: String = "",
    val departamento: String = "",
    val ubicacionExactaActiva: Boolean = false,

    val insignias: List<String> = emptyList() // IDs de insignias
)