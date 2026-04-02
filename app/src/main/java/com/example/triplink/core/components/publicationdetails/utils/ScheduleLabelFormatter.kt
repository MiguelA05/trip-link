package com.example.triplink.core.components.publicationdetails.utils

import com.example.triplink.core.components.publicationdetails.sections.DayScheduleUi
import java.time.DayOfWeek
import java.time.LocalDate

fun Pair<Long, Long>?.toScheduleLabel(): String = if (this == null) {
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

fun Pair<Long, Long>?.toWeeklyScheduleUi(): List<DayScheduleUi> {
    val days = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val hourLabel = this.toScheduleLabel()
    val closed = this == null
    return days.map { day ->
        DayScheduleUi(
            day = day,
            hours = if (closed) "Cerrado" else hourLabel,
            isClosed = closed
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


