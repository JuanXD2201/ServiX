package com.example.domain.model

data class UserProfile(
    val nombre: String = "Carlos Alberto Vega",
    val rol: String = "Técnico Especialista en Climatización y Redes",
    val especialidad: String = "Técnico Especialista en Climatización y Redes",
    val empresa: String = "Vega Servicios Técnicos",
    val telefono: String = "+57 310 456 7890",
    val correo: String = "carlos.vega@servixapp.com",
    val ciudad: String = "Medellín, Colombia",
    val descripcion: String = "Más de 8 años de experiencia en mantenimiento preventivo, correctivo y montaje de sistemas residenciales y comerciales.",
    val fotoPerfilBase64: String? = null
)
