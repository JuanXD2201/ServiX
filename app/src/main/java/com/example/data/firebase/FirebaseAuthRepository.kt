package com.example.data.firebase

import android.util.Log
import com.example.data.firebase.dto.UsuarioDto
import com.example.domain.model.AuthUser
import com.example.domain.model.UserRole
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : AuthRepository {

    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    override val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    override val isUserLoggedIn: Boolean
        get() = auth.currentUser != null

    private val usersCollection = firestore.collection("usuarios")

    init {
        // Escuchar cambios de estado en Firebase Auth
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                externalScope.launch {
                    val role = fetchUserRole(user.uid)
                    val authUser = AuthUser(
                        uid = user.uid,
                        email = user.email ?: "",
                        displayName = user.displayName ?: user.email?.substringBefore("@") ?: "Usuario",
                        phone = user.phoneNumber ?: "",
                        photoUrl = user.photoUrl?.toString(),
                        role = role
                    )
                    _currentUser.value = authUser
                }
            } else {
                _currentUser.value = null
            }
        }
    }

    private suspend fun fetchUserRole(uid: String): UserRole {
        return try {
            val doc = usersCollection.document(uid).get().await()
            val roleStr = doc.getString("role")
            UserRole.fromString(roleStr)
        } catch (e: Exception) {
            // Mínimo privilegio: si no se puede leer el rol, se trata como cliente.
            Log.w("FirebaseAuthRepo", "No se pudo leer el rol; se asume CLIENTE", e)
            UserRole.CLIENTE
        }
    }

    private fun syncUserToFirestore(user: AuthUser) {
        externalScope.launch {
            try {
                usersCollection.document(user.uid).set(UsuarioDto.fromDomain(user))
            } catch (e: Exception) {
                Log.e("FirebaseAuthRepo", "Error syncing user to Firestore", e)
            }
        }
    }

    override suspend fun signIn(email: String, password: String, requestedRole: UserRole?): AuthResult = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = result.user ?: return@withContext AuthResult.Error("No se pudo obtener el usuario")
            
            val storedRole = fetchUserRole(firebaseUser.uid)
            if (requestedRole != null && requestedRole != storedRole) {
                // El rol lo define la cuenta almacenada, nunca la selección de la pantalla.
                auth.signOut()
                return@withContext AuthResult.Error(
                    "Esta cuenta está registrada como ${storedRole.name.lowercase()}. Selecciona el perfil correcto."
                )
            }
            val finalRole = storedRole

            val user = AuthUser(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: email,
                displayName = firebaseUser.displayName ?: email.substringBefore("@"),
                phone = firebaseUser.phoneNumber ?: "",
                photoUrl = firebaseUser.photoUrl?.toString(),
                role = finalRole
            )
            _currentUser.value = user
            syncUserToFirestore(user)
            AuthResult.Success(user)
        } catch (e: Exception) {
            Log.e("FirebaseAuthRepo", "signIn error", e)
            AuthResult.Error(e.localizedMessage ?: "Error al iniciar sesión. Verifica tus credenciales.")
        }
    }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
        phone: String,
        role: UserRole
    ): AuthResult = withContext(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = result.user ?: return@withContext AuthResult.Error("No se pudo crear la cuenta")

            // Actualizar DisplayName en Firebase Auth
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name.trim())
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            val user = AuthUser(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: email,
                displayName = name.trim(),
                phone = phone.trim(),
                photoUrl = null,
                role = role
            )
            _currentUser.value = user
            syncUserToFirestore(user)
            AuthResult.Success(user)
        } catch (e: Exception) {
            Log.e("FirebaseAuthRepo", "signUp error", e)
            AuthResult.Error(e.localizedMessage ?: "Error al registrar cuenta. Inténtalo de nuevo.")
        }
    }

    override suspend fun signOut(): Unit = withContext(Dispatchers.IO) {
        try {
            auth.signOut()
            _currentUser.value = null
        } catch (e: Exception) {
            Log.e("FirebaseAuthRepo", "signOut error", e)
        }
        Unit
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuthRepo", "sendPasswordReset error", e)
            Result.failure(e)
        }
    }

    override suspend fun signInAnonymously(): AuthResult = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInAnonymously().await()
            val firebaseUser = result.user ?: return@withContext AuthResult.Error("Error en acceso de invitado")
            val user = AuthUser(
                uid = firebaseUser.uid,
                email = "invitado@servixapp.com",
                displayName = "Técnico Invitado",
                phone = ""
            )
            _currentUser.value = user
            syncUserToFirestore(user)
            AuthResult.Success(user)
        } catch (e: Exception) {
            Log.e("FirebaseAuthRepo", "signInAnonymously error", e)
            AuthResult.Error(e.localizedMessage ?: "Error al acceder como invitado")
        }
    }
}
