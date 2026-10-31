package com.example.presentation.dialogs

import com.example.presentation.components.InitialsAvatar
import com.example.presentation.components.StatusChip
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.model.Cliente
import com.example.domain.model.Servicio
import com.example.core.theme.ServixBlueDeep
import com.example.core.theme.ServixBluePrimary
import com.example.core.utils.Formatters

@Composable
fun GlobalSearchDialog(
  clientes: List<Cliente>,
  servicios: List<Servicio>,
  onDismiss: () -> Unit,
  onNavigateToClient: (String) -> Unit,
  onNavigateToService: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var filterCategory by remember { mutableStateOf("TODOS") } // "TODOS", "CLIENTES", "SERVICIOS"

  val clientMap = remember(clientes) { clientes.associateBy { it.clienteId } }

  val matchingClients = remember(searchQuery, clientes) {
    if (searchQuery.isBlank()) emptyList()
    else {
      val q = searchQuery.trim().lowercase()
      clientes.filter {
        it.nombre.lowercase().contains(q) ||
          it.documento.lowercase().contains(q) ||
          it.telefono.lowercase().contains(q) ||
          it.correo.lowercase().contains(q) ||
          it.direccion.lowercase().contains(q) ||
          it.notas.lowercase().contains(q)
      }
    }
  }

  val matchingServices = remember(searchQuery, servicios, clientMap) {
    if (searchQuery.isBlank()) emptyList()
    else {
      val q = searchQuery.trim().lowercase()
      servicios.filter { srv ->
        val clientName = clientMap[srv.clienteId]?.nombre?.lowercase() ?: ""
        srv.nombre.lowercase().contains(q) ||
          clientName.contains(q) ||
          srv.categoria.lowercase().contains(q) ||
          srv.estado.displayName.lowercase().contains(q) ||
          srv.prioridad.displayName.lowercase().contains(q) ||
          srv.descripcion.lowercase().contains(q) ||
          srv.observaciones.lowercase().contains(q)
      }
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth()
        .fillMaxHeight(0.88f)
        .clip(RoundedCornerShape(24.dp))
        .testTag("global_search_dialog"),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
      ) {
        // Search Input
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Buscar clientes, servicios, notas, categorías...") },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = null,
              tint = ServixBluePrimary
            )
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "Limpiar")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ServixBluePrimary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("global_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Categories
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FilterChip(
            selected = filterCategory == "TODOS",
            onClick = { filterCategory = "TODOS" },
            label = { Text("Todos (${matchingClients.size + matchingServices.size})") },
            shape = RoundedCornerShape(10.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = ServixBluePrimary,
              selectedLabelColor = Color.White
            )
          )
          FilterChip(
            selected = filterCategory == "CLIENTES",
            onClick = { filterCategory = "CLIENTES" },
            label = { Text("Clientes (${matchingClients.size})") },
            shape = RoundedCornerShape(10.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = ServixBluePrimary,
              selectedLabelColor = Color.White
            )
          )
          FilterChip(
            selected = filterCategory == "SERVICIOS",
            onClick = { filterCategory = "SERVICIOS" },
            label = { Text("Servicios (${matchingServices.size})") },
            shape = RoundedCornerShape(10.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = ServixBluePrimary,
              selectedLabelColor = Color.White
            )
          )
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(8.dp))

        if (searchQuery.isBlank()) {
          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "Escribe para buscar en todo tu negocio",
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              )
            }
          }
        } else if (matchingClients.isEmpty() && matchingServices.isEmpty()) {
          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "No se encontraron coincidencias para \"$searchQuery\"",
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
            if ((filterCategory == "TODOS" || filterCategory == "CLIENTES") && matchingClients.isNotEmpty()) {
              item {
                Text(
                  text = "CLIENTES (${matchingClients.size})",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = ServixBluePrimary,
                    letterSpacing = 1.sp
                  ),
                  modifier = Modifier.padding(vertical = 4.dp)
                )
              }
              items(matchingClients, key = { "cli_${it.clienteId}" }) { client ->
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                      onDismiss()
                      onNavigateToClient(client.clienteId)
                    },
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                  )
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    InitialsAvatar(initials = client.iniciales, size = 38.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = client.nombre,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                      Text(
                        text = "${client.telefono.ifBlank { "Sin teléfono" }} • ${client.direccion.ifBlank { "Sin dirección" }}",
                        style = MaterialTheme.typography.bodySmall.copy(
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                    }
                  }
                }
              }
            }

            if ((filterCategory == "TODOS" || filterCategory == "SERVICIOS") && matchingServices.isNotEmpty()) {
              item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "SERVICIOS (${matchingServices.size})",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = ServixBluePrimary,
                    letterSpacing = 1.sp
                  ),
                  modifier = Modifier.padding(vertical = 4.dp)
                )
              }
              items(matchingServices, key = { "srv_${it.servicioId}" }) { srv ->
                val client = clientMap[srv.clienteId]
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                      onDismiss()
                      onNavigateToService(srv.servicioId)
                    },
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
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
                        .background(srv.estado.containerColor),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.Default.Handyman,
                        contentDescription = null,
                        tint = srv.estado.primaryColor,
                        modifier = Modifier.size(20.dp)
                      )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = srv.nombre,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                      Text(
                        text = "${client?.nombre ?: "Cliente"} • ${srv.categoria} • ${Formatters.formatCurrency(srv.precio)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusChip(estado = srv.estado)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
