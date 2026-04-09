package com.example.triplink.data.seed

import com.example.triplink.domain.model.Usuario
import com.example.triplink.domain.model.enums.Rol

fun seedUsers(): List<Usuario> = listOf(
    Usuario(email = "carlos@email.com", nombre = "Carlos", password = "123456", puntos = 90, rol = Rol.USUARIO),
    Usuario(email = "laura@email.com", nombre = "Laura Gomez", password = "123456", puntos = 140, rol = Rol.USUARIO),
    Usuario(email = "martin@email.com", nombre = "Martin Ruiz", password = "123456", puntos = 110, rol = Rol.USUARIO),
    Usuario(email = "miguel@email.com", nombre = "Miguel Mira", password = "123456", puntos = 70, rol = Rol.USUARIO),
    Usuario(email = "camila@email.com", nombre = "Camila Torres", password = "123456", puntos = 45, rol = Rol.USUARIO),
    Usuario(email = "valentina@email.com", nombre = "Valentina Rios", password = "123456", puntos = 50, rol = Rol.USUARIO),
    Usuario(email = "luis@email.com", nombre = "Luis Herrera", password = "123456", puntos = 38, rol = Rol.USUARIO),
    Usuario(email = "admin@triplink.com", nombre = "Admin", password = "admin123", puntos = 0, rol = Rol.MODERADOR)
)

