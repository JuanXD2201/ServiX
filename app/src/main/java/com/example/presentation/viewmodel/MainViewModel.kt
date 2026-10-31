package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Actividad
import com.example.domain.model.AppConfig
import com.example.domain.model.AuthUser
import com.example.domain.model.ChatMessage
import com.example.domain.model.Cliente
import com.example.domain.model.EstadoServicio
import com.example.domain.model.Notificacion
import com.example.domain.model.PrioridadServicio
import com.example.domain.model.Servicio
import com.example.domain.model.UserProfile
import com.example.domain.model.UserRole
import com.example.domain.repository.ActividadRepository
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.AuthResult
import com.example.domain.repository.ClienteRepository
import com.example.domain.repository.ConfigRepository
import com.example.domain.repository.NotificationRepository
import com.example.domain.repository.ServicioRepository
import com.example.domain.usecase.AddClienteUseCase
import com.example.domain.usecase.AddServicioUseCase
import com.example.domain.usecase.DeleteClienteUseCase
import com.example.domain.usecase.DeleteNotificacionUseCase
import com.example.domain.usecase.DeleteServicioUseCase
import com.example.domain.usecase.DuplicarServicioUseCase
import com.example.domain.usecase.MarcarNotificacionLeidaUseCase
import com.example.domain.usecase.MarcarOnboardingVistoUseCase
import com.example.domain.usecase.MarcarTodasNotificacionesLeidasUseCase
import com.example.domain.usecase.ResetDataUseCase
import com.example.domain.usecase.RestaurarClienteUseCase
import com.example.domain.usecase.RestaurarServicioUseCase
import com.example.domain.usecase.SendAiChatMessageUseCase
import com.example.domain.usecase.SendPasswordResetUseCase
import com.example.domain.usecase.SignInAnonymouslyUseCase
import com.example.domain.usecase.SignInUseCase
import com.example.domain.usecase.SignOutUseCase
import com.example.domain.usecase.SignUpUseCase
import com.example.domain.usecase.ToggleClienteFavoritoUseCase
import com.example.domain.usecase.ToggleServicioFavoritoUseCase
import com.example.domain.usecase.UpdateClienteUseCase
import com.example.domain.usecase.UpdateConfigUseCase
import com.example.domain.usecase.UpdateObjetivosUseCase
import com.example.domain.usecase.UpdateProfileUseCase
import com.example.domain.usecase.UpdateServicioEstadoUseCase
import com.example.domain.usecase.UpdateServicioUseCase
import com.example.core.di.AppContainer
import com.example.domain.model.EstadoAtencion
import com.example.domain.model.EstadoChat
import com.example.domain.model.PrioridadAtencion
import com.example.domain.model.SenderType
import com.example.domain.model.SolicitudAtencion
import com.example.domain.repository.AtencionRepository
import com.example.domain.repository.MakeWebhookRepository
import com.example.domain.usecase.CrearSolicitudAtencionUseCase
import com.example.domain.usecase.ResolverSolicitudAtencionUseCase
import com.example.domain.usecase.ResponderSolicitudAtencionUseCase
import com.example.domain.usecase.TomarSolicitudAtencionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository,
    private val clienteRepository: ClienteRepository,
    private val servicioRepository: ServicioRepository,
    private val notificationRepository: NotificationRepository,
    private val configRepository: ConfigRepository,
    private val actividadRepository: ActividadRepository,
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val sendPasswordResetUseCase: SendPasswordResetUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
    private val addClienteUseCase: AddClienteUseCase,
    private val updateClienteUseCase: UpdateClienteUseCase,
    private val deleteClienteUseCase: DeleteClienteUseCase,
    private val restaurarClienteUseCase: RestaurarClienteUseCase,
    private val toggleClienteFavoritoUseCase: ToggleClienteFavoritoUseCase,
    private val addServicioUseCase: AddServicioUseCase,
    private val updateServicioUseCase: UpdateServicioUseCase,
    private val deleteServicioUseCase: DeleteServicioUseCase,
    private val restaurarServicioUseCase: RestaurarServicioUseCase,
    private val updateServicioEstadoUseCase: UpdateServicioEstadoUseCase,
    private val duplicarServicioUseCase: DuplicarServicioUseCase,
    private val toggleServicioFavoritoUseCase: ToggleServicioFavoritoUseCase,
    private val marcarNotificacionLeidaUseCase: MarcarNotificacionLeidaUseCase,
    private val deleteNotificacionUseCase: DeleteNotificacionUseCase,
    private val marcarTodasNotificacionesLeidasUseCase: MarcarTodasNotificacionesLeidasUseCase,
    private val updateObjetivosUseCase: UpdateObjetivosUseCase,
    private val marcarOnboardingVistoUseCase: MarcarOnboardingVistoUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val updateConfigUseCase: UpdateConfigUseCase,
    private val resetDataUseCase: ResetDataUseCase,
    private val sendAiChatMessageUseCase: SendAiChatMessageUseCase? = null,
    private val makeWebhookRepository: MakeWebhookRepository = AppContainer.makeWebhookRepository,
    private val atencionRepository: AtencionRepository = AppContainer.atencionRepository,
    private val crearSolicitudAtencionUseCase: CrearSolicitudAtencionUseCase = AppContainer.crearSolicitudAtencionUseCase,
    private val tomarSolicitudAtencionUseCase: TomarSolicitudAtencionUseCase = AppContainer.tomarSolicitudAtencionUseCase,
    private val responderSolicitudAtencionUseCase: ResponderSolicitudAtencionUseCase = AppContainer.responderSolicitudAtencionUseCase,
    private val resolverSolicitudAtencionUseCase: ResolverSolicitudAtencionUseCase = AppContainer.resolverSolicitudAtencionUseCase
) : ViewModel() {

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    val currentUser: StateFlow<AuthUser?> = authRepository.currentUser
    val clientes: StateFlow<List<Cliente>> = clienteRepository.clientes
    val servicios: StateFlow<List<Servicio>> = servicioRepository.servicios
    val actividades: StateFlow<List<Actividad>> = actividadRepository.actividades
    val notificaciones: StateFlow<List<Notificacion>> = notificationRepository.notificaciones
    val userProfile: StateFlow<UserProfile> = configRepository.userProfile
    val appConfig: StateFlow<AppConfig> = configRepository.appConfig
    val solicitudes: StateFlow<List<SolicitudAtencion>> = atencionRepository.solicitudes

    private val _chatEstado = MutableStateFlow<EstadoChat>(EstadoChat.ATENCION_IA)
    val chatEstado: StateFlow<EstadoChat> = _chatEstado.asStateFlow()

    // Chat con Agente IA / Atención Humana
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "¡Hola! Soy tu Asistente Virtual ServiX con Inteligencia Artificial. ¿En qué puedo orientarte hoy? Puedes consultarme sobre el estado de tus servicios, precios, tiempos de entrega o funcionamiento de la app.",
                isFromUser = false,
                senderType = SenderType.AGENTE_IA,
                senderName = "Agente IA"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    fun sendChatMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank() || _isChatLoading.value) return

        val senderType = if (currentUser.value?.role == UserRole.CLIENTE) SenderType.CLIENTE else SenderType.ADMINISTRADOR_VENDEDOR
        val senderName = currentUser.value?.displayName ?: if (senderType == SenderType.CLIENTE) "Cliente" else "Administrador / Vendedor"

        val userMsg = ChatMessage(
            text = trimmed,
            isFromUser = true,
            senderType = senderType,
            senderName = senderName
        )
        _chatMessages.value = _chatMessages.value + userMsg
        _isChatLoading.value = true

        viewModelScope.launch {
            val isClient = currentUser.value?.role == UserRole.CLIENTE
            val outcome = sendAiChatMessageUseCase?.invoke(
                userMessage = trimmed,
                history = _chatMessages.value,
                isClient = isClient,
                clientId = currentUser.value?.uid,
                clientEmail = currentUser.value?.email,
                clientName = currentUser.value?.displayName,
                servicios = servicios.value,
                clientes = clientes.value,
                userProfile = userProfile.value
            )

            if (outcome != null) {
                if (outcome.fueEscalado) {
                    _chatEstado.value = EstadoChat.ESPERANDO_ADMINISTRADOR
                }
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    text = outcome.responseText,
                    isFromUser = false,
                    senderType = SenderType.AGENTE_IA,
                    senderName = "Agente IA"
                )
            } else {
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    text = "Disculpa, ocurrió un inconveniente temporal al consultar la información. Por favor inténtalo de nuevo o solicita atención personalizada.",
                    isFromUser = false,
                    senderType = SenderType.AGENTE_IA,
                    senderName = "Agente IA"
                )
            }
            _isChatLoading.value = false
        }
    }

    fun solicitarAtencionHumana(
        motivo: String = "Solicitud de atención personalizada",
        mensaje: String = "El cliente solicita comunicarse con un asesor humano.",
        prioridad: PrioridadAtencion = PrioridadAtencion.NORMAL
    ) {
        val clienteId = currentUser.value?.uid ?: "cliente-anonimo"
        val clienteNombre = currentUser.value?.displayName ?: "Cliente"
        val clienteCorreo = currentUser.value?.email ?: "cliente@servix.com"

        val solicitud = crearSolicitudAtencionUseCase(
            clienteId = clienteId,
            clienteNombre = clienteNombre,
            clienteCorreo = clienteCorreo,
            motivo = motivo,
            mensaje = mensaje,
            prioridad = prioridad
        )
        _chatEstado.value = EstadoChat.ESPERANDO_ADMINISTRADOR
        _chatMessages.value = _chatMessages.value + ChatMessage(
            text = "👨‍💼 Has solicitado atención con un **Administrador / Vendedor**.\n• Motivo: $motivo\n• Prioridad: ${prioridad.displayName}\n• Radicado: #${solicitud.id.take(8).uppercase()}\n\nTu conversación ha sido puesta en espera para nuestro equipo. Te responderemos directamente por este chat en cuanto un asesor esté disponible.",
            isFromUser = false,
            senderType = SenderType.SISTEMA,
            senderName = "ServiX"
        )
    }

    fun solicitarServicioCliente(
        nombre: String,
        categoria: String,
        descripcion: String,
        prioridad: PrioridadServicio,
        fecha: String,
        observaciones: String,
        onSuccess: () -> Unit = {}
    ) {
        val clienteEmail = currentUser.value?.email ?: ""
        val clienteNombre = currentUser.value?.displayName ?: "Cliente ServiX"
        val clienteUid = currentUser.value?.uid ?: ""

        val clienteActual = clientes.value.find {
            (clienteEmail.isNotBlank() && it.correo.equals(clienteEmail, ignoreCase = true)) ||
            it.clienteId == clienteUid
        } ?: addCliente(
            nombre = clienteNombre,
            correo = clienteEmail,
            telefono = currentUser.value?.phone ?: "",
            documento = "",
            direccion = "",
            notas = "Cliente creado desde Portal ServiX"
        )

        addServicio(
            clienteId = clienteActual.clienteId,
            nombre = nombre,
            descripcion = descripcion,
            precio = 0.0,
            fecha = fecha.ifBlank {
                java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
            },
            estado = EstadoServicio.PENDIENTE,
            prioridad = prioridad,
            observaciones = observaciones,
            categoria = categoria.ifBlank { "General" },
            fechaEntrega = ""
        )
        onSuccess()
    }

    fun tomarSolicitudAtencion(solicitudId: String) {
        val adminNombre = userProfile.value.nombre.ifBlank { "Administrador / Vendedor" }
        tomarSolicitudAtencionUseCase(solicitudId, adminNombre)
        _chatEstado.value = EstadoChat.ATENCION_HUMANA
        _chatMessages.value = _chatMessages.value + ChatMessage(
            text = "👨‍💼 El asesor **$adminNombre** (Administrador / Vendedor) ha tomado tu conversación y te está atendiendo personalmente.",
            isFromUser = false,
            senderType = SenderType.SISTEMA,
            senderName = "ServiX"
        )
    }

    fun responderSolicitudAtencion(solicitudId: String, respuesta: String) {
        if (respuesta.isBlank()) return
        val adminNombre = userProfile.value.nombre.ifBlank { "Administrador / Vendedor" }
        responderSolicitudAtencionUseCase(solicitudId, respuesta, adminNombre)
        _chatEstado.value = EstadoChat.ATENCION_HUMANA
        _chatMessages.value = _chatMessages.value + ChatMessage(
            text = respuesta,
            isFromUser = false,
            senderType = SenderType.ADMINISTRADOR_VENDEDOR,
            senderName = adminNombre
        )
    }

    fun resolverSolicitudAtencion(solicitudId: String, notasResolucion: String = "") {
        resolverSolicitudAtencionUseCase(solicitudId, notasResolucion)
        _chatEstado.value = EstadoChat.RESUELTA
        _chatMessages.value = _chatMessages.value + ChatMessage(
            text = "✅ Esta solicitud de atención ha sido marcada como **Resuelta** por el Administrador / Vendedor. ¡Gracias por confiar en ServiX!",
            isFromUser = false,
            senderType = SenderType.SISTEMA,
            senderName = "ServiX"
        )
    }

    fun clearAuthError() {
        _authUiState.value = AuthUiState.Idle
    }

    fun clearChat() {
        _chatEstado.value = EstadoChat.ATENCION_IA
        _chatMessages.value = listOf(
            ChatMessage(
                text = "¡Hola! Soy tu Asistente Virtual ServiX con Inteligencia Artificial. ¿En qué puedo orientarte hoy?",
                isFromUser = false,
                senderType = SenderType.AGENTE_IA,
                senderName = "Agente IA"
            )
        )
    }

    fun signIn(email: String, pass: String, requestedRole: UserRole? = null, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            when (val res = signInUseCase(email, pass, requestedRole)) {
                is AuthResult.Success -> {
                    _authUiState.value = AuthUiState.Authenticated(res.user)
                    updateProfile(
                        userProfile.value.copy(
                            nombre = res.user.displayName,
                            correo = res.user.email,
                            telefono = if (res.user.phone.isNotBlank()) res.user.phone else userProfile.value.telefono
                        )
                    )
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _authUiState.value = AuthUiState.Error(res.message)
                }
            }
        }
    }

    fun signUp(
        name: String,
        email: String,
        pass: String,
        phone: String = "",
        role: UserRole = UserRole.ADMINISTRADOR,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            when (val res = signUpUseCase(name, email, pass, phone, role)) {
                is AuthResult.Success -> {
                    _authUiState.value = AuthUiState.Authenticated(res.user)
                    updateProfile(
                        userProfile.value.copy(
                            nombre = res.user.displayName,
                            correo = res.user.email,
                            telefono = if (res.user.phone.isNotBlank()) res.user.phone else userProfile.value.telefono
                        )
                    )
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _authUiState.value = AuthUiState.Error(res.message)
                }
            }
        }
    }

    fun signOut(onSignedOut: () -> Unit = {}) {
        viewModelScope.launch {
            signOutUseCase()
            _authUiState.value = AuthUiState.Idle
            onSignedOut()
        }
    }

    fun sendPasswordReset(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = sendPasswordResetUseCase(email)
            if (res.isSuccess) {
                onResult(true, "Enlace de recuperación enviado a tu correo.")
            } else {
                val errorMsg = res.exceptionOrNull()?.localizedMessage ?: "Error al enviar correo de recuperación."
                onResult(false, errorMsg)
            }
        }
    }

    fun signInAnonymously(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            when (val res = signInAnonymouslyUseCase()) {
                is AuthResult.Success -> {
                    _authUiState.value = AuthUiState.Authenticated(res.user)
                    onSuccess()
                }
                is AuthResult.Error -> {
                    _authUiState.value = AuthUiState.Error(res.message)
                }
            }
        }
    }

    fun addCliente(
        nombre: String,
        documento: String = "",
        telefono: String = "",
        correo: String = "",
        direccion: String = "",
        notas: String = "",
        esFavorito: Boolean = false
    ): Cliente {
        return addClienteUseCase(nombre, documento, telefono, correo, direccion, notas, esFavorito)
    }

    fun updateCliente(cliente: Cliente) {
        updateClienteUseCase(cliente)
    }

    fun toggleClienteFavorito(clienteId: String) {
        toggleClienteFavoritoUseCase(clienteId)
    }

    fun deleteCliente(clienteId: String): Pair<Cliente?, List<Servicio>> {
        return deleteClienteUseCase(clienteId)
    }

    fun restaurarCliente(cliente: Cliente, serviciosAsociados: List<Servicio>) {
        restaurarClienteUseCase(cliente, serviciosAsociados)
    }

    fun addServicio(
        clienteId: String,
        nombre: String,
        descripcion: String = "",
        precio: Double = 0.0,
        fecha: String = "",
        estado: EstadoServicio = EstadoServicio.PENDIENTE,
        prioridad: PrioridadServicio = PrioridadServicio.MEDIA,
        observaciones: String = "",
        categoria: String = "",
        fechaEntrega: String = "",
        esFavorito: Boolean = false
    ): Servicio {
        return addServicioUseCase(
            clienteId,
            nombre,
            descripcion,
            precio,
            fecha,
            estado,
            prioridad,
            observaciones,
            categoria,
            fechaEntrega,
            esFavorito
        )
    }

    fun updateServicio(servicio: Servicio) {
        updateServicioUseCase(servicio)
    }

    fun toggleServicioFavorito(servicioId: String) {
        toggleServicioFavoritoUseCase(servicioId)
    }

    fun duplicarServicio(servicioId: String): Servicio? {
        return duplicarServicioUseCase(servicioId)
    }

    fun updateServicioEstado(servicioId: String, nuevoEstado: EstadoServicio) {
        updateServicioEstadoUseCase(servicioId, nuevoEstado)
    }

    fun deleteServicio(servicioId: String): Servicio? {
        return deleteServicioUseCase(servicioId)
    }

    fun restaurarServicio(servicio: Servicio) {
        restaurarServicioUseCase(servicio)
    }

    fun marcarNotificacionLeida(notificacionId: String) {
        marcarNotificacionLeidaUseCase(notificacionId)
    }

    fun deleteNotificacion(notificacionId: String) {
        deleteNotificacionUseCase(notificacionId)
    }

    fun marcarTodasNotificacionesLeidas() {
        marcarTodasNotificacionesLeidasUseCase()
    }

    fun updateObjetivos(servicios: Int, ingresos: Double) {
        updateObjetivosUseCase(servicios, ingresos)
    }

    fun marcarOnboardingVisto(visto: Boolean) {
        marcarOnboardingVistoUseCase(visto)
    }

    fun updateProfile(profile: UserProfile) {
        updateProfileUseCase(profile)
    }

    fun updateConfig(config: AppConfig) {
        updateConfigUseCase(config)
    }

    fun resetData() {
        resetDataUseCase()
    }

    fun sendMakeReport(servicioId: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val servicio = servicioRepository.getServicioById(servicioId)
            if (servicio == null) {
                onResult(false, "Servicio no encontrado.")
                return@launch
            }
            val cliente = clienteRepository.getClienteById(servicio.clienteId)
            val result = makeWebhookRepository.sendServicioEventSync(
                evento = "SERVICIO_COMPLETADO",
                servicio = servicio.copy(estado = EstadoServicio.COMPLETADO),
                cliente = cliente,
                tecnico = userProfile.value,
                usuarioId = currentUser.value?.uid
            )
            result.fold(
                onSuccess = {
                    if (servicio.estado != EstadoServicio.COMPLETADO) {
                        updateServicioEstado(servicioId, EstadoServicio.COMPLETADO)
                    }
                    onResult(true, "¡Reporte generado en Make con éxito! (Google Sheets y correo enviados)")
                },
                onFailure = { err ->
                    onResult(false, "Error al comunicar con Make: ${err.message ?: "Sin respuesta"}")
                }
            )
        }
    }

    fun testMakeConnection(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val testService = com.example.domain.model.Servicio(
                servicioId = "srv-" + java.util.UUID.randomUUID().toString().take(8),
                clienteId = "cli-test",
                nombre = "Mantenimiento Preventivo Servix Make",
                descripcion = "Comprobación de flujo: App -> Make -> Sheets -> Correo",
                categoria = "Climatización",
                precio = 240000.0,
                fecha = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
                fechaEntrega = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
                estado = EstadoServicio.COMPLETADO,
                prioridad = PrioridadServicio.ALTA,
                observaciones = "Prueba de integración exitosa"
            )
            val testClient = com.example.domain.model.Cliente(
                clienteId = "cli-test",
                nombre = currentUser.value?.displayName ?: "Usuario de Prueba",
                correo = currentUser.value?.email ?: "sin-correo@servix.invalid",
                telefono = "+57 300 123 4567"
            )
            val result = makeWebhookRepository.sendServicioEventSync(
                evento = "SERVICIO_COMPLETADO",
                servicio = testService,
                cliente = testClient,
                tecnico = userProfile.value,
                usuarioId = currentUser.value?.uid
            )
            result.fold(
                onSuccess = {
                    onResult(true, "¡Conexión con Make exitosa (HTTP 200)! Se ejecutó el escenario completo.")
                },
                onFailure = { err ->
                    onResult(false, "Error al conectar con Make: ${err.message ?: "Sin respuesta"}")
                }
            )
        }
    }
}
