package com.example.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.model.EstadoServicio
import com.example.domain.model.Servicio
import com.example.core.theme.ServixBlueAccent
import com.example.core.theme.ServixBlueDeep
import com.example.core.theme.ServixBluePrimary
import com.example.core.utils.Formatters

@Composable
fun WeeklyGoalsCard(
  servicios: List<Servicio>,
  targetServices: Int,
  targetRevenue: Double,
  monedaSimbolo: String,
  onUpdateGoals: (Int, Double) -> Unit,
  modifier: Modifier = Modifier
) {
  var showEditDialog by remember { mutableStateOf(false) }

  val completedServices = remember(servicios) {
    servicios.filter { it.estado == EstadoServicio.COMPLETADO }
  }
  val completedCount = completedServices.size
  val completedRevenue = completedServices.sumOf { it.precio }

  val countProgress = if (targetServices > 0) (completedCount.toFloat() / targetServices).coerceIn(0f, 1f) else 0f
  val revenueProgress = if (targetRevenue > 0) (completedRevenue.toFloat() / targetRevenue.toFloat()).coerceIn(0f, 1f) else 0f

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("weekly_goals_card"),
    shape = RoundedCornerShape(20.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
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
          Box(
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(ServixBluePrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.TrackChanges,
              contentDescription = null,
              tint = ServixBluePrimary,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Objetivo Semanal",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            )
            Text(
              text = "Meta de productividad e ingresos",
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }

        IconButton(
          onClick = { showEditDialog = true },
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Editar objetivos",
            tint = ServixBluePrimary,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Servicios meta
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Servicios Concluidos",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
          )
          Text(
            text = "$completedCount / $targetServices trabajos",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = ServixBluePrimary
            )
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
          progress = { countProgress },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = ServixBluePrimary,
          trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Ingresos meta
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Ingresos Recaudados",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
          )
          Text(
            text = "${Formatters.formatCurrency(completedRevenue, monedaSimbolo)} / ${Formatters.formatCurrency(targetRevenue, monedaSimbolo)}",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Color(0xFF059669)
            )
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
          progress = { revenueProgress },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = Color(0xFF059669),
          trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
      }
    }
  }

  if (showEditDialog) {
    var srvCountStr by remember { mutableStateOf(targetServices.toString()) }
    var revStr by remember { mutableStateOf(targetRevenue.toLong().toString()) }

    Dialog(onDismissRequest = { showEditDialog = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          Text(
            text = "Ajustar Objetivos Semanales",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = srvCountStr,
            onValueChange = { srvCountStr = it.filter { ch -> ch.isDigit() } },
            label = { Text("Meta de servicios") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = revStr,
            onValueChange = { revStr = it.filter { ch -> ch.isDigit() } },
            label = { Text("Meta de facturación ($monedaSimbolo)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(18.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            OutlinedButton(onClick = { showEditDialog = false }) {
              Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
              onClick = {
                val srvCount = srvCountStr.toIntOrNull() ?: targetServices
                val rev = revStr.toDoubleOrNull() ?: targetRevenue
                onUpdateGoals(srvCount, rev)
                showEditDialog = false
              }
            ) {
              Text("Guardar Metas")
            }
          }
        }
      }
    }
  }
}
