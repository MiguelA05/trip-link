package com.example.triplink.features.admin.reports

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Reporte
import com.example.triplink.domain.model.enums.EstadoReporte

data class AdminReportUi(
    val report: Reporte,
    val pointOfInterest: PuntoInteres,
    val reporterName: String,
    val acceptedReportsCount: Int = 0
) {
    val id: String get() = report.id
    val title: String get() = pointOfInterest.titulo
    val categoryLabel: String get() = pointOfInterest.categoria.toLabel()
    val authorName: String get() = reporterName
    val timeLabel: String get() = report.fechaCreacion.toRelativeTimeLabel()
    val cityLabel: String get() = pointOfInterest.ubicacion.ciudad
    val priceLabel: String get() = pointOfInterest.rangoPrecios.toLabel()
    val scheduleLabel: String get() = pointOfInterest.horario.toLabel()
    val imageUrl: String get() = pointOfInterest.fotos.firstOrNull().orEmpty()
    val status: EstadoReporte get() = report.estado
    val reasonMessage: String get() = report.toReasonMessage()
    val rejectReason: String? get() = report.descripcion
}

