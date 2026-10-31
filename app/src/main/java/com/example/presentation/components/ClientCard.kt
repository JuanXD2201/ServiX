package com.example.presentation.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Cliente
import com.example.domain.model.SegmentoCliente
import com.example.core.utils.Formatters

@Composable
fun ClientCard(
  cliente: Cliente,
  onClick: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onToggleFavorite: () -> Unit = {},
  onNewService: () -> Unit = {},
  segmentos: List<SegmentoCliente> = emptyList(),
  activeServicesCount: Int = 0,
  totalFacturado: Double = 0.0,
  monedaSimbolo: String = "$",
  modifier: Modifier = Modifier
) {
  var menuExpanded by remember { mutableStateOf(false) }
  val context = LocalContext.current

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("client_card_${cliente.clienteId}")
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
        verticalAlignment = Alignment.CenterVertically
      ) {
        InitialsAvatar(
          initials = cliente.iniciales,
          size = 46.dp,
          useGradient = true,
          base64Image = cliente.fotoBase64
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = cliente.nombre,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.weight(1f, fill = false)
            )

            if (cliente.esFavorito) {
              Spacer(modifier = Modifier.width(6.dp))
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Cliente favorito",
                tint = androidx.compose.ui.graphics.Color(0xFFF59E0B),
                modifier = Modifier.size(16.dp)
              )
            }
          }

          if (cliente.documento.isNotBlank()) {
            Text(
              text = "Doc: ${cliente.documento}",
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontSize = 11.sp
              )
            )
          }
        }

        FavoriteStarButton(
          isFavorite = cliente.esFavorito,
          onToggle = onToggleFavorite,
          size = 20.dp,
          testTag = "star_cliente_${cliente.clienteId}"
        )

        Box {
          IconButton(
            onClick = { menuExpanded = true },
            modifier = Modifier.testTag("client_menu_button_${cliente.clienteId}")
          ) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = "Opciones del cliente",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
          ) {
            DropdownMenuItem(
              text = { Text("Ver perfil completo") },
              leadingIcon = {
                Icon(Icons.Default.Visibility, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              },
              onClick = {
                menuExpanded = false
                onClick()
              }
            )
            DropdownMenuItem(
              text = { Text("Nuevo servicio") },
              leadingIcon = {
                Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              },
              onClick = {
                menuExpanded = false
                onNewService()
              }
            )
            if (cliente.telefono.isNotBlank()) {
              DropdownMenuItem(
                text = { Text("Llamar cliente") },
                leadingIcon = {
                  Icon(Icons.Default.Call, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                onClick = {
                  menuExpanded = false
                  val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${cliente.telefono}")
                  }
                  try {
                    context.startActivity(intent)
                  } catch (_: Exception) {}
                }
              )
              DropdownMenuItem(
                text = { Text("Enviar WhatsApp") },
                leadingIcon = {
                  Icon(Icons.Default.Chat, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                },
                onClick = {
                  menuExpanded = false
                  val cleanNum = cliente.telefono.replace(Regex("[^0-9]"), "")
                  val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/$cleanNum")
                  }
                  try {
                    context.startActivity(intent)
                  } catch (_: Exception) {}
                }
              )
            }
            DropdownMenuItem(
              text = { Text(if (cliente.esFavorito) "Quitar de favoritos" else "Marcar como favorito") },
              leadingIcon = {
                Icon(
                  if (cliente.esFavorito) Icons.Default.StarOutline else Icons.Default.Star,
                  contentDescription = null,
                  tint = androidx.compose.ui.graphics.Color(0xFFF59E0B)
                )
              },
              onClick = {
                menuExpanded = false
                onToggleFavorite()
              }
            )
            DropdownMenuItem(
              text = { Text("Editar cliente") },
              leadingIcon = {
                Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              },
              onClick = {
                menuExpanded = false
                onEdit()
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

      if (segmentos.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          segmentos.take(3).forEach { seg ->
            SegmentBadge(segmento = seg)
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Contact details
      if (cliente.telefono.isNotBlank()) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(bottom = 3.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Call,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = cliente.telefono,
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }
      }

      if (cliente.direccion.isNotBlank()) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(bottom = 3.dp)
        ) {
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = cliente.direccion,
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Bottom Badges: Services count & Billing
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = "$activeServicesCount servicio(s)",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Bold
            )
          )
        }

        if (totalFacturado > 0) {
          Text(
            text = "Total: ${Formatters.formatCurrency(totalFacturado, monedaSimbolo)}",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.SemiBold
            )
          )
        }
      }
    }
  }
}
