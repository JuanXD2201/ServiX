package com.example.domain.usecase

import com.example.domain.model.ChatMessage
import com.example.domain.model.Cliente
import com.example.domain.model.EstadoServicio
import com.example.domain.model.IntencionUsuario
import com.example.domain.model.PrioridadAtencion
import com.example.domain.model.Servicio
import com.example.domain.model.SolicitudAtencion
import com.example.domain.model.UserProfile
import com.example.domain.repository.AiChatRepository

data class AiChatOutcome(
    val responseText: String,
    val intencion: IntencionUsuario,
    val fueEscalado: Boolean,
    val solicitudGenerada: SolicitudAtencion? = null
)

class SendAiChatMessageUseCase(
    private val aiChatRepository: AiChatRepository,
    private val crearSolicitudAtencionUseCase: CrearSolicitudAtencionUseCase? = null
) {
    suspend operator fun invoke(
        userMessage: String,
        history: List<ChatMessage>,
        isClient: Boolean,
        clientId: String?,
        clientEmail: String?,
        clientName: String?,
        servicios: List<Servicio>,
        clientes: List<Cliente>,
        userProfile: UserProfile?
    ): AiChatOutcome {
        val trimmed = userMessage.trim()
        val intencion = clasificarIntencion(trimmed)

        // Evaluar si requiere escalamiento a Administrador / Vendedor
        val debeEscalar = isClient && evaluarEscalamiento(trimmed, intencion)

        if (debeEscalar && crearSolicitudAtencionUseCase != null) {
            val prioridad = determinarPrioridad(trimmed, intencion)
            val motivo = determinarMotivo(trimmed, intencion)

            val solicitud = crearSolicitudAtencionUseCase(
                clienteId = clientId ?: "cliente-anonimo",
                clienteNombre = clientName ?: "Cliente",
                clienteCorreo = clientEmail ?: "sin-correo@servix.com",
                motivo = motivo,
                mensaje = trimmed,
                prioridad = prioridad
            )

            val mensajeEscalamiento = buildString {
                append("Entiendo perfectamente la situación. ")
                when (intencion) {
                    IntencionUsuario.QUEJA -> append("Lamento los inconvenientes presentados con tu servicio. ")
                    IntencionUsuario.PROBLEMA -> append("He detectado un reporte de problema operativo. ")
                    IntencionUsuario.CANCELACION -> append("He recibido tu solicitud de cancelación. ")
                    IntencionUsuario.SOLICITUD_HUMANA -> append("Con gusto te transferiré con nuestro personal. ")
                    else -> append("Esta consulta requiere atención personalizada. ")
                }
                append("\n\nHe generado una **Solicitud de Atención Humana** con prioridad **${prioridad.displayName}**.")
                append("\n• **Motivo:** $motivo")
                append("\n• **Radicado:** ${solicitud.id.take(8).uppercase()}")
                append("\n\nTu conversación ha sido enviada al panel de **Administrador / Vendedor**. Un asesor se comunicará contigo directamente por este chat a la brevedad.")
            }

            return AiChatOutcome(
                responseText = mensajeEscalamiento,
                intencion = intencion,
                fueEscalado = true,
                solicitudGenerada = solicitud
            )
        }

        // Si no escala, consultar con el Agente IA contextualizado
        val systemContext = buildSystemContext(
            isClient = isClient,
            clientId = clientId,
            clientEmail = clientEmail,
            clientName = clientName,
            servicios = servicios,
            clientes = clientes,
            userProfile = userProfile,
            intencion = intencion
        )

        val result = aiChatRepository.getAiResponse(
            userMessage = trimmed,
            history = history,
            systemContext = systemContext
        )

        val responseText = result.getOrNull()
            ?: "Disculpa, ocurrió un inconveniente temporal al consultar los datos de ServiX. Por favor intenta de nuevo o solicita atención con un asesor humano."

        return AiChatOutcome(
            responseText = responseText,
            intencion = intencion,
            fueEscalado = false,
            solicitudGenerada = null
        )
    }

    private fun clasificarIntencion(text: String): IntencionUsuario {
        val q = text.lowercase()
        return when {
            q.contains("hablar con una persona") || q.contains("asesor") || q.contains("humano") ||
            q.contains("atención humana") || q.contains("administrador") || q.contains("vendedor") ||
            q.contains("operador") || q.contains("alguien real") -> IntencionUsuario.SOLICITUD_HUMANA

            q.contains("queja") || q.contains("reclamo") || q.contains("inconforme") ||
            q.contains("mal servicio") || q.contains("pésimo") || q.contains("pesimo") ||
            q.contains("denuncia") || q.contains("demanda") -> IntencionUsuario.QUEJA

            q.contains("cancelar") || q.contains("anular") || q.contains("dar de baja") ||
            q.contains("reembolso") || q.contains("devolución") || q.contains("devolucion") -> IntencionUsuario.CANCELACION

            q.contains("problema") || q.contains("falla") || q.contains("daño") || q.contains("dañado") ||
            q.contains("retrasado") || q.contains("tarde") || q.contains("no han llegado") ||
            q.contains("no vinieron") || q.contains("precio no es correcto") || q.contains("cobro mal") ||
            q.contains("cobro incorrecto") -> IntencionUsuario.PROBLEMA

            q.contains("error en la app") || q.contains("no carga") || q.contains("pantalla negra") ||
            q.contains("se cierra") -> IntencionUsuario.ERROR_APP

            q.contains("estado") || q.contains("cómo va mi") || q.contains("como va mi") ||
            q.contains("en qué estado") || q.contains("en que estado") || q.contains("cuándo terminan") -> IntencionUsuario.CONSULTA_ESTADO

            q.contains("mi servicio") || q.contains("mis servicios") || q.contains("mis ordenes") ||
            q.contains("mi orden") || q.contains("solicitud") -> IntencionUsuario.CONSULTA_SERVICIO

            q.contains("nuevo servicio") || q.contains("solicitar servicio") || q.contains("agendar") ||
            q.contains("cotizar") || q.contains("cuanto cuesta") || q.contains("precio de") -> IntencionUsuario.NUEVA_SOLICITUD

            q.contains("horario") || q.contains("contacto") || q.contains("garantia") ||
            q.contains("garantía") || q.contains("telefono") || q.contains("teléfono") ||
            q.contains("direccion") || q.contains("dirección") || q.contains("que es servix") -> IntencionUsuario.CONSULTA_GENERAL

            else -> IntencionUsuario.OTRO
        }
    }

    private fun evaluarEscalamiento(text: String, intencion: IntencionUsuario): Boolean {
        return when (intencion) {
            IntencionUsuario.SOLICITUD_HUMANA,
            IntencionUsuario.QUEJA,
            IntencionUsuario.CANCELACION,
            IntencionUsuario.PROBLEMA -> true
            IntencionUsuario.ERROR_APP -> true
            else -> {
                val q = text.lowercase()
                q.contains("hablar con alguien") || q.contains("comunicarme con un asesor") ||
                q.contains("inconformidad") || q.contains("retraso grave")
            }
        }
    }

    private fun determinarPrioridad(text: String, intencion: IntencionUsuario): PrioridadAtencion {
        val q = text.lowercase()
        return when {
            q.contains("urgente") || q.contains("inmediatamente") || q.contains("grave") ||
            q.contains("emergencia") || q.contains("falla crítica") || q.contains("cobro indebido") -> PrioridadAtencion.URGENTE

            intencion == IntencionUsuario.QUEJA || intencion == IntencionUsuario.CANCELACION ||
            intencion == IntencionUsuario.PROBLEMA || q.contains("retrasado") || q.contains("inconforme") -> PrioridadAtencion.IMPORTANTE

            else -> PrioridadAtencion.NORMAL
        }
    }

    private fun determinarMotivo(text: String, intencion: IntencionUsuario): String {
        return when (intencion) {
            IntencionUsuario.QUEJA -> "Queja sobre la atención o calidad del servicio"
            IntencionUsuario.CANCELACION -> "Solicitud de cancelación o anulación de orden"
            IntencionUsuario.PROBLEMA -> "Reporte de problema, retraso o discrepancia en servicio"
            IntencionUsuario.SOLICITUD_HUMANA -> "Solicitud directa de comunicación con asesor humano"
            IntencionUsuario.ERROR_APP -> "Reporte de incidente técnico en la aplicación móvil"
            else -> "Atención personalizada requerida"
        }
    }

    private fun buildSystemContext(
        isClient: Boolean,
        clientId: String?,
        clientEmail: String?,
        clientName: String?,
        servicios: List<Servicio>,
        clientes: List<Cliente>,
        userProfile: UserProfile?,
        intencion: IntencionUsuario
    ): String {
        val sb = StringBuilder()
        sb.append("Eres el Agente Virtual Inteligente de ServiX. No eres un usuario, eres un servicio inteligente del sistema. ")
        sb.append("Responde siempre en español de manera profesional, empática, cortés y concisa. ")
        sb.append("REGLA ESTRICTA: No inventes precios, datos de clientes ni estados de servicios que no existan. Si no conoces un dato, admítelo con transparencia. ")

        if (isClient) {
            sb.append("El usuario actual es un CLIENTE registrado llamado: ${clientName ?: "Cliente"} (correo: ${clientEmail ?: "no registrado"}). ")
            sb.append("SEGURIDAD Y PRIVACIDAD ABSOLUTA: Tienes terminantemente prohibido mencionar o exponer información de otros clientes o datos privados del negocio. ")

            // Filtrar únicamente los servicios del cliente
            val misServicios = servicios.filter { srv ->
                (clientId != null && srv.clienteId == clientId) ||
                (clientEmail != null && clientes.find { it.clienteId == srv.clienteId }?.correo?.equals(clientEmail, ignoreCase = true) == true)
            }

            if (misServicios.isNotEmpty()) {
                sb.append("Sus órdenes de servicio registradas son: ")
                misServicios.forEachIndexed { i, srv ->
                    sb.append("[Servicio ${i + 1}: Nombre='${srv.nombre}', Estado='${srv.estado.displayName}', Prioridad='${srv.prioridad.displayName}', Categoría='${srv.categoria}', Precio=$${srv.precio}, Fecha='${srv.fecha}', Entrega='${srv.fechaEntrega}', Observaciones='${srv.observaciones}']. ")
                }
            } else {
                sb.append("Actualmente este cliente no tiene servicios activos asignados a su cuenta en ServiX. ")
            }

            sb.append("Instrucciones para el cliente: Explica claramente los estados (Pendiente: registrado, En Proceso: técnico trabajando, Completado: terminado, Cancelado: anulado). ")
            sb.append("Si el cliente desea agendar una nueva orden, indícale los datos requeridos (categoría, descripción, dirección, fecha deseada).")
        } else {
            // Perfil Administrador / Vendedor
            sb.append("El usuario actual es del equipo ADMINISTRADOR / VENDEDOR de ServiX (Nombre: ${userProfile?.nombre ?: "Carlos Vega"}, Empresa: ${userProfile?.empresa ?: "ServiX"}). ")
            sb.append("Métricas del sistema: Clientes totales: ${clientes.size}, Servicios totales: ${servicios.size}. ")
            val pend = servicios.count { it.estado == EstadoServicio.PENDIENTE }
            val proc = servicios.count { it.estado == EstadoServicio.EN_PROCESO }
            val comp = servicios.count { it.estado == EstadoServicio.COMPLETADO }
            sb.append("Órdenes pendientes: $pend, En proceso: $proc, Completadas: $comp. ")
            sb.append("Puedes orientar al administrador en la gestión de órdenes, asignación técnica y atención de clientes.")
        }

        return sb.toString()
    }
}
