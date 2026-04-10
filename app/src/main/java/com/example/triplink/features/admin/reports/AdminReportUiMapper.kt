package com.example.triplink.features.admin.reports

import android.content.Context
import com.example.triplink.domain.model.Reporte
import com.example.triplink.domain.model.admin.AdminReportCase
import com.example.triplink.core.components.publicationdetails.utils.toScheduleLabel
import com.example.triplink.core.localization.localizedLabel
import com.example.triplink.core.localization.localizedLabelOrNoPrice
import com.example.triplink.core.localization.localizedRelativeTimeLabel
import com.example.triplink.domain.model.enums.EstadoReporte
import com.example.triplink.domain.model.enums.RazonReporte
import com.example.triplink.features.admin.moderation.ModerationPublicationCardStatus
import com.example.triplink.features.admin.moderation.ModerationPublicationCardUi

fun AdminReportCase.toUi(
    context: Context,
    now: Long = System.currentTimeMillis()
): AdminReportUi = AdminReportUi(
    report = report,
    pointOfInterest = pointOfInterest,
    reporterName = reporterName,
    acceptedReportsCount = acceptedReportsCount,
    categoryLabel = pointOfInterest.categoria.localizedLabel(context),
    timeLabel = report.fechaCreacion.localizedRelativeTimeLabel(context, now),
    cityLabel = pointOfInterest.ubicacion.ciudad,
    priceLabel = pointOfInterest.rangoPrecios.localizedLabelOrNoPrice(context),
    scheduleLabel = pointOfInterest.horarios.toScheduleLabel(context),
    reasonMessage = report.toReasonMessage(context)
)

fun AdminReportUi.toCardUi(): ModerationPublicationCardUi = ModerationPublicationCardUi(
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

private fun EstadoReporte.toPublicationModerationStatus(): ModerationPublicationCardStatus = when (this) {
    EstadoReporte.PENDIENTE -> ModerationPublicationCardStatus.PENDING
    EstadoReporte.REVISADO, EstadoReporte.APROBADO -> ModerationPublicationCardStatus.VERIFIED
    EstadoReporte.RECHAZADO -> ModerationPublicationCardStatus.REJECTED
}

private fun Reporte.toReasonMessage(context: Context): String {
    val baseReason = motivo.localizedLabel(context)
    val detail = descripcion?.trim().orEmpty()
    return if (detail.isBlank()) baseReason else "$baseReason: $detail"
}




