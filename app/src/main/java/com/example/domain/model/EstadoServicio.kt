package com.example.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.core.theme.ServixStatusColors

enum class EstadoServicio(val displayName: String, val icon: ImageVector) {
    PENDIENTE("Pendiente", Icons.Default.PendingActions),
    EN_PROCESO("En Proceso", Icons.Default.HourglassTop),
    COMPLETADO("Completado", Icons.Default.CheckCircle),
    CANCELADO("Cancelado", Icons.Default.Cancel);

    val primaryColor: Color
        get() = when (this) {
            PENDIENTE -> ServixStatusColors.PendientePrimary
            EN_PROCESO -> ServixStatusColors.EnProcesoPrimary
            COMPLETADO -> ServixStatusColors.CompletadoPrimary
            CANCELADO -> ServixStatusColors.CanceladoPrimary
        }

    val containerColor: Color
        get() = when (this) {
            PENDIENTE -> ServixStatusColors.PendienteContainer
            EN_PROCESO -> ServixStatusColors.EnProcesoContainer
            COMPLETADO -> ServixStatusColors.CompletadoContainer
            CANCELADO -> ServixStatusColors.CanceladoContainer
        }

    val textColor: Color
        get() = when (this) {
            PENDIENTE -> ServixStatusColors.PendienteText
            EN_PROCESO -> ServixStatusColors.EnProcesoText
            COMPLETADO -> ServixStatusColors.CompletadoText
            CANCELADO -> ServixStatusColors.CanceladoText
        }
}
