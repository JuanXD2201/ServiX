package com.example.data.firebase

import android.util.Log
import com.example.data.firebase.dto.NotificacionDto
import com.example.domain.model.Notificacion
import com.example.domain.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FirebaseNotificationRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : NotificationRepository {

    private val _notificaciones = MutableStateFlow<List<Notificacion>>(emptyList())
    override val notificaciones: StateFlow<List<Notificacion>> = _notificaciones.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    private fun getNotificacionesCollection(uid: String) = firestore.collection("usuarios").document(uid).collection("notificaciones")

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                listenToNotificaciones(user.uid)
            } else {
                listenerRegistration?.remove()
                listenerRegistration = null
                _notificaciones.value = emptyList()
            }
        }
    }

    private fun listenToNotificaciones(uid: String) {
        listenerRegistration?.remove()
        listenerRegistration = getNotificacionesCollection(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FirebaseNotifRepo", "Error listening to notificaciones", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(NotificacionDto::class.java)?.toDomain()
                }.sortedByDescending { it.fecha }
                _notificaciones.value = list
            }
        }
    }

    override fun marcarNotificacionLeida(notificacionId: String) {
        val uid = auth.currentUser?.uid ?: return
        _notificaciones.value = _notificaciones.value.map {
            if (it.id == notificacionId) it.copy(leida = true) else it
        }
        externalScope.launch {
            try {
                getNotificacionesCollection(uid).document(notificacionId).update("leida", true)
            } catch (e: Exception) {
                Log.e("FirebaseNotifRepo", "Error marking notificacion as read in Firestore", e)
            }
        }
    }

    override fun deleteNotificacion(notificacionId: String) {
        val uid = auth.currentUser?.uid ?: return
        _notificaciones.value = _notificaciones.value.filter { it.id != notificacionId }
        externalScope.launch {
            try {
                getNotificacionesCollection(uid).document(notificacionId).delete()
            } catch (e: Exception) {
                Log.e("FirebaseNotifRepo", "Error deleting notificacion in Firestore", e)
            }
        }
    }

    override fun marcarTodasNotificacionesLeidas() {
        val uid = auth.currentUser?.uid ?: return
        _notificaciones.value = _notificaciones.value.map { it.copy(leida = true) }
        externalScope.launch {
            try {
                getNotificacionesCollection(uid).whereEqualTo("leida", false).get()
                    .addOnSuccessListener { snap ->
                        for (doc in snap.documents) {
                            doc.reference.update("leida", true)
                        }
                    }
            } catch (e: Exception) {
                Log.e("FirebaseNotifRepo", "Error marking all notificaciones as read in Firestore", e)
            }
        }
    }

    override fun addNotificacion(notificacion: Notificacion) {
        val uid = auth.currentUser?.uid ?: return
        _notificaciones.value = listOf(notificacion) + _notificaciones.value
        externalScope.launch {
            try {
                getNotificacionesCollection(uid).document(notificacion.id)
                    .set(NotificacionDto.fromDomain(notificacion))
            } catch (e: Exception) {
                Log.e("FirebaseNotifRepo", "Error adding notificacion to Firestore", e)
            }
        }
    }
}
