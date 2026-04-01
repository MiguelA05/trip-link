package com.example.triplink.domain.model.moderator

import com.example.triplink.domain.model.PuntoInteres

data class ModerationPublication(
    val id: String,
    val pointOfInterest: PuntoInteres,
    val authorName: String,
    val createdAtMillis: Long,
    val reportCount: Int = 0,
    val moderationReason: String? = null,
    val rejectReason: String? = null
)

