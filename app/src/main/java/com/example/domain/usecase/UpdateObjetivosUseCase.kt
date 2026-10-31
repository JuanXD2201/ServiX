package com.example.domain.usecase

import com.example.domain.repository.ConfigRepository

class UpdateObjetivosUseCase(
    private val configRepository: ConfigRepository
) {
    operator fun invoke(servicios: Int, ingresos: Double) {
        configRepository.updateObjetivos(servicios, ingresos)
    }
}
