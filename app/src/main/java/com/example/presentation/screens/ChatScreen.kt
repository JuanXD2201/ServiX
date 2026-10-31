package com.example.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.domain.model.ChatMessage
import com.example.domain.model.EstadoChat
import com.example.domain.model.PrioridadAtencion
import com.example.domain.model.SenderType
import com.example.domain.model.UserRole
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    chatEstado: EstadoChat = EstadoChat.ATENCION_IA,
    currentUserRole: UserRole? = null,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    onSolicitarAtencionHumana: ((motivo: String, mensaje: String, prioridad: PrioridadAtencion) -> Unit)? = null,
    onResolverChat: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var showEscalationDialog by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Auto-scroll al recibir o enviar mensaje
    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickQuestions = listOf(
        "¿Cómo van mis servicios?",
        "¿Qué significa estado 'En Proceso'?",
        "¿Cuáles son los costos y tarifas?",
        "Hablar con un asesor humano",
        "¿Cuáles son los horarios de atención?"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = when (chatEstado) {
                                EstadoChat.ATENCION_IA -> MaterialTheme.colorScheme.primaryContainer
                                EstadoChat.ESPERANDO_ADMINISTRADOR -> Color(0xFFFEF3C7)
                                EstadoChat.ATENCION_HUMANA -> Color(0xFFE0F2FE)
                                EstadoChat.RESUELTA -> Color(0xFFDCFCE7)
                            },
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = when (chatEstado) {
                                        EstadoChat.ATENCION_IA -> Icons.Default.SmartToy
                                        EstadoChat.ESPERANDO_ADMINISTRADOR -> Icons.Default.HourglassTop
                                        EstadoChat.ATENCION_HUMANA -> Icons.Default.SupportAgent
                                        EstadoChat.RESUELTA -> Icons.Default.CheckCircle
                                    },
                                    contentDescription = "Estado del Chat",
                                    tint = when (chatEstado) {
                                        EstadoChat.ATENCION_IA -> MaterialTheme.colorScheme.primary
                                        EstadoChat.ESPERANDO_ADMINISTRADOR -> Color(0xFFD97706)
                                        EstadoChat.ATENCION_HUMANA -> ServixBluePrimary
                                        EstadoChat.RESUELTA -> Color(0xFF16A34A)
                                    },
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Asistente ServiX",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )

                            // Chip de Estado de la Conversación
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (chatEstado) {
                                    EstadoChat.ATENCION_IA -> Color(0xFFDCFCE7)
                                    EstadoChat.ESPERANDO_ADMINISTRADOR -> Color(0xFFFEF3C7)
                                    EstadoChat.ATENCION_HUMANA -> Color(0xFFE0F2FE)
                                    EstadoChat.RESUELTA -> Color(0xFFF1F5F9)
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (chatEstado) {
                                                    EstadoChat.ATENCION_IA -> Color(0xFF16A34A)
                                                    EstadoChat.ESPERANDO_ADMINISTRADOR -> Color(0xFFD97706)
                                                    EstadoChat.ATENCION_HUMANA -> ServixBluePrimary
                                                    EstadoChat.RESUELTA -> Color(0xFF64748B)
                                                }
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = chatEstado.displayName.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        color = when (chatEstado) {
                                            EstadoChat.ATENCION_IA -> Color(0xFF15803D)
                                            EstadoChat.ESPERANDO_ADMINISTRADOR -> Color(0xFFB45309)
                                            EstadoChat.ATENCION_HUMANA -> Color(0xFF0369A1)
                                            EstadoChat.RESUELTA -> Color(0xFF475569)
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("chat_back_button")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar"
                            )
                        }
                    }
                },
                actions = {
                    if (onSolicitarAtencionHumana != null && chatEstado == EstadoChat.ATENCION_IA) {
                        IconButton(
                            onClick = { showEscalationDialog = true },
                            modifier = Modifier.testTag("chat_request_human_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SupportAgent,
                                contentDescription = "Solicitar atención humana",
                                tint = ServixBluePrimary
                            )
                        }
                    }

                    if (onResolverChat != null && chatEstado != EstadoChat.RESUELTA && currentUserRole == UserRole.ADMINISTRADOR) {
                        IconButton(
                            onClick = onResolverChat,
                            modifier = Modifier.testTag("chat_resolve_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Marcar chat como resuelto",
                                tint = Color(0xFF16A34A)
                            )
                        }
                    }

                    IconButton(onClick = onClearChat, modifier = Modifier.testTag("chat_clear_button")) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Limpiar conversación"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Banner de Alerta si la conversación está en espera de asesor
            AnimatedVisibility(visible = chatEstado == EstadoChat.ESPERANDO_ADMINISTRADOR) {
                Surface(
                    color = Color(0xFFFEF3C7),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Conversación escalada: Tu caso está en la bandeja del equipo Administrador / Vendedor.",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = chatEstado == EstadoChat.ATENCION_HUMANA) {
                Surface(
                    color = Color(0xFFE0F2FE),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = ServixBluePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Atención Humana Activa: Estás conversando con el equipo de ServiX.",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = Color(0xFF0369A1)
                        )
                    }
                }
            }

            // Conversación
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 700.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        ChatBubble(message = msg)
                    }

                    if (isLoading) {
                        item {
                            TypingIndicator()
                        }
                    }
                }
            }

            // Preguntas Rápidas sugeridas
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickQuestions) { question ->
                    SuggestionChip(
                        onClick = {
                            if (!isLoading) {
                                if (question.contains("asesor humano", ignoreCase = true) && onSolicitarAtencionHumana != null) {
                                    showEscalationDialog = true
                                } else {
                                    onSendMessage(question)
                                }
                            }
                        },
                        label = { Text(question, style = MaterialTheme.typography.bodySmall) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        )
                    )
                }
            }

            // Barra de entrada de texto
            Surface(
                tonalElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 700.dp)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                if (chatEstado == EstadoChat.ATENCION_HUMANA)
                                    "Escribe a tu asesor humano..."
                                else
                                    "Escribe una consulta sobre tus servicios..."
                            )
                        },
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            val textToSend = inputText.trim()
                            if (textToSend.isNotBlank() && !isLoading) {
                                onSendMessage(textToSend)
                                inputText = ""
                            }
                        },
                        enabled = inputText.isNotBlank() && !isLoading,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar mensaje",
                            tint = if (inputText.isNotBlank() && !isLoading) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Modal para Solicitar Atención Humana Directa
    if (showEscalationDialog && onSolicitarAtencionHumana != null) {
        var motivo by remember { mutableStateOf("Consulta sobre mis servicios") }
        var mensaje by remember { mutableStateOf("") }
        var prioridad by remember { mutableStateOf(PrioridadAtencion.NORMAL) }

        AlertDialog(
            onDismissRequest = { showEscalationDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SupportAgent, contentDescription = null, tint = ServixBluePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Solicitar Asesor Humano",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Un integrante del equipo Administrador / Vendedor se unirá a este chat para brindarte atención personalizada.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = motivo,
                        onValueChange = { motivo = it },
                        label = { Text("Motivo de la solicitud") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = mensaje,
                        onValueChange = { mensaje = it },
                        label = { Text("Detalle o mensaje adicional") },
                        placeholder = { Text("Describe brevemente en qué podemos ayudarte...") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Nivel de Urgencia:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PrioridadAtencion.entries.forEach { p ->
                            FilterChip(
                                selected = prioridad == p,
                                onClick = { prioridad = p },
                                label = { Text(p.displayName) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSolicitarAtencionHumana(motivo.trim(), mensaje.trim().ifBlank { motivo.trim() }, prioridad)
                        showEscalationDialog = false
                    },
                    modifier = Modifier.testTag("btn_confirm_escalate")
                ) {
                    Text("Solicitar Atención")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEscalationDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormatter.format(Date(message.timestamp)) }

    // Mensaje del Sistema
    if (message.senderType == SenderType.SISTEMA) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.widthIn(max = 380.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = ServixBluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        return
    }

    val isClient = message.senderType == SenderType.CLIENTE
    val isAdmin = message.senderType == SenderType.ADMINISTRADOR_VENDEDOR
    val isAi = message.senderType == SenderType.AGENTE_IA

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isClient) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isClient) {
            Surface(
                shape = CircleShape,
                color = if (isAi) MaterialTheme.colorScheme.primaryContainer else Color(0xFFE0F2FE),
                modifier = Modifier
                    .size(34.dp)
                    .padding(bottom = 2.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isAi) Icons.Default.SmartToy else Icons.Default.SupportAgent,
                        contentDescription = if (isAi) "Agente IA" else "Administrador / Vendedor",
                        tint = if (isAi) MaterialTheme.colorScheme.primary else ServixBluePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isClient) 16.dp else 4.dp,
                bottomEnd = if (isClient) 4.dp else 16.dp
            ),
            color = when {
                isClient -> MaterialTheme.colorScheme.primary
                isAdmin -> Color(0xFFF0FDF4)
                else -> MaterialTheme.colorScheme.surfaceVariant
            },
            border = if (isAdmin) BorderStroke(1.dp, Color(0xFF16A34A).copy(alpha = 0.4f)) else null,
            tonalElevation = if (isClient) 2.dp else 1.dp,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                // Etiqueta distintiva del emisor
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when {
                            isClient -> "👤 Cliente"
                            isAdmin -> "👨‍💼 Administrador / Vendedor (${message.senderName.ifBlank { "Personal ServiX" }})"
                            else -> "🤖 Agente IA ServiX"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = when {
                            isClient -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                            isAdmin -> Color(0xFF15803D)
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        isClient -> MaterialTheme.colorScheme.onPrimary
                        isAdmin -> Color(0xFF14532D)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = if (isClient)
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }

        if (isClient) {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier
                    .size(34.dp)
                    .padding(bottom = 2.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Cliente",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "Agente IA",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "El Agente ServiX está pensando...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
