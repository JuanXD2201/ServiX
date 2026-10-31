package com.example.domain.model

data class Notificacion(
    val id: String,
    val tipo: TipoNotificacion,
    val titulo: String,
    val descripcion: String,
    val fecha: Long = System.currentTimeMillis(),
    val leida: Boolean = false,
    val referenciaId: String? = null,
    val tipoReferencia: String = "servicio"
)
