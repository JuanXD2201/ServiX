package com.example.presentation.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.core.utils.ImageUtils
import android.widget.Toast
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AppConfig
import com.example.domain.model.Cliente
import com.example.domain.model.EstadoServicio
import com.example.domain.model.PrioridadServicio
import com.example.domain.model.Servicio
import com.example.presentation.dialogs.ClientFormDialog
import com.example.presentation.components.ConfirmDialog
import com.example.presentation.components.EmptyStateView
import com.example.presentation.components.FavoriteStarButton
import com.example.presentation.components.InitialsAvatar
import com.example.presentation.components.SectionHeader
import com.example.presentation.components.SegmentBadge
import com.example.presentation.components.ServiceCard
import com.example.presentation.dialogs.ServiceFormDialog
import com.example.presentation.components.StatisticCard
import com.example.core.theme.ServixBluePrimary
import com.example.core.theme.ServixBlueSecondary
import com.example.core.theme.ServixStatusColors
import com.example.core.utils.Formatters

@Composable
fun ClienteDetalleScreen(
  cliente: Cliente?,
  servicios: List<Servicio>,
  allClientes: List<Cliente>,
  appConfig: AppConfig,
  onBack: () -> Unit,
  onNavigateToServiceDetail: (String) -> Unit,
  onUpdateCliente: (Cliente) -> Unit,
  onDeleteCliente: (String) -> Unit,
  onToggleFavorite: (String) -> Unit = {},
  onToggleServiceFavorite: (String) -> Unit = {},
  onDuplicateService: (String) -> Unit = {},
  onAddServicio: (clienteId: String, nombre: String, descripcion: String, precio: Double, fecha: String, estado: EstadoServicio, prioridad: PrioridadServicio, observaciones: String, categoria: String, fechaEntrega: String) -> Unit,
  onUpdateServicio: (Servicio) -> Unit = {},
  onUpdateServicioEstado: (String, EstadoServicio) -> Unit,
  onDeleteServicio: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  if (cliente == null) {
  Box(      modifier = modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Text("Cliente no encontrado.")
    }
    return
  }

    var showEditClientDialog by remember { mutableStateOf(false) }
  var showDeleteClientConfirm by remember { mutableStateOf(false) }
  var showAddServiceDialog by remember { mutableStateOf(false) }
  var editingServicio by remember { mutableStateOf<Servicio?>(null) }
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Activos, 1: Historial

  val clienteServicios = servicios.filter { it.clienteId == cliente.clienteId }
  val serviciosActivosList = clienteServicios.filter { it.estado == EstadoServicio.PENDIENTE || it.estado == EstadoServicio.EN_PROCESO }
  val serviciosHistorialList = clienteServicios.filter { it.estado == EstadoServicio.COMPLETADO || it.estado == EstadoServicio.CANCELADO }
  val totalFacturado = clienteServicios.filter { it.estado == EstadoServicio.COMPLETADO }.sumOf { it.precio }
  val segmentos = cliente.calcularSegmentos(clienteServicios)

  var showPhotoOptions by remember { mutableStateOf(false) }

  val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
    if (uri != null) {
        try {
            val base64 = ImageUtils.processImageUri(context, uri)
            if (base64 != null) {
                onUpdateCliente(cliente.copy(fotoBase64 = base64))
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "Error al cargar la imagen", Toast.LENGTH_LONG).show()
        }
    }
  }

  Box(    modifier = modifier
      .fillMaxSize()
      .testTag("cliente_detalle_screen")
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            FavoriteStarButton(
              isFavorite = cliente.esFavorito,
              onToggle = { onToggleFavorite(cliente.clienteId) },
              size = 22.dp
            )

            IconButton(
              onClick = { showEditClientDialog = true },
              modifier = Modifier.testTag("edit_client_header_button")
            ) {
              Icon(
                Icons.Default.Edit,
                contentDescription = "Editar",
                tint = ServixBluePrimary
              )
            }
            IconButton(
              onClick = { showDeleteClientConfirm = true },
              modifier = Modifier.testTag("delete_client_header_button")
            ) {
              Icon(
                Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = MaterialTheme.colorScheme.error
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Card de Información del Cliente
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(22.dp),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            InitialsAvatar(
              initials = cliente.iniciales,
              size = 64.dp,
              useGradient = true,
              base64Image = cliente.fotoBase64,
              modifier = Modifier.clickable { showPhotoOptions = true }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = cliente.nombre,
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
              )
              if (cliente.esFavorito) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                  imageVector = Icons.Default.Star,
                  contentDescription = null,
                  tint = Color(0xFFF59E0B),
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            if (cliente.documento.isNotBlank()) {
              Text(
                text = "CC / NIT: ${cliente.documento}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Commercial Status Pill & Segments
            Row(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              val hasActiveJobs = serviciosActivosList.isNotEmpty()
              val statusBg = if (hasActiveJobs) Color(0xFFDCFCE7) else Color(0xFFF3F4F6)
              val statusColor = if (hasActiveJobs) Color(0xFF166534) else Color(0xFF6B7280)
              val statusLabel = if (hasActiveJobs) "Cartera Activa" else "Inactivo / Al día"

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = statusBg
              ) {
                Text(
                  text = statusLabel,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                  ),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }

              segmentos.forEach { seg ->
                SegmentBadge(segmento = seg)
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Botones de Contacto Rápido
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              if (cliente.telefono.isNotBlank()) {
                val cleanDigits = cliente.telefono.filter { it.isDigit() || it == '+' }
                val whatsappDigits = cliente.telefono.filter { it.isDigit() }

                FilledTonalButton(
                  onClick = {
                    runCatching {
                      val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanDigits"))
                      context.startActivity(dialIntent)
                    }
                  },
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Llamar", style = MaterialTheme.typography.labelMedium)
                }

                FilledTonalButton(
                  onClick = {
                    runCatching {
                      val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$whatsappDigits"))
                      context.startActivity(waIntent)
                    }
                  },
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFF25D366).copy(alpha = 0.2f),
                    contentColor = Color(0xFF075E54)
                  )
                ) {
                  Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("WhatsApp", style = MaterialTheme.typography.labelMedium)
                }
              }

              if (cliente.correo.isNotBlank()) {
                FilledTonalButton(
                  onClick = {
                    runCatching {
                      val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${cliente.correo}"))
                      context.startActivity(emailIntent)
                    }
                  },
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Email", style = MaterialTheme.typography.labelMedium)
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
              if (cliente.telefono.isNotBlank()) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(vertical = 4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null,
                    tint = ServixBluePrimary,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(text = cliente.telefono, style = MaterialTheme.typography.bodyMedium)
                }
              }

              if (cliente.correo.isNotBlank()) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(vertical = 4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = ServixBluePrimary,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(text = cliente.correo, style = MaterialTheme.typography.bodyMedium)
                }
              }

              if (cliente.direccion.isNotBlank()) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(vertical = 4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = ServixBluePrimary,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(text = cliente.direccion, style = MaterialTheme.typography.bodyMedium)
                }
              }

              if (cliente.notas.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "Notas comerciales:",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = cliente.notas,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Métricas del Cliente
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          StatisticCard(
            title = "Servicios",
            value = clienteServicios.size.toString(),
            icon = Icons.Default.Build,
            iconTint = ServixBlueSecondary,
            subtitle = "${serviciosActivosList.size} activos",
            modifier = Modifier.weight(1f)
          )

          StatisticCard(
            title = "Facturado",
            value = Formatters.formatCurrency(totalFacturado, appConfig.monedaSimbolo),
            icon = Icons.Default.Payments,
            iconTint = ServixStatusColors.CompletadoPrimary,
            subtitle = "${serviciosHistorialList.count { it.estado == EstadoServicio.COMPLETADO }} completados",
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        SectionHeader(
          title = "Servicios del Cliente",
          actionLabel = "+ Agregar servicio",
          onActionClick = { showAddServiceDialog = true }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Pestañas (Servicios Activos vs Historial)
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = MaterialTheme.colorScheme.surface,
          contentColor = ServixBluePrimary
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = { Text("Activos (${serviciosActivosList.size})") }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = { Text("Historial (${serviciosHistorialList.size})") }
          )
        }

        Spacer(modifier = Modifier.height(12.dp))
      }

      val displayList = if (selectedTab == 0) serviciosActivosList else serviciosHistorialList

      if (displayList.isEmpty()) {
        item {
          EmptyStateView(
            title = if (selectedTab == 0) "No hay servicios activos" else "Sin historial completado",
            subtitle = if (selectedTab == 0) "Todos los servicios están completados o puedes agregar uno nuevo." else "Los servicios finalizados aparecerán aquí.",
            icon = if (selectedTab == 0) Icons.Default.Build else Icons.Default.CheckCircle
          )
        }
      } else {
        items(displayList, key = { it.servicioId }) { servicio ->
          ServiceCard(
            servicio = servicio,
            clienteNombre = cliente.nombre,
            onClick = { onNavigateToServiceDetail(servicio.servicioId) },
            onEdit = { editingServicio = servicio },
            onDelete = { onDeleteServicio(servicio.servicioId) },
            onStatusChange = { newStatus -> onUpdateServicioEstado(servicio.servicioId, newStatus) },
            onToggleFavorite = { onToggleServiceFavorite(servicio.servicioId) },
            onDuplicate = { onDuplicateService(servicio.servicioId) },
            monedaSimbolo = appConfig.monedaSimbolo,
            modifier = Modifier.padding(bottom = 12.dp)
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }

    FloatingActionButton(
      onClick = { showAddServiceDialog = true },
      containerColor = ServixBluePrimary,
      contentColor = Color.White,
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
        .testTag("add_service_for_client_fab")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Nuevo Servicio")
    }
  }

  if (showEditClientDialog) {
    ClientFormDialog(
      clienteToEdit = cliente,
      onDismiss = { showEditClientDialog = false },
      onSave = { nom, doc, tel, cor, dir, not ->
        onUpdateCliente(
          cliente.copy(
            nombre = nom,
            documento = doc,
            telefono = tel,
            correo = cor,
            direccion = dir,
            notas = not
          )
        )
        showEditClientDialog = false
      }
    )
  }

  if (showDeleteClientConfirm) {
    ConfirmDialog(
      title = "Eliminar Cliente",
      message = "¿Deseas eliminar a \"${cliente.nombre}\"? También se retirarán sus ${clienteServicios.size} servicios asociados.",
      confirmButtonText = "Eliminar",
      isDestructive = true,
      onConfirm = {
        onDeleteCliente(cliente.clienteId)
        showDeleteClientConfirm = false
        onBack()
      },
      onDismiss = { showDeleteClientConfirm = false }
    )
  }

  if (showAddServiceDialog) {
    ServiceFormDialog(
      preselectedClienteId = cliente.clienteId,
      clientes = allClientes,
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
      clientes = allClientes,
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
}
