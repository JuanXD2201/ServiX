package com.example.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.theme.ServixBluePrimary
import com.example.core.theme.ServixStatusColors
import com.example.core.utils.Formatters
import com.example.domain.model.*
import com.example.presentation.components.PriorityBadge
import com.example.presentation.components.ServiceProgressStepper
import com.example.presentation.components.ServixBrandIcon
import com.example.presentation.components.ServixTagline
import com.example.presentation.components.StatusChip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class ClienteTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Servicios : ClienteTab("Mis Servicios", Icons.Default.Handyman)
    data object Solicitudes : ClienteTab("Atención", Icons.Default.SupportAgent)
    data object Chat : ClienteTab("Chat IA", Icons.Default.SmartToy)
    data object Perfil : ClienteTab("Mi Perfil", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientePortalScreen(
    currentUser: AuthUser?,
    allServicios: List<Servicio>,
    allClientes: List<Cliente>,
    solicitudes: List<SolicitudAtencion> = emptyList(),
    chatMessages: List<ChatMessage>,
    chatEstado: EstadoChat = EstadoChat.ATENCION_IA,
    isChatLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    onSolicitarAtencionHumana: (motivo: String, mensaje: String, prioridad: PrioridadAtencion) -> Unit,
    onSolicitarServicioCliente: (nombre: String, categoria: String, descripcion: String, prioridad: PrioridadServicio, fecha: String, observaciones: String) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf<ClienteTab>(ClienteTab.Servicios) }
    var statusFilter by remember { mutableStateOf<EstadoServicio?>(null) }
    var selectedServiceForDetail by remember { mutableStateOf<Servicio?>(null) }
    var showNewServiceDialog by remember { mutableStateOf(false) }
    var showNewAttentionDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = selectedServiceForDetail != null || selectedTab != ClienteTab.Servicios) {
        if (selectedServiceForDetail != null) {
            selectedServiceForDetail = null
        } else if (selectedTab != ClienteTab.Servicios) {
            selectedTab = ClienteTab.Servicios
        }
    }

    // Identificar qué cliente corresponde al usuario autenticado de forma estricta
    val clienteAsociado = remember(currentUser, allClientes) {
        allClientes.find {
            (currentUser?.email?.isNotBlank() == true && it.correo.equals(currentUser.email, ignoreCase = true)) ||
            (currentUser?.displayName?.isNotBlank() == true && it.nombre.equals(currentUser.displayName, ignoreCase = true)) ||
            it.clienteId == (currentUser?.uid ?: "")
        }
    }

    // Filtrar los servicios exclusivos para este cliente
    val misServicios = remember(allServicios, clienteAsociado, currentUser) {
        if (clienteAsociado != null) {
            allServicios.filter { it.clienteId == clienteAsociado.clienteId }
        } else {
            allServicios.filter { it.clienteId == (currentUser?.uid ?: "") }
        }
    }

    // Filtrar las solicitudes exclusivas para este cliente
    val misSolicitudes = remember(solicitudes, clienteAsociado, currentUser) {
        solicitudes.filter {
            (currentUser?.uid != null && it.clienteId == currentUser.uid) ||
            (clienteAsociado != null && it.clienteId == clienteAsociado.clienteId) ||
            (currentUser?.email != null && it.clienteCorreo.equals(currentUser.email, ignoreCase = true))
        }
    }

    val serviciosFiltrados = remember(misServicios, statusFilter) {
        if (statusFilter == null) misServicios else misServicios.filter { it.estado == statusFilter }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("cliente_bottom_nav")
            ) {
                val tabs = listOf(ClienteTab.Servicios, ClienteTab.Solicitudes, ClienteTab.Chat, ClienteTab.Perfil)
                tabs.forEach { tab ->
                    val selected = selectedTab == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { selectedTab = tab },
                        icon = {
                            if (tab == ClienteTab.Solicitudes && misSolicitudes.any { it.estado == EstadoAtencion.EN_ATENCION }) {
                                BadgedBox(badge = { Badge { Text("!") } }) {
                                    Icon(tab.icon, contentDescription = tab.title)
                                }
                            } else {
                                Icon(tab.icon, contentDescription = tab.title)
                            }
                        },
                        label = { Text(tab.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ServixBluePrimary,
                            selectedTextColor = ServixBluePrimary,
                            indicatorColor = ServixBluePrimary.copy(alpha = 0.12f)
                        ),
                        modifier = Modifier.testTag("tab_${tab.title.lowercase().replace(" ", "_")}")
                    )
                }
            }
        },
        floatingActionButton = {
            when (selectedTab) {
                ClienteTab.Servicios -> {
                    ExtendedFloatingActionButton(
                        onClick = { showNewServiceDialog = true },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text("Solicitar Servicio", fontWeight = FontWeight.Bold) },
                        containerColor = ServixBluePrimary,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("cliente_fab_solicitar_servicio")
                    )
                }
                ClienteTab.Solicitudes -> {
                    ExtendedFloatingActionButton(
                        onClick = { showNewAttentionDialog = true },
                        icon = { Icon(Icons.Default.SupportAgent, contentDescription = null) },
                        text = { Text("Nueva Solicitud", fontWeight = FontWeight.Bold) },
                        containerColor = ServixBluePrimary,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("cliente_fab_solicitar_atencion")
                    )
                }
                else -> Unit
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                ClienteTab.Servicios -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        // Header del Cliente
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "PORTAL DE CLIENTE",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                letterSpacing = 1.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            ),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Hola, ${currentUser?.displayName ?: "Cliente"}",
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        ServixTagline(fontSize = 9.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    ServixBrandIcon(
                                        size = 46.dp,
                                        modifier = Modifier.clickable { selectedTab = ClienteTab.Perfil }
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Métricas del Cliente
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val pendientesCount = misServicios.count { it.estado == EstadoServicio.PENDIENTE }
                                    val enProcesoCount = misServicios.count { it.estado == EstadoServicio.EN_PROCESO }
                                    val completadosCount = misServicios.count { it.estado == EstadoServicio.COMPLETADO }

                                    ResumenMetricCard("Pendientes", pendientesCount.toString(), Color(0xFFF57C00), Modifier.weight(1f))
                                    ResumenMetricCard("En Proceso", enProcesoCount.toString(), Color(0xFF1976D2), Modifier.weight(1f))
                                    ResumenMetricCard("Completados", completadosCount.toString(), Color(0xFF388E3C), Modifier.weight(1f))
                                }
                            }
                        }

                        // Filtros de estado
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = statusFilter == null,
                                onClick = { statusFilter = null },
                                label = { Text("Todos (${misServicios.size})") }
                            )
                            FilterChip(
                                selected = statusFilter == EstadoServicio.PENDIENTE,
                                onClick = { statusFilter = if (statusFilter == EstadoServicio.PENDIENTE) null else EstadoServicio.PENDIENTE },
                                label = { Text("Pendientes") }
                            )
                            FilterChip(
                                selected = statusFilter == EstadoServicio.EN_PROCESO,
                                onClick = { statusFilter = if (statusFilter == EstadoServicio.EN_PROCESO) null else EstadoServicio.EN_PROCESO },
                                label = { Text("En Proceso") }
                            )
                            FilterChip(
                                selected = statusFilter == EstadoServicio.COMPLETADO,
                                onClick = { statusFilter = if (statusFilter == EstadoServicio.COMPLETADO) null else EstadoServicio.COMPLETADO },
                                label = { Text("Completados") }
                            )
                        }

                        // Lista de Servicios del Cliente
                        if (serviciosFiltrados.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Handyman,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = if (misServicios.isEmpty()) "Aún no tienes órdenes de servicio registradas" else "No hay servicios con este filtro",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Usa el botón '+ Solicitar Servicio' para pedir mantenimiento, reparación o instalación con nuestro equipo técnico.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { showNewServiceDialog = true },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Solicitar Servicio Ahora")
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(serviciosFiltrados, key = { it.servicioId }) { srv ->
                                    ClienteServicioCard(
                                        servicio = srv,
                                        onClick = { selectedServiceForDetail = srv }
                                    )
                                }
                            }
                        }
                    }
                }

                ClienteTab.Solicitudes -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Mis Solicitudes de Atención Humana",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Casos escalados y respuestas de asesores ServiX",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (misSolicitudes.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.SupportAgent,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No tienes solicitudes de atención abiertas",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Si tienes dudas complejas, problemas o deseas hablar con una persona, puedes radicar una solicitud en cualquier momento.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(onClick = { showNewAttentionDialog = true }) {
                                        Text("Crear Solicitud de Atención")
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(misSolicitudes, key = { it.id }) { sol ->
                                    ClienteSolicitudCard(
                                        solicitud = sol,
                                        onIrAlChat = { selectedTab = ClienteTab.Chat }
                                    )
                                }
                            }
                        }
                    }
                }

                ClienteTab.Chat -> {
                    ChatScreen(
                        messages = chatMessages,
                        isLoading = isChatLoading,
                        chatEstado = chatEstado,
                        currentUserRole = UserRole.CLIENTE,
                        onSendMessage = onSendMessage,
                        onClearChat = onClearChat,
                        onSolicitarAtencionHumana = onSolicitarAtencionHumana,
                        onBack = null
                    )
                }

                ClienteTab.Perfil -> {
                    ClientePerfilTab(
                        currentUser = currentUser,
                        misServiciosCount = misServicios.size,
                        misSolicitudesCount = misSolicitudes.size,
                        onSignOut = onSignOut
                    )
                }
            }
        }
    }

    // Modal de Detalle de Servicio para Cliente con Timeline Stepper
    if (selectedServiceForDetail != null) {
        val srv = selectedServiceForDetail!!
        AlertDialog(
            onDismissRequest = { selectedServiceForDetail = null },
            title = {
                Text(
                    text = srv.nombre,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusChip(estado = srv.estado)
                        PriorityBadge(prioridad = srv.prioridad)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stepper Visual
                    ServiceProgressStepper(servicio = srv)

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Categoría: ${srv.categoria.ifBlank { "General" }}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Valor del Servicio: ${if (srv.precio > 0) Formatters.formatCurrency(srv.precio) else "Pendiente de cotización"}",
                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    )

                    if (srv.descripcion.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Descripción del Trabajo:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text(srv.descripcion, style = MaterialTheme.typography.bodySmall)
                    }

                    if (srv.observaciones.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Observaciones del Equipo:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text(srv.observaciones, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = {
                            selectedServiceForDetail = null
                            selectedTab = ClienteTab.Chat
                            onSendMessage("¿Cuál es el estado actual y avance de mi orden '${srv.nombre}'?")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Preguntar a la IA sobre esta orden")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedServiceForDetail = null }) {
                    Text("Cerrar")
                }
            }
        )
    }

    // Modal para Solicitar Nuevo Servicio
    if (showNewServiceDialog) {
        SolicitarServicioClienteDialog(
            onDismiss = { showNewServiceDialog = false },
            onConfirm = { nom, cat, desc, pri, fec, obs ->
                onSolicitarServicioCliente(nom, cat, desc, pri, fec, obs)
                showNewServiceDialog = false
            }
        )
    }

    // Modal para Nueva Solicitud de Atención Humana
    if (showNewAttentionDialog) {
        NuevaSolicitudAtencionDialog(
            onDismiss = { showNewAttentionDialog = false },
            onConfirm = { motivo, mensaje, prioridad ->
                onSolicitarAtencionHumana(motivo, mensaje, prioridad)
                showNewAttentionDialog = false
            }
        )
    }
}

@Composable
fun ClienteServicioCard(
    servicio: Servicio,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("cliente_servicio_${servicio.servicioId}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = servicio.nombre,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = servicio.categoria.ifBlank { "General" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusChip(estado = servicio.estado)
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (servicio.descripcion.isNotBlank()) {
                Text(
                    text = servicio.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Valor estimado", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (servicio.precio > 0) Formatters.formatCurrency(servicio.precio) else "Por cotizar",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Ver detalles",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ClienteSolicitudCard(
    solicitud: SolicitudAtencion,
    onIrAlChat: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Radicado #${solicitud.id.take(8).uppercase()}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

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
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = when (solicitud.estado) {
                            EstadoAtencion.PENDIENTE -> ServixStatusColors.PendientePrimary
                            EstadoAtencion.EN_ATENCION -> ServixStatusColors.EnProcesoPrimary
                            EstadoAtencion.RESUELTO -> ServixStatusColors.CompletadoPrimary
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = solicitud.motivo,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "\"${solicitud.mensaje}\"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (solicitud.atendidoPor.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Atendido por asesor: ${solicitud.atendidoPor}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (solicitud.respuestaAdmin.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ServixBluePrimary.copy(alpha = 0.08f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Respuesta del Asesor ServiX:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ServixBluePrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = solicitud.respuestaAdmin,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = onIrAlChat,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ver en el Chat")
            }
        }
    }
}

@Composable
fun SolicitarServicioClienteDialog(
    onDismiss: () -> Unit,
    onConfirm: (nombre: String, categoria: String, descripcion: String, prioridad: PrioridadServicio, fecha: String, observaciones: String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Mantenimiento") }
    var descripcion by remember { mutableStateOf("") }
    var prioridad by remember { mutableStateOf(PrioridadServicio.MEDIA) }
    var fecha by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))
    }
    var observaciones by remember { mutableStateOf("") }

    val categorias = listOf("Mantenimiento", "Reparación", "Instalación", "Inspección", "General")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Solicitar Nuevo Servicio", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("¿Qué servicio necesitas?") },
                    placeholder = { Text("Ej. Revisión eléctrica, cambio de filtros...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Categoría:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categorias.take(3).forEach { cat ->
                        FilterChip(
                            selected = categoria == cat,
                            onClick = { categoria = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Describe el trabajo o problema") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha preferida (DD/MM/AAAA)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = observaciones,
                    onValueChange = { observaciones = it },
                    label = { Text("Dirección o notas adicionales") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isNotBlank()) {
                        onConfirm(nombre.trim(), categoria, descripcion.trim(), prioridad, fecha.trim(), observaciones.trim())
                    }
                },
                enabled = nombre.isNotBlank()
            ) {
                Text("Enviar Solicitud")
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
fun NuevaSolicitudAtencionDialog(
    onDismiss: () -> Unit,
    onConfirm: (motivo: String, mensaje: String, prioridad: PrioridadAtencion) -> Unit
) {
    var motivo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var prioridad by remember { mutableStateOf(PrioridadAtencion.NORMAL) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Solicitar Atención Humana", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = motivo,
                    onValueChange = { motivo = it },
                    label = { Text("Motivo de contacto") },
                    placeholder = { Text("Ej. Problema con una orden, cotización...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = mensaje,
                    onValueChange = { mensaje = it },
                    label = { Text("Detalle de lo que requieres") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Prioridad:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PrioridadAtencion.entries.forEach { p ->
                        FilterChip(
                            selected = prioridad == p,
                            onClick = { prioridad = p },
                            label = { Text(p.displayName) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (motivo.isNotBlank()) {
                        onConfirm(motivo.trim(), mensaje.trim().ifBlank { motivo.trim() }, prioridad)
                    }
                },
                enabled = motivo.isNotBlank()
            ) {
                Text("Radicar Solicitud")
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
fun ResumenMetricCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = color)
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ClientePerfilTab(
    currentUser: AuthUser?,
    misServiciosCount: Int,
    misSolicitudesCount: Int = 0,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = currentUser?.displayName ?: "Cliente ServiX",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = "Rol: Cliente Registrado",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                PerfilItemRow(Icons.Default.Email, "Correo Electrónico", currentUser?.email ?: "No registrado")
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                PerfilItemRow(Icons.Default.Phone, "Teléfono", currentUser?.phone?.ifBlank { "No especificado" } ?: "No especificado")
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                PerfilItemRow(Icons.Default.Handyman, "Servicios Registrados", "$misServiciosCount órdenes de trabajo")
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                PerfilItemRow(Icons.Default.SupportAgent, "Solicitudes de Atención", "$misSolicitudesCount tickets radicados")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSignOut,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("cliente_sign_out_button")
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PerfilItemRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
