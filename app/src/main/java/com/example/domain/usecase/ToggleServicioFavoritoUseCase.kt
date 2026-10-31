package com.example.domain.usecase

import com.example.domain.repository.ServicioRepository

class ToggleServicioFavoritoUseCase(
    private val servicioRepository: ServicioRepository
) {
    operator fun invoke(servicioId: String) {
        servicioRepository.toggleServicioFavorito(servicioId)
    }
}
