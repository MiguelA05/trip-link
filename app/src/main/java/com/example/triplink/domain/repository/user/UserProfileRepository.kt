package com.example.triplink.domain.repository.user

import com.example.triplink.domain.model.Usuario
import kotlinx.coroutines.flow.StateFlow

interface UserProfileRepository {
    val users: StateFlow<List<Usuario>>

    fun getUserById(userId: String): Usuario?
    fun findUserNameById(userId: String): String?
    fun updateUser(user: Usuario): Boolean
    fun deleteUser(email: String): Boolean
}

