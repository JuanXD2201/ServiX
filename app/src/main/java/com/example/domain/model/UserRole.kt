package com.example.domain.model

enum class UserRole {
    CLIENTE,
    ADMINISTRADOR;

    val displayName: String
        get() = when (this) {
            CLIENTE -> "Cliente"
            ADMINISTRADOR -> "Administrador / Vendedor"
        }

    companion object {
        fun fromString(value: String?): UserRole {
            return when (value?.uppercase()?.trim()) {
                "ADMINISTRADOR", "ADMIN", "VENDEDOR", "ADMIN_VENDEDOR" -> ADMINISTRADOR
                else -> CLIENTE
            }
        }
    }
}
