package com.example.domain.usecase

import com.example.domain.model.UserRole
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.AuthResult

class SignInUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, requestedRole: UserRole? = null): AuthResult {
        if (email.isBlank()) {
            return AuthResult.Error("El correo no puede estar vacío")
        }
        if (password.length < 6) {
            return AuthResult.Error("La contraseña debe tener al menos 6 caracteres")
        }
        return authRepository.signIn(email, password, requestedRole)
    }
}

class SignUpUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        phone: String = "",
        role: UserRole = UserRole.CLIENTE
    ): AuthResult {
        if (name.isBlank()) {
            return AuthResult.Error("El nombre completo es obligatorio")
        }
        if (email.isBlank() || !email.contains("@")) {
            return AuthResult.Error("Ingresa un correo electrónico válido")
        }
        if (password.length < 6) {
            return AuthResult.Error("La contraseña debe tener mínimo 6 caracteres")
        }
        return authRepository.signUp(name, email, password, phone, role)
    }
}

class SignOutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke() {
        authRepository.signOut()
    }
}

class SendPasswordResetUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Ingresa tu correo para recuperar contraseña"))
        }
        return authRepository.sendPasswordReset(email)
    }
}

class SignInAnonymouslyUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): AuthResult {
        return authRepository.signInAnonymously()
    }
}
