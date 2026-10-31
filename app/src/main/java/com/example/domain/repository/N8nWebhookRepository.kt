package com.example.domain.repository

import com.example.domain.model.SolicitudAtencion
import com.example.domain.model.ChatMessage
import com.example.domain.model.Servicio

/**
 * Contrato conceptual para disparar webhooks hacia n8n.
 * Orquestador principal del flujo:
 * ServiX -> Webhook n8n -> Make / Retell AI -> Firebase -> ServiX
 *
 * NOTA DE SEGURIDAD:
 * No transporta tokens privados, contraseñas, claves API ni credenciales de Firebase.
 */
interface N8nWebhookRepository {
    fun sendEvent(
        evento: String,
        payload: Map<String, Any?>
    )

    suspend fun sendEventSync(
        evento: String,
        payload: Map<String, Any?>
    ): Result<String>
}
