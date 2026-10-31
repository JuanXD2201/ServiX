package com.example.data.firebase

import android.util.Log
import com.example.data.firebase.dto.AppConfigDto
import com.example.data.firebase.dto.UserProfileDto
import com.example.domain.model.AppConfig
import com.example.domain.model.UserProfile
import com.example.domain.repository.ConfigRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FirebaseConfigRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : ConfigRepository {

    private val _appConfig = MutableStateFlow(AppConfig())
    override val appConfig: StateFlow<AppConfig> = _appConfig.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    override val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private var configListener: ListenerRegistration? = null
    private var profileListener: ListenerRegistration? = null

    private fun getConfigCollection(uid: String) = firestore.collection("usuarios").document(uid).collection("configuracion")

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                listenToConfigAndProfile(user.uid)
            } else {
                configListener?.remove()
                configListener = null
                profileListener?.remove()
                profileListener = null
                _appConfig.value = AppConfig()
                _userProfile.value = UserProfile()
            }
        }
    }

    private fun listenToConfigAndProfile(uid: String) {
        configListener?.remove()
        configListener = getConfigCollection(uid).document("app_config").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FirebaseConfigRepo", "Error listening to app_config", error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val config = snapshot.toObject(AppConfigDto::class.java)?.toDomain()
                if (config != null) {
                    _appConfig.value = config
                }
            }
        }

        profileListener?.remove()
        profileListener = getConfigCollection(uid).document("user_profile").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FirebaseConfigRepo", "Error listening to user_profile", error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val profile = snapshot.toObject(UserProfileDto::class.java)?.toDomain()
                if (profile != null) {
                    _userProfile.value = profile
                }
            }
        }
    }

    override fun updateObjetivos(servicios: Int, ingresos: Double) {
        val updated = _appConfig.value.copy(
            objetivoServiciosSemanal = servicios,
            objetivoIngresosSemanal = ingresos
        )
        updateConfig(updated)
    }

    override fun marcarOnboardingVisto(visto: Boolean) {
        val updated = _appConfig.value.copy(onboardingVisto = visto)
        updateConfig(updated)
    }

    override fun updateProfile(profile: UserProfile) {
        val uid = auth.currentUser?.uid ?: return
        _userProfile.value = profile
        externalScope.launch {
            try {
                getConfigCollection(uid).document("user_profile").set(UserProfileDto.fromDomain(profile))
            } catch (e: Exception) {
                Log.e("FirebaseConfigRepo", "Error updating user_profile in Firestore", e)
            }
        }
    }

    override fun updateConfig(config: AppConfig) {
        val uid = auth.currentUser?.uid ?: return
        _appConfig.value = config
        externalScope.launch {
            try {
                getConfigCollection(uid).document("app_config").set(AppConfigDto.fromDomain(config))
            } catch (e: Exception) {
                Log.e("FirebaseConfigRepo", "Error updating app_config in Firestore", e)
            }
        }
    }

    override fun resetData() {
        val uid = auth.currentUser?.uid ?: return
        _appConfig.value = AppConfig()
        _userProfile.value = UserProfile()
        updateConfig(_appConfig.value)
        updateProfile(_userProfile.value)
    }
}
