package com.example.domain.model

data class AppConfig(
    val monedaSimbolo: String = "$",
    val notificacionesActivas: Boolean = true,
    val sonidos: Boolean = true,
    val modoOscuroForzado: Boolean = false,
    val autoguardadoLocal: Boolean = true,
    val onboardingVisto: Boolean = true,
    val objetivoServiciosSemanal: Int = 10,
    val objetivoIngresosSemanal: Double = 1200000.0
) {
    val notificaciones: Boolean get() = notificacionesActivas
    val modoOscuro: Boolean get() = modoOscuroForzado
}
