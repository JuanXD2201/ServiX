package com.example.presentation.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Cliente
import com.example.domain.model.EstadoServicio
import com.example.domain.model.PrioridadServicio
import com.example.domain.model.SegmentoCliente
import com.example.domain.model.Servicio
import com.example.presentation.components.ClientCard
import com.example.presentation.dialogs.ClientFormDialog
import com.example.presentation.components.ConfirmDialog
import com.example.presentation.components.EmptyStateView
import com.example.presentation.dialogs.ServiceFormDialog
import com.example.presentation.components.ServixSearchBar
import com.example.core.theme.ServixBluePrimary

enum class ClientFilterMode(val label: String) {
  TODOS("Todos"),
  FAVORITOS("Favoritos ⭐"),
  VIP("VIP 👑"),
  FRECUENTES("Frecuentes"),
  NUEVOS("Nuevos ✨"),
  ACTIVOS("Con activos"),
  SIN_SERVICIOS("Sin servicios")
}

enum class ClientSortOrder(val label: String) {
  NOMBRE_ASC("Nombre (A-Z)"),
  NOMBRE_DESC("Nombre (Z-A)"),
  RECIENTES("Más recientes"),
  MAS_SERVICIOS("Más servicios"),
  MAYOR_VALOR("Mayor facturación")
}

@Composable
fun ClientesScreen(
  clientes: List<Cliente>,
  servicios: List<Servicio>,
  onClientClick: (String) -> Unit,
  onAddClient: (nombre: String, documento: String, telefono: String, correo: String, direccion: String, notas: String) -> Unit,
  onUpdateClient: (Cliente) -> Unit,
  onDeleteClient: (String) -> Unit,
  onToggleFavorite: (String) -> Unit = {},
  onAddService: (clienteId: String, nombre: String, descripcion: String, precio: Double, fecha: String, estado: EstadoServicio, prioridad: PrioridadServicio, observaciones: String, categoria: String, fechaEntrega: String) -> Unit = { _, _, _, _, _, _, _, _, _, _ -> },
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedFilter by remember { mutableStateOf(ClientFilterMode.TODOS) }
  var selectedSort by remember { mutableStateOf(ClientSortOrder.NOMBRE_ASC) }
  var sortMenuExpanded by remember { mutableStateOf(false) }

  var showAddDialog by remember { mutableStateOf(false) }
  var newServiceClienteId by remember { mutableStateOf<String?>(null) }
  var editingCliente by remember { mutableStateOf<Cliente?>(null) }
  var clienteToDelete by remember { mutableStateOf<Cliente?>(null) }

  val filteredAndSortedClientes = remember(clientes, servicios, searchQuery, selectedFilter, selectedSort) {
    var result = clientes.filter { client ->
      if (searchQuery.isBlank()) true
      else {
        client.nombre.contains(searchQuery, ignoreCase = true) ||
          client.documento.contains(searchQuery, ignoreCase = true) ||
          client.telefono.contains(searchQuery, ignoreCase = true) ||
          client.correo.contains(searchQuery, ignoreCase = true) ||
          client.direccion.contains(searchQuery, ignoreCase = true) ||
          client.notas.contains(searchQuery, ignoreCase = true)
      }
    }

    result = when (selectedFilter) {
      ClientFilterMode.TODOS -> result
      ClientFilterMode.FAVORITOS -> result.filter { it.esFavorito }
      ClientFilterMode.VIP -> result.filter { c ->
        val cServs = servicios.filter { it.clienteId == c.clienteId }
        c.calcularSegmentos(cServs).contains(SegmentoCliente.VIP)
      }
      ClientFilterMode.FRECUENTES -> result.filter { c ->
        val cServs = servicios.filter { it.clienteId == c.clienteId }
        c.calcularSegmentos(cServs).contains(SegmentoCliente.FRECUENTE)
      }
      ClientFilterMode.NUEVOS -> result.filter { c ->
        val cServs = servicios.filter { it.clienteId == c.clienteId }
        c.calcularSegmentos(cServs).contains(SegmentoCliente.NUEVO)
      }
      ClientFilterMode.ACTIVOS -> result.filter { c ->
        servicios.any { it.clienteId == c.clienteId && (it.estado == EstadoServicio.PENDIENTE || it.estado == EstadoServicio.EN_PROCESO) }
      }
      ClientFilterMode.SIN_SERVICIOS -> result.filter { c ->
        servicios.none { it.clienteId == c.clienteId }
      }
    }

    when (selectedSort) {
      ClientSortOrder.NOMBRE_ASC -> result.sortedBy { it.nombre.lowercase() }
      ClientSortOrder.NOMBRE_DESC -> result.sortedByDescending { it.nombre.lowercase() }
      ClientSortOrder.RECIENTES -> result.sortedByDescending { it.fechaRegistro }
      ClientSortOrder.MAS_SERVICIOS -> result.sortedByDescending { c -> servicios.count { it.clienteId == c.clienteId } }
      ClientSortOrder.MAYOR_VALOR -> result.sortedByDescending { c ->
        servicios.filter { it.clienteId == c.clienteId && it.estado == EstadoServicio.COMPLETADO }.sumOf { it.precio }
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .testTag("clientes_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Clientes",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
          )
          Text(
            text = "${clientes.size} clientes registrados • Cartera comercial",
            style = MaterialTheme.typography.bodyMedium.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }

        // Sort selector
        Box {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.testTag("client_sort_button")
          ) {
            IconButton(onClick = { sortMenuExpanded = true }, modifier = Modifier.size(38.dp)) {
              Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = "Ordenar lista",
                tint = ServixBluePrimary
              )
            }
          }

          DropdownMenu(
            expanded = sortMenuExpanded,
            onDismissRequest = { sortMenuExpanded = false }
          ) {
            ClientSortOrder.entries.forEach { sort ->
              DropdownMenuItem(
                text = {
                  Text(
                    text = sort.label,
                    fontWeight = if (selectedSort == sort) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedSort == sort) ServixBluePrimary else MaterialTheme.colorScheme.onSurface
                  )
                },
                onClick = {
                  selectedSort = sort
                  sortMenuExpanded = false
                }
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      ServixSearchBar(
        query = searchQuery,
        onQueryChange = { searchQuery = it },
        placeholder = "Buscar por nombre, documento, teléfono, notas..."
      )

      Spacer(modifier = Modifier.height(10.dp))

      // CRM Filter Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ClientFilterMode.entries.forEach { mode ->
          FilterChip(
            selected = selectedFilter == mode,
            onClick = { selectedFilter = mode },
            label = {
              Text(
                text = mode.label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
              )
            },
            shape = RoundedCornerShape(12.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = ServixBluePrimary,
              selectedLabelColor = Color.White
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Counter bar & active filters
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Mostrando ${filteredAndSortedClientes.size} de ${clientes.size} clientes",
          style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
        )

        if (searchQuery.isNotBlank() || selectedFilter != ClientFilterMode.TODOS) {
          OutlinedButton(
            onClick = {
              searchQuery = ""
              selectedFilter = ClientFilterMode.TODOS
            },
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Limpiar filtros", fontSize = 11.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      if (filteredAndSortedClientes.isEmpty()) {
        EmptyStateView(
          title = if (searchQuery.isNotBlank()) "Sin resultados de búsqueda" else "No hay clientes con este filtro",
          subtitle = if (searchQuery.isNotBlank()) "No encontramos clientes que coincidan con \"$searchQuery\"." else "Prueba seleccionando otro filtro o agrega un nuevo cliente.",
          icon = Icons.Default.People
        )
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredAndSortedClientes, key = { it.clienteId }) { cliente ->
            val clientServs = servicios.filter { it.clienteId == cliente.clienteId }
            val activeCount = clientServs.count { it.estado == EstadoServicio.PENDIENTE || it.estado == EstadoServicio.EN_PROCESO }
            val facturado = clientServs.filter { it.estado == EstadoServicio.COMPLETADO }.sumOf { it.precio }
            val segmentos = cliente.calcularSegmentos(clientServs)

            ClientCard(
              cliente = cliente,
              onClick = { onClientClick(cliente.clienteId) },
              onEdit = { editingCliente = cliente },
              onDelete = { clienteToDelete = cliente },
              onToggleFavorite = { onToggleFavorite(cliente.clienteId) },
              onNewService = { newServiceClienteId = cliente.clienteId },
              segmentos = segmentos,
              activeServicesCount = activeCount,
              totalFacturado = facturado
            )
          }

          item {
            Spacer(modifier = Modifier.height(80.dp))
          }
        }
      }
    }

    // Floating Action Button
    FloatingActionButton(
      onClick = { showAddDialog = true },
      containerColor = ServixBluePrimary,
      contentColor = Color.White,
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("add_client_fab")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Nuevo Cliente")
    }
  }

  // Dialogs
  if (showAddDialog) {
    ClientFormDialog(
      onDismiss = { showAddDialog = false },
      onSave = { nom, doc, tel, cor, dir, not ->
        onAddClient(nom, doc, tel, cor, dir, not)
        showAddDialog = false
      }
    )
  }

  if (newServiceClienteId != null) {
    ServiceFormDialog(
      preselectedClienteId = newServiceClienteId,
      clientes = clientes,
      onDismiss = { newServiceClienteId = null },
      onSave = { cliId, nom, desc, pre, fec, est, pri, obs, cat, fecEnt ->
        onAddService(cliId, nom, desc, pre, fec, est, pri, obs, cat, fecEnt)
        newServiceClienteId = null
      }
    )
  }

  if (editingCliente != null) {
    ClientFormDialog(
      clienteToEdit = editingCliente,
      onDismiss = { editingCliente = null },
      onSave = { nom, doc, tel, cor, dir, not ->
        editingCliente?.let {
          onUpdateClient(
            it.copy(
              nombre = nom,
              documento = doc,
              telefono = tel,
              correo = cor,
              direccion = dir,
              notas = not
            )
          )
        }
        editingCliente = null
      }
    )
  }

  if (clienteToDelete != null) {
    ConfirmDialog(
      title = "Eliminar Cliente",
      message = "¿Estás seguro de que deseas eliminar a \"${clienteToDelete?.nombre}\"? Se retirarán también todos sus servicios asignados.",
      confirmButtonText = "Eliminar",
      isDestructive = true,
      onConfirm = {
        clienteToDelete?.let { onDeleteClient(it.clienteId) }
        clienteToDelete = null
      },
      onDismiss = { clienteToDelete = null }
    )
  }
}
