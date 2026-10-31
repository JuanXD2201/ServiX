package com.example

import com.example.data.mock.MockClienteRepository
import com.example.data.mock.MockServicioRepository
import com.example.data.mock.MockDataStore
import com.example.domain.model.Cliente
import com.example.domain.model.EstadoServicio
import com.example.domain.model.PrioridadServicio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ServixUnitTests {

  private lateinit var clienteRepository: MockClienteRepository
  private lateinit var servicioRepository: MockServicioRepository

  @Before
  fun setUp() {
    MockDataStore.loadInitialData()
    clienteRepository = MockClienteRepository()
    servicioRepository = MockServicioRepository()
  }

  @Test
  fun testInitialDataLoaded() {
    val clientes = clienteRepository.clientes.value
    val servicios = servicioRepository.servicios.value

    assertTrue("Clientes should not be empty", clientes.isNotEmpty())
    assertTrue("Servicios should not be empty", servicios.isNotEmpty())
  }

  @Test
  fun testClienteInitials() {
    val singleName = Cliente(clienteId = "1", nombre = "Mariana")
    assertEquals("MA", singleName.iniciales)

    val doubleName = Cliente(clienteId = "2", nombre = "Carlos Vega")
    assertEquals("CV", doubleName.iniciales)

    val tripleName = Cliente(clienteId = "3", nombre = "Alejandro Gómez Silva")
    assertEquals("AG", tripleName.iniciales)
  }

  @Test
  fun testAddCliente() {
    val nuevo = clienteRepository.addCliente(
      nombre = "Roberto Pérez",
      documento = "123456",
      telefono = "+57 300 111 2233",
      correo = "roberto@test.com",
      direccion = "Calle 100",
      notas = "Nota de prueba"
    )

    assertNotNull(nuevo.clienteId)
    val encontrado = clienteRepository.getClienteById(nuevo.clienteId)
    assertNotNull(encontrado)
    assertEquals("Roberto Pérez", encontrado?.nombre)
  }

  @Test
  fun testAddAndChangeStatusServicio() {
    val clientes = clienteRepository.clientes.value
    val firstClient = clientes.first()

    val servicio = servicioRepository.addServicio(
      clienteId = firstClient.clienteId,
      nombre = "Instalación Eléctrica",
      descripcion = "Revisión general",
      precio = 180000.0,
      fecha = "06/09/2026",
      estado = EstadoServicio.PENDIENTE,
      prioridad = PrioridadServicio.MEDIA,
      observaciones = "Sin notas",
      categoria = "General",
      fechaEntrega = ""
    )

    assertEquals(EstadoServicio.PENDIENTE, servicio.estado)

    servicioRepository.updateServicioEstado(servicio.servicioId, EstadoServicio.EN_PROCESO)
    val enProceso = servicioRepository.getServicioById(servicio.servicioId)
    assertEquals(EstadoServicio.EN_PROCESO, enProceso?.estado)

    servicioRepository.updateServicioEstado(servicio.servicioId, EstadoServicio.COMPLETADO)
    val completado = servicioRepository.getServicioById(servicio.servicioId)
    assertEquals(EstadoServicio.COMPLETADO, completado?.estado)
  }

  @Test
  fun testDeleteClienteCascade() {
    val nuevo = clienteRepository.addCliente(
      nombre = "Cliente Temporal",
      documento = "",
      telefono = "",
      correo = "",
      direccion = "",
      notas = ""
    )
    val servicio = servicioRepository.addServicio(
      clienteId = nuevo.clienteId,
      nombre = "Trabajo Temporal",
      descripcion = "",
      precio = 50000.0,
      fecha = "",
      estado = EstadoServicio.PENDIENTE,
      prioridad = PrioridadServicio.MEDIA,
      observaciones = "",
      categoria = "General",
      fechaEntrega = ""
    )

    val srvAntes = servicioRepository.getServiciosByCliente(nuevo.clienteId)
    assertEquals(1, srvAntes.size)

    clienteRepository.deleteCliente(nuevo.clienteId)

    val clienteDespues = clienteRepository.getClienteById(nuevo.clienteId)
    val srvDespues = servicioRepository.getServiciosByCliente(nuevo.clienteId)

    assertEquals(null, clienteDespues)
    assertEquals(0, srvDespues.size)
  }
}
