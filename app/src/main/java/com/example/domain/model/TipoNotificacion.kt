package com.example.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.ui.graphics.vector.ImageVector

enum class TipoNotificacion(val label: String, val icon: ImageVector) {
    SERVICIO_HOY("Servicio de hoy", Icons.Default.Event),
    SERVICIO_URGENTE("Servicio urgente", Icons.Default.NotificationImportant),
    SERVICIO_PENDIENTE("Servicio pendiente", Icons.Default.Schedule),
    SERVICIO_COMPLETADO("Servicio finalizado", Icons.Default.CheckCircle),
    NUEVO_CLIENTE("Nuevo cliente", Icons.Default.PersonAdd),
    RECORDATORIO_SEGUIMIENTO("Seguimiento", Icons.Default.AccessTime),
    SOLICITUD_ATENCION("Solicitud de atención", Icons.Default.SupportAgent)
}
