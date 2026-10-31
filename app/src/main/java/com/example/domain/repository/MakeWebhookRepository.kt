package com.example.domain.repository

import com.example.domain.model.Cliente
import com.example.domain.model.Servicio
import com.example.domain.model.UserProfile

interface MakeWebhookRepository {
    fun sendServicioEvent(
        evento: String,
        servicio: Servicio,
        cliente: Cliente?,
        tecnico: UserProfile?,
        usuarioId: String?
    )

    suspend fun sendServicioEventSync(
        evento: String,
        servicio: Servicio,
        cliente: Cliente?,
        tecnico: UserProfile?,
        usuarioId: String?
    ): Result<String>
}
