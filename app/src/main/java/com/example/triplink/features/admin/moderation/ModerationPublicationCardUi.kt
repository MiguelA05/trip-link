package com.example.triplink.features.admin.moderation

enum class ModerationPublicationCardStatus {
    PENDING,
    VERIFIED,
    REJECTED
}

data class ModerationPublicationCardUi(
    val id: String,
    val title: String,
    val categoryLabel: String,
    val authorName: String,
    val timeLabel: String,
    val cityLabel: String,
    val priceLabel: String,
    val scheduleLabel: String,
    val imageUrl: String,
    val status: ModerationPublicationCardStatus,
    val reportCount: Int = 0,
    val reasonMessage: String? = null,
    val rejectReason: String? = null
)


