package com.example

import com.example.domain.model.Cliente
import com.example.domain.model.EstadoServicio
import com.example.domain.model.PrioridadServicio
import com.example.domain.model.Servicio
import com.example.domain.model.UserProfile
import com.example.domain.repository.MakeWebhookRepository
import com.example.domain.usecase.AddServicioUseCase
import com.example.domain.usecase.UpdateServicioEstadoUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ServixMakeIntegrationTest {

    private class FakeMakeWebhookRepository : MakeWebhookRepository {
        var lastEvento: String? = null
        var lastServicio: Servicio? = null
        var lastCliente: Cliente? = null

        override fun sendServicioEvent(
            evento: String,
            servicio: Servicio,
            cliente: Cliente?,
            tecnico: UserProfile?,
            usuarioId: String?
        ) {
            lastEvento = evento
            lastServicio = servicio
            lastCliente = cliente
        }

        override suspend fun sendServicioEventSync(
            evento: String,
            servicio: Servicio,
            cliente: Cliente?,
            tecnico: UserProfile?,
            usuarioId: String?
        ): Result<String> {
            lastEvento = evento
            lastServicio = servicio
            lastCliente = cliente
            return Result.success("HTTP 200: OK")
        }
    }

    @Test
    fun testAddServicioDispatchesMakeWebhook() {
        val servicioRepo = com.example.data.mock.MockServicioRepository()
        val clienteRepo = com.example.data.mock.MockClienteRepository()
        val actividadRepo = com.example.data.mock.MockActividadRepository()
        val notifRepo = com.example.data.mock.MockNotificationRepository()
        val configRepo = com.example.data.mock.MockConfigRepository()
        val authRepo = com.example.data.mock.MockAuthRepository()
        val fakeWebhook = FakeMakeWebhookRepository()

        val addServicioUseCase = AddServicioUseCase(
            servicioRepository = servicioRepo,
            clienteRepository = clienteRepo,
            actividadRepository = actividadRepo,
            notificationRepository = notifRepo,
            makeWebhookRepository = fakeWebhook,
            configRepository = configRepo,
            authRepository = authRepo
        )

        val cliente = clienteRepo.clientes.value.first()
        val servicio = addServicioUseCase(
            clienteId = cliente.clienteId,
            nombre = "Prueba Integración Make",
            precio = 180000.0,
            prioridad = PrioridadServicio.ALTA
        )

        assertNotNull(servicio)
        assertEquals("SERVICIO_CREADO", fakeWebhook.lastEvento)
        assertEquals("Prueba Integración Make", fakeWebhook.lastServicio?.nombre)
    }

    @Test
    fun testUpdateServicioEstadoCompletadoDispatchesMakeWebhook() {
        val servicioRepo = com.example.data.mock.MockServicioRepository()
        val clienteRepo = com.example.data.mock.MockClienteRepository()
        val actividadRepo = com.example.data.mock.MockActividadRepository()
        val notifRepo = com.example.data.mock.MockNotificationRepository()
        val configRepo = com.example.data.mock.MockConfigRepository()
        val authRepo = com.example.data.mock.MockAuthRepository()
        val fakeWebhook = FakeMakeWebhookRepository()

        val addServicioUseCase = AddServicioUseCase(
            servicioRepository = servicioRepo,
            clienteRepository = clienteRepo,
            actividadRepository = actividadRepo,
            notificationRepository = notifRepo,
            makeWebhookRepository = fakeWebhook,
            configRepository = configRepo,
            authRepository = authRepo
        )

        val updateEstadoUseCase = UpdateServicioEstadoUseCase(
            servicioRepository = servicioRepo,
            actividadRepository = actividadRepo,
            notificationRepository = notifRepo,
            clienteRepository = clienteRepo,
            configRepository = configRepo,
            authRepository = authRepo,
            makeWebhookRepository = fakeWebhook
        )

        val cliente = clienteRepo.clientes.value.first()
        val servicio = addServicioUseCase(
            clienteId = cliente.clienteId,
            nombre = "Mantenimiento Para Completar",
            precio = 250000.0
        )

        assertEquals("SERVICIO_CREADO", fakeWebhook.lastEvento)

        // Marcar como COMPLETADO
        updateEstadoUseCase(servicio.servicioId, EstadoServicio.COMPLETADO)

        assertEquals("SERVICIO_COMPLETADO", fakeWebhook.lastEvento)
        assertEquals(EstadoServicio.COMPLETADO, fakeWebhook.lastServicio?.estado)
    }

    @Test
    fun testUpdateServicioCompletadoDispatchesMakeWebhook() {
        val servicioRepo = com.example.data.mock.MockServicioRepository()
        val clienteRepo = com.example.data.mock.MockClienteRepository()
        val actividadRepo = com.example.data.mock.MockActividadRepository()
        val notifRepo = com.example.data.mock.MockNotificationRepository()
        val configRepo = com.example.data.mock.MockConfigRepository()
        val authRepo = com.example.data.mock.MockAuthRepository()
        val fakeWebhook = FakeMakeWebhookRepository()

        val addServicioUseCase = AddServicioUseCase(
            servicioRepository = servicioRepo,
            clienteRepository = clienteRepo,
            actividadRepository = actividadRepo,
            notificationRepository = notifRepo,
            makeWebhookRepository = fakeWebhook,
            configRepository = configRepo,
            authRepository = authRepo
        )

        val updateServicioUseCase = com.example.domain.usecase.UpdateServicioUseCase(
            servicioRepository = servicioRepo,
            actividadRepository = actividadRepo,
            clienteRepository = clienteRepo,
            configRepository = configRepo,
            authRepository = authRepo,
            makeWebhookRepository = fakeWebhook,
            notificationRepository = notifRepo
        )

        val cliente = clienteRepo.clientes.value.first()
        val servicio = addServicioUseCase(
            clienteId = cliente.clienteId,
            nombre = "Reparación General",
            precio = 320000.0,
            estado = EstadoServicio.PENDIENTE
        )

        // Actualizar servicio a COMPLETADO
        updateServicioUseCase(servicio.copy(estado = EstadoServicio.COMPLETADO))

        assertEquals("SERVICIO_COMPLETADO", fakeWebhook.lastEvento)
        assertEquals(EstadoServicio.COMPLETADO, fakeWebhook.lastServicio?.estado)
    }
}
