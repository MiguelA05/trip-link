package com.example.triplink.data.seed

import com.example.triplink.domain.model.HorarioPuntoInteres
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.DiaSemana
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
        fotos = listOf(
            "https://visitmycolombia.com/wp-content/uploads/2024/01/bosque-de-palmas-valle-de-cocora-1536x864.jpg",
            "https://images.unsplash.com/photo-1501785888041-af3ef285b470?q=80&w=1200&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1472396961693-142e6e269027?q=80&w=1200&auto=format&fit=crop"
        ),
        comments = seedCommentsFor("1"),
        horarios = defaultFullWeekSchedule(8L, 0L, 17L, 0L),
        fechaCreacion = System.currentTimeMillis() - 2L * 60L * 60L * 1000L,
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
        fotos = listOf(
            "https://images.unsplash.com/photo-1509042239860-f550ce710b93?q=80&w=1200&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?q=80&w=1200&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1494314671902-399b18174975?q=80&w=1200&auto=format&fit=crop"
        ),
        comments = seedCommentsFor("2"),
        horarios = defaultFullWeekSchedule(9L, 0L, 20L, 0L),
        fechaCreacion = System.currentTimeMillis() - 6L * 60L * 60L * 1000L,
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
        fotos = listOf(
            "https://images.unsplash.com/photo-1554118811-1e0d58224f24?q=80&w=1200&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1513883049090-d0b7439799bf?q=80&w=1200&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?q=80&w=1200&auto=format&fit=crop"
        ),
        comments = seedCommentsFor("3"),
        horarios = defaultFullWeekSchedule(8L, 0L, 18L, 0L),
        fechaCreacion = System.currentTimeMillis() - 26L * 60L * 60L * 1000L,
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
        fotos = listOf(
            "https://images.unsplash.com/photo-1469474968028-56623f02e42e?q=80&w=1200&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1521295121783-8a321d551ad2?q=80&w=1200&auto=format&fit=crop"
        ),
        comments = seedCommentsFor("4"),
        horarios = defaultFullWeekSchedule(10L, 0L, 18L, 0L),
        fechaCreacion = System.currentTimeMillis() - 5L * 60L * 1000L,
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
        fotos = listOf(
            "https://images.unsplash.com/photo-1521017432531-fbd92d768814?q=80&w=1200&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?q=80&w=1200&auto=format&fit=crop"
        ),
        comments = seedCommentsFor("5"),
        horarios = emptyList(),
        fechaCreacion = System.currentTimeMillis() - 3L * 24L * 60L * 60L * 1000L,
        estado = EstadoPublicacion.RECHAZADA,
        rangoPrecios = RangoPrecios.MODERADO,
        motivoRechazo = "Falta evidencia fotografica del recorrido y puntos de encuentro."
    ),
    PuntoInteres(
        id = "6",
        titulo = "Jardin Botanico del Quindio",
        informacion = "Senderos ecologicos, mariposario y espacios para fotografia de naturaleza.",
        usuarioAutorId = "camila@email.com",
        categoria = Categoria.NATURALEZA,
        ubicacion = Ubicacion(4.5278, -75.6649, "Calarca, Quindio"),
        fotos = listOf(
            "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?q=80&w=1200&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1448375240586-882707db888b?q=80&w=1200&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1473773508845-188df298d2d1?q=80&w=1200&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1501004318641-b39e6451bec6?q=80&w=1200&auto=format&fit=crop"
        ),
        comments = seedCommentsFor("6"),
        horarios = defaultFullWeekSchedule(9L, 0L, 17L, 30L),
        fechaCreacion = System.currentTimeMillis() - 10L * 60L * 60L * 1000L,
        estado = EstadoPublicacion.VERIFICADA,
        rangoPrecios = RangoPrecios.ECONOMICO
    ),
    PuntoInteres(
        id = "7",
        titulo = "Calle Real de Salento",
        informacion = "Recorrido cultural con arquitectura tradicional, artesanias y cafes de autor.",
        usuarioAutorId = "valentina@email.com",
        categoria = Categoria.CULTURA,
        ubicacion = Ubicacion(4.6367, -75.5708, "Salento, Quindio"),
        fotos = listOf(
            "https://images.unsplash.com/photo-1467269204594-9661b134dd2b?q=80&w=1200&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1470004914212-05527e49370b?q=80&w=1200&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1482192505345-5655af888cc4?q=80&w=1200&auto=format&fit=crop"
        ),
        comments = seedCommentsFor("7"),
        horarios = defaultFullWeekSchedule(8L, 30L, 21L, 0L),
        fechaCreacion = System.currentTimeMillis() - 16L * 60L * 60L * 1000L,
        estado = EstadoPublicacion.VERIFICADA,
        rangoPrecios = RangoPrecios.MODERADO
    ),
    PuntoInteres(
        id = "8",
        titulo = "Mercado de Sabores en Filandia",
        informacion = "Plazoleta con cocina local, postres artesanales y musica en vivo los fines de semana.",
        usuarioAutorId = "luis@email.com",
        categoria = Categoria.GASTRONOMIA,
        ubicacion = Ubicacion(4.6744, -75.6587, "Filandia, Quindio"),
        fotos = listOf(
            "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?q=80&w=1200&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?q=80&w=1200&auto=format&fit=crop"
        ),
        comments = seedCommentsFor("8"),
        horarios = defaultFullWeekSchedule(11L, 0L, 22L, 0L),
        fechaCreacion = System.currentTimeMillis() - 34L * 60L * 60L * 1000L,
        estado = EstadoPublicacion.PENDIENTE,
        rangoPrecios = RangoPrecios.ECONOMICO
    )
)

private fun defaultFullWeekSchedule(
    startHour: Long,
    startMinute: Long,
    endHour: Long,
    endMinute: Long
): List<HorarioPuntoInteres> {
    val start = (startHour * 60L + startMinute) * 60_000L
    val end = (endHour * 60L + endMinute) * 60_000L
    return DiaSemana.entries.map { day ->
        HorarioPuntoInteres(
            dia = day,
            fechaInicio = start,
            fechaFin = end
        )
    }
}

