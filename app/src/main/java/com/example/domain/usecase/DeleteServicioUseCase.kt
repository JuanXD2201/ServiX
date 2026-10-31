package com.example.domain.usecase

import com.example.domain.model.Servicio
import com.example.domain.model.TipoActividad
import com.example.domain.repository.ActividadRepository
import com.example.domain.repository.ServicioRepository

class DeleteServicioUseCase(
    private val servicioRepository: ServicioRepository,
    private val actividadRepository: ActividadRepository
) {
    operator fun invoke(servicioId: String): Servicio? {
        val target = servicioRepository.deleteServicio(servicioId)
        val nombre = target?.nombre ?: "Servicio"
        actividadRepository.logActividad(
            TipoActividad.SERVICIO_CANCELADO,
            "Servicio eliminado",
            "\"$nombre\" fue retirado del sistema."
        )
        return target
    }
}
