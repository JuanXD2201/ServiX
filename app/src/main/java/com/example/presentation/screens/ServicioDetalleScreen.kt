package com.example.presentation.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.presentation.components.ConfirmDialog
import com.example.presentation.components.FavoriteStarButton
import com.example.presentation.components.InitialsAvatar
import com.example.presentation.components.PriorityBadge
import com.example.presentation.components.SectionHeader
import com.example.presentation.dialogs.ServiceFormDialog
import com.example.presentation.components.StatusChip
import com.example.core.theme.ServixBluePrimary
import com.example.core.theme.ServixStatusColors
import com.example.core.utils.Formatters

@Composable
fun ServicioDetalleScreen(
  servicio: Servicio?,
  cliente: Cliente?,
  allClientes: List<Cliente>,
  appConfig: AppConfig,
  onBack: () -> Unit,
  onNavigateToClient: (String) -> Unit,
  onUpdateServicio: (Servicio) -> Unit,
  onUpdateServicioEstado: (String, EstadoServicio) -> Unit,
  onDeleteServicio: (String) -> Unit,
  onToggleFavorite: (String) -> Unit = {},
  onDuplicateService: (String) -> Unit = {},
  onSendMakeReport: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  if (servicio == null) {
    Box(
      modifier = modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Text("Servicio no encontrado.")
    }
    return
  }

  var showEditDialog by remember { mutableStateOf(false) }
  var showDeleteConfirm by remember { mutableStateOf(false) }

  val orderIdDisplay = remember(servicio.servicioId) {
    "#ORD-${servicio.servicioId.takeLast(6).uppercase()}"
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .verticalScroll(rememberScrollState())
      .testTag("servicio_detalle_screen")
  ) {
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
          isFavorite = servicio.esFavorito,
          onToggle = { onToggleFavorite(servicio.servicioId) },
          size = 22.dp
        )

        IconButton(
          onClick = { onDuplicateService(servicio.servicioId) },
          modifier = Modifier.testTag("duplicate_service_button")
        ) {
          Icon(Icons.Default.ContentCopy, contentDescription = "Duplicar servicio", tint = ServixBluePrimary)
        }

        IconButton(
          onClick = {
            val shareSummary = """
              *ORDEN DE TRABAJO: $orderIdDisplay*
              *Servicio:* ${servicio.nombre}
              *Cliente:* ${cliente?.nombre ?: "Sin asignar"}
              *Categoría:* ${servicio.categoria}
              *Prioridad:* ${servicio.prioridad.displayName}
              *Estado:* ${servicio.estado.displayName}
              *Fecha:* ${servicio.fecha}
              *Entrega:* ${servicio.fechaEntrega}
              *Total:* ${Formatters.formatCurrency(servicio.precio, appConfig.monedaSimbolo)}
              
              _Gestionado profesionalmente con ServiX_
            """.trimIndent()
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
              putExtra(Intent.EXTRA_TEXT, shareSummary)
              type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, "Compartir Orden de Trabajo"))
          },
          modifier = Modifier.testTag("share_service_button")
        ) {
          Icon(Icons.Default.Share, contentDescription = "Compartir orden", tint = ServixBluePrimary)
        }

        IconButton(
          onClick = { showEditDialog = true },
          modifier = Modifier.testTag("edit_service_header_button")
        ) {
          Icon(Icons.Default.Edit, contentDescription = "Editar", tint = ServixBluePrimary)
        }

        IconButton(
          onClick = { showDeleteConfirm = true },
          modifier = Modifier.testTag("delete_service_header_button")
        ) {
          Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Header Card: Order ID, Service Title & Status
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(22.dp),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
          ) {
            Text(
              text = orderIdDisplay,
              style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              ),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            PriorityBadge(prioridad = servicio.prioridad)
            StatusChip(estado = servicio.estado)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = servicio.nombre,
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        )

        if (servicio.categoria.isNotBlank() && servicio.categoria != "General") {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Categoría: ${servicio.categoria}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.Medium
            )
          )
        }

        if (servicio.fecha.isNotBlank() || servicio.fechaEntrega.isNotBlank()) {
          Spacer(modifier = Modifier.height(8.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CalendarToday,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = buildString {
                if (servicio.fecha.isNotBlank()) append("Inicio: ${servicio.fecha}")
                if (servicio.fechaEntrega.isNotBlank()) {
                  if (servicio.fecha.isNotBlank()) append(" • ")
                  append("Entrega: ${servicio.fechaEntrega}")
                }
              },
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Progress Stepper Visualizer
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Línea de Progreso Operativo",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          // Step 1: Registrado
          val step1Active = true
          StepItem(
            label = "Registrado",
            isActive = step1Active,
            isCompleted = servicio.estado != EstadoServicio.PENDIENTE,
            modifier = Modifier.weight(1f)
          )

          // Step 2: En Proceso
          val step2Active = servicio.estado == EstadoServicio.EN_PROCESO || servicio.estado == EstadoServicio.COMPLETADO
          StepItem(
            label = "En Proceso",
            isActive = step2Active,
            isCompleted = servicio.estado == EstadoServicio.COMPLETADO,
            modifier = Modifier.weight(1f)
          )

          // Step 3: Concluido
          val step3Active = servicio.estado == EstadoServicio.COMPLETADO
          StepItem(
            label = if (servicio.estado == EstadoServicio.CANCELADO) "Cancelado" else "Completado",
            isActive = step3Active || servicio.estado == EstadoServicio.CANCELADO,
            isCompleted = step3Active,
            isCancelled = servicio.estado == EstadoServicio.CANCELADO,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Selector de Cambio de Estado Rápido (Segmented Style)
    SectionHeader(title = "Actualizar Estado Operativo")

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      EstadoServicio.entries.forEach { estado ->
        val isSelected = servicio.estado == estado
        FilterChip(
          selected = isSelected,
          onClick = { onUpdateServicioEstado(servicio.servicioId, estado) },
          label = {
            Text(
              text = estado.displayName,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
          },
          shape = RoundedCornerShape(12.dp),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = when (estado) {
              EstadoServicio.PENDIENTE -> ServixStatusColors.PendientePrimary
              EstadoServicio.EN_PROCESO -> ServixStatusColors.EnProcesoPrimary
              EstadoServicio.COMPLETADO -> ServixStatusColors.CompletadoPrimary
              EstadoServicio.CANCELADO -> ServixStatusColors.CanceladoPrimary
            },
            selectedLabelColor = Color.White
          ),
          modifier = Modifier.testTag("status_chip_${estado.name.lowercase()}")
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Integración y Automatización Make (Optativa II)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("make_automation_card"),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
      ),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.CloudUpload,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Automatización Make (Optativa II)",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Flujo: App → Make → Google Sheets → Correo",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Presiona el botón para disparar inmediatamente el Webhook de Make con los datos de este servicio, registrándolo en tu hoja de Google Sheets y enviando el correo de confirmación comprobable.",
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
          onClick = { onSendMakeReport(servicio.servicioId) },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .testTag("btn_send_make_report")
        ) {
          Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Generar Reporte en Make Ahora",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Cliente Asociado Card
    SectionHeader(title = "Cliente Asociado")

    Spacer(modifier = Modifier.height(8.dp))

    if (cliente != null) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToClient(cliente.clienteId) }
          .testTag("service_client_card"),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          InitialsAvatar(
            initials = cliente.iniciales,
            size = 46.dp,
            useGradient = true
          )

          Spacer(modifier = Modifier.width(14.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = cliente.nombre,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            )
            if (cliente.telefono.isNotBlank()) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Call,
                  contentDescription = null,
                  tint = ServixBluePrimary,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = cliente.telefono,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                )
              }
            }
            if (cliente.direccion.isNotBlank()) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.LocationOn,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = cliente.direccion,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                )
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Descripción del Trabajo
    SectionHeader(title = "Alcance y Descripción del Trabajo")

    Spacer(modifier = Modifier.height(8.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Text(
        text = servicio.descripcion.ifBlank { "Sin descripción detallada registrada." },
        style = MaterialTheme.typography.bodyMedium.copy(
          color = MaterialTheme.colorScheme.onSurface,
          lineHeight = 20.sp
        ),
        modifier = Modifier.padding(16.dp)
      )
    }

    if (servicio.observaciones.isNotBlank()) {
      Spacer(modifier = Modifier.height(18.dp))

      SectionHeader(title = "Observaciones Técnicas y Requisitos")

      Spacer(modifier = Modifier.height(8.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      ) {
        Text(
          text = servicio.observaciones,
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
          ),
          modifier = Modifier.padding(16.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Resumen Económico (Desglose Comercial)
    SectionHeader(title = "Resumen Económico")

    Spacer(modifier = Modifier.height(8.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Mano de obra y servicios base",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
          Text(
            text = Formatters.formatCurrency(servicio.precio, appConfig.monedaSimbolo),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Impuestos / Retenciones (0%)",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
          Text(
            text = "${appConfig.monedaSimbolo} 0",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Total a Cobrar:",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          )
          Text(
            text = Formatters.formatCurrency(servicio.precio, appConfig.monedaSimbolo),
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.ExtraBold,
              color = ServixBluePrimary,
              fontSize = 22.sp
            )
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Actions Footer
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      OutlinedButton(
        onClick = { onDuplicateService(servicio.servicioId) },
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.weight(1f)
      ) {
        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Duplicar")
      }

      Button(
        onClick = { showEditDialog = true },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ServixBluePrimary),
        modifier = Modifier
          .weight(1.5f)
          .testTag("edit_service_full_button")
      ) {
        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Editar Orden", fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(80.dp))
  }

  if (showEditDialog) {
    ServiceFormDialog(
      servicioToEdit = servicio,
      clientes = allClientes,
      onDismiss = { showEditDialog = false },
      onSave = { cliId, nom, desc, pre, fec, est, pri, obs, cat, fecEnt ->
        onUpdateServicio(
          servicio.copy(
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
        showEditDialog = false
      }
    )
  }

  if (showDeleteConfirm) {
    ConfirmDialog(
      title = "Eliminar Servicio",
      message = "¿Estás seguro de eliminar el servicio \"${servicio.nombre}\"?",
      confirmButtonText = "Eliminar",
      isDestructive = true,
      onConfirm = {
        onDeleteServicio(servicio.servicioId)
        showDeleteConfirm = false
        onBack()
      },
      onDismiss = { showDeleteConfirm = false }
    )
  }
}

@Composable
private fun StepItem(
  label: String,
  isActive: Boolean,
  isCompleted: Boolean,
  modifier: Modifier = Modifier,
  isCancelled: Boolean = false
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(28.dp)
        .clip(CircleShape)
        .background(
          when {
            isCancelled -> Color(0xFFEF4444)
            isCompleted -> ServixStatusColors.CompletadoPrimary
            isActive -> ServixBluePrimary
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
          }
        ),
      contentAlignment = Alignment.Center
    ) {
      if (isCompleted) {
        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
      } else {
        Text(
          text = if (isCancelled) "✕" else "•",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
      }
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.sp,
        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
        color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
      )
    )
  }
}
