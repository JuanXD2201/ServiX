package com.example.data.firebase

import android.util.Log
import com.example.data.firebase.dto.ActividadDto
import com.example.domain.model.Actividad
import com.example.domain.model.TipoActividad
import com.example.domain.repository.ActividadRepository
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

class FirebaseActividadRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : ActividadRepository {

    private val _actividades = MutableStateFlow<List<Actividad>>(emptyList())
    override val actividades: StateFlow<List<Actividad>> = _actividades.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    private fun getActividadesCollection(uid: String) = firestore.collection("usuarios").document(uid).collection("actividades")

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                listenToActividades(user.uid)
            } else {
                listenerRegistration?.remove()
                listenerRegistration = null
                _actividades.value = emptyList()
            }
        }
    }

    private fun listenToActividades(uid: String) {
        listenerRegistration?.remove()
        listenerRegistration = getActividadesCollection(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FirebaseActividadRepo", "Error listening to actividades", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ActividadDto::class.java)?.toDomain()
                }.sortedByDescending { it.fecha }
                _actividades.value = list
            }
        }
    }

    override fun logActividad(tipo: TipoActividad, titulo: String, descripcion: String) {
        val newId = "act-" + UUID.randomUUID().toString().take(8)
        val nueva = Actividad(
            id = newId,
            tipo = tipo,
            titulo = titulo,
            descripcion = descripcion,
            fecha = System.currentTimeMillis()
        )
        val uid = auth.currentUser?.uid
        if (uid != null) {
            _actividades.value = listOf(nueva) + _actividades.value
            externalScope.launch {
                try {
                    getActividadesCollection(uid).document(newId).set(ActividadDto.fromDomain(nueva))
                } catch (e: Exception) {
                    Log.e("FirebaseActividadRepo", "Error logging actividad in Firestore", e)
                }
            }
        }
    }
}
