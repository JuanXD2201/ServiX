package com.example.presentation.dialogs

import com.example.presentation.components.PrioritySelector
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.model.Cliente
import com.example.domain.model.EstadoServicio
import com.example.domain.model.PrioridadServicio
import com.example.domain.model.Servicio
import com.example.core.utils.Formatters
import com.example.core.utils.Validators

@Composable
fun ServiceFormDialog(
  servicioToEdit: Servicio? = null,
  preselectedClienteId: String? = null,
  clientes: List<Cliente>,
  onDismiss: () -> Unit,
  onSave: (
    clienteId: String,
    nombre: String,
    descripcion: String,
    precio: Double,
    fecha: String,
    estado: EstadoServicio,
    prioridad: PrioridadServicio,
    observaciones: String,
    categoria: String,
    fechaEntrega: String
  ) -> Unit
) {
  val defaultToday = remember { Formatters.formatDate(System.currentTimeMillis()) }

  var selectedClienteId by remember {
    mutableStateOf(servicioToEdit?.clienteId ?: preselectedClienteId ?: clientes.firstOrNull()?.clienteId ?: "")
  }
  var nombre by remember { mutableStateOf(servicioToEdit?.nombre ?: "") }
  var descripcion by remember { mutableStateOf(servicioToEdit?.descripcion ?: "") }
  var categoria by remember { mutableStateOf(servicioToEdit?.categoria ?: "General") }
  var precioStr by remember { mutableStateOf(if (servicioToEdit != null && servicioToEdit.precio > 0) servicioToEdit.precio.toInt().toString() else "") }
  var fecha by remember { mutableStateOf(if (!servicioToEdit?.fecha.isNullOrBlank()) servicioToEdit!!.fecha else defaultToday) }
  var fechaEntrega by remember { mutableStateOf(servicioToEdit?.fechaEntrega ?: "") }
  var estado by remember { mutableStateOf(servicioToEdit?.estado ?: EstadoServicio.PENDIENTE) }
  var prioridad by remember { mutableStateOf(servicioToEdit?.prioridad ?: PrioridadServicio.MEDIA) }
  var observaciones by remember { mutableStateOf(servicioToEdit?.observaciones ?: "") }

  var showSummaryConfirm by remember { mutableStateOf(false) }
  var clientMenuExpanded by remember { mutableStateOf(false) }
  var statusMenuExpanded by remember { mutableStateOf(false) }

  var nombreError by remember { mutableStateOf<String?>(null) }
  var clienteError by remember { mutableStateOf<String?>(null) }

  val predefinedCategories = listOf("General", "Climatización", "Electricidad", "Redes", "Fontanería", "Mantenimiento General", "Domótica")
  val selectedCliente = clientes.find { it.clienteId == selectedClienteId }

  if (showSummaryConfirm) {
    ServiceOrderSummaryDialog(
      clienteNombre = selectedCliente?.nombre ?: "Cliente",
      servicioNombre = nombre,
      categoria = categoria,
      precio = precioStr.toDoubleOrNull() ?: 0.0,
      fecha = fecha,
      fechaEntrega = fechaEntrega,
      prioridad = prioridad,
      estado = estado,
      onConfirm = {
        showSummaryConfirm = false
        onSave(
          selectedClienteId,
          nombre.trim(),
          descripcion.trim(),
          precioStr.toDoubleOrNull() ?: 0.0,
          fecha.trim(),
          estado,
          prioridad,
          observaciones.trim(),
          categoria.trim(),
          fechaEntrega.trim()
        )
      },
      onDismiss = { showSummaryConfirm = false }
    )
    return
  }

  Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("service_form_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Text(
          text = if (servicioToEdit == null) "Nueva Orden de Servicio" else "Editar Orden de Servicio",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (clientes.isEmpty()) {
          Card(
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "No tienes clientes registrados. Primero crea un cliente antes de asignarle un servicio.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onErrorContainer)
              )
            }
          }
          Spacer(modifier = Modifier.height(16.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            Button(onClick = onDismiss) {
              Text("Cerrar")
            }
          }
          return@Card
        }

        // Selector de Cliente
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = selectedCliente?.nombre ?: "Seleccionar Cliente",
            onValueChange = {},
            readOnly = true,
            label = { Text("Cliente Asignado *") },
            trailingIcon = {
              Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            },
            isError = clienteError != null,
            supportingText = {
              clienteError?.let { Text(it) }
            },
            modifier = Modifier
              .fillMaxWidth()
              .clickable { clientMenuExpanded = true }
              .testTag("select_client_field")
          )
          DropdownMenu(
            expanded = clientMenuExpanded,
            onDismissRequest = { clientMenuExpanded = false }
          ) {
            clientes.forEach { cli ->
              DropdownMenuItem(
                text = { Text(cli.nombre) },
                onClick = {
                  selectedClienteId = cli.clienteId
                  clienteError = null
                  clientMenuExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = nombre,
          onValueChange = {
            nombre = it
            if (Validators.isValidServiceTitle(it)) nombreError = null
          },
          label = { Text("Nombre del Trabajo / Servicio *") },
          isError = nombreError != null,
          supportingText = {
            nombreError?.let { Text(it) }
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_service_name")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Prioridad Selector
        Text(
          text = "Nivel de Prioridad:",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        PrioritySelector(
          selectedPrioridad = prioridad,
          onSelect = { prioridad = it },
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Categoría Selector
        Text(
          text = "Categoría:",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          predefinedCategories.forEach { cat ->
            FilterChip(
              selected = categoria == cat,
              onClick = { categoria = cat },
              label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = descripcion,
          onValueChange = { descripcion = it },
          label = { Text("Descripción del Trabajo") },
          minLines = 2,
          maxLines = 4,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_service_description")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = precioStr,
            onValueChange = { precioStr = it.filter { char -> char.isDigit() } },
            label = { Text("Precio / Costo ($)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("input_service_price")
          )

          Spacer(modifier = Modifier.width(8.dp))

          OutlinedTextField(
            value = fecha,
            onValueChange = { fecha = it },
            label = { Text("Fecha Inicio") },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("input_service_date")
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = fechaEntrega,
          onValueChange = { fechaEntrega = it },
          label = { Text("Fecha Entrega Estimada (opcional)") },
          placeholder = { Text("ej. 10/09/2026") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Selector de Estado
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = estado.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Estado del Servicio") },
            trailingIcon = {
              Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            },
            modifier = Modifier
              .fillMaxWidth()
              .clickable { statusMenuExpanded = true }
              .testTag("select_status_field")
          )
          DropdownMenu(
            expanded = statusMenuExpanded,
            onDismissRequest = { statusMenuExpanded = false }
          ) {
            EstadoServicio.entries.forEach { est ->
              DropdownMenuItem(
                text = { Text(est.displayName) },
                onClick = {
                  estado = est
                  statusMenuExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = observaciones,
          onValueChange = { observaciones = it },
          label = { Text("Observaciones / Notas Técnicas") },
          minLines = 2,
          maxLines = 3,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_service_observations")
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("cancel_service_button")
          ) {
            Text("Cancelar")
          }

          Spacer(modifier = Modifier.width(10.dp))

          Button(
            onClick = {
              var valid = true
              if (!Validators.isValidServiceTitle(nombre)) {
                nombreError = "El título debe tener al menos 3 caracteres"
                valid = false
              }
              if (selectedClienteId.isBlank()) {
                clienteError = "Selecciona un cliente"
                valid = false
              }
              if (valid) {
                // Show summary before saving if creating new service
                if (servicioToEdit == null) {
                  showSummaryConfirm = true
                } else {
                  val parsedPrice = precioStr.toDoubleOrNull() ?: 0.0
                  onSave(
                    selectedClienteId,
                    nombre.trim(),
                    descripcion.trim(),
                    parsedPrice,
                    fecha.trim(),
                    estado,
                    prioridad,
                    observaciones.trim(),
                    categoria.trim(),
                    fechaEntrega.trim()
                  )
                }
              }
            },
            modifier = Modifier.testTag("save_service_button")
          ) {
            Text(if (servicioToEdit == null) "Continuar" else "Guardar")
          }
        }
      }
    }
  }
}
