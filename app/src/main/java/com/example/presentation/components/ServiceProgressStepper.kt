package com.example.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.theme.ServixBluePrimary
import com.example.core.theme.ServixStatusColors
import com.example.domain.model.EstadoServicio
import com.example.domain.model.Servicio

@Composable
fun ServiceProgressStepper(
    servicio: Servicio,
    modifier: Modifier = Modifier
) {
    val isCancelled = servicio.estado == EstadoServicio.CANCELADO

    val steps = listOf(
        Triple("Registrado", "Solicitud en espera", Icons.Default.AssignmentTurnedIn),
        Triple("En Proceso", "Técnico trabajando", Icons.Default.Handyman),
        Triple("Completado", "Servicio entregado", Icons.Default.CheckCircle)
    )

    val currentStepIndex = when (servicio.estado) {
        EstadoServicio.PENDIENTE -> 0
        EstadoServicio.EN_PROCESO -> 1
        EstadoServicio.COMPLETADO -> 2
        EstadoServicio.CANCELADO -> -1
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        modifier = modifier
            .fillMaxWidth()
            .testTag("service_progress_stepper")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progreso del Servicio",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isCancelled) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ServixStatusColors.CanceladoContainer
                    ) {
                        Text(
                            text = "ORDEN CANCELADA",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ServixStatusColors.CanceladoPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (servicio.estado) {
                            EstadoServicio.PENDIENTE -> ServixStatusColors.PendienteContainer
                            EstadoServicio.EN_PROCESO -> ServixStatusColors.EnProcesoContainer
                            EstadoServicio.COMPLETADO -> ServixStatusColors.CompletadoContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Text(
                            text = servicio.estado.displayName.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = when (servicio.estado) {
                                EstadoServicio.PENDIENTE -> ServixStatusColors.PendientePrimary
                                EstadoServicio.EN_PROCESO -> ServixStatusColors.EnProcesoPrimary
                                EstadoServicio.COMPLETADO -> ServixStatusColors.CompletadoPrimary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isCancelled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                        tint = ServixStatusColors.CanceladoPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Este servicio fue marcado como cancelado",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Si consideras que es un error, puedes solicitar atención humana desde el chat.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    steps.forEachIndexed { index, step ->
                        val isDone = index <= currentStepIndex
                        val isCurrent = index == currentStepIndex

                        val circleColor by animateColorAsState(
                            targetValue = when {
                                isCurrent -> ServixBluePrimary
                                isDone -> Color(0xFF2E7D32)
                                else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            },
                            label = "stepColor"
                        )

                        // Step Item
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(circleColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isDone && !isCurrent) Icons.Default.Check else step.third,
                                    contentDescription = step.first,
                                    tint = if (isDone) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = step.first,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                color = if (isCurrent) ServixBluePrimary else if (isDone) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = step.second,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                maxLines = 1
                            )
                        }

                        // Connecting line between steps
                        if (index < steps.size - 1) {
                            val lineDone = index < currentStepIndex
                            Box(
                                modifier = Modifier
                                    .weight(0.6f)
                                    .height(3.dp)
                                    .offset(y = (-14).dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (lineDone) Color(0xFF2E7D32)
                                        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                    )
                            )
                        }
                    }
                }
            }

            // Fechas clave
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Fecha programada",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = servicio.fecha.ifBlank { "Por definir" },
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (servicio.fechaEntrega.isNotBlank()) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Fecha estimada entrega",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = servicio.fechaEntrega,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
