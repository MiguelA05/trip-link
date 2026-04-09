package com.example.triplink.domain.repository.comment

import com.example.triplink.domain.model.Comentario

interface CommentRepository {
    fun saveComment(publicationId: String, comment: Comentario): Boolean
    fun updateComment(publicationId: String, comment: Comentario): Boolean
    fun deleteComment(publicationId: String, commentId: String): Boolean
    fun getCommentsByPublicationId(publicationId: String): List<Comentario>
    fun getAverageRating(publicationId: String): Double
}

