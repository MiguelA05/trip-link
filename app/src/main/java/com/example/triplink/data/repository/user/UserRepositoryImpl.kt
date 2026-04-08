package com.example.triplink.data.repository.user

import com.example.triplink.data.repository.user.publications.createUserPublicationsSeedState
import com.example.triplink.domain.model.Comentario
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

    // Store favorites per user: userId -> set of publicationIds
    private val _favorites = mutableMapOf<String, MutableSet<String>>()

    // Store comments per publication: publicationId -> list of comments
    private val _comments = mutableMapOf<String, MutableList<Comentario>>()

    override fun save(user: Usuario): Boolean {
        if (findByEmail(user.email) != null) return false
        _users.value = _users.value + user
        _favorites[user.email] = mutableSetOf()
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

    override fun updateUser(user: Usuario): Boolean {
        val index = _users.value.indexOfFirst { it.email.equals(user.email, ignoreCase = true) }
        if (index == -1) return false
        val updatedList = _users.value.toMutableList()
        updatedList[index] = user
        _users.value = updatedList
        return true
    }

    override fun getUserById(userId: String): Usuario? {
        return _users.value.firstOrNull { it.email.equals(userId, ignoreCase = true) }
    }

    override fun homePublications(): List<PuntoInteres> = _publications.value.take(2)

    override fun explorePublications(): List<PuntoInteres> = _publications.value

    override fun getPublicationById(publicationId: String): PuntoInteres? {
        return _publications.value.firstOrNull { it.id == publicationId }
    }

    override fun savePuntoInteres(publication: PuntoInteres): Boolean {
        if (getPublicationById(publication.id) != null) return false
        _publications.value = _publications.value + publication
        // Initialize comments list for this publication
        _comments[publication.id] = mutableListOf()
        return true
    }

    override fun getUserPublications(userId: String): List<PuntoInteres> {
        return _publications.value.filter { it.usuarioAutorId.equals(userId, ignoreCase = true) }
    }

    override fun toggleFavorite(userId: String, publicationId: String): Boolean {
        val favorites = _favorites.getOrPut(userId) { mutableSetOf() }
        return if (favorites.contains(publicationId)) {
            favorites.remove(publicationId)
        } else {
            favorites.add(publicationId)
        }
    }

    override fun getFavoritePublications(userId: String): List<PuntoInteres> {
        val favorites = _favorites[userId] ?: return emptyList()
        return _publications.value.filter { it.id in favorites }
    }

    override fun isFavorite(userId: String, publicationId: String): Boolean {
        return _favorites[userId]?.contains(publicationId) ?: false
    }

    override fun saveComment(publicationId: String, comment: Comentario): Boolean {
        if (getPublicationById(publicationId) == null) return false
        val comments = _comments.getOrPut(publicationId) { mutableListOf() }
        comments.add(comment)
        return true
    }

    override fun getCommentsByPublicationId(publicationId: String): List<Comentario> {
        return _comments[publicationId] ?: emptyList()
    }
}

