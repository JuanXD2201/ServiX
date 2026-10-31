package com.example.domain.usecase

import com.example.domain.model.Cliente
import com.example.domain.model.Notificacion
import com.example.domain.model.TipoActividad
import com.example.domain.model.TipoNotificacion
import com.example.domain.repository.ActividadRepository
import com.example.domain.repository.ClienteRepository
import com.example.domain.repository.NotificationRepository
import java.util.UUID

class AddClienteUseCase(
    private val clienteRepository: ClienteRepository,
    private val actividadRepository: ActividadRepository,
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(
        nombre: String,
        documento: String = "",
        telefono: String = "",
        correo: String = "",
        direccion: String = "",
        notas: String = "",
        esFavorito: Boolean = false
    ): Cliente {
        val newCliente = clienteRepository.addCliente(nombre, documento, telefono, correo, direccion, notas, esFavorito)
        val tel = if (newCliente.telefono.isBlank()) "Sin registrar" else newCliente.telefono
        val dir = if (newCliente.direccion.isBlank()) "Sin dirección" else newCliente.direccion
        actividadRepository.logActividad(
            TipoActividad.CLIENTE_CREADO,
            "Nuevo cliente: ${newCliente.nombre}",
            "Tel: $tel • $dir"
        )
        notificationRepository.addNotificacion(
            Notificacion(
                id = UUID.randomUUID().toString(),
                tipo = TipoNotificacion.NUEVO_CLIENTE,
                titulo = "Nuevo cliente registrado",
                descripcion = "${newCliente.nombre} fue añadido a tu cartera de clientes.",
                fecha = System.currentTimeMillis(),
                leida = false,
                referenciaId = newCliente.clienteId,
                tipoReferencia = "cliente"
            )
        )
        return newCliente
    }
}
