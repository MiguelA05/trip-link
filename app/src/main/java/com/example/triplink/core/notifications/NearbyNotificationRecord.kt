package com.example.triplink.core.notifications

import kotlinx.serialization.Serializable

@Serializable
data class NearbyNotificationRecord(
    val id: String,
    val publicationId: String,
    val publicationTitle: String,
    val publicationInfo: String,
    val publicationCreatedAtMillis: Long,
    val notifiedAtMillis: Long,
    val isRead: Boolean = false
)

