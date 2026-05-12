package com.example.triplink.data.repository.remote.user

import com.example.triplink.domain.model.Usuario
import com.example.triplink.domain.repository.user.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val store: UserRepositoryStore
) : AuthRepository {

    override suspend fun login(email: String, password: String): Usuario? {
        val user = store.findUserByEmail(email) ?: return null
        return if (user.password == password && user.activo) user else null
    }

    override suspend fun findByEmail(email: String): Usuario? {
        return store.findUserByEmail(email)
    }

    override suspend fun save(user: Usuario): Boolean {
        if (findByEmail(user.email) != null) return false
        store.ensureFavoriteBucket(user.email)
        return store.saveUser(user)
    }
}

