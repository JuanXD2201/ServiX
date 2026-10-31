package com.example.data.mock

import com.example.domain.model.EstadoAtencion
import com.example.domain.model.PrioridadAtencion
import com.example.domain.model.SolicitudAtencion
import com.example.domain.repository.AtencionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class MockAtencionRepository : AtencionRepository {

    private val _solicitudes = MutableStateFlow<List<SolicitudAtencion>>(
        listOf(
            SolicitudAtencion(
                id = "sol-101",
                clienteId = "cli-001",
                clienteNombre = "Mariana Ríos Restrepo",
                clienteCorreo = "mariana.rios@email.com",
                fechaHora = System.currentTimeMillis() - 3600000L,
                motivo = "Consulta sobre tiempo de entrega de repuestos",
                mensaje = "Quisiera confirmar si el técnico ya recibió el repuesto para la unidad de climatización.",
                prioridad = PrioridadAtencion.NORMAL,
                estado = EstadoAtencion.PENDIENTE
            ),
            SolicitudAtencion(
                id = "sol-102",
                clienteId = "cli-002",
                clienteNombre = "Alejandro Gómez Henao",
                clienteCorreo = "alejandro.gomez@empresa.com",
                fechaHora = System.currentTimeMillis() - 7200000L,
                motivo = "Reporte de retraso en visita técnica",
                mensaje = "El técnico tenía programada la visita a las 2:00 PM y aún no ha llegado a la sede.",
                prioridad = PrioridadAtencion.IMPORTANTE,
                estado = EstadoAtencion.EN_ATENCION,
                atendidoPor = "Carlos Vega (Vendedor)"
            )
        )
    )
    override val solicitudes: StateFlow<List<SolicitudAtencion>> = _solicitudes.asStateFlow()

    override fun crearSolicitud(
        clienteId: String,
        clienteNombre: String,
        clienteCorreo: String,
        motivo: String,
        mensaje: String,
        prioridad: PrioridadAtencion
    ): SolicitudAtencion {
        val nueva = SolicitudAtencion(
            id = "sol-" + UUID.randomUUID().toString().take(6),
            clienteId = clienteId,
            clienteNombre = clienteNombre,
            clienteCorreo = clienteCorreo,
            motivo = motivo,
            mensaje = mensaje,
            prioridad = prioridad,
            estado = EstadoAtencion.PENDIENTE,
            fechaHora = System.currentTimeMillis()
        )
        _solicitudes.value = listOf(nueva) + _solicitudes.value
        return nueva
    }

    override fun tomarSolicitud(solicitudId: String, adminNombre: String) {
        _solicitudes.value = _solicitudes.value.map {
            if (it.id == solicitudId) {
                it.copy(estado = EstadoAtencion.EN_ATENCION, atendidoPor = adminNombre)
            } else it
        }
    }

    override fun responderSolicitud(solicitudId: String, respuesta: String, adminNombre: String) {
        _solicitudes.value = _solicitudes.value.map {
            if (it.id == solicitudId) {
                it.copy(
                    respuestaAdmin = respuesta,
                    atendidoPor = adminNombre,
                    estado = EstadoAtencion.EN_ATENCION
                )
            } else it
        }
    }

    override fun resolverSolicitud(solicitudId: String, notasResolucion: String) {
        _solicitudes.value = _solicitudes.value.map {
            if (it.id == solicitudId) {
                it.copy(
                    estado = EstadoAtencion.RESUELTO,
                    fechaResolucion = System.currentTimeMillis(),
                    respuestaAdmin = if (notasResolucion.isNotBlank()) notasResolucion else it.respuestaAdmin
                )
            } else it
        }
    }

    override fun getSolicitudesByCliente(clienteId: String): List<SolicitudAtencion> {
        return _solicitudes.value.filter { it.clienteId == clienteId }
    }

    override fun getSolicitudById(solicitudId: String): SolicitudAtencion? {
        return _solicitudes.value.find { it.id == solicitudId }
    }
}
