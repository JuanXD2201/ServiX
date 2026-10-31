package com.example.domain.model

import java.util.UUID

enum class PrioridadAtencion(val displayName: String) {
    NORMAL("Normal"),
    IMPORTANTE("Importante"),
    URGENTE("Urgente");

    companion object {
        fun fromString(v: String?): PrioridadAtencion {
            return entries.find { it.name.equals(v, ignoreCase = true) } ?: NORMAL
        }
    }
}

enum class EstadoAtencion(val displayName: String) {
    PENDIENTE("Pendiente"),
    EN_ATENCION("En Atención"),
    RESUELTO("Resuelto");

    companion object {
        fun fromString(v: String?): EstadoAtencion {
            return entries.find { it.name.equals(v, ignoreCase = true) } ?: PENDIENTE
        }
    }
}

enum class IntencionUsuario(val displayName: String) {
    CONSULTA_GENERAL("Consulta General"),
    CONSULTA_SERVICIO("Consulta de Servicio"),
    CONSULTA_ESTADO("Consulta de Estado"),
    NUEVA_SOLICITUD("Nueva Solicitud"),
    PROBLEMA("Problema con Servicio"),
    QUEJA("Queja o Reclamo"),
    CANCELACION("Cancelación"),
    SOLICITUD_HUMANA("Solicitud de Atención Humana"),
    ERROR_APP("Error en la Aplicación"),
    OTRO("Otro")
}

enum class SenderType {
    CLIENTE,
    AGENTE_IA,
    ADMINISTRADOR_VENDEDOR,
    SISTEMA
}

enum class EstadoChat(val displayName: String) {
    ATENCION_IA("Atención IA"),
    ESPERANDO_ADMINISTRADOR("Esperando Administrador"),
    ATENCION_HUMANA("Atención Humana"),
    RESUELTA("Resuelta")
}

data class SolicitudAtencion(
    val id: String = UUID.randomUUID().toString(),
    val clienteId: String,
    val clienteNombre: String,
    val clienteCorreo: String,
    val fechaHora: Long = System.currentTimeMillis(),
    val motivo: String,
    val mensaje: String,
    val prioridad: PrioridadAtencion = PrioridadAtencion.NORMAL,
    val estado: EstadoAtencion = EstadoAtencion.PENDIENTE,
    val respuestaAdmin: String = "",
    val atendidoPor: String = "",
    val fechaResolucion: Long? = null
)
