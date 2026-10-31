package com.example.domain.usecase

import com.example.domain.model.Servicio
import com.example.domain.model.TipoActividad
import com.example.domain.repository.ActividadRepository
import com.example.domain.repository.ServicioRepository

class RestaurarServicioUseCase(
    private val servicioRepository: ServicioRepository,
    private val actividadRepository: ActividadRepository
) {
    operator fun invoke(servicio: Servicio) {
        servicioRepository.restaurarServicio(servicio)
        actividadRepository.logActividad(
            TipoActividad.SERVICIO_CREADO,
            "Servicio restaurado: ${servicio.nombre}",
            "Se recuperó el servicio eliminado."
        )
    }
}
