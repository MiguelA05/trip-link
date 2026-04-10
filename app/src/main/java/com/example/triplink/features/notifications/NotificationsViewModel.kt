package com.example.triplink.features.notifications

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.triplink.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

data class NotificationItem(
    val id: Int,
    val title: String,
    val description: String,
    val time: String
)

@HiltViewModel
 class NotificationsViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context
) : ViewModel() {

    var notifications by mutableStateOf(
        listOf(
            NotificationItem(
                1,
                appContext.getString(R.string.vm_notifications_nearby_title),
                appContext.getString(R.string.vm_notifications_item_one_description),
                appContext.getString(R.string.vm_notifications_item_one_time)
            ),
            NotificationItem(
                2,
                appContext.getString(R.string.vm_notifications_nearby_title),
                appContext.getString(R.string.vm_notifications_item_two_description),
                appContext.getString(R.string.vm_notifications_item_two_time)
            )
        )
    )

    fun areNotificationsEmpty(): Boolean = notifications.isEmpty()
}