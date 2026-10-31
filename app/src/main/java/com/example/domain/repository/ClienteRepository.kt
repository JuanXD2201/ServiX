package com.example.domain.repository

import com.example.domain.model.Cliente
import com.example.domain.model.Servicio
import kotlinx.coroutines.flow.StateFlow

interface ClienteRepository {
    val clientes: StateFlow<List<Cliente>>

    fun getClienteById(clienteId: String): Cliente?
    fun addCliente(
        nombre: String,
        documento: String,
        telefono: String,
        correo: String,
        direccion: String,
        notas: String,
        esFavorito: Boolean = false
    ): Cliente
    fun updateCliente(cliente: Cliente)
    fun toggleClienteFavorito(clienteId: String)
    fun deleteCliente(clienteId: String): Pair<Cliente?, List<Servicio>>
    fun restaurarCliente(cliente: Cliente, serviciosAsociados: List<Servicio>)
}
