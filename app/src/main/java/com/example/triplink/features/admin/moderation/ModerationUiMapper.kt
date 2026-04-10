package com.example.triplink.features.admin.moderation

import android.content.Context
import com.example.triplink.core.components.publicationdetails.utils.toScheduleLabel
import com.example.triplink.core.localization.localizedLabel
import com.example.triplink.core.localization.localizedLabelOrNoPrice
import com.example.triplink.core.localization.localizedRelativeTimeLabel
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.moderator.ModerationPublication

fun ModerationPublication.toCardUi(
    context: Context,
    now: Long = System.currentTimeMillis()
): ModerationPublicationCardUi =
    ModerationPublicationCardUi(
        id = id,
        title = pointOfInterest.titulo,
        categoryLabel = pointOfInterest.categoria.localizedLabel(context),
        authorName = authorName,
        timeLabel = createdAtMillis.localizedRelativeTimeLabel(context, now),
        cityLabel = pointOfInterest.ubicacion.ciudad,
        priceLabel = pointOfInterest.rangoPrecios.localizedLabelOrNoPrice(context),
        scheduleLabel = pointOfInterest.horarios.toScheduleLabel(context),
        imageUrl = pointOfInterest.fotos.firstOrNull().orEmpty(),
        status = pointOfInterest.estado.toCardStatus(),
        reportCount = reportCount,
        reasonMessage = moderationReason,
        rejectReason = rejectReason
    )

private fun EstadoPublicacion.toCardStatus(): ModerationPublicationCardStatus = when (this) {
    EstadoPublicacion.PENDIENTE -> ModerationPublicationCardStatus.PENDING
    EstadoPublicacion.VERIFICADA -> ModerationPublicationCardStatus.VERIFIED
    EstadoPublicacion.RECHAZADA -> ModerationPublicationCardStatus.REJECTED
}


