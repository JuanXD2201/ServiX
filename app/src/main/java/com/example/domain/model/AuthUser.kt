package com.example.domain.model

data class AuthUser(
    val uid: String,
    val email: String,
    val displayName: String,
    val phone: String = "",
    val photoUrl: String? = null,
    val role: UserRole = UserRole.ADMINISTRADOR,
    val createdAt: Long = System.currentTimeMillis()
)
