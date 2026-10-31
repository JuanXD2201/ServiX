package com.example.domain.usecase

import com.example.domain.model.EstadoServicio
import com.example.domain.model.Notificacion
import com.example.domain.model.Servicio
import com.example.domain.model.TipoActividad
import com.example.domain.model.TipoNotificacion
import com.example.domain.repository.ActividadRepository
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.ClienteRepository
import com.example.domain.repository.ConfigRepository
import com.example.domain.repository.MakeWebhookRepository
import com.example.domain.repository.NotificationRepository
import com.example.domain.repository.ServicioRepository
import java.util.UUID

class UpdateServicioUseCase(
    private val servicioRepository: ServicioRepository,
    private val actividadRepository: ActividadRepository,
    private val clienteRepository: ClienteRepository? = null,
    private val configRepository: ConfigRepository? = null,
    private val authRepository: AuthRepository? = null,
    private val makeWebhookRepository: MakeWebhookRepository? = null,
    private val notificationRepository: NotificationRepository? = null
) {
    operator fun invoke(servicio: Servicio) {
        val servicioAnterior = servicioRepository.getServicioById(servicio.servicioId)
        servicioRepository.updateServicio(servicio)
        actividadRepository.logActividad(
            TipoActividad.SERVICIO_ACTUALIZADO,
            "Servicio modificado: ${servicio.nombre}",
            "Prioridad: ${servicio.prioridad.displayName} • Estado: ${servicio.estado.displayName}"
        )

        if (servicio.estado == EstadoServicio.COMPLETADO) {
            if (servicioAnterior?.estado != EstadoServicio.COMPLETADO) {
                notificationRepository?.addNotificacion(
                    Notificacion(
                        id = UUID.randomUUID().toString(),
                        tipo = TipoNotificacion.SERVICIO_COMPLETADO,
                        titulo = "Servicio Finalizado",
                        descripcion = "${servicio.nombre} ha sido marcado como completado.",
                        fecha = System.currentTimeMillis(),
                        leida = false,
                        referenciaId = servicio.servicioId,
                        tipoReferencia = "servicio"
                    )
                )
            }

            // Notificar a Make automáticamente al completar servicio
            val cliente = clienteRepository?.getClienteById(servicio.clienteId)
            makeWebhookRepository?.sendServicioEvent(
                evento = "SERVICIO_COMPLETADO",
                servicio = servicio,
                cliente = cliente,
                tecnico = configRepository?.userProfile?.value,
                usuarioId = authRepository?.currentUser?.value?.uid
            )
        }
    }
}
