package com.example.triplink.data.repository.user

import com.example.triplink.domain.model.Comentario
import com.example.triplink.domain.repository.comment.CommentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepositoryImpl @Inject constructor(
    private val store: UserRepositoryStore
) : CommentRepository {

    override fun saveComment(publicationId: String, comment: Comentario): Boolean {
        val publicationExists = store.publications.value.any { it.id == publicationId }
        if (!publicationExists) return false

        val comments = store.comments.getOrPut(publicationId) { mutableListOf() }
        comments.add(comment)
        return true
    }

    override fun updateComment(publicationId: String, comment: Comentario): Boolean {
        val comments = store.comments[publicationId] ?: return false
        val index = comments.indexOfFirst { it.id == comment.id }
        if (index == -1) return false

        comments[index] = comment
        return true
    }

    override fun deleteComment(publicationId: String, commentId: String): Boolean {
        val comments = store.comments[publicationId] ?: return false
        val initialSize = comments.size
        store.comments[publicationId] = comments.filter { it.id != commentId }.toMutableList()
        return comments.size > initialSize
    }

    override fun getCommentsByPublicationId(publicationId: String): List<Comentario> {
        return store.comments[publicationId] ?: emptyList()
    }

    override fun getAverageRating(publicationId: String): Double {
        val comments = store.comments[publicationId] ?: return 0.0
        if (comments.isEmpty()) return 0.0
        return comments.map { it.rating }.average()
    }
}

