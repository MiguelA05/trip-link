package com.example.triplink.data.repository.admin.reports

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Reporte
import com.example.triplink.domain.model.Ubicacion
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.EstadoReporte
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.RazonReporte

internal data class AdminReportsSeedState(
    val publicationsById: SnapshotStateMap<String, PuntoInteres>,
    val pendingReports: SnapshotStateList<AdminReportSeedEntry>,
    val acceptedReportsCountByPublication: SnapshotStateMap<String, Int>
)

internal data class AdminReportSeedEntry(
    val report: Reporte,
    val pointOfInterest: PuntoInteres,
    val reporterName: String
)

internal fun createAdminReportsSeedState(): AdminReportsSeedState {
    val publicationsById = mutableStateMapOf(
        "poi-1" to PuntoInteres(
            id = "poi-1",
            titulo = "Mercado Artesanal del Quindío",
            informacion = "Oferta gastronomica y artesanal en el centro del departamento.",
            usuarioAutorId = "user-10",
            categoria = Categoria.GASTRONOMIA,
            ubicacion = Ubicacion(latitud = 4.533, longitud = -75.681, ciudad = "Armenia, Quindio"),
            fotos = listOf("https://images.unsplash.com/photo-1601050690597-df0568f70950?q=80&w=1200&auto=format&fit=crop"),
            horario = 7L * 60L * 60L * 1000L to 14L * 60L * 60L * 1000L,
            estado = EstadoPublicacion.PENDIENTE,
            rangoPrecios = RangoPrecios.COSTOSO
        ),
        "poi-2" to PuntoInteres(
            id = "poi-2",
            titulo = "Plaza Principal Filandia",
            informacion = "Recorrido patrimonial y comercial de la zona central.",
            usuarioAutorId = "user-11",
            categoria = Categoria.CULTURA,
            ubicacion = Ubicacion(latitud = 4.668, longitud = -75.660, ciudad = "Filandia, Quindio"),
            fotos = listOf("https://images.unsplash.com/photo-1544735716-392fe2489ffa?q=80&w=1200&auto=format&fit=crop"),
            horario = null,
            estado = EstadoPublicacion.PENDIENTE,
            rangoPrecios = RangoPrecios.GRATUITO
        ),
        "poi-3" to PuntoInteres(
            id = "poi-3",
            titulo = "Mirador Alto de la Cruz",
            informacion = "Mirador panoramico con vista al valle del Quindio.",
            usuarioAutorId = "user-12",
            categoria = Categoria.NATURALEZA,
            ubicacion = Ubicacion(latitud = 4.636, longitud = -75.571, ciudad = "Salento, Quindio"),
            fotos = listOf("https://images.unsplash.com/photo-1469474968028-56623f02e42e?q=80&w=1200&auto=format&fit=crop"),
            horario = 8L * 60L * 60L * 1000L to 17L * 60L * 60L * 1000L,
            estado = EstadoPublicacion.PENDIENTE,
            rangoPrecios = RangoPrecios.MODERADO
        )
    )

    val pendingReports = mutableStateListOf(
        AdminReportSeedEntry(
            report = Reporte(
                id = "rep-1",
                reportadorId = "Laura Fernandez",
                puntoInteresId = "poi-1",
                motivo = RazonReporte.INFORMACION_FALSA,
                descripcion = "La direccion no coincide con el lugar real",
                estado = EstadoReporte.PENDIENTE,
                fechaCreacion = System.currentTimeMillis() - 3L * 24L * 60L * 60L * 1000L
            ),
            pointOfInterest = publicationsById.getValue("poi-1"),
            reporterName = "Laura Fernandez"
        ),
        AdminReportSeedEntry(
            report = Reporte(
                id = "rep-2",
                reportadorId = "Juan Pablo Torres",
                puntoInteresId = "poi-1",
                motivo = RazonReporte.CONTENIDO_INAPROPIADO,
                descripcion = "Incluye publicidad no relacionada con el sitio",
                estado = EstadoReporte.PENDIENTE,
                fechaCreacion = System.currentTimeMillis() - 5L * 60L * 60L * 1000L
            ),
            pointOfInterest = publicationsById.getValue("poi-1"),
            reporterName = "Juan Pablo Torres"
        )
    )

    val acceptedReportsCountByPublication = mutableStateMapOf<String, Int>()

    return AdminReportsSeedState(
        publicationsById = publicationsById,
        pendingReports = pendingReports,
        acceptedReportsCountByPublication = acceptedReportsCountByPublication
    )
}

