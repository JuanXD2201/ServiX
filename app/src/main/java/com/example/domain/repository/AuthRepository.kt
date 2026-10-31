package com.example.domain.repository

import com.example.domain.model.AuthUser
import com.example.domain.model.UserRole
import kotlinx.coroutines.flow.StateFlow

sealed class AuthResult {
    data class Success(val user: AuthUser) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

interface AuthRepository {
    val currentUser: StateFlow<AuthUser?>
    val isUserLoggedIn: Boolean

    suspend fun signIn(email: String, password: String, requestedRole: UserRole? = null): AuthResult
    suspend fun signUp(nombre: String, email: String, password: String, phone: String = "", role: UserRole = UserRole.ADMINISTRADOR): AuthResult
    suspend fun signOut()
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun signInAnonymously(): AuthResult
}
