package com.example.data.mock

import com.example.domain.model.Notificacion
import com.example.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.StateFlow

class MockNotificationRepository : NotificationRepository {
    override val notificaciones: StateFlow<List<Notificacion>> = MockDataStore.notificaciones
    override fun marcarNotificacionLeida(notificacionId: String) = MockDataStore.marcarNotificacionLeida(notificacionId)
    override fun deleteNotificacion(notificacionId: String) = MockDataStore.deleteNotificacion(notificacionId)
    override fun marcarTodasNotificacionesLeidas() = MockDataStore.marcarTodasNotificacionesLeidas()
    override fun addNotificacion(notificacion: Notificacion) = MockDataStore.addNotificacion(notificacion)
}
