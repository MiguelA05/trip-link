package com.example.triplink.domain.repository.user

import com.example.triplink.domain.model.Usuario
import kotlinx.coroutines.flow.StateFlow

interface UserProfileRepository {
    val users: StateFlow<List<Usuario>>

    suspend fun getUserById(userId: String): Usuario?
    suspend fun findUserNameById(userId: String): String?
    suspend fun updateUser(user: Usuario): Boolean
    suspend fun deleteUser(email: String): Boolean
}

