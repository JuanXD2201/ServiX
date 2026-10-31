package com.example.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Actividad
import com.example.domain.model.AppConfig
import com.example.domain.model.Cliente
import com.example.domain.model.EstadoServicio
import com.example.domain.model.Notificacion
import com.example.domain.model.PrioridadServicio
import com.example.domain.model.Servicio
import com.example.domain.model.UserProfile
import com.example.presentation.components.ActivityItemCard
import com.example.presentation.components.AttentionRequiredSection
import com.example.presentation.dialogs.ClientFormDialog
import com.example.presentation.components.ConfirmDialog
import com.example.presentation.dialogs.GlobalSearchDialog
import com.example.presentation.components.InitialsAvatar
import com.example.presentation.dialogs.NotificationCenterDialog
import com.example.presentation.components.QuickActionButton
import com.example.presentation.components.SectionHeader
import com.example.presentation.components.ServiceCard
import com.example.presentation.dialogs.ServiceFormDialog
import com.example.presentation.components.ServixLogo
import com.example.presentation.components.StatisticCard
import com.example.presentation.components.WeeklyGoalsCard
import com.example.core.theme.ServixBlueAccent
import com.example.core.theme.ServixBlueDark
import com.example.core.theme.ServixBlueDeep
import com.example.core.theme.ServixBluePrimary
import com.example.core.theme.ServixBlueSecondary
import com.example.core.theme.ServixStatusColors
import com.example.core.utils.Formatters
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
  clientes: List<Cliente>,
  servicios: List<Servicio>,
  actividades: List<Actividad>,
  notificaciones: List<Notificacion>,
  solicitudes: List<com.example.domain.model.SolicitudAtencion> = emptyList(),
  userProfile: UserProfile,
  appConfig: AppConfig,
  onNavigateToClientes: () -> Unit,
  onNavigateToServicios: (EstadoServicio?) -> Unit,
  onNavigateToServiceDetail: (String) -> Unit,
  onNavigateToClienteDetail: (String) -> Unit,
  onNavigateToPerfil: () -> Unit,
  onNavigateToChat: () -> Unit = {},
  onTomarSolicitud: (String) -> Unit = {},
  onResponderSolicitud: (String, String) -> Unit = { _, _ -> },
  onResolverSolicitud: (String, String) -> Unit = { _, _ -> },
  onAddCliente: (nombre: String, documento: String, telefono: String, correo: String, direccion: String, notas: String) -> Unit,
  onAddServicio: (clienteId: String, nombre: String, descripcion: String, precio: Double, fecha: String, estado: EstadoServicio, prioridad: PrioridadServicio, observaciones: String, categoria: String, fechaEntrega: String) -> Unit,
  onUpdateServicio: (Servicio) -> Unit,
  onUpdateServicioEstado: (String, EstadoServicio) -> Unit,
  onDeleteServicio: (String) -> Unit,
  onToggleClientFavorite: (String) -> Unit,
  onToggleServiceFavorite: (String) -> Unit,
  onDuplicateService: (String) -> Unit,
  onMarkNotificationRead: (String) -> Unit,
  onMarkAllNotificationsRead: () -> Unit,
  onDeleteNotification: (String) -> Unit = {},
  onUpdateGoals: (Int, Double) -> Unit,
  modifier: Modifier = Modifier
) {
  var showAddClientDialog by remember { mutableStateOf(false) }
  var showAddServiceDialog by remember { mutableStateOf(false) }
  var editingServicio by remember { mutableStateOf<Servicio?>(null) }
  var servicioToDelete by remember { mutableStateOf<Servicio?>(null) }

  var showNotificationsCenter by remember { mutableStateOf(false) }
  var showGlobalSearch by remember { mutableStateOf(false) }

  val unreadNotificationsCount = remember(notificaciones) {
    notificaciones.count { !it.leida }
  }

  val todayStr = remember {
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
  }

  val hora = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
  val saludo = remember(hora) {
    when (hora) {
      in 5..11 -> "Buenos días"
      in 12..18 -> "Buenas tardes"
      else -> "Buenas noches"
    }
  }

  // Metrics
  val serviciosPendientes = servicios.count { it.estado == EstadoServicio.PENDIENTE }
  val serviciosEnProceso = servicios.count { it.estado == EstadoServicio.EN_PROCESO }
  val serviciosActivos = serviciosPendientes + serviciosEnProceso
  val serviciosCompletados = servicios.count { it.estado == EstadoServicio.COMPLETADO }
  val totalFacturado = servicios.filter { it.estado == EstadoServicio.COMPLETADO }.sumOf { it.precio }
  val ticketPromedio = if (serviciosCompletados > 0) totalFacturado / serviciosCompletados else 0.0

  // Filter today's and upcoming services
  val serviciosHoy = remember(servicios, todayStr) {
    servicios.filter { it.fecha == todayStr || it.fechaEntrega == todayStr }
  }

  val proximosServicios = remember(servicios, todayStr) {
    servicios.filter {
      (it.estado == EstadoServicio.PENDIENTE || it.estado == EstadoServicio.EN_PROCESO) &&
        it.fecha != todayStr
    }.take(3)
  }

  // Favorites
  val favoriteClients = remember(clientes) { clientes.filter { it.esFavorito } }
  val favoriteServices = remember(servicios) { servicios.filter { it.esFavorito } }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("home_screen")
  ) {
    item {
      Spacer(modifier = Modifier.height(12.dp))

      // Header Branding & Greeting Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("home_header_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.linearGradient(
                colors = listOf(
                  ServixBlueDeep,
                  ServixBluePrimary
                )
              )
            )
            .padding(20.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              ServixLogo(showBadge = true, onDarkBackground = true, showTagline = true)

              Row(verticalAlignment = Alignment.CenterVertically) {
                // Search button
                IconButton(
                  onClick = { showGlobalSearch = true },
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .testTag("header_search_button")
                ) {
                  Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar en ServiX",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                  )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Notification bell with badge
                IconButton(
                  onClick = { showNotificationsCenter = true },
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .testTag("header_notification_button")
                ) {
                  if (unreadNotificationsCount > 0) {
                    BadgedBox(
                      badge = {
                        Badge(
                          containerColor = Color(0xFFEF4444),
                          contentColor = Color.White
                        ) {
                          Text(
                            text = if (unreadNotificationsCount > 9) "9+" else unreadNotificationsCount.toString(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                          )
                        }
                      }
                    ) {
                      Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                      )
                    }
                  } else {
                    Icon(
                      imageVector = Icons.Default.Notifications,
                      contentDescription = "Notificaciones",
                      tint = Color.White,
                      modifier = Modifier.size(18.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                  shape = RoundedCornerShape(20.dp),
                  color = Color.White.copy(alpha = 0.15f),
                  modifier = Modifier.clickable { onNavigateToPerfil() }
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    InitialsAvatar(
                      initials = userProfile.nombre.trim().split("\\s+".toRegex())
                        .mapNotNull { it.firstOrNull()?.toString() }
                        .take(2)
                        .joinToString("")
                        .ifBlank { "PX" },
                      size = 24.dp,
                      backgroundColor = ServixBlueAccent,
                      textColor = Color.White
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val displayName = userProfile.nombre.trim().split("\\s+".toRegex()).firstOrNull()?.ifBlank { null } ?: "Profesional"
            Text(
              text = "$saludo, $displayName",
              style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-0.5).sp
              )
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
              text = userProfile.empresa.ifBlank { "Servicios Especializados • Gestión Operativa" },
              style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White.copy(alpha = 0.85f)
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Search Quick Trigger Pill
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { showGlobalSearch = true }
          .testTag("home_search_pill")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = ServixBluePrimary,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Buscar clientes, servicios, notas...",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Quick Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        QuickActionButton(
          icon = Icons.Default.Add,
          label = "+ Servicio",
          onClick = { showAddServiceDialog = true },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = Color.White,
          modifier = Modifier.weight(1f)
        )

        QuickActionButton(
          icon = Icons.Default.PersonAdd,
          label = "+ Cliente",
          onClick = { showAddClientDialog = true },
          containerColor = ServixBlueSecondary,
          contentColor = Color.White,
          modifier = Modifier.weight(1f)
        )

        QuickActionButton(
          icon = Icons.Default.SmartToy,
          label = "Chat IA",
          onClick = { onNavigateToChat() },
          containerColor = ServixBlueDark,
          contentColor = Color.White,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // BANDEJA DE ATENCIÓN Y ESCALAMIENTOS IA (Módulo Administrador / Vendedor)
      com.example.presentation.components.EscalamientosAdminSection(
        solicitudes = solicitudes,
        onTomarSolicitud = onTomarSolicitud,
        onResponderSolicitud = onResponderSolicitud,
        onResolverSolicitud = onResolverSolicitud,
        onNavigateToChat = onNavigateToChat
      )

      Spacer(modifier = Modifier.height(16.dp))

      // ATENCIÓN REQUERIDA (Section 12)
      AttentionRequiredSection(
        servicios = servicios,
        clientes = clientes,
        onNavigateToService = onNavigateToServiceDetail,
        onNavigateToClient = onNavigateToClienteDetail,
        onViewAllAttention = { onNavigateToServicios(EstadoServicio.PENDIENTE) }
      )

      Spacer(modifier = Modifier.height(14.dp))

      // OBJETIVO SEMANAL (Section 14)
      WeeklyGoalsCard(
        servicios = servicios,
        targetServices = appConfig.objetivoServiciosSemanal,
        targetRevenue = appConfig.objetivoIngresosSemanal,
        monedaSimbolo = appConfig.monedaSimbolo,
        onUpdateGoals = onUpdateGoals
      )

      Spacer(modifier = Modifier.height(20.dp))

      // FAVORITOS (Section 16)
      if (favoriteClients.isNotEmpty() || favoriteServices.isNotEmpty()) {
        SectionHeader(title = "Accesos Rápidos • Favoritos")
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          items(favoriteClients, key = { "fav_cli_${it.clienteId}" }) { cli ->
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
              ),
              modifier = Modifier
                .clickable { onNavigateToClienteDetail(cli.clienteId) }
                .testTag("fav_client_${cli.clienteId}")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                InitialsAvatar(initials = cli.iniciales, size = 28.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = cli.nombre,
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                      maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                      imageVector = Icons.Default.Star,
                      contentDescription = null,
                      tint = Color(0xFFF59E0B),
                      modifier = Modifier.size(12.dp)
                    )
                  }
                  Text(
                    text = "Cliente",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 10.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  )
                }
              }
            }
          }

          items(favoriteServices, key = { "fav_srv_${it.servicioId}" }) { srv ->
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
              ),
              modifier = Modifier
                .clickable { onNavigateToServiceDetail(srv.servicioId) }
                .testTag("fav_service_${srv.servicioId}")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(srv.estado.containerColor),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Handyman,
                    contentDescription = null,
                    tint = srv.estado.primaryColor,
                    modifier = Modifier.size(16.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = srv.nombre,
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                      maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                      imageVector = Icons.Default.Star,
                      contentDescription = null,
                      tint = Color(0xFFF59E0B),
                      modifier = Modifier.size(12.dp)
                    )
                  }
                  Text(
                    text = srv.estado.displayName,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 10.sp,
                      color = srv.estado.primaryColor
                    )
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))
      }

      // Resumen del Negocio (Métricas clave)
      SectionHeader(title = "Resumen del Negocio")

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatisticCard(
          title = "Clientes",
          value = clientes.size.toString(),
          icon = Icons.Default.People,
          iconTint = ServixBlueSecondary,
          subtitle = "En cartera",
          trendText = "+${clientes.takeLast(3).size} recientes",
          onClick = onNavigateToClientes,
          modifier = Modifier.weight(1f)
        )

        StatisticCard(
          title = "Activos",
          value = serviciosActivos.toString(),
          icon = Icons.Default.HourglassTop,
          iconTint = ServixStatusColors.EnProcesoPrimary,
          subtitle = "En ejecución",
          onClick = { onNavigateToServicios(EstadoServicio.EN_PROCESO) },
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatisticCard(
          title = "Pendientes",
          value = serviciosPendientes.toString(),
          icon = Icons.Default.PendingActions,
          iconTint = ServixStatusColors.PendientePrimary,
          subtitle = "Por iniciar",
          onClick = { onNavigateToServicios(EstadoServicio.PENDIENTE) },
          modifier = Modifier.weight(1f)
        )

        StatisticCard(
          title = "Facturación",
          value = Formatters.formatCurrency(totalFacturado, appConfig.monedaSimbolo),
          icon = Icons.Default.CheckCircle,
          iconTint = ServixStatusColors.CompletadoPrimary,
          subtitle = "$serviciosCompletados completados",
          trendText = "Prom: ${Formatters.formatCurrency(ticketPromedio, appConfig.monedaSimbolo)}",
          onClick = { onNavigateToServicios(EstadoServicio.COMPLETADO) },
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // SERVICIOS DE HOY
      SectionHeader(
        title = "Servicios de Hoy",
        actionLabel = "Ver todos",
        onActionClick = { onNavigateToServicios(null) }
      )

      Spacer(modifier = Modifier.height(8.dp))
    }

    if (serviciosHoy.isEmpty()) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.CalendarToday,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Sin servicios para hoy",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "No tienes trabajos con fecha programada para el día de hoy.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
            }
          }
        }
      }
    } else {
      items(serviciosHoy, key = { "today_${it.servicioId}" }) { servicio ->
        val cliente = clientes.find { it.clienteId == servicio.clienteId }
        ServiceCard(
          servicio = servicio,
          clienteNombre = cliente?.nombre ?: "Cliente",
          onClick = { onNavigateToServiceDetail(servicio.servicioId) },
          onEdit = { editingServicio = servicio },
          onDelete = { servicioToDelete = servicio },
          onStatusChange = { newStatus -> onUpdateServicioEstado(servicio.servicioId, newStatus) },
          onToggleFavorite = { onToggleServiceFavorite(servicio.servicioId) },
          onDuplicate = { onDuplicateService(servicio.servicioId) },
          onViewClient = { onNavigateToClienteDetail(servicio.clienteId) },
          monedaSimbolo = appConfig.monedaSimbolo,
          modifier = Modifier.padding(bottom = 10.dp)
        )
      }
    }

    // PRÓXIMOS SERVICIOS
    if (proximosServicios.isNotEmpty()) {
      item {
        Spacer(modifier = Modifier.height(18.dp))
        SectionHeader(
          title = "Próximos Trabajos",
          actionLabel = "Ver agenda",
          onActionClick = { onNavigateToServicios(EstadoServicio.PENDIENTE) }
        )
        Spacer(modifier = Modifier.height(8.dp))
      }

      items(proximosServicios, key = { "upcoming_${it.servicioId}" }) { servicio ->
        val cliente = clientes.find { it.clienteId == servicio.clienteId }
        ServiceCard(
          servicio = servicio,
          clienteNombre = cliente?.nombre ?: "Cliente",
          onClick = { onNavigateToServiceDetail(servicio.servicioId) },
          onEdit = { editingServicio = servicio },
          onDelete = { servicioToDelete = servicio },
          onStatusChange = { newStatus -> onUpdateServicioEstado(servicio.servicioId, newStatus) },
          onToggleFavorite = { onToggleServiceFavorite(servicio.servicioId) },
          onDuplicate = { onDuplicateService(servicio.servicioId) },
          onViewClient = { onNavigateToClienteDetail(servicio.clienteId) },
          monedaSimbolo = appConfig.monedaSimbolo,
          modifier = Modifier.padding(bottom = 10.dp)
        )
      }
    }

    // ACTIVIDAD RECIENTE
    if (actividades.isNotEmpty()) {
      item {
        Spacer(modifier = Modifier.height(20.dp))
        SectionHeader(title = "Actividad Reciente")
        Spacer(modifier = Modifier.height(10.dp))
      }

      val recentActs = actividades.take(5)
      items(recentActs, key = { it.id }) { act ->
        ActivityItemCard(
          actividad = act,
          modifier = Modifier.padding(bottom = 8.dp)
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  // Dialogs
  if (showNotificationsCenter) {
    NotificationCenterDialog(
      notificaciones = notificaciones,
      onDismiss = { showNotificationsCenter = false },
      onNotificationClick = { notif ->
        onMarkNotificationRead(notif.id)
        showNotificationsCenter = false
        if (!notif.referenciaId.isNullOrBlank()) {
          if (notif.tipoReferencia == "cliente") {
            onNavigateToClienteDetail(notif.referenciaId)
          } else {
            onNavigateToServiceDetail(notif.referenciaId)
          }
        }
      },
      onMarkAllAsRead = onMarkAllNotificationsRead,
      onDeleteNotification = onDeleteNotification
    )
  }

  if (showGlobalSearch) {
    GlobalSearchDialog(
      clientes = clientes,
      servicios = servicios,
      onDismiss = { showGlobalSearch = false },
      onNavigateToClient = { cliId ->
        showGlobalSearch = false
        onNavigateToClienteDetail(cliId)
      },
      onNavigateToService = { srvId ->
        showGlobalSearch = false
        onNavigateToServiceDetail(srvId)
      }
    )
  }

  if (showAddClientDialog) {
    ClientFormDialog(
      onDismiss = { showAddClientDialog = false },
      onSave = { nom, doc, tel, cor, dir, not ->
        onAddCliente(nom, doc, tel, cor, dir, not)
        showAddClientDialog = false
      }
    )
  }

  if (showAddServiceDialog) {
    ServiceFormDialog(
      clientes = clientes,
      onDismiss = { showAddServiceDialog = false },
      onSave = { cliId, nom, desc, pre, fec, est, pri, obs, cat, fecEnt ->
        onAddServicio(cliId, nom, desc, pre, fec, est, pri, obs, cat, fecEnt)
        showAddServiceDialog = false
      }
    )
  }

  if (editingServicio != null) {
    ServiceFormDialog(
      servicioToEdit = editingServicio,
      clientes = clientes,
      onDismiss = { editingServicio = null },
      onSave = { cliId, nom, desc, pre, fec, est, pri, obs, cat, fecEnt ->
        editingServicio?.let { srv ->
          onUpdateServicio(
            srv.copy(
              clienteId = cliId,
              nombre = nom,
              descripcion = desc,
              categoria = cat,
              precio = pre,
              fecha = fec,
              fechaEntrega = fecEnt,
              estado = est,
              prioridad = pri,
              observaciones = obs
            )
          )
        }
        editingServicio = null
      }
    )
  }

  if (servicioToDelete != null) {
    ConfirmDialog(
      title = "Eliminar Servicio",
      message = "¿Estás seguro de que deseas eliminar el servicio \"${servicioToDelete?.nombre}\"?",
      confirmButtonText = "Eliminar",
      isDestructive = true,
      onConfirm = {
        servicioToDelete?.let { onDeleteServicio(it.servicioId) }
        servicioToDelete = null
      },
      onDismiss = { servicioToDelete = null }
    )
  }
}
