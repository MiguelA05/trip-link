package com.example.triplink.domain.model.enums.moderator

import kotlinx.serialization.Serializable

@Serializable
enum class ModerationFilter {
    ALL,
    PENDING,
    VERIFIED,
    REJECTED
}
