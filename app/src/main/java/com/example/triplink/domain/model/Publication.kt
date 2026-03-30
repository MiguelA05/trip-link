package com.example.triplink.domain.model

data class Publication(
    val id: String,
    val title: String,
    val authorName: String,
    val authorInitials: String,
    val timeAgo: String,
    val distance: String,
    val category: String,
    val rating: Double,
    val location: String,
    val imageUrl: String,
    val commentsCount: Int,
    val likesCount: Int,
    val isFavorite: Boolean = false
)
