package com.example.data.mock

import com.example.domain.model.Servicio
import com.example.domain.model.EstadoServicio
import com.example.domain.model.PrioridadServicio
import com.example.domain.repository.ServicioRepository
import kotlinx.coroutines.flow.StateFlow

class MockServicioRepository : ServicioRepository {
    override val servicios: StateFlow<List<Servicio>> = MockDataStore.servicios

    override fun getServicioById(servicioId: String): Servicio? = MockDataStore.getServicioById(servicioId)

    override fun getServiciosByCliente(clienteId: String): List<Servicio> = MockDataStore.getServiciosByCliente(clienteId)

    override fun addServicio(clienteId: String, nombre: String, descripcion: String, precio: Double, fecha: String, estado: EstadoServicio, prioridad: PrioridadServicio, observaciones: String, categoria: String, fechaEntrega: String, esFavorito: Boolean): Servicio {
        return MockDataStore.addServicio(clienteId, nombre, descripcion, precio, fecha, estado, prioridad, observaciones, categoria, fechaEntrega, esFavorito)
    }

    override fun updateServicio(servicio: Servicio) = MockDataStore.updateServicio(servicio)

    override fun toggleServicioFavorito(servicioId: String) = MockDataStore.toggleServicioFavorito(servicioId)

    override fun duplicarServicio(servicioId: String): Servicio? = MockDataStore.duplicarServicio(servicioId)

    override fun updateServicioEstado(servicioId: String, nuevoEstado: EstadoServicio) = MockDataStore.updateServicioEstado(servicioId, nuevoEstado)

    override fun deleteServicio(servicioId: String): Servicio? {
        return MockDataStore.deleteServicio(servicioId)
    }
    override fun restaurarServicio(servicio: Servicio) = MockDataStore.restaurarServicio(servicio)
}
