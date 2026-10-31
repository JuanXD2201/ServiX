package com.example.domain.repository

import com.example.domain.model.EstadoServicio
import com.example.domain.model.PrioridadServicio
import com.example.domain.model.Servicio
import kotlinx.coroutines.flow.StateFlow

interface ServicioRepository {
    val servicios: StateFlow<List<Servicio>>

    fun getServicioById(servicioId: String): Servicio?
    fun getServiciosByCliente(clienteId: String): List<Servicio>
    fun addServicio(
        clienteId: String,
        nombre: String,
        descripcion: String,
        precio: Double,
        fecha: String,
        estado: EstadoServicio,
        prioridad: PrioridadServicio,
        observaciones: String,
        categoria: String,
        fechaEntrega: String,
        esFavorito: Boolean = false
    ): Servicio
    fun updateServicio(servicio: Servicio)
    fun toggleServicioFavorito(servicioId: String)
    fun duplicarServicio(servicioId: String): Servicio?
    fun updateServicioEstado(servicioId: String, nuevoEstado: EstadoServicio)
    fun deleteServicio(servicioId: String): Servicio?
    fun restaurarServicio(servicio: Servicio)
}
