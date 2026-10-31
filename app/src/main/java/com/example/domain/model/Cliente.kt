package com.example.domain.model

import java.util.Locale

data class Cliente(
    val clienteId: String,
    val nombre: String,
    val documento: String = "",
    val telefono: String = "",
    val correo: String = "",
    val direccion: String = "",
    val notas: String = "",
    val fechaRegistro: Long = System.currentTimeMillis(),
    val activo: Boolean = true,
    val esFavorito: Boolean = false,
    val fotoBase64: String? = null
) {
    val iniciales: String
        get() {
            val parts = nombre.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
            return when {
                parts.isEmpty() -> "CL"
                parts.size == 1 -> parts[0].take(2).uppercase(Locale.ROOT)
                else -> (parts[0].take(1) + parts[1].take(1)).uppercase(Locale.ROOT)
            }
        }

    fun calcularSegmentos(serviciosDelCliente: List<Servicio>): List<SegmentoCliente> {
        val result = mutableListOf<SegmentoCliente>()
        val completed = serviciosDelCliente.count { it.estado == EstadoServicio.COMPLETADO }
        val pending = serviciosDelCliente.count { it.estado == EstadoServicio.PENDIENTE || it.estado == EstadoServicio.EN_PROCESO }
        val totalFacturado = serviciosDelCliente.filter { it.estado == EstadoServicio.COMPLETADO }.sumOf { it.precio }

        if (totalFacturado >= 500000.0 || completed >= 3) {
            result.add(SegmentoCliente.VIP)
        }
        if (serviciosDelCliente.size >= 2) {
            result.add(SegmentoCliente.FRECUENTE)
        }
        if (pending > 0) {
            result.add(SegmentoCliente.PENDIENTE)
        }
        val isNew = System.currentTimeMillis() - fechaRegistro < 1296000000L
        if (isNew && serviciosDelCliente.size <= 1) {
            result.add(SegmentoCliente.NUEVO)
        }
        if ((serviciosDelCliente.isEmpty() || (pending == 0 && System.currentTimeMillis() - fechaRegistro > 1728000000L)) && !result.contains(SegmentoCliente.PENDIENTE)) {
            result.add(SegmentoCliente.INACTIVO)
        }
        if (result.isEmpty()) {
            result.add(SegmentoCliente.FRECUENTE)
        }
        return result.distinct()
    }
}
