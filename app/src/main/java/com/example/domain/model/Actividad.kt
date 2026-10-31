package com.example.domain.model

data class Actividad(
    val id: String,
    val tipo: TipoActividad,
    val titulo: String,
    val descripcion: String,
    val fecha: Long = System.currentTimeMillis()
)
