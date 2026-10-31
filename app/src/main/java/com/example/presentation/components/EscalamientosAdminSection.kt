package com.example.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.theme.ServixBluePrimary
import com.example.core.theme.ServixStatusColors
import com.example.domain.model.EstadoAtencion
import com.example.domain.model.PrioridadAtencion
import com.example.domain.model.SolicitudAtencion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EscalamientosAdminSection(
    solicitudes: List<SolicitudAtencion>,
    onTomarSolicitud: (String) -> Unit,
    onResponderSolicitud: (String, String) -> Unit,
    onResolverSolicitud: (String, String) -> Unit,
    onNavigateToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<EstadoAtencion?>(EstadoAtencion.PENDIENTE) }
    var respondingToSolicitud by remember { mutableStateOf<SolicitudAtencion?>(null) }
    var resolvingSolicitud by remember { mutableStateOf<SolicitudAtencion?>(null) }

    val pendientesCount = remember(solicitudes) {
        solicitudes.count { it.estado == EstadoAtencion.PENDIENTE }
    }

    val solicitudesFiltradas = remember(solicitudes, selectedFilter) {
        if (selectedFilter == null) solicitudes else solicitudes.filter { it.estado == selectedFilter }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("escalamientos_admin_section"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header del Módulo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (pendientesCount > 0) Color(0xFFFEE2E2) else ServixBluePrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (pendientesCount > 0) Icons.Default.PriorityHigh else Icons.Default.SupportAgent,
                                contentDescription = null,
                                tint = if (pendientesCount > 0) Color(0xFFDC2626) else ServixBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Bandeja de Atención y Escalamientos",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (pendientesCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFDC2626)
                                ) {
                                    Text(
                                        text = "$pendientesCount PENDIENTES",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 9.sp
                                        ),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Casos escalados por el Agente IA para atención humana",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onNavigateToChat, modifier = Modifier.testTag("admin_open_chat_button")) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = "Ir al Chat",
                        tint = ServixBluePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filtros de Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == EstadoAtencion.PENDIENTE,
                    onClick = {
                        selectedFilter = if (selectedFilter == EstadoAtencion.PENDIENTE) null else EstadoAtencion.PENDIENTE
                    },
                    label = { Text("Pendientes ($pendientesCount)") },
                    shape = RoundedCornerShape(10.dp)
                )
                FilterChip(
                    selected = selectedFilter == EstadoAtencion.EN_ATENCION,
                    onClick = {
                        selectedFilter = if (selectedFilter == EstadoAtencion.EN_ATENCION) null else EstadoAtencion.EN_ATENCION
                    },
                    label = {
                        val count = solicitudes.count { it.estado == EstadoAtencion.EN_ATENCION }
                        Text("En Atención ($count)")
                    },
                    shape = RoundedCornerShape(10.dp)
                )
                FilterChip(
                    selected = selectedFilter == EstadoAtencion.RESUELTO,
                    onClick = {
                        selectedFilter = if (selectedFilter == EstadoAtencion.RESUELTO) null else EstadoAtencion.RESUELTO
                    },
                    label = {
                        val count = solicitudes.count { it.estado == EstadoAtencion.RESUELTO }
                        Text("Resueltos ($count)")
                    },
                    shape = RoundedCornerShape(10.dp)
                )
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("Todos (${solicitudes.size})") },
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lista de Solicitudes
            if (solicitudesFiltradas.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (selectedFilter == EstadoAtencion.PENDIENTE)
                                "¡Excelente! No hay solicitudes pendientes de atención humana."
                            else
                                "No se encontraron solicitudes con el filtro seleccionado.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    solicitudesFiltradas.take(5).forEach { sol ->
                        SolicitudItemCard(
                            solicitud = sol,
                            onTomar = { onTomarSolicitud(sol.id) },
                            onResponder = { respondingToSolicitud = sol },
                            onResolver = { resolvingSolicitud = sol },
                            onOpenChat = onNavigateToChat
                        )
                    }

                    if (solicitudesFiltradas.size > 5) {
                        Text(
                            text = "Y ${solicitudesFiltradas.size - 5} solicitudes más...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }

    // Modal para Responder Solicitud
    if (respondingToSolicitud != null) {
        val sol = respondingToSolicitud!!
        ResponderSolicitudDialog(
            solicitud = sol,
            onDismiss = { respondingToSolicitud = null },
            onSendResponse = { respuesta ->
                onResponderSolicitud(sol.id, respuesta)
                respondingToSolicitud = null
            }
        )
    }

    // Modal para Resolver Solicitud
    if (resolvingSolicitud != null) {
        val sol = resolvingSolicitud!!
        ResolverSolicitudDialog(
            solicitud = sol,
            onDismiss = { resolvingSolicitud = null },
            onConfirmResolve = { notas ->
                onResolverSolicitud(sol.id, notas)
                resolvingSolicitud = null
            }
        )
    }
}

@Composable
fun SolicitudItemCard(
    solicitud: SolicitudAtencion,
    onTomar: () -> Unit,
    onResponder: () -> Unit,
    onResolver: () -> Unit,
    onOpenChat: () -> Unit
) {
    val (priorityBorder, priorityBg, priorityText) = when (solicitud.prioridad) {
        PrioridadAtencion.URGENTE -> Triple(Color(0xFFDC2626), Color(0xFFFEE2E2), Color(0xFF991B1B))
        PrioridadAtencion.IMPORTANTE -> Triple(Color(0xFFD97706), Color(0xFFFEF3C7), Color(0xFF92400E))
        PrioridadAtencion.NORMAL -> Triple(ServixBluePrimary, Color(0xFFE0F2FE), Color(0xFF0369A1))
    }

    val timeAgo = remember(solicitud.fechaHora) {
        val diff = System.currentTimeMillis() - solicitud.fechaHora
        val mins = diff / (60 * 1000)
        val hours = mins / 60
        val days = hours / 24
        when {
            mins < 2 -> "Ahora mismo"
            mins < 60 -> "Hace $mins min"
            hours < 24 -> "Hace $hours h"
            days == 1L -> "Ayer"
            days < 30 -> "Hace $days días"
            else -> SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(solicitud.fechaHora))
        }
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
        border = BorderStroke(
            1.dp,
            if (solicitud.estado == EstadoAtencion.PENDIENTE) priorityBorder.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("solicitud_item_${solicitud.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Fila superior: Cliente y Prioridad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(priorityBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = solicitud.clienteNombre.take(1).uppercase(),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = priorityText
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = solicitud.clienteNombre,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${solicitud.clienteCorreo} • $timeAgo",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Badges de Prioridad y Estado
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = priorityBg
                    ) {
                        Text(
                            text = solicitud.prioridad.displayName.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = priorityText,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (solicitud.estado) {
                            EstadoAtencion.PENDIENTE -> ServixStatusColors.PendienteContainer
                            EstadoAtencion.EN_ATENCION -> ServixStatusColors.EnProcesoContainer
                            EstadoAtencion.RESUELTO -> ServixStatusColors.CompletadoContainer
                        }
                    ) {
                        Text(
                            text = solicitud.estado.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = when (solicitud.estado) {
                                EstadoAtencion.PENDIENTE -> ServixStatusColors.PendientePrimary
                                EstadoAtencion.EN_ATENCION -> ServixStatusColors.EnProcesoPrimary
                                EstadoAtencion.RESUELTO -> ServixStatusColors.CompletadoPrimary
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Motivo del caso
            Text(
                text = "Motivo: ${solicitud.motivo}",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            // Mensaje o queja del cliente
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "\"${solicitud.mensaje}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Datos de asignación o respuesta previa
            if (solicitud.atendidoPor.isNotBlank()) {
                Text(
                    text = "Asignado a: ${solicitud.atendidoPor}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (solicitud.respuestaAdmin.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ServixBluePrimary.copy(alpha = 0.08f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Row(modifier = Modifier.padding(8.dp)) {
                        Icon(
                            imageVector = Icons.Default.Reply,
                            contentDescription = null,
                            tint = ServixBluePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Respuesta enviada: ${solicitud.respuestaAdmin}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botones de Acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (solicitud.estado) {
                    EstadoAtencion.PENDIENTE -> {
                        Button(
                            onClick = onTomar,
                            colors = ButtonDefaults.buttonColors(containerColor = ServixBluePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("btn_tomar_${solicitud.id}")
                        ) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tomar Caso", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }

                        OutlinedButton(
                            onClick = onResponder,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("btn_responder_${solicitud.id}")
                        ) {
                            Icon(Icons.Default.Reply, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Responder", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    EstadoAtencion.EN_ATENCION -> {
                        OutlinedButton(
                            onClick = onResponder,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                        ) {
                            Icon(Icons.Default.Reply, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Responder", style = MaterialTheme.typography.labelMedium)
                        }

                        Button(
                            onClick = onResolver,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("btn_resolver_${solicitud.id}")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Resolver", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    EstadoAtencion.RESUELTO -> {
                        FilledTonalButton(
                            onClick = onOpenChat,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Caso Resuelto • Ver Chat", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResponderSolicitudDialog(
    solicitud: SolicitudAtencion,
    onDismiss: () -> Unit,
    onSendResponse: (String) -> Unit
) {
    var responseText by remember { mutableStateOf("") }
    val cannedResponses = listOf(
        "Hola ${solicitud.clienteNombre}, hemos tomado tu caso y lo estamos revisando de inmediato.",
        "Un técnico especialista se comunicará contigo hoy mismo para coordinar.",
        "Hemos verificado tu orden y el estado ha sido actualizado correctamente.",
        "Disculpa los inconvenientes; se ha programado una visita prioritaria."
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Responder a ${solicitud.clienteNombre}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Motivo: ${solicitud.motivo}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "\"${solicitud.mensaje}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Respuestas sugeridas:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )

                cannedResponses.forEach { suggestion ->
                    SuggestionChip(
                        onClick = { responseText = suggestion },
                        label = { Text(suggestion, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = responseText,
                    onValueChange = { responseText = it },
                    label = { Text("Escribe tu respuesta al cliente") },
                    placeholder = { Text("Mensaje formal de atención...") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_respuesta_atencion")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (responseText.isNotBlank()) {
                        onSendResponse(responseText.trim())
                    }
                },
                enabled = responseText.isNotBlank(),
                modifier = Modifier.testTag("btn_confirm_send_response")
            ) {
                Text("Enviar Respuesta")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ResolverSolicitudDialog(
    solicitud: SolicitudAtencion,
    onDismiss: () -> Unit,
    onConfirmResolve: (String) -> Unit
) {
    var notasResolucion by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Marcar Solicitud como Resuelta",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "¿Confirmas que el caso de ${solicitud.clienteNombre} (${solicitud.motivo}) ha sido solventado satisfactoriamente?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = notasResolucion,
                    onValueChange = { notasResolucion = it },
                    label = { Text("Notas de resolución (opcional)") },
                    placeholder = { Text("Ej. Cliente atendido telefónicamente, fecha reagendada.") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmResolve(notasResolucion.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                modifier = Modifier.testTag("btn_confirm_resolve")
            ) {
                Text("Marcar Resuelta")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
