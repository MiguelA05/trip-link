package com.example.triplink.data.repository.remote.user

import com.example.triplink.domain.model.Comentario
import com.example.triplink.domain.repository.user.CommentRepository
import com.example.triplink.domain.repository.user.PublicationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepositoryImpl @Inject constructor(
    private val publicationRepository: PublicationRepository
) : CommentRepository {

    override suspend fun saveComment(publicationId: String, comment: Comentario): Boolean {
        val publication = publicationRepository.getPublicationById(publicationId) ?: return false
        val normalizedComment = comment.copy(puntoInteresId = publicationId)
        val updatedComments = (publication.comments + normalizedComment)
        val updatedPublication = publication.copy(comments = updatedComments, commentCount = updatedComments.size)
        return publicationRepository.updatePuntoInteres(updatedPublication)
    }

    override suspend fun updateComment(publicationId: String, comment: Comentario): Boolean {
        val publication = publicationRepository.getPublicationById(publicationId) ?: return false
        val comments = publication.comments.toMutableList()
        val index = comments.indexOfFirst { it.id == comment.id }
        if (index == -1) return false

        comments[index] = comment
        val updatedPublication = publication.copy(comments = comments, commentCount = comments.size)
        return publicationRepository.updatePuntoInteres(updatedPublication)
    }

    override suspend fun deleteComment(publicationId: String, commentId: String): Boolean {
        val publication = publicationRepository.getPublicationById(publicationId) ?: return false
        val comments = publication.comments
        val initialSize = comments.size
        val updatedComments = comments.filter { it.id != commentId }
        val updatedPublication = publication.copy(comments = updatedComments, commentCount = updatedComments.size)
        val updated = publicationRepository.updatePuntoInteres(updatedPublication)
        return updated && updatedComments.size < initialSize
    }

    override suspend fun getCommentsByPublicationId(publicationId: String): List<Comentario> {
        return publicationRepository.getPublicationById(publicationId)?.comments.orEmpty()
    }

    override suspend fun getAverageRating(publicationId: String): Double {
        val comments = getCommentsByPublicationId(publicationId)
        if (comments.isEmpty()) return 0.0
        return comments.map { it.rating }.average()
    }

    // Comments persistence now uses store.updatePublication directly; helper removed.
}

