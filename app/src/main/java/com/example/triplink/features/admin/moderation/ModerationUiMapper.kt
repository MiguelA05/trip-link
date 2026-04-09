package com.example.triplink.features.admin.moderation

import com.example.triplink.core.components.publicationdetails.utils.toScheduleLabel
import com.example.triplink.domain.model.enums.Categoria
import com.example.triplink.domain.model.enums.EstadoPublicacion
import com.example.triplink.domain.model.enums.RangoPrecios
import com.example.triplink.domain.model.moderator.ModerationPublication

fun ModerationPublication.toCardUi(now: Long = System.currentTimeMillis()): ModerationPublicationCardUi =
    ModerationPublicationCardUi(
        id = id,
        title = pointOfInterest.titulo,
        categoryLabel = pointOfInterest.categoria.toLabel(),
        authorName = authorName,
        timeLabel = createdAtMillis.toRelativeTimeLabel(now),
        cityLabel = pointOfInterest.ubicacion.ciudad,
        priceLabel = pointOfInterest.rangoPrecios.toLabel(),
        scheduleLabel = pointOfInterest.horarios.toScheduleLabel(),
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

private fun Categoria.toLabel(): String = when (this) {
    Categoria.GASTRONOMIA -> "Gastronomia"
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

