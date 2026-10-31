package com.example.domain.model

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean = true,
    val senderType: SenderType = if (isFromUser) SenderType.CLIENTE else SenderType.AGENTE_IA,
    val senderName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
