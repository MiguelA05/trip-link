package com.example.triplink.features.notifications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class NotificationItem(
    val id: Int,
    val title: String,
    val description: String,
    val time: String
)

class NotificationsViewModel: ViewModel() {

    var notifications by mutableStateOf(listOf(
        NotificationItem(
            1,
            "Nuevo lugar cercano",
            "Mirador de Salento fue añadido a 1.2 km de tu ubicación actual.",
            "Hace 4 horas"
        ),
        NotificationItem(
            2,
            "Nuevo lugar cercano",
            "Hotel El Zafiro fue añadido a 1.9 km de tu ubicación actual.",
            "Hace 8 horas"
        )
    ))

    fun areNotificationsEmpty(): Boolean {
        return notifications.isEmpty()
    }

}