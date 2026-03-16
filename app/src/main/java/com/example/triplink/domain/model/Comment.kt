package com.example.triplink.domain.model

data class Comment(
    val id: String,
    val userName: String,
    val date: String,
    val rating: Float,
    val text: String,
    val userInitials: String
)
