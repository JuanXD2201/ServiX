package com.example.domain.usecase

import com.example.domain.model.Cliente
import com.example.domain.model.Servicio
import com.example.domain.model.TipoActividad
import com.example.domain.repository.ActividadRepository
import com.example.domain.repository.ClienteRepository

class RestaurarClienteUseCase(
    private val clienteRepository: ClienteRepository,
    private val actividadRepository: ActividadRepository
) {
    operator fun invoke(cliente: Cliente, serviciosAsociados: List<Servicio>) {
        clienteRepository.restaurarCliente(cliente, serviciosAsociados)
        actividadRepository.logActividad(
            TipoActividad.CLIENTE_CREADO,
            "Cliente restaurado: ${cliente.nombre}",
            "Se recuperó el cliente y sus ${serviciosAsociados.size} servicios."
        )
    }
}
