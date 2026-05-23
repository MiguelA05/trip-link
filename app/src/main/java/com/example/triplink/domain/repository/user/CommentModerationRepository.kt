package com.example.triplink.domain.repository.user

import com.example.triplink.domain.model.CommentModerationResult

interface CommentModerationRepository {
    suspend fun moderateComment(comment: String, publicationId: String? = null): Result<CommentModerationResult>
}

