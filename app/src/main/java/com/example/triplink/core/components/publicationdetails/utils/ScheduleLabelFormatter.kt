package com.example.triplink.core.components.publicationdetails.utils

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

