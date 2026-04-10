package com.example.triplink.features.admin.reports

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Reporte
import com.example.triplink.domain.model.enums.EstadoReporte

data class AdminReportUi(
    val report: Reporte,
    val pointOfInterest: PuntoInteres,
    val reporterName: String,
    val acceptedReportsCount: Int = 0,
    val categoryLabel: String,
    val timeLabel: String,
    val cityLabel: String,
    val priceLabel: String,
    val scheduleLabel: String,
    val reasonMessage: String
) {
    val id: String get() = report.id
    val title: String get() = pointOfInterest.titulo
    val authorName: String get() = reporterName
    val imageUrl: String get() = pointOfInterest.fotos.firstOrNull().orEmpty()
    val status: EstadoReporte get() = report.estado
    val rejectReason: String? get() = report.descripcion
}

