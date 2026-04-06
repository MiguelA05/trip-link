package com.example.triplink.data.model

import com.example.triplink.domain.model.enums.Rol

data class UserSession(
    val userId: String,
    val role: Rol
)