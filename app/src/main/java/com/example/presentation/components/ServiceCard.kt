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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.EstadoServicio
import com.example.domain.model.Servicio
import com.example.core.theme.ServixStatusColors
import com.example.core.utils.Formatters

@Composable
fun ServiceCard(
  servicio: Servicio,
  clienteNombre: String,
  onClick: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onStatusChange: (EstadoServicio) -> Unit,
  onToggleFavorite: () -> Unit = {},
  onDuplicate: () -> Unit = {},
  onViewClient: () -> Unit = {},
  monedaSimbolo: String = "$",
  modifier: Modifier = Modifier
) {
  var menuExpanded by remember { mutableStateOf(false) }

  val progressFraction = when (servicio.estado) {
    EstadoServicio.PENDIENTE -> 0.15f
    EstadoServicio.EN_PROCESO -> 0.60f
    EstadoServicio.COMPLETADO -> 1.0f
    EstadoServicio.CANCELADO -> 0.0f
  }

  val progressColor = when (servicio.estado) {
    EstadoServicio.PENDIENTE -> ServixStatusColors.PendientePrimary
    EstadoServicio.EN_PROCESO -> ServixStatusColors.EnProcesoPrimary
    EstadoServicio.COMPLETADO -> ServixStatusColors.CompletadoPrimary
    EstadoServicio.CANCELADO -> ServixStatusColors.CanceladoPrimary
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("service_card_${servicio.servicioId}")
      .clickable { onClick() },
    shape = RoundedCornerShape(18.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          // Badges: Category + Priority
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            PriorityBadge(prioridad = servicio.prioridad)

            if (servicio.categoria.isNotBlank() && servicio.categoria != "General") {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
              ) {
                Text(
                  text = servicio.categoria.uppercase(),
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    letterSpacing = 0.5.sp
                  ),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = servicio.nombre,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )

          Spacer(modifier = Modifier.height(4.dp))

          // Client name
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 1.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = clienteNombre.ifBlank { "Cliente no asignado" },
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          // Dates
          if (servicio.fecha.isNotBlank() || servicio.fechaEntrega.isNotBlank()) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(vertical = 1.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = buildString {
                  if (servicio.fecha.isNotBlank()) append(servicio.fecha)
                  if (servicio.fechaEntrega.isNotBlank()) append(" → Entrega: ${servicio.fechaEntrega}")
                },
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp
                )
              )
            }
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          FavoriteStarButton(
            isFavorite = servicio.esFavorito,
            onToggle = onToggleFavorite,
            size = 20.dp,
            testTag = "star_servicio_${servicio.servicioId}"
          )

          Box {
            IconButton(
              onClick = { menuExpanded = true },
              modifier = Modifier.testTag("service_menu_button_${servicio.servicioId}")
            ) {
              Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Opciones del servicio",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            DropdownMenu(
              expanded = menuExpanded,
              onDismissRequest = { menuExpanded = false }
            ) {
              DropdownMenuItem(
                text = { Text("Ver orden técnica") },
                leadingIcon = {
                  Icon(Icons.Default.Visibility, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                onClick = {
                  menuExpanded = false
                  onClick()
                }
              )
              DropdownMenuItem(
                text = { Text("Ver cliente asociado") },
                leadingIcon = {
                  Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                onClick = {
                  menuExpanded = false
                  onViewClient()
                }
              )
              DropdownMenuItem(
                text = { Text("Duplicar orden") },
                leadingIcon = {
                  Icon(Icons.Default.ContentCopy, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                onClick = {
                  menuExpanded = false
                  onDuplicate()
                }
              )
              DropdownMenuItem(
                text = { Text(if (servicio.esFavorito) "Quitar de favoritos" else "Marcar como favorito") },
                leadingIcon = {
                  Icon(
                    if (servicio.esFavorito) Icons.Default.StarOutline else Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B)
                  )
                },
                onClick = {
                  menuExpanded = false
                  onToggleFavorite()
                }
              )
              DropdownMenuItem(
                text = { Text("Editar servicio") },
                leadingIcon = {
                  Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                onClick = {
                  menuExpanded = false
                  onEdit()
                }
              )
              DropdownMenuItem(
                text = { Text("Marcar Pendiente") },
                onClick = {
                  menuExpanded = false
                  onStatusChange(EstadoServicio.PENDIENTE)
                }
              )
              DropdownMenuItem(
                text = { Text("Marcar En Proceso") },
                onClick = {
                  menuExpanded = false
                  onStatusChange(EstadoServicio.EN_PROCESO)
                }
              )
              DropdownMenuItem(
                text = { Text("Marcar Completado") },
                onClick = {
                  menuExpanded = false
                  onStatusChange(EstadoServicio.COMPLETADO)
                }
              )
              DropdownMenuItem(
                text = { Text("Marcar Cancelado") },
                onClick = {
                  menuExpanded = false
                  onStatusChange(EstadoServicio.CANCELADO)
                }
              )
              DropdownMenuItem(
                text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                leadingIcon = {
                  Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                },
                onClick = {
                  menuExpanded = false
                  onDelete()
                }
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Progress line indicator
      if (servicio.estado != EstadoServicio.CANCELADO) {
        LinearProgressIndicator(
          progress = { progressFraction },
          modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp)),
          color = progressColor,
          trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.height(10.dp))
      }

      // Bottom Row: Status Chip & Price
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        StatusChip(estado = servicio.estado)

        Text(
          text = Formatters.formatCurrency(servicio.precio, monedaSimbolo),
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 17.sp
          )
        )
      }
    }
  }
}
