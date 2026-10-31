package com.example.data.firebase

import android.util.Log
import com.example.data.firebase.dto.SolicitudAtencionDto
import com.example.domain.model.EstadoAtencion
import com.example.domain.model.PrioridadAtencion
import com.example.domain.model.SolicitudAtencion
import com.example.domain.repository.AtencionRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class FirebaseAtencionRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : AtencionRepository {

    private val _solicitudes = MutableStateFlow<List<SolicitudAtencion>>(emptyList())
    override val solicitudes: StateFlow<List<SolicitudAtencion>> = _solicitudes.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null
    private val collectionRef = firestore.collection("solicitudes_atencion")

    init {
        listenToSolicitudes()
    }

    private fun listenToSolicitudes() {
        listenerRegistration?.remove()
        listenerRegistration = collectionRef
            .orderBy("fechaHora", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirebaseAtencionRepo", "Error listening to solicitudes", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(SolicitudAtencionDto::class.java)?.toDomain()
                    }
                    _solicitudes.value = list
                }
            }
    }

    override fun crearSolicitud(
        clienteId: String,
        clienteNombre: String,
        clienteCorreo: String,
        motivo: String,
        mensaje: String,
        prioridad: PrioridadAtencion
    ): SolicitudAtencion {
        val newId = "sol-" + UUID.randomUUID().toString().take(8)
        val nueva = SolicitudAtencion(
            id = newId,
            clienteId = clienteId,
            clienteNombre = clienteNombre,
            clienteCorreo = clienteCorreo,
            motivo = motivo,
            mensaje = mensaje,
            prioridad = prioridad,
            estado = EstadoAtencion.PENDIENTE,
            fechaHora = System.currentTimeMillis()
        )

        _solicitudes.value = listOf(nueva) + _solicitudes.value
        externalScope.launch {
            try {
                collectionRef.document(newId).set(SolicitudAtencionDto.fromDomain(nueva))
            } catch (e: Exception) {
                Log.e("FirebaseAtencionRepo", "Error adding solicitud to Firestore", e)
            }
        }

        return nueva
    }

    override fun tomarSolicitud(solicitudId: String, adminNombre: String) {
        val actual = getSolicitudById(solicitudId) ?: return
        val actualizada = actual.copy(
            estado = EstadoAtencion.EN_ATENCION,
            atendidoPor = adminNombre
        )
        updateSolicitud(actualizada)
    }

    override fun responderSolicitud(solicitudId: String, respuesta: String, adminNombre: String) {
        val actual = getSolicitudById(solicitudId) ?: return
        val actualizada = actual.copy(
            respuestaAdmin = respuesta,
            atendidoPor = adminNombre,
            estado = EstadoAtencion.EN_ATENCION
        )
        updateSolicitud(actualizada)
    }

    override fun resolverSolicitud(solicitudId: String, notasResolucion: String) {
        val actual = getSolicitudById(solicitudId) ?: return
        val actualizada = actual.copy(
            estado = EstadoAtencion.RESUELTO,
            fechaResolucion = System.currentTimeMillis(),
            respuestaAdmin = if (notasResolucion.isNotBlank()) notasResolucion else actual.respuestaAdmin
        )
        updateSolicitud(actualizada)
    }

    private fun updateSolicitud(solicitud: SolicitudAtencion) {
        _solicitudes.value = _solicitudes.value.map { if (it.id == solicitud.id) solicitud else it }
        externalScope.launch {
            try {
                collectionRef.document(solicitud.id).set(SolicitudAtencionDto.fromDomain(solicitud))
            } catch (e: Exception) {
                Log.e("FirebaseAtencionRepo", "Error updating solicitud in Firestore", e)
            }
        }
    }

    override fun getSolicitudesByCliente(clienteId: String): List<SolicitudAtencion> {
        return _solicitudes.value.filter { it.clienteId == clienteId }
    }

    override fun getSolicitudById(solicitudId: String): SolicitudAtencion? {
        return _solicitudes.value.find { it.id == solicitudId }
    }
}
