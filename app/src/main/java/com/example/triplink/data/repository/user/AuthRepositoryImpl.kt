package com.example.triplink.data.repository.user

import com.example.triplink.domain.model.Usuario
import com.example.triplink.domain.repository.auth.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val store: UserRepositoryStore
) : AuthRepository {

    override fun login(email: String, password: String): Usuario? {
        return store.users.value.firstOrNull {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
    }

    override fun findByEmail(email: String): Usuario? {
        return store.users.value.firstOrNull { it.email.equals(email, ignoreCase = true) }
    }

    override fun save(user: Usuario): Boolean {
        if (findByEmail(user.email) != null) return false
        store.setUsers(store.users.value + user)
        store.favorites[user.email] = mutableSetOf()
        return true
    }
}

