package com.example.triplink.features.notifications

import android.content.Context
import android.text.format.DateUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            notifications = feedStore.getAll().map { record ->
                NotificationItem(
                    id = record.id,
                    publicationId = record.publicationId,
                    title = record.publicationTitle,
                    description = record.publicationInfo,
                    time = DateUtils.getRelativeTimeSpanString(
                        record.notifiedAtMillis,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE
                    ).toString(),
                    isRead = record.isRead
                )
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            feedStore.markAllAsRead()
            refresh()
        }
    }

    fun onNotificationOpened(notificationId: String) {
        viewModelScope.launch {
            feedStore.markAsRead(notificationId)
            refresh()
        }
    }

    fun areNotificationsEmpty(): Boolean = notifications.isEmpty()
}
