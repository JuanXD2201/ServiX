package com.example.domain.usecase

import com.example.domain.model.EstadoServicio
import com.example.domain.model.Notificacion
import com.example.domain.model.PrioridadServicio
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

class AddServicioUseCase(
    private val servicioRepository: ServicioRepository,
    private val clienteRepository: ClienteRepository,
    private val actividadRepository: ActividadRepository,
    private val notificationRepository: NotificationRepository,
    private val makeWebhookRepository: MakeWebhookRepository? = null,
    private val configRepository: ConfigRepository? = null,
    private val authRepository: AuthRepository? = null
) {
    operator fun invoke(
        clienteId: String,
        nombre: String,
        descripcion: String = "",
        precio: Double = 0.0,
        fecha: String = "",
        estado: EstadoServicio = EstadoServicio.PENDIENTE,
        prioridad: PrioridadServicio = PrioridadServicio.MEDIA,
        observaciones: String = "",
        categoria: String = "General",
        fechaEntrega: String = "",
        esFavorito: Boolean = false
    ): Servicio {
        val newServicio = servicioRepository.addServicio(
            clienteId, nombre, descripcion, precio, fecha, estado, prioridad, observaciones, categoria, fechaEntrega, esFavorito
        )
        val cliente = clienteRepository.getClienteById(clienteId)
        val clienteNombre = cliente?.nombre ?: "Cliente"
        actividadRepository.logActividad(
            TipoActividad.SERVICIO_CREADO,
            "Nuevo servicio: ${newServicio.nombre}",
            "Asignado a $clienteNombre • Prioridad: ${newServicio.prioridad.displayName} • Estado: ${newServicio.estado.displayName}"
        )
        if (prioridad == PrioridadServicio.URGENTE) {
            notificationRepository.addNotificacion(
                Notificacion(
                    id = UUID.randomUUID().toString(),
                    tipo = TipoNotificacion.SERVICIO_URGENTE,
                    titulo = "Servicio Urgente Creado",
                    descripcion = "${newServicio.nombre} para $clienteNombre requiere atención prioritaria.",
                    fecha = System.currentTimeMillis(),
                    leida = false,
                    referenciaId = newServicio.servicioId,
                    tipoReferencia = "servicio"
                )
            )
        }

        // Notificar automáticamente a Make
        val evento = if (newServicio.estado == EstadoServicio.COMPLETADO) "SERVICIO_COMPLETADO" else "SERVICIO_CREADO"
        makeWebhookRepository?.sendServicioEvent(
            evento = evento,
            servicio = newServicio,
            cliente = cliente,
            tecnico = configRepository?.userProfile?.value,
            usuarioId = authRepository?.currentUser?.value?.uid
        )

        return newServicio
    }
}
