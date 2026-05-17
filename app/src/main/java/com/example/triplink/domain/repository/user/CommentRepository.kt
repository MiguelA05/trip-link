package com.example.triplink.domain.repository.user

import com.example.triplink.domain.model.Comentario

interface CommentRepository {
    suspend fun saveComment(publicationId: String, comment: Comentario): Boolean
    suspend fun updateComment(publicationId: String, comment: Comentario): Boolean
    suspend fun deleteComment(publicationId: String, commentId: String): Boolean
    suspend fun getCommentsByPublicationId(publicationId: String): List<Comentario>
    suspend fun getAverageRating(publicationId: String): Double
}