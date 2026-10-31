package com.example.domain.usecase

import com.example.domain.model.AppConfig
import com.example.domain.repository.ConfigRepository

class UpdateConfigUseCase(
    private val configRepository: ConfigRepository
) {
    operator fun invoke(config: AppConfig) {
        configRepository.updateConfig(config)
    }
}
