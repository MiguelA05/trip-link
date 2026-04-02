package com.example.triplink.domain.repository.admin.reports

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
			informacion = "Oferta gastronómica y artesanal en el centro del departamento.",
			usuarioAutorId = "user-10",
			categoria = Categoria.GASTRONOMIA,
			ubicacion = Ubicacion(latitud = 4.533, longitud = -75.681, ciudad = "Armenia, Quindío"),
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
			ubicacion = Ubicacion(latitud = 4.668, longitud = -75.660, ciudad = "Filandia, Quindío"),
			fotos = listOf("https://images.unsplash.com/photo-1544735716-392fe2489ffa?q=80&w=1200&auto=format&fit=crop"),
			horario = null,
			estado = EstadoPublicacion.PENDIENTE,
			rangoPrecios = RangoPrecios.GRATUITO
		),
		"poi-3" to PuntoInteres(
			id = "poi-3",
			titulo = "Mirador Alto de la Cruz",
			informacion = "Mirador panorámico con vista al valle del Quindío.",
			usuarioAutorId = "user-12",
			categoria = Categoria.NATURALEZA,
			ubicacion = Ubicacion(latitud = 4.636, longitud = -75.571, ciudad = "Salento, Quindío"),
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
				reportadorId = "Laura Fernández",
				puntoInteresId = "poi-1",
				motivo = RazonReporte.INFORMACION_FALSA,
				descripcion = "La dirección no coincide con el lugar real",
				estado = EstadoReporte.PENDIENTE,
				fechaCreacion = System.currentTimeMillis() - 3L * 24L * 60L * 60L * 1000L
			),
			pointOfInterest = publicationsById.getValue("poi-1"),
			reporterName = "Laura Fernández"
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
		),
		AdminReportSeedEntry(
			report = Reporte(
				id = "rep-3",
				reportadorId = "Valentina Ríos",
				puntoInteresId = "poi-1",
				motivo = RazonReporte.SPAM,
				descripcion = "Se repite en varios grupos con contenido promocional",
				estado = EstadoReporte.PENDIENTE,
				fechaCreacion = System.currentTimeMillis() - 25L * 60L * 60L * 1000L
			),
			pointOfInterest = publicationsById.getValue("poi-1"),
			reporterName = "Valentina Ríos"
		),
		AdminReportSeedEntry(
			report = Reporte(
				id = "rep-4",
				reportadorId = "Andrés Ramírez",
				puntoInteresId = "poi-2",
				motivo = RazonReporte.LENGUAJE_OFENSIVO,
				descripcion = "La descripción contiene insultos a usuarios",
				estado = EstadoReporte.PENDIENTE,
				fechaCreacion = System.currentTimeMillis() - 8L * 60L * 60L * 1000L
			),
			pointOfInterest = publicationsById.getValue("poi-2"),
			reporterName = "Andrés Ramírez"
		),
		AdminReportSeedEntry(
			report = Reporte(
				id = "rep-5",
				reportadorId = "Sofía Gómez",
				puntoInteresId = "poi-3",
				motivo = RazonReporte.OTRO,
				descripcion = "No coincide con la ubicación real del mirador",
				estado = EstadoReporte.PENDIENTE,
				fechaCreacion = System.currentTimeMillis() - 2L * 24L * 60L * 60L * 1000L
			),
			pointOfInterest = publicationsById.getValue("poi-3"),
			reporterName = "Sofía Gómez"
		)
	)

	val acceptedReportsCountByPublication = mutableStateMapOf<String, Int>()

	return AdminReportsSeedState(
		publicationsById = publicationsById,
		pendingReports = pendingReports,
		acceptedReportsCountByPublication = acceptedReportsCountByPublication
	)
}

