package com.example.triplink.data.repository.user

import com.example.triplink.data.repository.user.publications.createUserPublicationsSeedState
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Usuario
import com.example.triplink.domain.model.enums.Rol
import com.example.triplink.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {

    private val publicationsSeedState = createUserPublicationsSeedState()

    private val _users = MutableStateFlow(
        listOf(
            Usuario(
                email = "carlos@email.com",
                nombre = "Carlos",
                password = "123456",
                puntos = 90,
                rol = Rol.USUARIO
            ),
            Usuario(
                email = "admin@triplink.com",
                nombre = "Admin",
                password = "admin123",
                puntos = 0,
                rol = Rol.MODERADOR
            )
        )
    )
    override val users: StateFlow<List<Usuario>> = _users.asStateFlow()

    private val _publications = MutableStateFlow(publicationsSeedState.publications.toList())
    override val publications: StateFlow<List<PuntoInteres>> = _publications.asStateFlow()

    override fun save(user: Usuario): Boolean {
        if (findByEmail(user.email) != null) return false
        _users.value = _users.value + user
        return true
    }

    override fun findByEmail(email: String): Usuario? {
        return _users.value.firstOrNull { it.email.equals(email, ignoreCase = true) }
    }

    override fun login(email: String, password: String): Usuario? {
        return _users.value.firstOrNull {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
    }

    override fun homePublications(): List<PuntoInteres> = _publications.value.take(2)

    override fun explorePublications(): List<PuntoInteres> = _publications.value

    override fun getPublicationById(publicationId: String): PuntoInteres? {
        return _publications.value.firstOrNull { it.id == publicationId }
    }
}

