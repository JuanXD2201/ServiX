package com.example.domain.repository

import com.example.domain.model.EstadoAtencion
import com.example.domain.model.PrioridadAtencion
import com.example.domain.model.SolicitudAtencion
import kotlinx.coroutines.flow.StateFlow

interface AtencionRepository {
    val solicitudes: StateFlow<List<SolicitudAtencion>>

    fun crearSolicitud(
        clienteId: String,
        clienteNombre: String,
        clienteCorreo: String,
        motivo: String,
        mensaje: String,
        prioridad: PrioridadAtencion
    ): SolicitudAtencion

    fun tomarSolicitud(solicitudId: String, adminNombre: String)

    fun responderSolicitud(solicitudId: String, respuesta: String, adminNombre: String)

    fun resolverSolicitud(solicitudId: String, notasResolucion: String = "")

    fun getSolicitudesByCliente(clienteId: String): List<SolicitudAtencion>

    fun getSolicitudById(solicitudId: String): SolicitudAtencion?
}
