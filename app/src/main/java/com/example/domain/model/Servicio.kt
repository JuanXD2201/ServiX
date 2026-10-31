package com.example.domain.model

data class Servicio(
    val servicioId: String,
    val clienteId: String,
    val nombre: String,
    val descripcion: String = "",
    val categoria: String = "",
    val precio: Double = 0.0,
    val fecha: String = "",
    val fechaEntrega: String = "",
    val estado: EstadoServicio = EstadoServicio.PENDIENTE,
    val prioridad: PrioridadServicio = PrioridadServicio.MEDIA,
    val observaciones: String = "",
    val esFavorito: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
