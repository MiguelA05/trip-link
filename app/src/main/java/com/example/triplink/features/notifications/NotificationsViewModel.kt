package com.example.triplink.features.notifications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class NotificationItem(
    val id: Int,
    val title: String,
    val description: String,
    val time: String
)

@HiltViewModel
class NotificationsViewModel @Inject constructor() : ViewModel() {

    var notifications by mutableStateOf(
        listOf(
            NotificationItem(
                1,
                "Nuevo lugar cercano",
                "Mirador de Salento fue anadido a 1.2 km de tu ubicacion actual.",
                "Hace 4 horas"
            ),
            NotificationItem(
                2,
                "Nuevo lugar cercano",
                "Hotel El Zafiro fue anadido a 1.9 km de tu ubicacion actual.",
                "Hace 8 horas"
            )
        )
    )

    fun areNotificationsEmpty(): Boolean = notifications.isEmpty()
}