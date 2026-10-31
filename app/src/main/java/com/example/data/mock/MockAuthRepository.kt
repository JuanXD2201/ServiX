package com.example.data.mock

import com.example.domain.model.AuthUser
import com.example.domain.model.UserRole
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockAuthRepository : AuthRepository {

    private val defaultUser = AuthUser(
        uid = "user-mock-001",
        email = "carlos.vega@servixapp.com",
        displayName = "Carlos Alberto Vega",
        phone = "+57 310 456 7890",
        role = UserRole.ADMINISTRADOR
    )

    private val _currentUser = MutableStateFlow<AuthUser?>(defaultUser)
    override val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    override val isUserLoggedIn: Boolean
        get() = _currentUser.value != null

    override suspend fun signIn(email: String, password: String, requestedRole: UserRole?): AuthResult {
        val user = AuthUser(
            uid = "user-" + email.hashCode().toString(),
            email = email,
            displayName = email.substringBefore("@").replace(".", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
            phone = "+57 300 000 0000",
            role = requestedRole ?: UserRole.ADMINISTRADOR
        )
        _currentUser.value = user
        return AuthResult.Success(user)
    }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
        phone: String,
        role: UserRole
    ): AuthResult {
        val user = AuthUser(
            uid = "user-" + email.hashCode().toString(),
            email = email,
            displayName = name,
            phone = phone,
            role = role
        )
        _currentUser.value = user
        return AuthResult.Success(user)
    }

    override suspend fun signOut() {
        _currentUser.value = null
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun signInAnonymously(): AuthResult {
        val user = AuthUser(
            uid = "guest-" + System.currentTimeMillis(),
            email = "invitado@servixapp.com",
            displayName = "Técnico Invitado",
            phone = "",
            role = UserRole.ADMINISTRADOR
        )
        _currentUser.value = user
        return AuthResult.Success(user)
    }
}
