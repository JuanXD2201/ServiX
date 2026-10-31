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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Sort
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
import com.example.domain.model.AppConfig
import com.example.domain.model.Cliente
import com.example.domain.model.EstadoServicio
import com.example.domain.model.PrioridadServicio
import com.example.domain.model.Servicio
import com.example.presentation.components.ConfirmDialog
import com.example.presentation.components.EmptyStateView
import com.example.presentation.components.ServiceCard
import com.example.presentation.dialogs.ServiceFormDialog
import com.example.presentation.components.ServixSearchBar
import com.example.core.theme.ServixBluePrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ServiceDateFilter(val label: String) {
  TODOS("Cualquier fecha"),
  HOY("Hoy"),
  ESTA_SEMANA("Esta semana")
}

enum class ServiceSortOrder(val label: String) {
  PRIORIDAD("Prioridad (Urgente 1°)"),
  RECIENTES("Más recientes"),
  ANTIGUOS("Más antiguos"),
  MAYOR_PRECIO("Mayor precio"),
  MENOR_PRECIO("Menor precio")
}

@Composable
fun ServiciosScreen(
  servicios: List<Servicio>,
  clientes: List<Cliente>,
  appConfig: AppConfig,
  initialFilterStatus: EstadoServicio? = null,
  onServiceClick: (String) -> Unit,
  onAddServicio: (clienteId: String, nombre: String, descripcion: String, precio: Double, fecha: String, estado: EstadoServicio, prioridad: PrioridadServicio, observaciones: String, categoria: String, fechaEntrega: String) -> Unit,
  onUpdateServicio: (Servicio) -> Unit,
  onUpdateServicioEstado: (String, EstadoServicio) -> Unit,
  onDeleteServicio: (String) -> Unit,
  onToggleFavorite: (String) -> Unit = {},
  onDuplicateService: (String) -> Unit = {},
  onNavigateToClienteDetail: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedStatusFilter by remember { mutableStateOf<EstadoServicio?>(initialFilterStatus) }
  var filterOnlyFavorites by remember { mutableStateOf(false) }
  var filterOnlyUrgents by remember { mutableStateOf(false) }
  var selectedDateFilter by remember { mutableStateOf(ServiceDateFilter.TODOS) }
  var selectedSortOrder by remember { mutableStateOf(ServiceSortOrder.PRIORIDAD) }
  var sortMenuExpanded by remember { mutableStateOf(false) }

  var showAddDialog by remember { mutableStateOf(false) }
  var editingServicio by remember { mutableStateOf<Servicio?>(null) }
  var servicioToDelete by remember { mutableStateOf<Servicio?>(null) }

  val todayStr = remember {
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
  }

  val filteredServicios = remember(
    servicios,
    searchQuery,
    selectedStatusFilter,
    filterOnlyFavorites,
    filterOnlyUrgents,
    selectedDateFilter,
    selectedSortOrder
  ) {
    var result = servicios.filter { servicio ->
      val matchesStatus = selectedStatusFilter == null || servicio.estado == selectedStatusFilter
      val matchesFav = !filterOnlyFavorites || servicio.esFavorito
      val matchesUrgent = !filterOnlyUrgents || servicio.prioridad == PrioridadServicio.URGENTE

      val cliente = clientes.find { it.clienteId == servicio.clienteId }
      val matchesSearch = searchQuery.isBlank() ||
        servicio.nombre.contains(searchQuery, ignoreCase = true) ||
        servicio.categoria.contains(searchQuery, ignoreCase = true) ||
        servicio.descripcion.contains(searchQuery, ignoreCase = true) ||
        servicio.observaciones.contains(searchQuery, ignoreCase = true) ||
        (cliente?.nombre?.contains(searchQuery, ignoreCase = true) == true)

      val matchesDate = when (selectedDateFilter) {
        ServiceDateFilter.TODOS -> true
        ServiceDateFilter.HOY -> servicio.fecha == todayStr || servicio.fechaEntrega == todayStr
        ServiceDateFilter.ESTA_SEMANA -> true
      }

      matchesStatus && matchesFav && matchesUrgent && matchesSearch && matchesDate
    }

    when (selectedSortOrder) {
      ServiceSortOrder.PRIORIDAD -> result.sortedWith(
        compareByDescending<Servicio> { it.prioridad == PrioridadServicio.URGENTE }
          .thenByDescending { it.prioridad == PrioridadServicio.ALTA }
          .thenByDescending { it.timestamp }
      )
      ServiceSortOrder.RECIENTES -> result.sortedByDescending { it.timestamp }
      ServiceSortOrder.ANTIGUOS -> result.sortedBy { it.timestamp }
      ServiceSortOrder.MAYOR_PRECIO -> result.sortedByDescending { it.precio }
      ServiceSortOrder.MENOR_PRECIO -> result.sortedBy { it.precio }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .testTag("servicios_screen")
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
            text = "Servicios",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
          )
          Text(
            text = "${servicios.size} trabajos registrados en total",
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
            modifier = Modifier.testTag("service_sort_button")
          ) {
            IconButton(onClick = { sortMenuExpanded = true }, modifier = Modifier.size(38.dp)) {
              Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = "Ordenar lista de servicios",
                tint = ServixBluePrimary
              )
            }
          }

          DropdownMenu(
            expanded = sortMenuExpanded,
            onDismissRequest = { sortMenuExpanded = false }
          ) {
            ServiceSortOrder.entries.forEach { sort ->
              DropdownMenuItem(
                text = {
                  Text(
                    text = sort.label,
                    fontWeight = if (selectedSortOrder == sort) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedSortOrder == sort) ServixBluePrimary else MaterialTheme.colorScheme.onSurface
                  )
                },
                onClick = {
                  selectedSortOrder = sort
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
        placeholder = "Buscar por servicio, categoría o cliente..."
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Status Filters
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          selected = selectedStatusFilter == null && !filterOnlyFavorites && !filterOnlyUrgents,
          onClick = {
            selectedStatusFilter = null
            filterOnlyFavorites = false
            filterOnlyUrgents = false
          },
          label = { Text("Todos (${servicios.size})") },
          shape = RoundedCornerShape(12.dp),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = ServixBluePrimary,
            selectedLabelColor = Color.White
          )
        )

        FilterChip(
          selected = filterOnlyUrgents,
          onClick = {
            filterOnlyUrgents = !filterOnlyUrgents
            if (filterOnlyUrgents) {
              selectedStatusFilter = null
              filterOnlyFavorites = false
            }
          },
          label = { Text("Urgentes 🚨 (${servicios.count { it.prioridad == PrioridadServicio.URGENTE }})") },
          shape = RoundedCornerShape(12.dp),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFFDC2626),
            selectedLabelColor = Color.White
          )
        )

        FilterChip(
          selected = filterOnlyFavorites,
          onClick = {
            filterOnlyFavorites = !filterOnlyFavorites
            if (filterOnlyFavorites) {
              selectedStatusFilter = null
              filterOnlyUrgents = false
            }
          },
          label = { Text("Favoritos ⭐ (${servicios.count { it.esFavorito }})") },
          shape = RoundedCornerShape(12.dp),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFFF59E0B),
            selectedLabelColor = Color.White
          )
        )

        EstadoServicio.entries.forEach { estado ->
          val count = servicios.count { it.estado == estado }
          FilterChip(
            selected = selectedStatusFilter == estado && !filterOnlyFavorites && !filterOnlyUrgents,
            onClick = {
              selectedStatusFilter = if (selectedStatusFilter == estado) null else estado
              filterOnlyFavorites = false
              filterOnlyUrgents = false
            },
            label = { Text("${estado.displayName} ($count)") },
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
          text = "Mostrando ${filteredServicios.size} de ${servicios.size} servicios",
          style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
        )

        if (searchQuery.isNotBlank() || selectedStatusFilter != null || filterOnlyFavorites || filterOnlyUrgents) {
          OutlinedButton(
            onClick = {
              searchQuery = ""
              selectedStatusFilter = null
              filterOnlyFavorites = false
              filterOnlyUrgents = false
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

      if (filteredServicios.isEmpty()) {
        EmptyStateView(
          title = "No hay servicios con estos filtros",
          subtitle = "Prueba cambiando el estado o la búsqueda para encontrar trabajos.",
          icon = Icons.Default.Build
        )
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredServicios, key = { it.servicioId }) { servicio ->
            val cliente = clientes.find { it.clienteId == servicio.clienteId }
            ServiceCard(
              servicio = servicio,
              clienteNombre = cliente?.nombre ?: "Cliente",
              onClick = { onServiceClick(servicio.servicioId) },
              onEdit = { editingServicio = servicio },
              onDelete = { servicioToDelete = servicio },
              onStatusChange = { newStatus -> onUpdateServicioEstado(servicio.servicioId, newStatus) },
              onToggleFavorite = { onToggleFavorite(servicio.servicioId) },
              onDuplicate = { onDuplicateService(servicio.servicioId) },
              onViewClient = { onNavigateToClienteDetail(servicio.clienteId) },
              monedaSimbolo = appConfig.monedaSimbolo
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
        .testTag("add_service_fab")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Nuevo Servicio")
    }
  }

  // Dialogs
  if (showAddDialog) {
    ServiceFormDialog(
      clientes = clientes,
      onDismiss = { showAddDialog = false },
      onSave = { cliId, nom, desc, pre, fec, est, pri, obs, cat, fecEnt ->
        onAddServicio(cliId, nom, desc, pre, fec, est, pri, obs, cat, fecEnt)
        showAddDialog = false
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
