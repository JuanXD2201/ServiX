package com.example.domain.repository

import com.example.domain.model.ChatMessage

interface AiChatRepository {
    suspend fun getAiResponse(
        userMessage: String,
        history: List<ChatMessage>,
        systemContext: String
    ): Result<String>
}
