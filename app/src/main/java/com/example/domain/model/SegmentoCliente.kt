package com.example.domain.model

import androidx.compose.ui.graphics.Color

enum class SegmentoCliente(
    val displayName: String,
    val color: Color,
    val containerColor: Color
) {
    VIP("VIP", Color(0xFF6A1B9A), Color(0xFFF3E5F5)),
    FRECUENTE("Frecuente", Color(0xFF1E88E5), Color(0xFFE3F2FD)),
    NUEVO("Nuevo", Color(0xFF2E7D32), Color(0xFFE8F5E9)),
    PENDIENTE("Pendiente", Color(0xFFE65100), Color(0xFFFFE0B2)),
    INACTIVO("Inactivo", Color(0xFF757575), Color(0xFFEEEEEE));
}
