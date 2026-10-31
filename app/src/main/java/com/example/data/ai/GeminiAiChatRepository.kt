package com.example.data.ai

import android.util.Log
import com.example.domain.model.ChatMessage
import com.example.domain.repository.AiChatRepository
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig

/**
 * Chat con Gemini mediante Firebase AI Logic (Gemini Developer API).
 * La credencial vive en el proyecto Firebase, no en el APK.
 * Si la llamada falla se propaga el error: no se simulan respuestas de IA.
 */
class GeminiAiChatRepository : AiChatRepository {

    override suspend fun getAiResponse(
        userMessage: String,
        history: List<ChatMessage>,
        systemContext: String
    ): Result<String> {
        return try {
            val model = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
                modelName = MODEL_NAME,
                generationConfig = generationConfig {
                    temperature = 0.7f
                    maxOutputTokens = 800
                },
                systemInstruction = content { text(systemContext) }
            )

            // Gemini exige que el historial empiece con un turno del usuario.
            val turns = history.takeLast(MAX_HISTORY).dropWhile { !it.isFromUser }
            val chat = model.startChat(
                history = turns.map { msg ->
                    content(role = if (msg.isFromUser) "user" else "model") { text(msg.text) }
                }
            )

            val text = chat.sendMessage(userMessage).text.orEmpty()
            if (text.isBlank()) {
                Result.failure(IllegalStateException("Respuesta vacía del modelo"))
            } else {
                Result.success(text)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase AI Logic falló: ${e.message}", e)
            Result.failure(e)
        }
    }

    private companion object {
        const val TAG = "GeminiAiChatRepo"
        const val MODEL_NAME = "gemini-2.5-flash"
        const val MAX_HISTORY = 6
    }
}
