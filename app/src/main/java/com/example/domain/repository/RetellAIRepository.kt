package com.example.domain.repository

import com.example.domain.model.ChatMessage

/**
 * Contrato conceptual para futura integración de telefonía o agente conversacional de voz Retell AI.
 * ServiX -> Retell AI -> Respuesta del agente -> ServiX
 *
 * NOTA: No contiene llamadas reales ni credenciales hardcodeadas hasta recibir el Agent ID y API Key oficial.
 */
interface RetellAIRepository {
    suspend fun initiateCall(
        clienteTelefono: String,
        clienteNombre: String,
        motivo: String,
        metadata: Map<String, String> = emptyMap()
    ): Result<String>

    suspend fun getCallStatus(callId: String): Result<String>
}
