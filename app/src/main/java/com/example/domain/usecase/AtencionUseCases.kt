package com.example.domain.usecase

import com.example.domain.model.Notificacion
import com.example.domain.model.PrioridadAtencion
import com.example.domain.model.SolicitudAtencion
import com.example.domain.model.TipoActividad
import com.example.domain.model.TipoNotificacion
import com.example.domain.repository.ActividadRepository
import com.example.domain.repository.AtencionRepository
import com.example.domain.repository.NotificationRepository
import java.util.UUID

class CrearSolicitudAtencionUseCase(
    private val atencionRepository: AtencionRepository,
    private val notificationRepository: NotificationRepository,
    private val actividadRepository: ActividadRepository
) {
    operator fun invoke(
        clienteId: String,
        clienteNombre: String,
        clienteCorreo: String,
        motivo: String,
        mensaje: String,
        prioridad: PrioridadAtencion
    ): SolicitudAtencion {
        val solicitud = atencionRepository.crearSolicitud(
            clienteId = clienteId,
            clienteNombre = clienteNombre,
            clienteCorreo = clienteCorreo,
            motivo = motivo,
            mensaje = mensaje,
            prioridad = prioridad
        )

        // Registrar en historial de actividades del sistema
        actividadRepository.logActividad(
            TipoActividad.SERVICIO_ACTUALIZADO,
            "Atención requerida: $motivo",
            "Cliente: $clienteNombre • Prioridad: ${prioridad.displayName}"
        )

        // Generar notificación para el Administrador / Vendedor
        notificationRepository.addNotificacion(
            Notificacion(
                id = UUID.randomUUID().toString(),
                tipo = TipoNotificacion.SOLICITUD_ATENCION,
                titulo = "Atención Requerida (${prioridad.displayName})",
                descripcion = "$clienteNombre: \"$motivo\"",
                fecha = System.currentTimeMillis(),
                leida = false,
                referenciaId = solicitud.id,
                tipoReferencia = "atencion"
            )
        )

        return solicitud
    }
}

class TomarSolicitudAtencionUseCase(
    private val atencionRepository: AtencionRepository,
    private val actividadRepository: ActividadRepository
) {
    operator fun invoke(solicitudId: String, adminNombre: String) {
        atencionRepository.tomarSolicitud(solicitudId, adminNombre)
        actividadRepository.logActividad(
            TipoActividad.SERVICIO_ACTUALIZADO,
            "Solicitud tomada en atención",
            "Atendida por: $adminNombre"
        )
    }
}

class ResponderSolicitudAtencionUseCase(
    private val atencionRepository: AtencionRepository,
    private val actividadRepository: ActividadRepository
) {
    operator fun invoke(solicitudId: String, respuesta: String, adminNombre: String) {
        atencionRepository.responderSolicitud(solicitudId, respuesta, adminNombre)
        actividadRepository.logActividad(
            TipoActividad.SERVICIO_ACTUALIZADO,
            "Respuesta enviada a cliente",
            "Por: $adminNombre"
        )
    }
}

class ResolverSolicitudAtencionUseCase(
    private val atencionRepository: AtencionRepository,
    private val actividadRepository: ActividadRepository
) {
    operator fun invoke(solicitudId: String, notasResolucion: String = "") {
        atencionRepository.resolverSolicitud(solicitudId, notasResolucion)
        actividadRepository.logActividad(
            TipoActividad.SERVICIO_COMPLETADO,
            "Solicitud de atención resuelta",
            if (notasResolucion.isNotBlank()) notasResolucion else "Caso cerrado exitosamente"
        )
    }
}
