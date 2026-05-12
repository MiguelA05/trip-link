package com.example.triplink.domain.repository.user

import com.example.triplink.domain.model.Comentario

interface CommentRepository {
    suspend fun saveComment(publicationId: String, comment: Comentario): Boolean
    suspend fun updateComment(publicationId: String, comment: Comentario): Boolean
    suspend fun deleteComment(publicationId: String, commentId: String): Boolean
    fun getCommentsByPublicationId(publicationId: String): List<Comentario>
    fun getAverageRating(publicationId: String): Double
}