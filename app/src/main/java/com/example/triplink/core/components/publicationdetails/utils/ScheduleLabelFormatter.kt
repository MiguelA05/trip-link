package com.example.triplink.core.components.publicationdetails.utils

import com.example.triplink.core.components.publicationdetails.sections.DayScheduleUi
import com.example.triplink.domain.model.HorarioPuntoInteres
import com.example.triplink.domain.model.enums.DiaSemana
import java.time.DayOfWeek
import java.time.LocalDate

fun List<HorarioPuntoInteres>.toScheduleLabel(): String {
    val first = firstOrNull() ?: return "Horario no disponible"
    return "${first.fechaInicio.toTimeLabel()} - ${first.fechaFin.toTimeLabel()}"
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

fun List<HorarioPuntoInteres>.toWeeklyScheduleUi(): List<DayScheduleUi> {
    val scheduleByDay = associateBy { it.dia }

    return DiaSemana.entries.map { day ->
        val schedule = scheduleByDay[day]
        DayScheduleUi(
            day = day.fullLabel,
            hours = schedule?.let { "${it.fechaInicio.toTimeLabel()} - ${it.fechaFin.toTimeLabel()}" } ?: "Cerrado",
            isClosed = schedule == null
        )
    }
}


fun currentDayLabelEs(): String = when (LocalDate.now().dayOfWeek) {
    DayOfWeek.MONDAY -> "Lunes"
    DayOfWeek.TUESDAY -> "Martes"
    DayOfWeek.WEDNESDAY -> "Miércoles"
    DayOfWeek.THURSDAY -> "Jueves"
    DayOfWeek.FRIDAY -> "Viernes"
    DayOfWeek.SATURDAY -> "Sábado"
    DayOfWeek.SUNDAY -> "Domingo"
}


