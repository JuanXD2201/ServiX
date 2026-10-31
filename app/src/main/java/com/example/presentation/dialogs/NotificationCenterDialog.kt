package com.example.presentation.dialogs

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.model.Notificacion
import com.example.domain.model.TipoNotificacion
import com.example.core.theme.ServixBlueDeep
import com.example.core.theme.ServixBluePrimary
import com.example.core.theme.ServixStatusColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationCenterDialog(
  notificaciones: List<Notificacion>,
  onDismiss: () -> Unit,
  onNotificationClick: (Notificacion) -> Unit,
  onMarkAllAsRead: () -> Unit,
  onDeleteNotification: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  var filterMode by remember { mutableStateOf("TODAS") } // TODAS, NO_LEIDAS, URGENTES, COMPLETADAS

  val unreadCount = remember(notificaciones) {
    notificaciones.count { !it.leida }
  }

  val displayedNotifs = remember(notificaciones, filterMode) {
    when (filterMode) {
      "NO_LEIDAS" -> notificaciones.filter { !it.leida }
      "URGENTES" -> notificaciones.filter { it.tipo == TipoNotificacion.SERVICIO_URGENTE }
      "ATENCION" -> notificaciones.filter { it.tipo == TipoNotificacion.SOLICITUD_ATENCION }
      "COMPLETADAS" -> notificaciones.filter { it.tipo == TipoNotificacion.SERVICIO_COMPLETADO }
      else -> notificaciones
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.94f)
        .fillMaxHeight(0.85f)
        .clip(RoundedCornerShape(24.dp))
        .testTag("notification_center_dialog"),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Top Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ServixBluePrimary.copy(alpha = 0.12f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = ServixBluePrimary,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Centro de Avisos",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
              )
              Text(
                text = if (unreadCount > 0) "$unreadCount pendientes de leer" else "Todo al día",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = if (unreadCount > 0) ServixBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Actions & Filters
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            modifier = Modifier
              .weight(1f, fill = false)
              .padding(end = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            FilterChip(
              selected = filterMode == "TODAS",
              onClick = { filterMode = "TODAS" },
              label = { Text("Todas (${notificaciones.size})", style = MaterialTheme.typography.labelSmall) },
              shape = RoundedCornerShape(10.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ServixBluePrimary,
                selectedLabelColor = Color.White
              )
            )
            FilterChip(
              selected = filterMode == "NO_LEIDAS",
              onClick = { filterMode = "NO_LEIDAS" },
              label = { Text("No leídas ($unreadCount)", style = MaterialTheme.typography.labelSmall) },
              shape = RoundedCornerShape(10.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ServixBluePrimary,
                selectedLabelColor = Color.White
              )
            )
            FilterChip(
              selected = filterMode == "URGENTES",
              onClick = { filterMode = "URGENTES" },
              label = { Text("Urgentes", style = MaterialTheme.typography.labelSmall) },
              shape = RoundedCornerShape(10.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFFDC2626),
                selectedLabelColor = Color.White
              )
            )
            FilterChip(
              selected = filterMode == "ATENCION",
              onClick = { filterMode = "ATENCION" },
              label = { Text("Atención IA", style = MaterialTheme.typography.labelSmall) },
              shape = RoundedCornerShape(10.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFFE11D48),
                selectedLabelColor = Color.White
              )
            )
          }

          if (unreadCount > 0) {
            TextButton(
              onClick = onMarkAllAsRead,
              modifier = Modifier.testTag("mark_all_read_button")
            ) {
              Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Leídas", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(8.dp))

        if (displayedNotifs.isEmpty()) {
          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = if (filterMode == "NO_LEIDAS") "No tienes avisos no leídos" else "No hay notificaciones registradas",
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = FontWeight.Medium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              )
            }
          }
        } else {
          LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(displayedNotifs, key = { it.id }) { notif ->
              NotificationItemCard(
                notificacion = notif,
                onClick = { onNotificationClick(notif) },
                onDelete = { onDeleteNotification(notif.id) }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun NotificationItemCard(
  notificacion: Notificacion,
  onClick: () -> Unit,
  onDelete: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val (iconColor, bgColor) = when (notificacion.tipo) {
    TipoNotificacion.SOLICITUD_ATENCION -> Pair(Color(0xFFE11D48), Color(0xFFFFE4E6))
    TipoNotificacion.SERVICIO_URGENTE -> Pair(Color(0xFFDC2626), Color(0xFFFEE2E2))
    TipoNotificacion.SERVICIO_HOY -> Pair(ServixBluePrimary, Color(0xFFE0F2FE))
    TipoNotificacion.SERVICIO_PENDIENTE -> Pair(Color(0xFFD97706), Color(0xFFFEF3C7))
    TipoNotificacion.SERVICIO_COMPLETADO -> Pair(ServixStatusColors.CompletadoPrimary, ServixStatusColors.CompletadoContainer)
    TipoNotificacion.NUEVO_CLIENTE -> Pair(Color(0xFF7C3AED), Color(0xFFF3E8FF))
    TipoNotificacion.RECORDATORIO_SEGUIMIENTO -> Pair(Color(0xFF0284C7), Color(0xFFE0F2FE))
  }

  val timeAgo = remember(notificacion.fecha) {
    val diff = System.currentTimeMillis() - notificacion.fecha
    val minutes = diff / (60 * 1000)
    val hours = minutes / 60
    val days = hours / 24
    when {
      minutes < 2 -> "Ahora mismo"
      minutes < 60 -> "Hace $minutes min"
      hours < 24 -> "Hace $hours h"
      days == 1L -> "Ayer"
      days < 30 -> "Hace $days días"
      else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(notificacion.fecha))
    }
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .testTag("notification_item_${notificacion.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (!notificacion.leida)
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
      else
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(bgColor),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = notificacion.tipo.icon,
          contentDescription = null,
          tint = iconColor,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = notificacion.titulo,
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = if (!notificacion.leida) FontWeight.Bold else FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )

          if (!notificacion.leida) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(ServixBluePrimary)
            )
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = notificacion.descripcion,
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          ),
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = timeAgo,
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
          )

          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Descartar aviso",
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }
    }
  }
}
