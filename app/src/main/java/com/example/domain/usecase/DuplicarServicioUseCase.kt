package com.example.domain.usecase

import com.example.domain.model.Servicio
import com.example.domain.model.TipoActividad
import com.example.domain.repository.ActividadRepository
import com.example.domain.repository.ClienteRepository
import com.example.domain.repository.ServicioRepository

class DuplicarServicioUseCase(
    private val servicioRepository: ServicioRepository,
    private val clienteRepository: ClienteRepository,
    private val actividadRepository: ActividadRepository
) {
    operator fun invoke(servicioId: String): Servicio? {
        val original = servicioRepository.getServicioById(servicioId) ?: return null
        val duplicated = servicioRepository.duplicarServicio(servicioId) ?: return null

        val clienteNombre = clienteRepository.getClienteById(duplicated.clienteId)?.nombre ?: "Cliente"
        actividadRepository.logActividad(
            TipoActividad.SERVICIO_CREADO,
            "Servicio duplicado: ${duplicated.nombre}",
            "Copia creada a partir de ${original.nombre} para $clienteNombre."
        )
        return duplicated
    }
}
