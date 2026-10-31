package com.example.data.mock

import com.example.domain.model.Actividad
import com.example.domain.model.AppConfig
import com.example.domain.model.Cliente
import com.example.domain.model.EstadoServicio
import com.example.domain.model.Notificacion
import com.example.domain.model.PrioridadServicio
import com.example.domain.model.Servicio
import com.example.domain.model.TipoActividad
import com.example.domain.model.TipoNotificacion
import com.example.domain.model.UserProfile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object MockDataStore {
  private val _clientes = MutableStateFlow<List<Cliente>>(emptyList())
  val clientes: StateFlow<List<Cliente>> = _clientes.asStateFlow()

  private val _servicios = MutableStateFlow<List<Servicio>>(emptyList())
  val servicios: StateFlow<List<Servicio>> = _servicios.asStateFlow()

  private val _actividades = MutableStateFlow<List<Actividad>>(emptyList())
  val actividades: StateFlow<List<Actividad>> = _actividades.asStateFlow()

  private val _notificaciones = MutableStateFlow<List<Notificacion>>(emptyList())
  val notificaciones: StateFlow<List<Notificacion>> = _notificaciones.asStateFlow()

  private val _userProfile = MutableStateFlow(UserProfile())
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  private val _appConfig = MutableStateFlow(AppConfig())
  val appConfig: StateFlow<AppConfig> = _appConfig.asStateFlow()
  init {
    loadInitialData()
  }

  fun loadInitialData() {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val now = System.currentTimeMillis()
    val dayMillis = 86400000L

    val todayStr = dateFormat.format(Date(now))
    val tomorrowStr = dateFormat.format(Date(now + dayMillis))
    val inTwoDaysStr = dateFormat.format(Date(now + 2 * dayMillis))
    val inFiveDaysStr = dateFormat.format(Date(now + 5 * dayMillis))
    val yesterdayStr = dateFormat.format(Date(now - dayMillis))
    val threeDaysAgoStr = dateFormat.format(Date(now - 3 * dayMillis))
    val oneWeekAgoStr = dateFormat.format(Date(now - 7 * dayMillis))
    val twoWeeksAgoStr = dateFormat.format(Date(now - 14 * dayMillis))

    val initialClientes = listOf(
      Cliente(
        clienteId = "cli-001",
        nombre = "Mariana Ríos Restrepo",
        documento = "1020485921",
        telefono = "+57 300 234 5678",
        correo = "mariana.rios@example.com",
        direccion = "Carrera 43A #1-50, El Poblado, Medellín",
        notas = "Cliente residencial preferencial. Solicita mantenimientos preventivos periódicos en su sistema de aire acondicionado.",
        fechaRegistro = now - 45 * dayMillis,
        esFavorito = true
      ),
      Cliente(
        clienteId = "cli-002",
        nombre = "Soluciones Digitales SAS",
        documento = "901345678-2",
        telefono = "+57 312 876 5432",
        correo = "contacto@soldigitales.co",
        direccion = "Calle 10 #32-15, Of. 402, El Poblado",
        notas = "Empresa corporativa. Contacto: Ing. Carlos Pérez. Facturación electrónica y soporte en horarios no laborales.",
        fechaRegistro = now - 30 * dayMillis,
        esFavorito = true
      ),
      Cliente(
        clienteId = "cli-003",
        nombre = "Felipe Gómez Zuluaga",
        documento = "71294812",
        telefono = "+57 315 456 7890",
        correo = "felipe.gomez@hotmail.com",
        direccion = "Circular 4 #73-20, Laureles, Medellín",
        notas = "Consultor independiente. Horarios de atención flexibles en fines de semana.",
        fechaRegistro = now - 20 * dayMillis,
        esFavorito = false
      ),
      Cliente(
        clienteId = "cli-004",
        nombre = "Industrias Metalmecánicas del Valle",
        documento = "890204512-1",
        telefono = "+57 320 654 3210",
        correo = "mantenimiento@metalvalle.com",
        direccion = "Zona Industrial Belén, Manzana 3 Bodega 12",
        notas = "Planta de manufactura. Exige EPP completo y certificado de trabajo en alturas.",
        fechaRegistro = now - 15 * dayMillis,
        esFavorito = true
      ),
      Cliente(
        clienteId = "cli-005",
        nombre = "Restaurante Gourmet La Terraza",
        documento = "900543210-8",
        telefono = "+57 301 987 6543",
        correo = "gerencia@laterraza.com.co",
        direccion = "Carrera 37 #8A-42, Vía Primavera",
        notas = "Equipos de refrigeración y climatización. Mantenimiento únicamente antes de 11:00 AM.",
        fechaRegistro = now - 60 * dayMillis,
        esFavorito = false
      ),
      Cliente(
        clienteId = "cli-006",
        nombre = "Dra. Claudia Marcela Hoyos",
        documento = "43567890",
        telefono = "+57 318 345 6789",
        correo = "odontologia.hoyos@example.com",
        direccion = "Torre Médica Las Américas, Consultorio 810",
        notas = "Consultorio odontológico. Requiere equipos de esterilización y autoclave certificados.",
        fechaRegistro = now - 10 * dayMillis,
        esFavorito = false
      ),
      Cliente(
        clienteId = "cli-007",
        nombre = "Condominio Campestre Verde Real",
        documento = "811002345-9",
        telefono = "+57 310 543 2109",
        correo = "admin@verdereal.com",
        direccion = "Km 5 Vía Llanogrande, Rionegro",
        notas = "Urbanización de 45 casas. Contacto con el administrador Andrés Salazar.",
        fechaRegistro = now - 12 * dayMillis,
        esFavorito = true
      ),
      Cliente(
        clienteId = "cli-008",
        nombre = "Javier Cardona Méndez",
        documento = "98765432",
        telefono = "+57 305 765 4321",
        correo = "jcardona.ing@example.com",
        direccion = "Calle 50 #40-15, Sabaneta",
        notas = "Trabajos domésticos en apartamento nuevo. Interesado en domótica y automatización.",
        fechaRegistro = now - 5 * dayMillis,
        esFavorito = false
      ),
      Cliente(
        clienteId = "cli-009",
        nombre = "Laura Camila Restrepo",
        documento = "1037654981",
        telefono = "+57 311 889 4433",
        correo = "laura.restrepo@arquitectura.com",
        direccion = "Transversal 39B #72-10, Laureles",
        notas = "Estudio de arquitectura e interiorismo. Busca instalaciones de iluminación LED.",
        fechaRegistro = now - 8 * dayMillis,
        esFavorito = false
      ),
      Cliente(
        clienteId = "cli-010",
        nombre = "Distribuidora Andina Logística",
        documento = "901556778-4",
        telefono = "+57 314 990 1234",
        correo = "operaciones@andinalog.com",
        direccion = "Parque Industrial Guarne, Módulo 8",
        notas = "Centro logístico con racks de servidores y cámaras de vigilancia perimetral.",
        fechaRegistro = now - 2 * dayMillis,
        esFavorito = false
      ),
      Cliente(
        clienteId = "cli-011",
        nombre = "Ingeniería & Proyectos Andinos SAS",
        documento = "900892341-5",
        telefono = "+57 317 400 9821",
        correo = "proyectos@ingandinos.co",
        direccion = "Edificio Square, Piso 11, El Poblado",
        notas = "Contratos corporativos de consultoría eléctrica e ingeniería civil. Facturación a 30 días.",
        fechaRegistro = now - 90 * dayMillis,
        esFavorito = true
      ),
      Cliente(
        clienteId = "cli-012",
        nombre = "Centro Médico Salud Integral",
        documento = "890983214-0",
        telefono = "+57 302 445 6712",
        correo = "infraestructura@saludintegral.org",
        direccion = "Av. Oriental #54-20, Medellín",
        notas = "Instalaciones médicas y quirófanos. Protocolo estricto de asepsia y energía ininterrumpida UPS.",
        fechaRegistro = now - 50 * dayMillis,
        esFavorito = false
      ),
      Cliente(
        clienteId = "cli-013",
        nombre = "Dra. Valeria Osorio Gómez",
        documento = "1017234589",
        telefono = "+57 319 887 3412",
        correo = "valeria.osorio@estetica.com",
        direccion = "Centro Empresarial San Fernando Plaza, Local 302",
        notas = "Clínica de estética láser. Requiere calibración periódica de estabilizadores de voltaje.",
        fechaRegistro = now - 18 * dayMillis,
        esFavorito = false
      )
    )

    val initialServicios = listOf(
      // Servicio para HOY - URGENTE
      Servicio(
        servicioId = "srv-102",
        clienteId = "cli-005",
        nombre = "Calibración Termostato Cuarto Frío",
        descripcion = "Ajuste de sensor digital Carel, lubricación de difusores y verificación de sello hermético de puerta.",
        categoria = "Reparación",
        precio = 240000.0,
        fecha = todayStr,
        fechaEntrega = todayStr,
        estado = EstadoServicio.PENDIENTE,
        prioridad = PrioridadServicio.URGENTE,
        observaciones = "Atención prioritaria antes de mediodía por inventario de alimentos perecederos.",
        esFavorito = true,
        timestamp = now - 4 * 3600000L
      ),
      // Servicio para HOY - ALTA
      Servicio(
        servicioId = "srv-101",
        clienteId = "cli-001",
        nombre = "Mantenimiento Preventivo A/C",
        descripcion = "Limpieza profunda de serpentines, verificación de gas ecológico R410A y revisión eléctrica del condensador.",
        categoria = "Mantenimiento",
        precio = 180000.0,
        fecha = todayStr,
        fechaEntrega = todayStr,
        estado = EstadoServicio.EN_PROCESO,
        prioridad = PrioridadServicio.ALTA,
        observaciones = "Llevar escalera de 4 pasos y kit manométrico. Coordinado para la tarde.",
        esFavorito = true,
        timestamp = now - 2 * 3600000L
      ),
      // Próximos servicios
      Servicio(
        servicioId = "srv-103",
        clienteId = "cli-002",
        nombre = "Instalación Cableado Estructurado",
        descripcion = "Tendido de 12 puntos de red Cat6A, peinado de rack y certificación con reflectómetro.",
        categoria = "Instalación",
        precio = 560000.0,
        fecha = tomorrowStr,
        fechaEntrega = inTwoDaysStr,
        estado = EstadoServicio.PENDIENTE,
        prioridad = PrioridadServicio.ALTA,
        observaciones = "Materiales ya comprados y entregados en recepción del edificio.",
        esFavorito = false,
        timestamp = now - dayMillis
      ),
      Servicio(
        servicioId = "srv-104",
        clienteId = "cli-008",
        nombre = "Automatización Iluminación Domótica",
        descripcion = "Instalación de 8 interruptores táctiles inteligentes WiFi y configuración de app móvil.",
        categoria = "Instalación",
        precio = 280000.0,
        fecha = inTwoDaysStr,
        fechaEntrega = inTwoDaysStr,
        estado = EstadoServicio.PENDIENTE,
        prioridad = PrioridadServicio.MEDIA,
        observaciones = "Se verificó la existencia de neutro en todas las cajas de paso.",
        esFavorito = false,
        timestamp = now - 2 * dayMillis
      ),
      Servicio(
        servicioId = "srv-105",
        clienteId = "cli-009",
        nombre = "Diseño de Iluminación Arquitectónica",
        descripcion = "Levantamiento planimétrico y diseño de perfilería LED indirecta con control dimmer.",
        categoria = "Diseño",
        precio = 420000.0,
        fecha = inFiveDaysStr,
        fechaEntrega = inFiveDaysStr,
        estado = EstadoServicio.PENDIENTE,
        prioridad = PrioridadServicio.BAJA,
        observaciones = "Reunión de aprobación conceptual fijada para las 10:00 AM.",
        esFavorito = false,
        timestamp = now - 3 * dayMillis
      ),
      // Servicios En Proceso
      Servicio(
        servicioId = "srv-106",
        clienteId = "cli-004",
        nombre = "Revisión Tableros de Potencia",
        descripcion = "Termografía infrarroja de interruptores electromagnéticos y ajuste de bornes con torquímetro.",
        categoria = "Mantenimiento",
        precio = 650000.0,
        fecha = yesterdayStr,
        fechaEntrega = inTwoDaysStr,
        estado = EstadoServicio.EN_PROCESO,
        prioridad = PrioridadServicio.URGENTE,
        observaciones = "70% completado. Pendiente informe de ingeniería con curvas de disparo.",
        esFavorito = true,
        timestamp = now - 18 * 3600000L
      ),
      Servicio(
        servicioId = "srv-107",
        clienteId = "cli-010",
        nombre = "Consultoría en Seguridad de Servidores",
        descripcion = "Auditoría de canalizaciones, UPS de respaldo de 6kVA y sistema de extinción por gas limpio.",
        categoria = "Consultoría",
        precio = 850000.0,
        fecha = threeDaysAgoStr,
        fechaEntrega = tomorrowStr,
        estado = EstadoServicio.EN_PROCESO,
        prioridad = PrioridadServicio.ALTA,
        observaciones = "Se entregó primer borrador de recomendaciones de infraestructura física.",
        esFavorito = false,
        timestamp = now - 2 * dayMillis
      ),
      // Servicios Completados (Historial & Facturación)
      Servicio(
        servicioId = "srv-108",
        clienteId = "cli-003",
        nombre = "Reparación Tablero Residencial",
        descripcion = "Sustitución de breaker principal de 50A y balanceo de fases de cocina eléctrica.",
        categoria = "Reparación",
        precio = 210000.0,
        fecha = threeDaysAgoStr,
        fechaEntrega = threeDaysAgoStr,
        estado = EstadoServicio.COMPLETADO,
        prioridad = PrioridadServicio.MEDIA,
        observaciones = "Pruebas de aislamiento satisfactorias. Recibo conforme firmado.",
        esFavorito = false,
        timestamp = now - 3 * dayMillis
      ),
      Servicio(
        servicioId = "srv-109",
        clienteId = "cli-006",
        nombre = "Instalación Eléctrica Autoclave",
        descripcion = "Circuito exclusivo de 220V con breaker diferencial de 30mA y varilla copperweld.",
        categoria = "Instalación",
        precio = 320000.0,
        fecha = oneWeekAgoStr,
        fechaEntrega = oneWeekAgoStr,
        estado = EstadoServicio.COMPLETADO,
        prioridad = PrioridadServicio.ALTA,
        observaciones = "Aprobado por el departamento de bioseguridad de la clínica.",
        esFavorito = false,
        timestamp = now - 7 * dayMillis
      ),
      Servicio(
        servicioId = "srv-110",
        clienteId = "cli-007",
        nombre = "Mantenimiento Bombas de Agua",
        descripcion = "Cambio de rodamientos SKF en bomba sumergible y calibración de presostatos Square D.",
        categoria = "Mantenimiento",
        precio = 750000.0,
        fecha = oneWeekAgoStr,
        fechaEntrega = oneWeekAgoStr,
        estado = EstadoServicio.COMPLETADO,
        prioridad = PrioridadServicio.ALTA,
        observaciones = "Presión de la red comunal estabilizada en 55 PSI.",
        esFavorito = true,
        timestamp = now - 8 * dayMillis
      ),
      Servicio(
        servicioId = "srv-111",
        clienteId = "cli-007",
        nombre = "Soporte Técnico Cámaras NVR",
        descripcion = "Actualización de firmware de grabador Hikvision y reconfiguración de DNS dinámico.",
        categoria = "Soporte",
        precio = 190000.0,
        fecha = twoWeeksAgoStr,
        fechaEntrega = twoWeeksAgoStr,
        estado = EstadoServicio.COMPLETADO,
        prioridad = PrioridadServicio.BAJA,
        observaciones = "App móvil funcionando correctamente en caseta de vigilancia.",
        esFavorito = false,
        timestamp = now - 14 * dayMillis
      ),
      Servicio(
        servicioId = "srv-112",
        clienteId = "cli-002",
        nombre = "Soporte Red WiFi Corporativa",
        descripcion = "Optimización de canales de 5GHz en access points UniFi y roaming sin cortes.",
        categoria = "Soporte",
        precio = 350000.0,
        fecha = twoWeeksAgoStr,
        fechaEntrega = twoWeeksAgoStr,
        estado = EstadoServicio.COMPLETADO,
        prioridad = PrioridadServicio.MEDIA,
        observaciones = "Velocidad media aumentada de 35 Mbps a 280 Mbps en salas de reuniones.",
        esFavorito = false,
        timestamp = now - 15 * dayMillis
      ),
      Servicio(
        servicioId = "srv-113",
        clienteId = "cli-004",
        nombre = "Reparación Arrancador Suave Motor",
        descripcion = "Sustitución de tiristor dañado en arrancador Siemens de 25HP de bomba centrífuga.",
        categoria = "Reparación",
        precio = 480000.0,
        fecha = twoWeeksAgoStr,
        fechaEntrega = twoWeeksAgoStr,
        estado = EstadoServicio.COMPLETADO,
        prioridad = PrioridadServicio.ALTA,
        observaciones = "Equipo puesto en marcha bajo carga con corriente nominal dentro de especificación.",
        esFavorito = false,
        timestamp = now - 16 * dayMillis
      ),
      // Servicios Cancelados / Otros
      Servicio(
        servicioId = "srv-114",
        clienteId = "cli-001",
        nombre = "Revisión Fuga de Tubería",
        descripcion = "Geolocalización acústica de filtración en muro divisorio de patio.",
        categoria = "Reparación",
        precio = 95000.0,
        fecha = twoWeeksAgoStr,
        fechaEntrega = twoWeeksAgoStr,
        estado = EstadoServicio.CANCELADO,
        prioridad = PrioridadServicio.BAJA,
        observaciones = "El daño era de la red comunal del edificio y la administración se hizo cargo.",
        esFavorito = false,
        timestamp = now - 17 * dayMillis
      ),
      Servicio(
        servicioId = "srv-115",
        clienteId = "cli-003",
        nombre = "Certificación RETIE Instalación",
        descripcion = "Inspección técnica preliminar para certificación de conformidad de instalación eléctrica.",
        categoria = "Consultoría",
        precio = 390000.0,
        fecha = threeDaysAgoStr,
        fechaEntrega = threeDaysAgoStr,
        estado = EstadoServicio.CANCELADO,
        prioridad = PrioridadServicio.MEDIA,
        observaciones = "Cliente pospuso la certificación para el próximo trimestre.",
        esFavorito = false,
        timestamp = now - 4 * dayMillis
      ),
      Servicio(
        servicioId = "srv-116",
        clienteId = "cli-011",
        nombre = "Estudio de Calidad de Energía Eléctrica",
        descripcion = "Monitoreo con analizador de redes Fluke 435 durante 72 horas para detección de armónicos y fluctuaciones.",
        categoria = "Consultoría",
        precio = 1250000.0,
        fecha = todayStr,
        fechaEntrega = inTwoDaysStr,
        estado = EstadoServicio.EN_PROCESO,
        prioridad = PrioridadServicio.ALTA,
        observaciones = "Instalado en acometida principal de planta de producción.",
        esFavorito = true,
        timestamp = now - 12 * 3600000L
      ),
      Servicio(
        servicioId = "srv-117",
        clienteId = "cli-012",
        nombre = "Mantenimiento Preventivo Sistema UPS Quirófano",
        descripcion = "Sustitución de banco de 16 baterías AGM de 12V 100Ah y calibración de inversor estático.",
        categoria = "Mantenimiento",
        precio = 980000.0,
        fecha = tomorrowStr,
        fechaEntrega = inTwoDaysStr,
        estado = EstadoServicio.PENDIENTE,
        prioridad = PrioridadServicio.URGENTE,
        observaciones = "Ventana de trabajo autorizada de 6:00 PM a 10:00 PM sin interrumpir cirugías.",
        esFavorito = true,
        timestamp = now - dayMillis
      ),
      Servicio(
        servicioId = "srv-118",
        clienteId = "cli-013",
        nombre = "Instalación de Aislador Galvánico Láser",
        descripcion = "Circuito de protección galvánica y puesta a tierra dedicada menor a 2 ohmios para equipo láser Alexandrita.",
        categoria = "Instalación",
        precio = 450000.0,
        fecha = oneWeekAgoStr,
        fechaEntrega = oneWeekAgoStr,
        estado = EstadoServicio.COMPLETADO,
        prioridad = PrioridadServicio.ALTA,
        observaciones = "Medición de resistencia de tierra certificada en 1.4 Ohms.",
        esFavorito = false,
        timestamp = now - 7 * dayMillis
      )
    )

    val initialActividades = listOf(
      Actividad(
        id = UUID.randomUUID().toString(),
        tipo = TipoActividad.SERVICIO_CREADO,
        titulo = "Servicio programado para hoy",
        descripcion = "Mantenimiento Preventivo A/C para Mariana Ríos Restrepo.",
        fecha = now - 2 * 3600000L
      ),
      Actividad(
        id = UUID.randomUUID().toString(),
        tipo = TipoActividad.SERVICIO_ACTUALIZADO,
        titulo = "Estado actualizado a En Proceso",
        descripcion = "Revisión Tableros de Potencia para Industrias Metalmecánicas.",
        fecha = now - 6 * 3600000L
      ),
      Actividad(
        id = UUID.randomUUID().toString(),
        tipo = TipoActividad.CLIENTE_CREADO,
        titulo = "Nuevo cliente registrado",
        descripcion = "Distribuidora Andina Logística añadida a la cartera.",
        fecha = now - dayMillis
      ),
      Actividad(
        id = UUID.randomUUID().toString(),
        tipo = TipoActividad.SERVICIO_COMPLETADO,
        titulo = "Servicio finalizado con éxito",
        descripcion = "Reparación Tablero Residencial para Felipe Gómez Zuluaga ($ 210.000).",
        fecha = now - 3 * dayMillis
      ),
      Actividad(
        id = UUID.randomUUID().toString(),
        tipo = TipoActividad.SERVICIO_COMPLETADO,
        titulo = "Servicio finalizado con éxito",
        descripcion = "Mantenimiento Bombas de Agua para Condominio Verde Real ($ 750.000).",
        fecha = now - 8 * dayMillis
      )
    )

    val initialNotificaciones = listOf(
      Notificacion(
        id = "notif-001",
        tipo = TipoNotificacion.SERVICIO_URGENTE,
        titulo = "Servicio Urgente para hoy",
        descripcion = "Calibración Termostato Cuarto Frío en Restaurante La Terraza requiere atención antes de mediodía.",
        fecha = now - 45 * 60000L,
        leida = false,
        referenciaId = "srv-102",
        tipoReferencia = "servicio"
      ),
      Notificacion(
        id = "notif-002",
        tipo = TipoNotificacion.SERVICIO_HOY,
        titulo = "Servicio programado para hoy",
        descripcion = "Mantenimiento Preventivo A/C para Mariana Ríos Restrepo a las 2:30 PM.",
        fecha = now - 2 * 3600000L,
        leida = false,
        referenciaId = "srv-101",
        tipoReferencia = "servicio"
      ),
      Notificacion(
        id = "notif-003",
        tipo = TipoNotificacion.SERVICIO_PENDIENTE,
        titulo = "Próximo servicio mañana",
        descripcion = "Instalación Cableado Estructurado para Soluciones Digitales SAS.",
        fecha = now - 5 * 3600000L,
        leida = false,
        referenciaId = "srv-103",
        tipoReferencia = "servicio"
      ),
      Notificacion(
        id = "notif-004",
        tipo = TipoNotificacion.NUEVO_CLIENTE,
        titulo = "Nuevo cliente en cartera",
        descripcion = "Distribuidora Andina Logística fue creada exitosamente.",
        fecha = now - dayMillis,
        leida = true,
        referenciaId = "cli-010",
        tipoReferencia = "cliente"
      ),
      Notificacion(
        id = "notif-005",
        tipo = TipoNotificacion.RECORDATORIO_SEGUIMIENTO,
        titulo = "Seguimiento a cliente VIP",
        descripcion = "Soluciones Digitales SAS tiene 1 orden activa y 2 completadas.",
        fecha = now - 2 * dayMillis,
        leida = true,
        referenciaId = "cli-002",
        tipoReferencia = "cliente"
      )
    )

    _clientes.value = initialClientes
    _servicios.value = initialServicios
    _actividades.value = initialActividades
    _notificaciones.value = initialNotificaciones
  }

  fun logActividad(tipo: TipoActividad, titulo: String, descripcion: String) {
    val nuevaActividad = Actividad(
      id = UUID.randomUUID().toString(),
      tipo = tipo,
      titulo = titulo,
      descripcion = descripcion,
      fecha = System.currentTimeMillis()
    )
    _actividades.value = listOf(nuevaActividad) + _actividades.value
  }

  fun getClienteById(clienteId: String): Cliente? {
    return _clientes.value.find { it.clienteId == clienteId }
  }

  fun getServicioById(servicioId: String): Servicio? {
    return _servicios.value.find { it.servicioId == servicioId }
  }

  fun getServiciosByCliente(clienteId: String): List<Servicio> {
    return _servicios.value.filter { it.clienteId == clienteId }
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
    val newCliente = Cliente(
      clienteId = UUID.randomUUID().toString(),
      nombre = nombre.trim(),
      documento = documento.trim(),
      telefono = telefono.trim(),
      correo = correo.trim(),
      direccion = direccion.trim(),
      notas = notas.trim(),
      fechaRegistro = System.currentTimeMillis(),
      esFavorito = esFavorito
    )
    _clientes.value = listOf(newCliente) + _clientes.value
    
    return newCliente
  }

  fun updateCliente(cliente: Cliente) {
    _clientes.value = _clientes.value.map {
      if (it.clienteId == cliente.clienteId) cliente else it
    }
  }

  fun toggleClienteFavorito(clienteId: String) {
    var clienteNombre = ""
    var isFav = false
    _clientes.value = _clientes.value.map {
      if (it.clienteId == clienteId) {
        val updated = it.copy(esFavorito = !it.esFavorito)
        clienteNombre = updated.nombre
        isFav = updated.esFavorito
        updated
      } else it
    }
  }

  fun deleteCliente(clienteId: String): Pair<Cliente?, List<Servicio>> {
    val target = _clientes.value.find { it.clienteId == clienteId }
    val associatedServices = _servicios.value.filter { it.clienteId == clienteId }
    val nombre = target?.nombre ?: "Cliente"
    _clientes.value = _clientes.value.filter { it.clienteId != clienteId }
    _servicios.value = _servicios.value.filter { it.clienteId != clienteId }
    return Pair(target, associatedServices)
  }

  fun restaurarCliente(cliente: Cliente, serviciosAsociados: List<Servicio>) {
    _clientes.value = listOf(cliente) + _clientes.value
    _servicios.value = serviciosAsociados + _servicios.value
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
    categoria: String = "General",
    fechaEntrega: String = "",
    esFavorito: Boolean = false
  ): Servicio {
    val newServicio = Servicio(
      servicioId = UUID.randomUUID().toString(),
      clienteId = clienteId,
      nombre = nombre.trim(),
      descripcion = descripcion.trim(),
      categoria = categoria.trim().ifBlank { "General" },
      precio = precio,
      fecha = fecha.trim(),
      fechaEntrega = fechaEntrega.trim(),
      estado = estado,
      prioridad = prioridad,
      observaciones = observaciones.trim(),
      esFavorito = esFavorito,
      timestamp = System.currentTimeMillis()
    )
    _servicios.value = listOf(newServicio) + _servicios.value
    val clienteNombre = _clientes.value.find { it.clienteId == clienteId }?.nombre ?: "Cliente"
    
    return newServicio
  }

  fun updateServicio(servicio: Servicio) {
    _servicios.value = _servicios.value.map {
      if (it.servicioId == servicio.servicioId) servicio else it
    }
  }

  fun toggleServicioFavorito(servicioId: String) {
    _servicios.value = _servicios.value.map {
      if (it.servicioId == servicioId) it.copy(esFavorito = !it.esFavorito) else it
    }
  }

  fun duplicarServicio(servicioId: String): Servicio? {
    val original = _servicios.value.find { it.servicioId == servicioId } ?: return null
    val duplicated = original.copy(
      servicioId = UUID.randomUUID().toString(),
      nombre = "${original.nombre} (Copia)",
      estado = EstadoServicio.PENDIENTE,
      timestamp = System.currentTimeMillis()
    )
    _servicios.value = listOf(duplicated) + _servicios.value
    return duplicated
  }

  fun updateServicioEstado(servicioId: String, nuevoEstado: EstadoServicio) {
    var updatedService: Servicio? = null
    _servicios.value = _servicios.value.map {
      if (it.servicioId == servicioId) {
        val upd = it.copy(estado = nuevoEstado)
        updatedService = upd
        upd
      } else it
    }
    updatedService?.let { srv ->
      val actTipo = when (nuevoEstado) {
        EstadoServicio.COMPLETADO -> TipoActividad.SERVICIO_COMPLETADO
        EstadoServicio.CANCELADO -> TipoActividad.SERVICIO_CANCELADO
        else -> TipoActividad.SERVICIO_ACTUALIZADO
      }
      
    }
  }

  fun deleteServicio(servicioId: String): Servicio? {
    val target = _servicios.value.find { it.servicioId == servicioId }
    val nombre = target?.nombre ?: "Servicio"
    _servicios.value = _servicios.value.filter { it.servicioId != servicioId }
    return target
  }

  fun restaurarServicio(servicio: Servicio) {
    _servicios.value = listOf(servicio) + _servicios.value
  }

  fun addNotificacion(notificacion: Notificacion) {
    _notificaciones.value = listOf(notificacion) + _notificaciones.value
  }
  fun marcarNotificacionLeida(notificacionId: String) {
    _notificaciones.value = _notificaciones.value.map {
      if (it.id == notificacionId) it.copy(leida = true) else it
    }
  }

  fun deleteNotificacion(notificacionId: String) {
    _notificaciones.value = _notificaciones.value.filter { it.id != notificacionId }
  }

  fun marcarTodasNotificacionesLeidas() {
    _notificaciones.value = _notificaciones.value.map { it.copy(leida = true) }
  }

  fun updateObjetivos(servicios: Int, ingresos: Double) {
    _appConfig.value = _appConfig.value.copy(
      objetivoServiciosSemanal = servicios,
      objetivoIngresosSemanal = ingresos
    )
  }

  fun marcarOnboardingVisto(visto: Boolean) {
    _appConfig.value = _appConfig.value.copy(onboardingVisto = visto)
  }

  fun updateProfile(profile: UserProfile) {
    _userProfile.value = profile
  }

  fun updateConfig(config: AppConfig) {
    _appConfig.value = config
  }

  fun resetData() {
    loadInitialData()
  }
}
