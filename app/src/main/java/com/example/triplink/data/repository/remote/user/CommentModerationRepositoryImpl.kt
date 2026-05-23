package com.example.triplink.data.repository.remote.user

import android.util.Log
import com.example.triplink.core.utils.FirebaseAuthPersistenceManager
import com.example.triplink.domain.model.CommentModerationResult
import com.example.triplink.domain.repository.user.CommentModerationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentModerationRepositoryImpl @Inject constructor(
    private val firebaseFunctions: FirebaseFunctions,
    private val authPersistence: FirebaseAuthPersistenceManager
) : CommentModerationRepository {

    private val auth = FirebaseAuth.getInstance()

    override suspend fun moderateComment(comment: String, publicationId: String?): Result<CommentModerationResult> {
        val trimmedComment = comment.trim()
        Log.d("CommentModeration", "moderateComment called: length=${trimmedComment.length}, publicationId=$publicationId")

        if (trimmedComment.isBlank()) {
            Log.d("CommentModeration", "Comment is blank, skipping moderation")
            return Result.success(
                CommentModerationResult(
                    isInappropriate = false,
                    reason = "",
                    safeAlternative = ""
                )
            )
        }

        return try {
            ensureFirebaseAuthSession()

            // Force refresh the token to ensure it's current and valid for the function call
            try {
                val tokenResult = auth.currentUser?.getIdToken(false)?.await()
                Log.d("CommentModeration", "Token refreshed successfully for user: ${auth.currentUser?.uid}, token exists: ${tokenResult?.token != null}")
            } catch (tokenError: Exception) {
                Log.w("CommentModeration", "Token refresh failed, continuing with current token: ${tokenError.message}")
            }

            // Small delay to ensure token is propagated
            kotlinx.coroutines.delay(200)

            val payload = hashMapOf(
                "comment" to trimmedComment,
                "language" to "es",
                "context" to hashMapOf(
                    "feature" to "publication_review",
                    "publicationId" to publicationId.orEmpty()
                )
            )

            Log.d("CommentModeration", "Calling Firebase Function: moderateCommentDeepSeek with auth user: ${auth.currentUser?.uid}")
            val response = firebaseFunctions
                .getHttpsCallable("moderateCommentDeepSeek")
                .call(payload)
                .await()
            Log.d("CommentModeration", "Function response received: ${response.data}")
            val root = response.data as? Map<*, *>
                ?: run {
                    Log.e("CommentModeration", "Response data is null or not a Map")
                    return Result.failure(IllegalStateException("Respuesta de moderacion invalida"))
                }

            val resultMap = (root["result"] as? Map<*, *>) ?: root

            val isInappropriate = resultMap["isInappropriate"] as? Boolean ?: false
            val reason = (resultMap["reason"] as? String).orEmpty().trim()
            val safeAlternativeRaw = (resultMap["safeAlternative"] as? String).orEmpty().trim()
            val safeAlternative = if (isInappropriate && safeAlternativeRaw.isBlank()) {
                "El lugar no fue de mi agrado, mi experiencia fue mala."
            } else {
                safeAlternativeRaw
            }

            Log.d("CommentModeration", "Moderation result: isInappropriate=$isInappropriate, reason=$reason")
            Result.success(
                CommentModerationResult(
                    isInappropriate = isInappropriate,
                    reason = reason,
                    safeAlternative = safeAlternative
                )
            )
        } catch (error: Exception) {
            if (error is FirebaseFunctionsException && error.code == FirebaseFunctionsException.Code.UNAUTHENTICATED) {
                Log.e("CommentModeration", "Callable returned UNAUTHENTICATED. authCurrentUser=${auth.currentUser?.uid}", error)
                return Result.failure(
                    IllegalStateException(
                        "La sesión de Firebase no está lista para moderar el comentario. Vuelve a iniciar sesión y reintenta."
                    )
                )
            }
            Log.e("CommentModeration", "Exception during moderation: ${error.message}", error)
            Result.failure(error)
        }
    }

    private suspend fun ensureFirebaseAuthSession() {
        val authState = authPersistence.ensureAuthSession()

        if (authState != null) {
            Log.d("CommentModeration", "Firebase Auth session verified: uid=${authState.uid}, provider=${authState.provider}")
            return
        }

        Log.w("CommentModeration", "No Firebase Auth session found. Attempting anonymous sign-in for moderation.")
        val anonState = authPersistence.ensureAnonymousAuth()

        if (anonState != null) {
            Log.i("CommentModeration", "Anonymous Firebase Auth session created: uid=${anonState.uid}")
        } else {
            Log.e("CommentModeration", "Failed to establish any Firebase Auth session")
            throw IllegalStateException("No se pudo establecer una sesión de autenticación para moderar el comentario")
        }
    }
}

















