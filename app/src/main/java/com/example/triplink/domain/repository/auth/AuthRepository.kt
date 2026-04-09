package com.example.triplink.domain.repository.auth

import com.example.triplink.domain.model.Usuario

interface AuthRepository {
    fun login(email: String, password: String): Usuario?
    fun findByEmail(email: String): Usuario?
    fun save(user: Usuario): Boolean
}

