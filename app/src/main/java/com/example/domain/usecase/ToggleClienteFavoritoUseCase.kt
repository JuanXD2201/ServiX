package com.example.domain.usecase

import com.example.domain.repository.ClienteRepository

class ToggleClienteFavoritoUseCase(
    private val clienteRepository: ClienteRepository
) {
    operator fun invoke(clienteId: String) {
        clienteRepository.toggleClienteFavorito(clienteId)
    }
}
