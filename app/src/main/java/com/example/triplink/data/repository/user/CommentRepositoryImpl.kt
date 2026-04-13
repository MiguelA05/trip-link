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
        val publication = store.publications.value.firstOrNull { it.id == publicationId } ?: return false
        val normalizedComment = comment.copy(puntoInteresId = publicationId)
        val updatedComments = (publication.comments + normalizedComment)
        updatePublicationComments(publicationId, updatedComments)
        return true
    }

    override fun updateComment(publicationId: String, comment: Comentario): Boolean {
        val comments = getCommentsByPublicationId(publicationId)
        val index = comments.indexOfFirst { it.id == comment.id }
        if (index == -1) return false

        val updatedComments = comments.toMutableList().apply { this[index] = comment }
        updatePublicationComments(publicationId, updatedComments)
        return true
    }

    override fun deleteComment(publicationId: String, commentId: String): Boolean {
        val comments = getCommentsByPublicationId(publicationId)
        val initialSize = comments.size
        val updatedComments = comments.filter { it.id != commentId }
        updatePublicationComments(publicationId, updatedComments)
        return updatedComments.size < initialSize
    }

    override fun getCommentsByPublicationId(publicationId: String): List<Comentario> {
        return store.publications.value.firstOrNull { it.id == publicationId }?.comments.orEmpty()
    }

    override fun getAverageRating(publicationId: String): Double {
        val comments = getCommentsByPublicationId(publicationId)
        if (comments.isEmpty()) return 0.0
        return comments.map { it.rating }.average()
    }

    private fun updatePublicationComments(publicationId: String, comments: List<Comentario>) {
        val updatedPublications = store.publications.value.map { publication ->
            if (publication.id == publicationId) {
                publication.copy(
                    comments = comments,
                    commentCount = comments.size
                )
            } else {
                publication
            }
        }
        store.setPublications(updatedPublications)
    }
}

