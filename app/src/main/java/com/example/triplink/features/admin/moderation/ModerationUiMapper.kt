package com.example.triplink.features.admin.moderation

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
        scheduleLabel = pointOfInterest.horario.toLabel(),
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

