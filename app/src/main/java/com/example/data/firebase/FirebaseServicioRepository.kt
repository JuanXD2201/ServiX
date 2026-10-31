package com.example.data.firebase

import android.util.Log
import com.example.data.firebase.dto.ServicioDto
import com.example.domain.model.EstadoServicio
import com.example.domain.model.PrioridadServicio
import com.example.domain.model.Servicio
import com.example.domain.repository.ServicioRepository
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

class FirebaseServicioRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : ServicioRepository {

    private val _servicios = MutableStateFlow<List<Servicio>>(emptyList())
    override val servicios: StateFlow<List<Servicio>> = _servicios.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    private fun getServiciosCollection(uid: String) = firestore.collection("usuarios").document(uid).collection("servicios")

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                listenToServicios(user.uid)
            } else {
                listenerRegistration?.remove()
                listenerRegistration = null
                _servicios.value = emptyList()
            }
        }
    }

    private fun listenToServicios(uid: String) {
        listenerRegistration?.remove()
        listenerRegistration = getServiciosCollection(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FirebaseServicioRepo", "Error listening to servicios", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ServicioDto::class.java)?.toDomain()
                }
                _servicios.value = list
            }
        }
    }

    override fun getServicioById(servicioId: String): Servicio? {
        return _servicios.value.find { it.servicioId == servicioId }
    }

    override fun getServiciosByCliente(clienteId: String): List<Servicio> {
        return _servicios.value.filter { it.clienteId == clienteId }
    }

    override fun addServicio(
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
        esFavorito: Boolean
    ): Servicio {
        val newId = "srv-" + UUID.randomUUID().toString().take(8)
        val nuevo = Servicio(
            servicioId = newId,
            clienteId = clienteId,
            nombre = nombre,
            descripcion = descripcion,
            categoria = categoria,
            precio = precio,
            fecha = fecha,
            fechaEntrega = fechaEntrega,
            estado = estado,
            prioridad = prioridad,
            observaciones = observaciones,
            esFavorito = esFavorito,
            timestamp = System.currentTimeMillis()
        )

        val uid = auth.currentUser?.uid
        if (uid != null) {
            _servicios.value = _servicios.value + nuevo
            externalScope.launch {
                try {
                    getServiciosCollection(uid).document(newId).set(ServicioDto.fromDomain(nuevo))
                } catch (e: Exception) {
                    Log.e("FirebaseServicioRepo", "Error adding servicio to Firestore", e)
                }
            }
        }
        return nuevo
    }

    override fun updateServicio(servicio: Servicio) {
        val uid = auth.currentUser?.uid ?: return
        _servicios.value = _servicios.value.map { if (it.servicioId == servicio.servicioId) servicio else it }
        externalScope.launch {
            try {
                getServiciosCollection(uid).document(servicio.servicioId).set(ServicioDto.fromDomain(servicio))
            } catch (e: Exception) {
                Log.e("FirebaseServicioRepo", "Error updating servicio in Firestore", e)
            }
        }
    }

    override fun toggleServicioFavorito(servicioId: String) {
        val actual = getServicioById(servicioId) ?: return
        val actualizado = actual.copy(esFavorito = !actual.esFavorito)
        updateServicio(actualizado)
    }

    override fun duplicarServicio(servicioId: String): Servicio? {
        val original = getServicioById(servicioId) ?: return null
        return addServicio(
            clienteId = original.clienteId,
            nombre = "${original.nombre} (Copia)",
            descripcion = original.descripcion,
            precio = original.precio,
            fecha = original.fecha,
            estado = EstadoServicio.PENDIENTE,
            prioridad = original.prioridad,
            observaciones = original.observaciones,
            categoria = original.categoria,
            fechaEntrega = original.fechaEntrega,
            esFavorito = false
        )
    }

    override fun updateServicioEstado(servicioId: String, nuevoEstado: EstadoServicio) {
        val actual = getServicioById(servicioId) ?: return
        val actualizado = actual.copy(estado = nuevoEstado)
        updateServicio(actualizado)
    }

    override fun deleteServicio(servicioId: String): Servicio? {
        val uid = auth.currentUser?.uid ?: return null
        val servicio = getServicioById(servicioId)
        
        _servicios.value = _servicios.value.filter { it.servicioId != servicioId }

        externalScope.launch {
            try {
                getServiciosCollection(uid).document(servicioId).delete()
            } catch (e: Exception) {
                Log.e("FirebaseServicioRepo", "Error deleting servicio in Firestore", e)
            }
        }
        return servicio
    }

    override fun restaurarServicio(servicio: Servicio) {
        val uid = auth.currentUser?.uid ?: return
        _servicios.value = _servicios.value + servicio

        externalScope.launch {
            try {
                getServiciosCollection(uid).document(servicio.servicioId).set(ServicioDto.fromDomain(servicio))
            } catch (e: Exception) {
                Log.e("FirebaseServicioRepo", "Error restoring servicio in Firestore", e)
            }
        }
    }
}
