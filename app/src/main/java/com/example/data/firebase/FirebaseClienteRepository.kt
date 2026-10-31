package com.example.data.firebase

import android.util.Log
import com.example.data.firebase.dto.ClienteDto
import com.example.data.firebase.dto.ServicioDto
import com.example.domain.model.Cliente
import com.example.domain.model.Servicio
import com.example.domain.repository.ClienteRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class FirebaseClienteRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : ClienteRepository {

    private val _clientes = MutableStateFlow<List<Cliente>>(emptyList())
    override val clientes: StateFlow<List<Cliente>> = _clientes.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    private fun getClientesCollection(uid: String) = firestore.collection("usuarios").document(uid).collection("clientes")
    private fun getServiciosCollection(uid: String) = firestore.collection("usuarios").document(uid).collection("servicios")

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                listenToClientes(user.uid)
            } else {
                listenerRegistration?.remove()
                listenerRegistration = null
                _clientes.value = emptyList()
            }
        }
    }

    private fun listenToClientes(uid: String) {
        listenerRegistration?.remove()
        listenerRegistration = getClientesCollection(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FirebaseClienteRepo", "Error listening to clientes", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ClienteDto::class.java)?.toDomain()
                }
                _clientes.value = list
            }
        }
    }

    override fun getClienteById(clienteId: String): Cliente? {
        return _clientes.value.find { it.clienteId == clienteId }
    }

    override fun addCliente(
        nombre: String,
        documento: String,
        telefono: String,
        correo: String,
        direccion: String,
        notas: String,
        esFavorito: Boolean
    ): Cliente {
        val newId = "cli-" + UUID.randomUUID().toString().take(8)
        val nuevo = Cliente(
            clienteId = newId,
            nombre = nombre,
            documento = documento,
            telefono = telefono,
            correo = correo,
            direccion = direccion,
            notas = notas,
            fechaRegistro = System.currentTimeMillis(),
            activo = true,
            esFavorito = esFavorito
        )

        val uid = auth.currentUser?.uid
        if (uid != null) {
            _clientes.value = _clientes.value + nuevo
            externalScope.launch {
                try {
                    getClientesCollection(uid).document(newId).set(ClienteDto.fromDomain(nuevo))
                } catch (e: Exception) {
                    Log.e("FirebaseClienteRepo", "Error creating cliente in Firestore", e)
                }
            }
        }
        return nuevo
    }

    override fun updateCliente(cliente: Cliente) {
        val uid = auth.currentUser?.uid ?: return
        _clientes.value = _clientes.value.map { if (it.clienteId == cliente.clienteId) cliente else it }
        externalScope.launch {
            try {
                getClientesCollection(uid).document(cliente.clienteId).set(ClienteDto.fromDomain(cliente))
            } catch (e: Exception) {
                Log.e("FirebaseClienteRepo", "Error updating cliente in Firestore", e)
            }
        }
    }

    override fun toggleClienteFavorito(clienteId: String) {
        val actual = getClienteById(clienteId) ?: return
        val actualizado = actual.copy(esFavorito = !actual.esFavorito)
        updateCliente(actualizado)
    }

    override fun deleteCliente(clienteId: String): Pair<Cliente?, List<Servicio>> {
        val uid = auth.currentUser?.uid ?: return Pair(null, emptyList())
        val cliente = getClienteById(clienteId)
        
        _clientes.value = _clientes.value.filter { it.clienteId != clienteId }

        externalScope.launch {
            try {
                getClientesCollection(uid).document(clienteId).delete()
                getServiciosCollection(uid).whereEqualTo("clienteId", clienteId).get()
                    .addOnSuccessListener { querySnap ->
                        for (doc in querySnap.documents) {
                            doc.reference.delete()
                        }
                    }
            } catch (e: Exception) {
                Log.e("FirebaseClienteRepo", "Error deleting cliente in Firestore", e)
            }
        }
        return Pair(cliente, emptyList()) // Idealmente deberíamos devolver los servicios eliminados
    }

    override fun restaurarCliente(cliente: Cliente, serviciosAsociados: List<Servicio>) {
        val uid = auth.currentUser?.uid ?: return
        _clientes.value = _clientes.value + cliente

        externalScope.launch {
            try {
                getClientesCollection(uid).document(cliente.clienteId).set(ClienteDto.fromDomain(cliente))
                for (s in serviciosAsociados) {
                    getServiciosCollection(uid).document(s.servicioId).set(ServicioDto.fromDomain(s))
                }
            } catch (e: Exception) {
                Log.e("FirebaseClienteRepo", "Error restoring cliente in Firestore", e)
            }
        }
    }
}
