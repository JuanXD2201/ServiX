package com.example.domain.usecase

import com.example.domain.repository.NotificationRepository

class MarcarTodasNotificacionesLeidasUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke() {
        notificationRepository.marcarTodasNotificacionesLeidas()
    }
}
