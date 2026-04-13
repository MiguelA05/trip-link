package com.example.triplink.data.seed

import com.example.triplink.domain.model.Usuario
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.enums.Rol

fun seedUsers(): List<Usuario> = listOf(
    Usuario(
        email = "carlos@email.com",
        nombre = "Carlos",
        password = "123456",
        puntos = 90,
        rol = Rol.USUARIO,
        telefono = "3001234567",
        direccion = "Barrio Centro",
        departamento = "Quindio",
        ubicacion = Ubicacion(4.5339, -75.6811, "Armenia, Quindio")
    ),
    Usuario(
        email = "laura@email.com",
        nombre = "Laura Gomez",
        password = "123456",
        puntos = 140,
        rol = Rol.USUARIO,
        telefono = "3012345678",
        direccion = "Barrio Laureles",
        departamento = "Antioquia",
        ubicacion = Ubicacion(6.2442, -75.5812, "Medellin, Antioquia")
    ),
    Usuario(
        email = "martin@email.com",
        nombre = "Martin Ruiz",
        password = "123456",
        puntos = 110,
        rol = Rol.USUARIO,
        telefono = "3023456789",
        direccion = "Zona Norte",
        departamento = "Quindio",
        ubicacion = Ubicacion(4.6383, -75.4964, "Salento, Quindio")
    ),
    Usuario(
        email = "miguel@email.com",
        nombre = "Miguel Mira",
        password = "123456",
        puntos = 70,
        rol = Rol.USUARIO,
        telefono = "3034567890",
        direccion = "Av. Bolivar",
        departamento = "Quindio",
        ubicacion = Ubicacion(4.5339, -75.6811, "Armenia, Quindio")
    ),
    Usuario(
        email = "camila@email.com",
        nombre = "Camila Torres",
        password = "123456",
        puntos = 45,
        rol = Rol.USUARIO,
        telefono = "3045678901",
        direccion = "Sector Parque",
        departamento = "Quindio",
        ubicacion = Ubicacion(4.5666, -75.7519, "Montenegro, Quindio")
    ),
    Usuario(
        email = "valentina@email.com",
        nombre = "Valentina Rios",
        password = "123456",
        puntos = 50,
        rol = Rol.USUARIO,
        telefono = "3056789012",
        direccion = "Barrio Fundadores",
        departamento = "Risaralda",
        ubicacion = Ubicacion(4.8143, -75.6946, "Pereira, Risaralda")
    ),
    Usuario(
        email = "luis@email.com",
        nombre = "Luis Herrera",
        password = "123456",
        puntos = 38,
        rol = Rol.USUARIO,
        telefono = "3067890123",
        direccion = "Sector Mirador",
        departamento = "Quindio",
        ubicacion = Ubicacion(4.6383, -75.4964, "Salento, Quindio")
    ),
    Usuario(
        email = "admin@triplink.com",
        nombre = "Admin",
        password = "admin123",
        puntos = 0,
        rol = Rol.MODERADOR,
        telefono = "3000000000",
        direccion = "Sede Administrativa",
        departamento = "Quindio",
        ubicacion = Ubicacion(4.5339, -75.6811, "Armenia, Quindio")
    )
)

