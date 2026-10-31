package com.example.domain.usecase

import com.example.domain.model.Cliente
import com.example.domain.model.Servicio
import com.example.domain.model.TipoActividad
import com.example.domain.repository.ActividadRepository
import com.example.domain.repository.ClienteRepository

class DeleteClienteUseCase(
    private val clienteRepository: ClienteRepository,
    private val actividadRepository: ActividadRepository
) {
    operator fun invoke(clienteId: String): Pair<Cliente?, List<Servicio>> {
        val result = clienteRepository.deleteCliente(clienteId)
        val nombre = result.first?.nombre ?: "Cliente"
        actividadRepository.logActividad(
            TipoActividad.CLIENTE_ELIMINADO,
            "Cliente eliminado: $nombre",
            "Se retiró el cliente y sus servicios asociados del sistema."
        )
        return result
    }
}
