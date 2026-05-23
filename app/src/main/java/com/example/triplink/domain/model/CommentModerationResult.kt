package com.example.triplink.domain.model

data class CommentModerationResult(
    val isInappropriate: Boolean,
    val reason: String,
    val safeAlternative: String
)
