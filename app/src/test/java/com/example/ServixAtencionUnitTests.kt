package com.example

import com.example.data.mock.MockAtencionRepository
import com.example.domain.model.EstadoAtencion
import com.example.domain.model.PrioridadAtencion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ServixAtencionUnitTests {

    private lateinit var atencionRepository: MockAtencionRepository

    @Before
    fun setUp() {
        atencionRepository = MockAtencionRepository()
    }

    @Test
    fun testCrearSolicitudAtencion() {
        val sol = atencionRepository.crearSolicitud(
            clienteId = "cli_999",
            clienteNombre = "Andrés Gómez",
            clienteCorreo = "andres@correo.com",
            motivo = "Problema con orden",
            mensaje = "El técnico no llegó a la hora acordada",
            prioridad = PrioridadAtencion.URGENTE
        )

        assertNotNull(sol.id)
        assertEquals("cli_999", sol.clienteId)
        assertEquals(PrioridadAtencion.URGENTE, sol.prioridad)
        assertEquals(EstadoAtencion.PENDIENTE, sol.estado)
        assertTrue(atencionRepository.solicitudes.value.any { it.id == sol.id })
    }

    @Test
    fun testTomarSolicitudPorAdmin() {
        val sol = atencionRepository.crearSolicitud(
            clienteId = "cli_100",
            clienteNombre = "Diana López",
            clienteCorreo = "diana@correo.com",
            motivo = "Cotización",
            mensaje = "Requiero presupuesto para 3 aires",
            prioridad = PrioridadAtencion.NORMAL
        )

        atencionRepository.tomarSolicitud(sol.id, "Carlos Vega (Admin)")
        val actualizada = atencionRepository.getSolicitudById(sol.id)

        assertNotNull(actualizada)
        assertEquals(EstadoAtencion.EN_ATENCION, actualizada?.estado)
        assertEquals("Carlos Vega (Admin)", actualizada?.atendidoPor)
    }

    @Test
    fun testResponderSolicitudPorAdmin() {
        val sol = atencionRepository.crearSolicitud(
            clienteId = "cli_101",
            clienteNombre = "Felipe Ruiz",
            clienteCorreo = "felipe@correo.com",
            motivo = "Duda sobre garantía",
            mensaje = "¿Cuánto tiempo cubre la instalación?",
            prioridad = PrioridadAtencion.IMPORTANTE
        )

        atencionRepository.responderSolicitud(
            solicitudId = sol.id,
            respuesta = "La garantía cubre 6 meses a partir de la entrega.",
            adminNombre = "Carlos Vega"
        )
        val actualizada = atencionRepository.getSolicitudById(sol.id)

        assertNotNull(actualizada)
        assertEquals("La garantía cubre 6 meses a partir de la entrega.", actualizada?.respuestaAdmin)
        assertEquals(EstadoAtencion.EN_ATENCION, actualizada?.estado)
    }

    @Test
    fun testResolverSolicitud() {
        val sol = atencionRepository.crearSolicitud(
            clienteId = "cli_102",
            clienteNombre = "Marta Silva",
            clienteCorreo = "marta@correo.com",
            motivo = "Cambio de fecha",
            mensaje = "Favor reagendar para el viernes",
            prioridad = PrioridadAtencion.NORMAL
        )

        atencionRepository.resolverSolicitud(sol.id, "Reagendado para el viernes 10am")
        val resuelta = atencionRepository.getSolicitudById(sol.id)

        assertNotNull(resuelta)
        assertEquals(EstadoAtencion.RESUELTO, resuelta?.estado)
        assertNotNull(resuelta?.fechaResolucion)
    }
}
