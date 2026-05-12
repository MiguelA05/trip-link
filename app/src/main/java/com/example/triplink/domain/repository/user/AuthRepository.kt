package com.example.triplink.domain.repository.user

import com.example.triplink.domain.model.Usuario

interface AuthRepository {
    suspend fun login(email: String, password: String): Usuario?
    suspend fun findByEmail(email: String): Usuario?
    suspend fun save(user: Usuario): Boolean
}