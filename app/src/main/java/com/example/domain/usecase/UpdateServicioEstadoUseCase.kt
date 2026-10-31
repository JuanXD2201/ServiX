package com.example.domain.usecase

import com.example.domain.model.EstadoServicio
import com.example.domain.model.Notificacion
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

class UpdateServicioEstadoUseCase(
    private val servicioRepository: ServicioRepository,
    private val actividadRepository: ActividadRepository,
    private val notificationRepository: NotificationRepository,
    private val clienteRepository: ClienteRepository? = null,
    private val configRepository: ConfigRepository? = null,
    private val authRepository: AuthRepository? = null,
    private val makeWebhookRepository: MakeWebhookRepository? = null
) {
    operator fun invoke(servicioId: String, nuevoEstado: EstadoServicio) {
        servicioRepository.updateServicioEstado(servicioId, nuevoEstado)
        val srv = servicioRepository.getServicioById(servicioId) ?: return

        val actTipo = when (nuevoEstado) {
            EstadoServicio.COMPLETADO -> TipoActividad.SERVICIO_COMPLETADO
            EstadoServicio.CANCELADO -> TipoActividad.SERVICIO_CANCELADO
            else -> TipoActividad.SERVICIO_ACTUALIZADO
        }

        actividadRepository.logActividad(
            actTipo,
            "Estado: ${nuevoEstado.displayName}",
            "Servicio \"${srv.nombre}\" actualizado a ${nuevoEstado.displayName}."
        )

        if (nuevoEstado == EstadoServicio.COMPLETADO) {
            notificationRepository.addNotificacion(
                Notificacion(
                    id = UUID.randomUUID().toString(),
                    tipo = TipoNotificacion.SERVICIO_COMPLETADO,
                    titulo = "Servicio Finalizado",
                    descripcion = "${srv.nombre} ha sido marcado como completado.",
                    fecha = System.currentTimeMillis(),
                    leida = false,
                    referenciaId = srv.servicioId,
                    tipoReferencia = "servicio"
                )
            )

            // Disparar automatización a Make cuando el servicio se completa
            val cliente = clienteRepository?.getClienteById(srv.clienteId)
            makeWebhookRepository?.sendServicioEvent(
                evento = "SERVICIO_COMPLETADO",
                servicio = srv.copy(estado = nuevoEstado),
                cliente = cliente,
                tecnico = configRepository?.userProfile?.value,
                usuarioId = authRepository?.currentUser?.value?.uid
            )
        }
    }
}
