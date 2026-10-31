package com.example.domain.model

import androidx.compose.ui.graphics.Color

enum class PrioridadServicio(val displayName: String, val level: Int) {
    BAJA("Baja", 1),
    MEDIA("Media", 2),
    ALTA("Alta", 3),
    URGENTE("Urgente", 4);

    val color: Color
        get() = when (this) {
            BAJA -> Color(0xFF43A047)
            MEDIA -> Color(0xFF1E88E5)
            ALTA -> Color(0xFFFB8C00)
            URGENTE -> Color(0xFFE53935)
        }

    val containerColor: Color
        get() = when (this) {
            BAJA -> Color(0xFFE8F5E9)
            MEDIA -> Color(0xFFE3F2FD)
            ALTA -> Color(0xFFFFF3E0)
            URGENTE -> Color(0xFFFFEBEE)
        }

    val textColor: Color
        get() = when (this) {
            BAJA -> Color(0xFF1B5E20)
            MEDIA -> Color(0xFF0D47A1)
            ALTA -> Color(0xFFE65100)
            URGENTE -> Color(0xFFB71C1C)
        }
}
