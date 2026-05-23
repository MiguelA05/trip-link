package com.example.triplink.core.ai

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerationConfig
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.RequestOptions
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

sealed class ModerationOutcome {
    data class Clean(val text: String) : ModerationOutcome()
    data class Moderated(val originalText: String, val suggestedText: String) : ModerationOutcome()
}

@Singleton
class ReviewModerationService @Inject constructor() {
    private val model by lazy {
        val generationConfig = GenerationConfig.Builder()
            .setTemperature(0.2f)
            .setMaxOutputTokens(220)
            .build()

        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig,
            requestOptions = RequestOptions(timeoutInMillis = REQUEST_TIMEOUT_MS)
        )
    }

    suspend fun moderateReview(text: String): ModerationOutcome = withTimeout(REQUEST_TIMEOUT_MS) {
        android.util.Log.d(
            "AI_REQUEST",
            "Solicitud enviada: ${System.currentTimeMillis()}"
        )
        val prompt = buildPrompt(text)
        val response = model.generateContent(prompt)
        val candidate = response.text?.trim().orEmpty()

        if (candidate.isBlank()) {
            throw IllegalStateException("Respuesta vacia de moderacion")
        }

        val normalizedOriginal = normalize(text)
        val normalizedCandidate = normalize(candidate)
        if (normalizedOriginal == normalizedCandidate) {
            ModerationOutcome.Clean(text)
        } else {
            ModerationOutcome.Moderated(originalText = text, suggestedText = candidate)
        }
    }

    private fun buildPrompt(text: String): String =
        """Analiza la siguiente reseña. Si contiene insultos, lenguaje ofensivo, odio, vulgaridades, amenazas o contenido inapropiado, reescríbela de forma segura manteniendo la intención original del usuario. Si ya es apropiada, devuélvela igual. Devuelve únicamente la reseña final.

Reseña: "$text""".trimIndent()

    private fun normalize(text: String): String =
        text.trim().lowercase().replace(Regex("\\s+"), " ")

    companion object {
        private const val MODEL_NAME = "gemini-2.5-flash"
        private const val REQUEST_TIMEOUT_MS = 15000L
    }
}
