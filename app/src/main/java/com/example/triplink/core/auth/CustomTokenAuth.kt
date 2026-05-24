package com.example.triplink.core.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Helper to obtain a custom token from a backend endpoint and sign in with Firebase
 *
 * Expected backend response JSON: { "token": "<customToken>" }
 */
object CustomTokenAuth {
    private val httpClient = OkHttpClient()

    suspend fun signInWithBackendToken(backendUrl: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(backendUrl).get().build()
            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to fetch custom token: HTTP ${response.code}"))
            }
            val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response body from token endpoint"))
            val token = JSONObject(body).optString("token").takeIf { it.isNotEmpty() }
                ?: return@withContext Result.failure(Exception("Token not found in backend response"))

            // Sign in with Firebase using the custom token
            val authResult = suspendCoroutine<com.google.firebase.auth.AuthResult> { cont ->
                FirebaseAuth.getInstance().signInWithCustomToken(token)
                    .addOnSuccessListener { result -> cont.resume(result) }
                    .addOnFailureListener { ex -> cont.resumeWithException(ex) }
            }

            val uid = authResult.user?.uid ?: ""
            return@withContext Result.success(uid)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
}

