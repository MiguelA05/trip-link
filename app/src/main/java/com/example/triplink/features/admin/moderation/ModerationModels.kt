package com.example.triplink.features.admin.moderation

enum class PublicationModerationStatus {
    PENDING,
    VERIFIED,
    REJECTED
}

enum class ModerationFilter {
    ALL,
    PENDING,
    VERIFIED,
    REJECTED
}

enum class ModerationDecision {
    APPROVE,
    REJECT
}

data class ModerationPublicationUi(
    val id: String,
    val title: String,
    val categoryLabel: String,
    val authorName: String,
    val timeLabel: String,
    val cityLabel: String,
    val priceLabel: String,
    val scheduleLabel: String,
    val imageUrl: String,
    val status: PublicationModerationStatus,
    val reasonMessage: String? = null,
    val rejectReason: String? = null
)

