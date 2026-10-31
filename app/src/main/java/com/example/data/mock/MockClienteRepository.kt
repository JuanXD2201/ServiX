package com.example.data.mock

import com.example.domain.model.Cliente
import com.example.domain.model.Servicio
import com.example.domain.repository.ClienteRepository
import kotlinx.coroutines.flow.StateFlow

class MockClienteRepository : ClienteRepository {
    override val clientes: StateFlow<List<Cliente>> = MockDataStore.clientes

    override fun getClienteById(clienteId: String): Cliente? = MockDataStore.getClienteById(clienteId)

    override fun addCliente(nombre: String, documento: String, telefono: String, correo: String, direccion: String, notas: String, esFavorito: Boolean): Cliente {
        return MockDataStore.addCliente(nombre, documento, telefono, correo, direccion, notas, esFavorito)
    }

    override fun updateCliente(cliente: Cliente) = MockDataStore.updateCliente(cliente)

    override fun toggleClienteFavorito(clienteId: String) = MockDataStore.toggleClienteFavorito(clienteId)

    override fun deleteCliente(clienteId: String): Pair<Cliente?, List<Servicio>> {
        return MockDataStore.deleteCliente(clienteId)
    }
    override fun restaurarCliente(cliente: Cliente, serviciosAsociados: List<Servicio>) = MockDataStore.restaurarCliente(cliente, serviciosAsociados)
}
