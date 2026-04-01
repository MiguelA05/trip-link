package com.example.triplink.features.admin.reports

import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Reporte
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.EstadoReporte
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.enums.RazonReporte
import com.example.triplink.features.admin.moderation.ModerationPublicationUi
import com.example.triplink.features.admin.moderation.PublicationModerationStatus


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

fun AdminReportUi.toModerationPublicationUi(): ModerationPublicationUi = ModerationPublicationUi(
    id = id,
    title = title,
    categoryLabel = categoryLabel,
    authorName = authorName,
    timeLabel = timeLabel,
    cityLabel = cityLabel,
    priceLabel = priceLabel,
    scheduleLabel = scheduleLabel,
    imageUrl = imageUrl,
    status = status.toPublicationModerationStatus(),
    reportCount = acceptedReportsCount,
    reasonMessage = reasonMessage,
    rejectReason = rejectReason
)

private fun EstadoReporte.toPublicationModerationStatus(): PublicationModerationStatus = when (this) {
    EstadoReporte.PENDIENTE -> PublicationModerationStatus.PENDING
    EstadoReporte.REVISADO, EstadoReporte.APROBADO -> PublicationModerationStatus.VERIFIED
    EstadoReporte.RECHAZADO -> PublicationModerationStatus.REJECTED
}

private fun Reporte.toReasonMessage(): String {
    val baseReason = motivo.toLabel()
    val detail = descripcion?.trim().orEmpty()
    return if (detail.isBlank()) baseReason else "$baseReason: $detail"
}

private fun Categoria.toLabel(): String = when (this) {
    Categoria.GASTRONOMIA -> "Gastronomía"
    Categoria.CULTURA -> "Cultura"
    Categoria.NATURALEZA -> "Naturaleza"
    Categoria.ENTRETENIMIENTO -> "Entretenimiento"
    Categoria.HISTORIA -> "Historia"
}

private fun RangoPrecios?.toLabel(): String = when (this) {
    null -> "Sin precio"
    RangoPrecios.GRATUITO -> "Gratis"
    RangoPrecios.ECONOMICO -> "$"
    RangoPrecios.MODERADO -> "$$"
    RangoPrecios.COSTOSO -> "$$$"
}

private fun Pair<Long, Long>?.toLabel(): String = if (this == null) {
    "Horario no disponible"
} else {
    "${first.toTimeLabel()} - ${second.toTimeLabel()}"
}

private fun Long.toTimeLabel(): String {
    val hours = (this / 3_600_000L) % 24
    val minutes = (this / 60_000L) % 60
    val amPm = if (hours >= 12) "pm" else "am"
    val normalizedHours = when (hours % 12) {
        0L -> 12
        else -> hours % 12
    }
    return "%d:%02d %s".format(normalizedHours, minutes, amPm)
}

private fun Long.toRelativeTimeLabel(now: Long = System.currentTimeMillis()): String {
    val delta = (now - this).coerceAtLeast(0L)
    val minutes = delta / 60_000L
    val hours = minutes / 60L
    val days = hours / 24L
    return when {
        minutes < 1 -> "Ahora mismo"
        minutes < 60 -> "Hace $minutes min"
        hours < 24 -> "Hace $hours h"
        days < 7 -> "Hace $days d"
        else -> "Hace ${days / 7} sem"
    }
}

private fun RazonReporte.toLabel(): String = when (this) {
    RazonReporte.SPAM -> "Spam"
    RazonReporte.CONTENIDO_INAPROPIADO -> "Contenido inapropiado"
    RazonReporte.INFORMACION_FALSA -> "Información falsa"
    RazonReporte.LENGUAJE_OFENSIVO -> "Lenguaje ofensivo"
    RazonReporte.VIOLENCIA -> "Violencia"
    RazonReporte.OTRO -> "Otro"
}



