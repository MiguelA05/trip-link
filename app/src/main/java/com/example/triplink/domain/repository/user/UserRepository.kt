package com.example.triplink.domain.repository.user

import com.example.triplink.domain.model.Comentario
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Usuario
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val users: StateFlow<List<Usuario>>
    val publications: StateFlow<List<PuntoInteres>>

    // User management
    fun save(user: Usuario): Boolean
    fun findByEmail(email: String): Usuario?
    fun login(email: String, password: String): Usuario?
    fun updateUser(user: Usuario): Boolean
    fun getUserById(userId: String): Usuario?

    // Publication management
    fun homePublications(): List<PuntoInteres>
    fun explorePublications(): List<PuntoInteres>
    fun getPublicationById(publicationId: String): PuntoInteres?
    fun savePuntoInteres(publication: PuntoInteres): Boolean
    fun getUserPublications(userId: String): List<PuntoInteres>

    // Favorites management
    fun toggleFavorite(userId: String, publicationId: String): Boolean
    fun getFavoritePublications(userId: String): List<PuntoInteres>
    fun isFavorite(userId: String, publicationId: String): Boolean

    // Comments management
    fun saveComment(publicationId: String, comment: Comentario): Boolean
    fun getCommentsByPublicationId(publicationId: String): List<Comentario>
}

