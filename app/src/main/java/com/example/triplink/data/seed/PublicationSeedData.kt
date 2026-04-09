package com.example.triplink.data.seed

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.RangoPrecios

fun seedPublications(): List<PuntoInteres> = listOf(
    PuntoInteres(
        id = "1",
        titulo = "Valle del Cocora",
        informacion = "Paisajes de palmas y senderos en el Quindio",
        usuarioAutorId = "laura@email.com",
        categoria = Categoria.NATURALEZA,
        ubicacion = Ubicacion(4.6383, -75.4964, "Salento, Quindio"),
        fotos = listOf("https://visitmycolombia.com/wp-content/uploads/2024/01/bosque-de-palmas-valle-de-cocora-1536x864.jpg"),
        horario = 8L * 60L * 60L * 1000L to 17L * 60L * 60L * 1000L,
        estado = EstadoPublicacion.VERIFICADA,
        rangoPrecios = RangoPrecios.GRATUITO
    ),
    PuntoInteres(
        id = "2",
        titulo = "Cafe de Origen Quindio",
        informacion = "Cafe especial de la region",
        usuarioAutorId = "martin@email.com",
        categoria = Categoria.GASTRONOMIA,
        ubicacion = Ubicacion(4.5339, -75.6811, "Armenia, Quindio"),
        fotos = listOf("https://images.unsplash.com/photo-1509042239860-f550ce710b93?q=80&w=1200&auto=format&fit=crop"),
        horario = 9L * 60L * 60L * 1000L to 20L * 60L * 60L * 1000L,
        estado = EstadoPublicacion.VERIFICADA,
        rangoPrecios = RangoPrecios.ECONOMICO
    ),
    PuntoInteres(
        id = "3",
        titulo = "Parque del Cafe",
        informacion = "Parque tematico con atracciones",
        usuarioAutorId = "miguel@email.com",
        categoria = Categoria.ENTRETENIMIENTO,
        ubicacion = Ubicacion(4.5666, -75.7519, "Montenegro, Quindio"),
        fotos = listOf("https://images.unsplash.com/photo-1554118811-1e0d58224f24?q=80&w=1200&auto=format&fit=crop"),
        horario = 8L * 60L * 60L * 1000L to 18L * 60L * 60L * 1000L,
        estado = EstadoPublicacion.VERIFICADA,
        rangoPrecios = RangoPrecios.MODERADO
    ),
    PuntoInteres(
        id = "4",
        titulo = "Mirador del Quindio",
        informacion = "Punto panoramico ideal para atardeceres en familia.",
        usuarioAutorId = "carlos@email.com",
        categoria = Categoria.NATURALEZA,
        ubicacion = Ubicacion(4.54, -75.68, "Armenia, Quindio"),
        fotos = listOf("https://images.unsplash.com/photo-1469474968028-56623f02e42e?q=80&w=1200&auto=format&fit=crop"),
        horario = 10L * 60L * 60L * 1000L to 18L * 60L * 60L * 1000L,
        estado = EstadoPublicacion.PENDIENTE,
        rangoPrecios = RangoPrecios.GRATUITO
    ),
    PuntoInteres(
        id = "5",
        titulo = "Ruta Cafetera Nocturna",
        informacion = "Experiencia guiada por fincas tradicionales en horario nocturno.",
        usuarioAutorId = "carlos@email.com",
        categoria = Categoria.CULTURA,
        ubicacion = Ubicacion(4.62, -75.71, "Montenegro, Quindio"),
        fotos = listOf("https://images.unsplash.com/photo-1521017432531-fbd92d768814?q=80&w=1200&auto=format&fit=crop"),
        horario = null,
        estado = EstadoPublicacion.RECHAZADA,
        rangoPrecios = RangoPrecios.MODERADO,
        motivoRechazo = "Falta evidencia fotografica del recorrido y puntos de encuentro."
    )
)

