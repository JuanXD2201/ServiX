package com.example.domain.usecase

import com.example.domain.repository.NotificationRepository

class DeleteNotificacionUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(notificacionId: String) {
        notificationRepository.deleteNotificacion(notificacionId)
    }
}
