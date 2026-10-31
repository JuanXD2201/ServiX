package com.example.domain.usecase

import com.example.domain.repository.ConfigRepository

class MarcarOnboardingVistoUseCase(
    private val configRepository: ConfigRepository
) {
    operator fun invoke(visto: Boolean) {
        configRepository.marcarOnboardingVisto(visto)
    }
}
