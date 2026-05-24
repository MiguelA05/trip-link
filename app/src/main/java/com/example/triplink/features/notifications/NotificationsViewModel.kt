package com.example.triplink.features.notifications

import android.content.Context
import android.text.format.DateUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triplink.core.storage.NearbyNotificationFeedStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationItem(
    val id: String,
    val publicationId: String,
    val title: String,
    val description: String,
    val time: String,
    val isRead: Boolean
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context
) : ViewModel() {

    private val feedStore = NearbyNotificationFeedStore(appContext)

    var notifications by mutableStateOf(emptyList<NotificationItem>())
        private set

    init {
        viewModelScope.launch {
            feedStore.observeAll().collect { records ->
                notifications = records.map { record ->
                    record.toNotificationItem()
                }
            }
        }
    }

    private fun com.example.triplink.core.notifications.NearbyNotificationRecord.toNotificationItem(): NotificationItem {
        return NotificationItem(
            id = id,
            publicationId = publicationId,
            title = publicationTitle,
            description = publicationInfo,
            time = DateUtils.getRelativeTimeSpanString(
                notifiedAtMillis,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
            ).toString(),
            isRead = isRead
        )
    }

    fun refresh() {
        viewModelScope.launch {
            notifications = feedStore.getAll().map { record ->
                record.toNotificationItem()
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            feedStore.clearAll()
        }
    }

    fun onNotificationOpened(notificationId: String) {
        viewModelScope.launch {
            NotificationManagerCompat.from(appContext).cancel(notificationId.hashCode())
            feedStore.remove(notificationId)
        }
    }

    fun areNotificationsEmpty(): Boolean = notifications.isEmpty()
}
