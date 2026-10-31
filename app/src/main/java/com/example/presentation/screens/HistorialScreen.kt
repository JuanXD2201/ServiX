package com.example.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AppConfig
import com.example.domain.model.Cliente
import com.example.domain.model.EstadoServicio
import com.example.domain.model.Servicio
import com.example.presentation.components.EmptyStateView
import com.example.presentation.components.SectionHeader
import com.example.presentation.components.ServiceCard
import com.example.presentation.components.ServixSearchBar
import com.example.presentation.components.StatisticCard
import com.example.core.theme.ServixBlueDeep
import com.example.core.theme.ServixBluePrimary
import com.example.core.theme.ServixBlueSecondary
import com.example.core.theme.ServixStatusColors
import com.example.core.utils.Formatters
import java.util.Calendar

enum class TimeFilter(val label: String) {
  ESTA_SEMANA("Esta semana"),
  ESTE_MES("Este mes"),
  ULTIMOS_3_MESES("Últimos 3 meses"),
  TODO("Todo el historial")
}

enum class StatusFilterOption(val label: String) {
  TODOS("Todos los estados"),
  SOLO_COMPLETADOS("Solo completados"),
  SOLO_CANCELADOS("Solo cancelados")
}

@Composable
fun HistorialScreen(
  servicios: List<Servicio>,
  clientes: List<Cliente>,
  appConfig: AppConfig,
  onServiceClick: (String) -> Unit,
  onUpdateServicioEstado: (String, EstadoServicio) -> Unit,
  onDeleteServicio: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedTimeFilter by remember { mutableStateOf(TimeFilter.TODO) }
  var selectedStatusFilter by remember { mutableStateOf(StatusFilterOption.TODOS) }

  val now = remember { System.currentTimeMillis() }

  // Filter services by timeframe
  val timeFilteredServices = remember(servicios, selectedTimeFilter) {
    val cal = Calendar.getInstance()
    when (selectedTimeFilter) {
      TimeFilter.ESTA_SEMANA -> {
        cal.timeInMillis = now
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val startOfWeek = cal.timeInMillis
        servicios.filter { it.timestamp >= startOfWeek }
      }
      TimeFilter.ESTE_MES -> {
        cal.timeInMillis = now
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val startOfMonth = cal.timeInMillis
        servicios.filter { it.timestamp >= startOfMonth }
      }
      TimeFilter.ULTIMOS_3_MESES -> {
        cal.timeInMillis = now
        cal.add(Calendar.MONTH, -3)
        val threeMonthsAgo = cal.timeInMillis
        servicios.filter { it.timestamp >= threeMonthsAgo }
      }
      TimeFilter.TODO -> servicios
    }
  }

  // Financial calculations based on timeframe
  val completadosList = remember(timeFilteredServices) {
    timeFilteredServices.filter { it.estado == EstadoServicio.COMPLETADO }
  }
  val pendientesList = remember(timeFilteredServices) {
    timeFilteredServices.filter { it.estado == EstadoServicio.PENDIENTE || it.estado == EstadoServicio.EN_PROCESO }
  }
  val canceladosList = remember(timeFilteredServices) {
    timeFilteredServices.filter { it.estado == EstadoServicio.CANCELADO }
  }

  val totalIngresosRealizados = remember(completadosList) {
    completadosList.sumOf { it.precio }
  }
  val totalDineroPendiente = remember(pendientesList) {
    pendientesList.sumOf { it.precio }
  }
  val totalCanceladoNoFacturado = remember(canceladosList) {
    canceladosList.sumOf { it.precio }
  }
  val ticketPromedio = remember(completadosList, totalIngresosRealizados) {
    if (completadosList.isNotEmpty()) totalIngresosRealizados / completadosList.size else 0.0
  }

  // Filter for display list (search and status)
  val displayedServices = remember(timeFilteredServices, searchQuery, selectedStatusFilter) {
    var list = when (selectedStatusFilter) {
      StatusFilterOption.TODOS -> timeFilteredServices
      StatusFilterOption.SOLO_COMPLETADOS -> timeFilteredServices.filter { it.estado == EstadoServicio.COMPLETADO }
      StatusFilterOption.SOLO_CANCELADOS -> timeFilteredServices.filter { it.estado == EstadoServicio.CANCELADO }
    }.sortedByDescending { it.timestamp }

    if (searchQuery.isBlank()) list
    else {
      list.filter { srv ->
        val cliente = clientes.find { it.clienteId == srv.clienteId }
        srv.nombre.contains(searchQuery, ignoreCase = true) ||
          srv.categoria.contains(searchQuery, ignoreCase = true) ||
          srv.descripcion.contains(searchQuery, ignoreCase = true) ||
          (cliente?.nombre?.contains(searchQuery, ignoreCase = true) == true)
      }
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("historial_screen")
  ) {
    item {
      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Centro Financiero",
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
      )

      Text(
        text = "Balance contable, ingresos reales y auditoría de servicios",
        style = MaterialTheme.typography.bodyMedium.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Time Filter Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TimeFilter.entries.forEach { filter ->
          FilterChip(
            selected = selectedTimeFilter == filter,
            onClick = { selectedTimeFilter = filter },
            label = {
              Text(
                text = filter.label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
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

      Spacer(modifier = Modifier.height(14.dp))

      // Financial Primary Cards: INGRESOS REALIZADOS & DINERO PENDIENTE
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatisticCard(
          title = "Ingresos Realizados",
          value = Formatters.formatCurrency(totalIngresosRealizados, appConfig.monedaSimbolo),
          icon = Icons.Default.Payments,
          iconTint = ServixStatusColors.CompletadoPrimary,
          subtitle = "${completadosList.size} trabajos facturados",
          trendText = "100% Cobrado",
          modifier = Modifier.weight(1f)
        )

        StatisticCard(
          title = "Dinero Pendiente",
          value = Formatters.formatCurrency(totalDineroPendiente, appConfig.monedaSimbolo),
          icon = Icons.Default.HourglassTop,
          iconTint = Color(0xFFD97706),
          subtitle = "${pendientesList.size} por finalizar/cobrar",
          trendText = "En ejecución",
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Secondary Cards: TICKET PROMEDIO & CANCELADOS
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatisticCard(
          title = "Ticket Promedio",
          value = Formatters.formatCurrency(ticketPromedio, appConfig.monedaSimbolo),
          icon = Icons.AutoMirrored.Filled.ReceiptLong,
          iconTint = ServixBlueSecondary,
          subtitle = "Por servicio finalizado",
          modifier = Modifier.weight(1f)
        )

        StatisticCard(
          title = "Cancelados (No Fact.)",
          value = Formatters.formatCurrency(totalCanceladoNoFacturado, appConfig.monedaSimbolo),
          icon = Icons.Default.Cancel,
          iconTint = ServixStatusColors.CanceladoPrimary,
          subtitle = "${canceladosList.size} anulados",
          trendText = "Sin cobro",
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Visual Breakdown Chart (Proportion of Services)
      val totalCount = timeFilteredServices.size.coerceAtLeast(1)
      val pctCompletado = completadosList.size.toFloat() / totalCount
      val pctPendiente = pendientesList.size.toFloat() / totalCount
      val pctCancelado = canceladosList.size.toFloat() / totalCount

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.PieChart,
                contentDescription = null,
                tint = ServixBluePrimary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Distribución de Cartera (${timeFilteredServices.size} servicios)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Proportional Bar
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(12.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant)
          ) {
            if (pctCompletado <= 0f && pctPendiente <= 0f && pctCancelado <= 0f) {
              Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant))
            } else {
              if (pctCompletado > 0) {
                Box(
                  modifier = Modifier
                    .weight(pctCompletado.coerceAtLeast(0.01f))
                    .fillMaxHeight()
                    .background(ServixStatusColors.CompletadoPrimary)
                )
              }
              if (pctPendiente > 0) {
                Box(
                  modifier = Modifier
                    .weight(pctPendiente.coerceAtLeast(0.01f))
                    .fillMaxHeight()
                    .background(Color(0xFFD97706))
                )
              }
              if (pctCancelado > 0) {
                Box(
                  modifier = Modifier
                    .weight(pctCancelado.coerceAtLeast(0.01f))
                    .fillMaxHeight()
                    .background(ServixStatusColors.CanceladoPrimary)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Legend
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(ServixStatusColors.CompletadoPrimary)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Completados (${completadosList.size})",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFD97706))
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "En curso (${pendientesList.size})",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(ServixStatusColors.CanceladoPrimary)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Cancelados (${canceladosList.size})",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Search & Status filters
      ServixSearchBar(
        query = searchQuery,
        onQueryChange = { searchQuery = it },
        placeholder = "Buscar por cliente, servicio o categoría..."
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        StatusFilterOption.entries.forEach { option ->
          FilterChip(
            selected = selectedStatusFilter == option,
            onClick = { selectedStatusFilter = option },
            label = {
              Text(
                text = option.label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
              )
            },
            shape = RoundedCornerShape(10.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
              selectedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      SectionHeader(title = "Trazabilidad de Trabajos (${displayedServices.size})")

      Spacer(modifier = Modifier.height(8.dp))
    }

    if (displayedServices.isEmpty()) {
      item {
        EmptyStateView(
          title = "Sin registros para este filtro",
          subtitle = "No se encontraron servicios bajo los criterios de fecha y estado seleccionados.",
          icon = Icons.Default.History
        )
      }
    } else {
      items(displayedServices, key = { it.servicioId }) { servicio ->
        val cliente = clientes.find { it.clienteId == servicio.clienteId }
        ServiceCard(
          servicio = servicio,
          clienteNombre = cliente?.nombre ?: "Cliente",
          onClick = { onServiceClick(servicio.servicioId) },
          onEdit = { },
          onDelete = { onDeleteServicio(servicio.servicioId) },
          onStatusChange = { newStatus -> onUpdateServicioEstado(servicio.servicioId, newStatus) },
          monedaSimbolo = appConfig.monedaSimbolo,
          modifier = Modifier.padding(bottom = 10.dp)
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}
