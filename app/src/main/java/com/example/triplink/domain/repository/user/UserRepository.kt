package com.example.triplink.domain.repository.user

import com.example.triplink.domain.model.Comentario
import com.example.triplink.domain.model.PuntoInteres
import com.example.triplink.domain.model.Usuario
import com.example.triplink.domain.model.enums.EstadoPublicacion
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
    fun findUserNameById(userId: String): String?
    fun deleteUser(email: String): Boolean


    // Publication management
    fun homePublications(): List<PuntoInteres>
    fun explorePublications(): List<PuntoInteres>
    fun getPublicationById(publicationId: String): PuntoInteres?
    fun savePuntoInteres(publication: PuntoInteres): Boolean
    fun updatePuntoInteres(publication: PuntoInteres): Boolean
    fun deletePublicationById(publicationId: String): Boolean
    fun getUserPublications(userId: String): List<PuntoInteres>
    fun getPublicationsByState(estado: EstadoPublicacion): List<PuntoInteres>

    // Favorites management
    fun toggleFavorite(userId: String, publicationId: String): Boolean
    fun getFavoritePublications(userId: String): List<PuntoInteres>
    fun isFavorite(userId: String, publicationId: String): Boolean

    // Comments management
    fun saveComment(publicationId: String, comment: Comentario): Boolean
    fun updateComment(publicationId: String, comment: Comentario): Boolean
    fun deleteComment(publicationId: String, commentId: String): Boolean
    fun getCommentsByPublicationId(publicationId: String): List<Comentario>
    fun getAverageRating(publicationId: String): Double

}

