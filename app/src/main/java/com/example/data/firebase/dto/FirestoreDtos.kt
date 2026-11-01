package com.example.data.firebase.dto

import com.example.domain.model.Cliente
import com.example.domain.model.EstadoServicio
import com.example.domain.model.Notificacion
import com.example.domain.model.PrioridadServicio
import com.example.domain.model.Servicio
import com.example.domain.model.TipoActividad
import com.example.domain.model.TipoNotificacion
import com.example.domain.model.Actividad
import com.example.domain.model.AppConfig
import com.example.domain.model.AuthUser
import com.example.domain.model.UserProfile
import com.example.domain.model.UserRole

data class UsuarioDto(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val phone: String = "",
    val photoUrl: String? = null,
    val role: String = "CLIENTE",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): AuthUser = AuthUser(
        uid = uid,
        email = email,
        displayName = displayName,
        phone = phone,
        photoUrl = photoUrl,
        role = UserRole.fromString(role),
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(u: AuthUser): UsuarioDto = UsuarioDto(
            uid = u.uid,
            email = u.email,
            displayName = u.displayName,
            phone = u.phone,
            photoUrl = u.photoUrl,
            role = u.role.name,
            createdAt = u.createdAt
        )
    }
}

data class ClienteDto(
    val clienteId: String = "",
    val nombre: String = "",
    val documento: String = "",
    val telefono: String = "",
    val correo: String = "",
    val direccion: String = "",
    val notas: String = "",
    val fechaRegistro: Long = System.currentTimeMillis(),
    val activo: Boolean = true,
    val esFavorito: Boolean = false,
    val fotoBase64: String? = null
) {
    fun toDomain(): Cliente = Cliente(
        clienteId = clienteId,
        nombre = nombre,
        documento = documento,
        telefono = telefono,
        correo = correo,
        direccion = direccion,
        notas = notas,
        fechaRegistro = fechaRegistro,
        activo = activo,
        esFavorito = esFavorito,
        fotoBase64 = fotoBase64
    )

    companion object {
        fun fromDomain(c: Cliente): ClienteDto = ClienteDto(
            clienteId = c.clienteId,
            nombre = c.nombre,
            documento = c.documento,
            telefono = c.telefono,
            correo = c.correo,
            direccion = c.direccion,
            notas = c.notas,
            fechaRegistro = c.fechaRegistro,
            activo = c.activo,
            esFavorito = c.esFavorito,
            fotoBase64 = c.fotoBase64
        )
    }
}

data class ServicioDto(
    val servicioId: String = "",
    val clienteId: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val categoria: String = "General",
    val precio: Double = 0.0,
    val fecha: String = "",
    val fechaEntrega: String = "",
    val estado: String = EstadoServicio.PENDIENTE.name,
    val prioridad: String = PrioridadServicio.MEDIA.name,
    val observaciones: String = "",
    val esFavorito: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): Servicio = Servicio(
        servicioId = servicioId,
        clienteId = clienteId,
        nombre = nombre,
        descripcion = descripcion,
        categoria = categoria,
        precio = precio,
        fecha = fecha,
        fechaEntrega = fechaEntrega,
        estado = try { EstadoServicio.valueOf(estado) } catch (_: Exception) { EstadoServicio.PENDIENTE },
        prioridad = try { PrioridadServicio.valueOf(prioridad) } catch (_: Exception) { PrioridadServicio.MEDIA },
        observaciones = observaciones,
        esFavorito = esFavorito,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(s: Servicio): ServicioDto = ServicioDto(
            servicioId = s.servicioId,
            clienteId = s.clienteId,
            nombre = s.nombre,
            descripcion = s.descripcion,
            categoria = s.categoria,
            precio = s.precio,
            fecha = s.fecha,
            fechaEntrega = s.fechaEntrega,
            estado = s.estado.name,
            prioridad = s.prioridad.name,
            observaciones = s.observaciones,
            esFavorito = s.esFavorito,
            timestamp = s.timestamp
        )
    }
}

data class NotificacionDto(
    val id: String = "",
    val tipo: String = TipoNotificacion.SERVICIO_HOY.name,
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val leida: Boolean = false,
    val referenciaId: String? = null,
    val tipoReferencia: String = "servicio"
) {
    fun toDomain(): Notificacion = Notificacion(
        id = id,
        tipo = try { TipoNotificacion.valueOf(tipo) } catch (_: Exception) { TipoNotificacion.SERVICIO_HOY },
        titulo = titulo,
        descripcion = descripcion,
        fecha = fecha,
        leida = leida,
        referenciaId = referenciaId,
        tipoReferencia = tipoReferencia
    )

    companion object {
        fun fromDomain(n: Notificacion): NotificacionDto = NotificacionDto(
            id = n.id,
            tipo = n.tipo.name,
            titulo = n.titulo,
            descripcion = n.descripcion,
            fecha = n.fecha,
            leida = n.leida,
            referenciaId = n.referenciaId,
            tipoReferencia = n.tipoReferencia
        )
    }
}

data class ActividadDto(
    val id: String = "",
    val tipo: String = TipoActividad.SERVICIO_CREADO.name,
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: Long = System.currentTimeMillis()
) {
    fun toDomain(): Actividad = Actividad(
        id = id,
        tipo = try { TipoActividad.valueOf(tipo) } catch (_: Exception) { TipoActividad.SERVICIO_CREADO },
        titulo = titulo,
        descripcion = descripcion,
        fecha = fecha
    )

    companion object {
        fun fromDomain(a: Actividad): ActividadDto = ActividadDto(
            id = a.id,
            tipo = a.tipo.name,
            titulo = a.titulo,
            descripcion = a.descripcion,
            fecha = a.fecha
        )
    }
}

data class UserProfileDto(
    val nombre: String = "Carlos Alberto Vega",
    val rol: String = "Técnico Especialista en Climatización y Redes",
    val especialidad: String = "Técnico Especialista en Climatización y Redes",
    val empresa: String = "Vega Servicios Técnicos",
    val telefono: String = "+57 310 456 7890",
    val correo: String = "carlos.vega@servixapp.com",
    val ciudad: String = "Medellín, Colombia",
    val descripcion: String = "Más de 8 años de experiencia en mantenimiento preventivo, correctivo y montaje de sistemas residenciales y comerciales.",
    val fotoPerfilBase64: String? = null
) {
    fun toDomain(): UserProfile = UserProfile(
        nombre = nombre,
        rol = rol,
        especialidad = especialidad,
        empresa = empresa,
        telefono = telefono,
        correo = correo,
        ciudad = ciudad,
        descripcion = descripcion,
        fotoPerfilBase64 = fotoPerfilBase64
    )

    companion object {
        fun fromDomain(p: UserProfile): UserProfileDto = UserProfileDto(
            nombre = p.nombre,
            rol = p.rol,
            especialidad = p.especialidad,
            empresa = p.empresa,
            telefono = p.telefono,
            correo = p.correo,
            ciudad = p.ciudad,
            descripcion = p.descripcion,
            fotoPerfilBase64 = p.fotoPerfilBase64
        )
    }
}

data class AppConfigDto(
    val monedaSimbolo: String = "$",
    val notificacionesActivas: Boolean = true,
    val sonidos: Boolean = true,
    val modoOscuroForzado: Boolean = false,
    val autoguardadoLocal: Boolean = true,
    val onboardingVisto: Boolean = true,
    val objetivoServiciosSemanal: Int = 10,
    val objetivoIngresosSemanal: Double = 1200000.0
) {
    fun toDomain(): AppConfig = AppConfig(
        monedaSimbolo = monedaSimbolo,
        notificacionesActivas = notificacionesActivas,
        sonidos = sonidos,
        modoOscuroForzado = modoOscuroForzado,
        autoguardadoLocal = autoguardadoLocal,
        onboardingVisto = onboardingVisto,
        objetivoServiciosSemanal = objetivoServiciosSemanal,
        objetivoIngresosSemanal = objetivoIngresosSemanal
    )

    companion object {
        fun fromDomain(c: AppConfig): AppConfigDto = AppConfigDto(
            monedaSimbolo = c.monedaSimbolo,
            notificacionesActivas = c.notificacionesActivas,
            sonidos = c.sonidos,
            modoOscuroForzado = c.modoOscuroForzado,
            autoguardadoLocal = c.autoguardadoLocal,
            onboardingVisto = c.onboardingVisto,
            objetivoServiciosSemanal = c.objetivoServiciosSemanal,
            objetivoIngresosSemanal = c.objetivoIngresosSemanal
        )
    }
}

data class SolicitudAtencionDto(
    val id: String = "",
    val clienteId: String = "",
    val clienteNombre: String = "",
    val clienteCorreo: String = "",
    val fechaHora: Long = System.currentTimeMillis(),
    val motivo: String = "",
    val mensaje: String = "",
    val prioridad: String = "NORMAL",
    val estado: String = "PENDIENTE",
    val respuestaAdmin: String = "",
    val atendidoPor: String = "",
    val fechaResolucion: Long? = null
) {
    fun toDomain(): com.example.domain.model.SolicitudAtencion = com.example.domain.model.SolicitudAtencion(
        id = id,
        clienteId = clienteId,
        clienteNombre = clienteNombre,
        clienteCorreo = clienteCorreo,
        fechaHora = fechaHora,
        motivo = motivo,
        mensaje = mensaje,
        prioridad = com.example.domain.model.PrioridadAtencion.fromString(prioridad),
        estado = com.example.domain.model.EstadoAtencion.fromString(estado),
        respuestaAdmin = respuestaAdmin,
        atendidoPor = atendidoPor,
        fechaResolucion = fechaResolucion
    )

    companion object {
        fun fromDomain(s: com.example.domain.model.SolicitudAtencion): SolicitudAtencionDto = SolicitudAtencionDto(
            id = s.id,
            clienteId = s.clienteId,
            clienteNombre = s.clienteNombre,
            clienteCorreo = s.clienteCorreo,
            fechaHora = s.fechaHora,
            motivo = s.motivo,
            mensaje = s.mensaje,
            prioridad = s.prioridad.name,
            estado = s.estado.name,
            respuestaAdmin = s.respuestaAdmin,
            atendidoPor = s.atendidoPor,
            fechaResolucion = s.fechaResolucion
        )
    }
}
