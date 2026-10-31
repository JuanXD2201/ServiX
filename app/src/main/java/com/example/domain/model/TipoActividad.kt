package com.example.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Update
import androidx.compose.ui.graphics.vector.ImageVector

enum class TipoActividad(val tituloDefault: String, val icon: ImageVector) {
    CLIENTE_CREADO("Nuevo cliente registrado", Icons.Default.PersonAdd),
    CLIENTE_EDITADO("Cliente actualizado", Icons.Default.Edit),
    CLIENTE_ELIMINADO("Cliente eliminado", Icons.Default.Delete),
    SERVICIO_CREADO("Servicio creado", Icons.Default.Handyman),
    SERVICIO_ACTUALIZADO("Servicio actualizado", Icons.Default.Update),
    SERVICIO_COMPLETADO("Servicio completado", Icons.Default.CheckCircle),
    SERVICIO_CANCELADO("Servicio cancelado", Icons.Default.Cancel)
}
