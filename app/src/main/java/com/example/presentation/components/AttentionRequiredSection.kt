package com.example.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Cliente
import com.example.domain.model.EstadoServicio
import com.example.domain.model.PrioridadServicio
import com.example.domain.model.Servicio
import com.example.core.theme.ServixBluePrimary
import com.example.core.utils.Formatters

data class AttentionItem(
  val id: String,
  val title: String,
  val subtitle: String,
  val badgeText: String,
  val icon: ImageVector,
  val iconTint: Color,
  val bgContainer: Color,
  val onClick: () -> Unit
)

@Composable
fun AttentionRequiredSection(
  servicios: List<Servicio>,
  clientes: List<Cliente>,
  onNavigateToService: (String) -> Unit,
  onNavigateToClient: (String) -> Unit,
  onViewAllAttention: () -> Unit,
  modifier: Modifier = Modifier
) {
  val clientMap = remember(clientes) { clientes.associateBy { it.clienteId } }
  val todayStr = remember { Formatters.formatDate(System.currentTimeMillis()) }

  val attentionItems = remember(servicios, clientes, todayStr) {
    val items = mutableListOf<AttentionItem>()

    // 1. Urgent services
    val urgentServices = servicios.filter { it.prioridad == PrioridadServicio.URGENTE && it.estado != EstadoServicio.COMPLETADO && it.estado != EstadoServicio.CANCELADO }
    urgentServices.forEach { srv ->
      val client = clientMap[srv.clienteId]
      items.add(
        AttentionItem(
          id = "urg_${srv.servicioId}",
          title = srv.nombre,
          subtitle = "${client?.nombre ?: "Cliente"} • ${srv.estado.displayName}",
          badgeText = "URGENTE",
          icon = Icons.Default.NotificationImportant,
          iconTint = Color(0xFFDC2626),
          bgContainer = Color(0xFFFEE2E2),
          onClick = { onNavigateToService(srv.servicioId) }
        )
      )
    }

    // 2. Services for today
    val todayServices = servicios.filter { it.fecha == todayStr && it.prioridad != PrioridadServicio.URGENTE && it.estado != EstadoServicio.COMPLETADO }
    todayServices.forEach { srv ->
      val client = clientMap[srv.clienteId]
      items.add(
        AttentionItem(
          id = "today_${srv.servicioId}",
          title = srv.nombre,
          subtitle = "${client?.nombre ?: "Cliente"} • Para Hoy",
          badgeText = "HOY",
          icon = Icons.Default.Event,
          iconTint = ServixBluePrimary,
          bgContainer = Color(0xFFE0F2FE),
          onClick = { onNavigateToService(srv.servicioId) }
        )
      )
    }

    // 3. Pending high priority
    val highPending = servicios.filter { it.prioridad == PrioridadServicio.ALTA && it.estado == EstadoServicio.PENDIENTE && it.fecha != todayStr }
    highPending.take(2).forEach { srv ->
      val client = clientMap[srv.clienteId]
      items.add(
        AttentionItem(
          id = "high_${srv.servicioId}",
          title = srv.nombre,
          subtitle = "${client?.nombre ?: "Cliente"} • Fec: ${srv.fecha}",
          badgeText = "PRIORIDAD ALTA",
          icon = Icons.Default.PendingActions,
          iconTint = Color(0xFFD97706),
          bgContainer = Color(0xFFFEF3C7),
          onClick = { onNavigateToService(srv.servicioId) }
        )
      )
    }

    items
  }

  if (attentionItems.isEmpty()) return

  Column(modifier = modifier.fillMaxWidth()) {
    SectionHeader(
      title = "Atención Requerida",
      actionLabel = if (attentionItems.size > 2) "Ver todos" else null,
      onActionClick = onViewAllAttention
    )

    Spacer(modifier = Modifier.height(10.dp))

    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(attentionItems, key = { it.id }) { item ->
        Card(
          modifier = Modifier
            .width(260.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = item.onClick)
            .testTag("attention_item_${item.id}"),
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.dp, item.iconTint.copy(alpha = 0.3f)),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          )
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(item.bgContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = item.icon,
                  contentDescription = null,
                  tint = item.iconTint,
                  modifier = Modifier.size(18.dp)
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(item.bgContainer)
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = item.badgeText,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = item.iconTint
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = item.title,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            Text(
              text = item.subtitle,
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Revisar orden",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = item.iconTint
                )
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = item.iconTint,
                modifier = Modifier.size(12.dp)
              )
            }
          }
        }
      }
    }
  }
}
