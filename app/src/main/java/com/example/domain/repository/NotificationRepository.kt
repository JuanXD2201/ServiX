package com.example.domain.repository

import com.example.domain.model.Notificacion
import kotlinx.coroutines.flow.StateFlow

interface NotificationRepository {
    val notificaciones: StateFlow<List<Notificacion>>

    fun addNotificacion(notificacion: Notificacion)
    fun marcarNotificacionLeida(notificacionId: String)
    fun deleteNotificacion(notificacionId: String)
    fun marcarTodasNotificacionesLeidas()
}
