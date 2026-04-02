package com.example.triplink.data.repository.admin.moderation

import androidx.compose.runtime.mutableStateListOf
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.moderator.ModerationPublication

internal data class AdminModerationSeedState(
    val pendingPublications: androidx.compose.runtime.snapshots.SnapshotStateList<ModerationPublication>,
    val reviewedPublications: androidx.compose.runtime.snapshots.SnapshotStateList<ModerationPublication>
)

internal fun createAdminModerationSeedState(): AdminModerationSeedState {
    val pendingPublications = mutableStateListOf(
        ModerationPublication(
            id = "m-1",
            pointOfInterest = PuntoInteres(
                id = "poi-m-1",
                titulo = "Mercado Artesanal del Quindío",
                informacion = "Oferta gastronómica y artesanal en el centro del departamento.",
                usuarioAutorId = "u-1",
                categoria = Categoria.GASTRONOMIA,
                ubicacion = Ubicacion(latitud = 4.533, longitud = -75.681, ciudad = "Armenia, Quindío"),
                fotos = listOf("https://images.unsplash.com/photo-1601050690597-df0568f70950?q=80&w=1200&auto=format&fit=crop"),
                horario = 7L * 60L * 60L * 1000L to 14L * 60L * 60L * 1000L,
                estado = EstadoPublicacion.PENDIENTE,
                rangoPrecios = RangoPrecios.COSTOSO
            ),
            authorName = "Laura Fernández",
            createdAtMillis = System.currentTimeMillis() - 3L * 24L * 60L * 60L * 1000L
        ),
        ModerationPublication(
            id = "m-2",
            pointOfInterest = PuntoInteres(
                id = "poi-m-2",
                titulo = "Plaza Principal Filandia",
                informacion = "Recorrido patrimonial y comercial de la zona central.",
                usuarioAutorId = "u-2",
                categoria = Categoria.CULTURA,
                ubicacion = Ubicacion(latitud = 4.668, longitud = -75.660, ciudad = "Filandia, Quindío"),
                fotos = listOf("https://images.unsplash.com/photo-1544735716-392fe2489ffa?q=80&w=1200&auto=format&fit=crop"),
                horario = null,
                estado = EstadoPublicacion.PENDIENTE,
                rangoPrecios = RangoPrecios.ECONOMICO
            ),
            authorName = "Juan Pablo Torres",
            createdAtMillis = System.currentTimeMillis() - 5L * 60L * 60L * 1000L
        )
    )

    val reviewedPublications = mutableStateListOf(
        ModerationPublication(
            id = "m-3",
            pointOfInterest = PuntoInteres(
                id = "poi-m-3",
                titulo = "Mirador Alto de la Cruz",
                informacion = "Mirador panorámico con vista al valle del Quindío.",
                usuarioAutorId = "u-3",
                categoria = Categoria.NATURALEZA,
                ubicacion = Ubicacion(latitud = 4.636, longitud = -75.571, ciudad = "Salento, Quindío"),
                fotos = listOf("https://images.unsplash.com/photo-1469474968028-56623f02e42e?q=80&w=1200&auto=format&fit=crop"),
                horario = 8L * 60L * 60L * 1000L to 17L * 60L * 60L * 1000L,
                estado = EstadoPublicacion.RECHAZADA,
                rangoPrecios = RangoPrecios.MODERADO
            ),
            authorName = "Valentina Ríos",
            createdAtMillis = System.currentTimeMillis() - 24L * 60L * 60L * 1000L,
            moderationReason = "Información incompleta y sin ubicación en mapa",
            rejectReason = "La publicación no incluye coordenadas ni referencias verificables del lugar."
        )
    )

    return AdminModerationSeedState(
        pendingPublications = pendingPublications,
        reviewedPublications = reviewedPublications
    )
}

