package com.example

import com.example.data.mock.MockClienteRepository
import com.example.data.mock.MockServicioRepository
import com.example.data.mock.MockDataStore
import com.example.domain.model.EstadoServicio
import com.example.domain.model.PrioridadServicio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ServixRepositoryTest {

  private lateinit var clienteRepository: MockClienteRepository
  private lateinit var servicioRepository: MockServicioRepository

  @Before
  fun setup() {
    MockDataStore.loadInitialData()
    clienteRepository = MockClienteRepository()
    servicioRepository = MockServicioRepository()
  }

  @Test
  fun testInitialDataLoaded() {
    assertTrue(clienteRepository.clientes.value.isNotEmpty())
    assertTrue(servicioRepository.servicios.value.isNotEmpty())
  }

  @Test
  fun testAddAndGetCliente() {
    val initialCount = clienteRepository.clientes.value.size
    val nuevo = clienteRepository.addCliente(
      nombre = "Laura Mendoza",
      documento = "11223344",
      telefono = "315 999 8877",
      correo = "laura@mendoza.com",
      direccion = "Calle 50 #10-20",
      notas = "Prueba de cliente"
    )

    assertEquals(initialCount + 1, clienteRepository.clientes.value.size)
    val fetched = clienteRepository.getClienteById(nuevo.clienteId)
    assertNotNull(fetched)
    assertEquals("Laura Mendoza", fetched?.nombre)
    assertEquals("LM", fetched?.iniciales)
  }

  @Test
  fun testAddAndUpdateServicioStatus() {
    val cliente = clienteRepository.clientes.value.first()
    val servicio = servicioRepository.addServicio(
      clienteId = cliente.clienteId,
      nombre = "Revisión técnica",
      descripcion = "Chequeo preliminar",
      precio = 75000.0,
      fecha = "Hoy",
      estado = EstadoServicio.PENDIENTE,
      prioridad = PrioridadServicio.MEDIA,
      observaciones = "Sin novedad",
      categoria = "General",
      fechaEntrega = ""
    )

    assertEquals(EstadoServicio.PENDIENTE, servicio.estado)

    servicioRepository.updateServicioEstado(servicio.servicioId, EstadoServicio.COMPLETADO)
    val updated = servicioRepository.getServicioById(servicio.servicioId)
    assertEquals(EstadoServicio.COMPLETADO, updated?.estado)
  }
}
