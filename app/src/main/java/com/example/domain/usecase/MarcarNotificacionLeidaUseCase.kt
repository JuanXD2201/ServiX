package com.example.domain.usecase

import com.example.domain.repository.NotificationRepository

class MarcarNotificacionLeidaUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(notificacionId: String) {
        notificationRepository.marcarNotificacionLeida(notificacionId)
    }
}
