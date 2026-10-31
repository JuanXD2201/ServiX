package com.example.domain.usecase

import com.example.domain.model.Cliente
import com.example.domain.model.TipoActividad
import com.example.domain.repository.ActividadRepository
import com.example.domain.repository.ClienteRepository

class UpdateClienteUseCase(
    private val clienteRepository: ClienteRepository,
    private val actividadRepository: ActividadRepository
) {
    operator fun invoke(cliente: Cliente) {
        clienteRepository.updateCliente(cliente)
        actividadRepository.logActividad(
            TipoActividad.CLIENTE_EDITADO,
            "Cliente actualizado: ${cliente.nombre}",
            "Datos de contacto e información comercial guardados."
        )
    }
}
