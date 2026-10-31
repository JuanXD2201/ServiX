package com.example.domain.usecase

import com.example.domain.repository.ConfigRepository

class ResetDataUseCase(
    private val configRepository: ConfigRepository
) {
    operator fun invoke() {
        configRepository.resetData()
    }
}
