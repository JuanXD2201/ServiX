package com.example.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.domain.model.Actividad
import com.example.domain.model.TipoActividad
import com.example.core.theme.ServixStatusColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ServixSearchBar(
  query: String,
  onQueryChange: (String) -> Unit,
  placeholder: String,
  modifier: Modifier = Modifier,
  testTag: String = "search_bar"
) {
  OutlinedTextField(
    value = query,
    onValueChange = onQueryChange,
    placeholder = {
      Text(
        text = placeholder,
        style = MaterialTheme.typography.bodyMedium.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
      )
    },
    leadingIcon = {
      Icon(
        imageVector = Icons.Default.Search,
        contentDescription = "Buscar",
        tint = MaterialTheme.colorScheme.onSurfaceVariant
      )
    },
    trailingIcon = {
      AnimatedVisibility(
        visible = query.isNotEmpty(),
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        IconButton(onClick = { onQueryChange("") }) {
          Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Limpiar búsqueda",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    },
    singleLine = true,
    shape = RoundedCornerShape(14.dp),
    colors = OutlinedTextFieldDefaults.colors(
      focusedContainerColor = MaterialTheme.colorScheme.surface,
      unfocusedContainerColor = MaterialTheme.colorScheme.surface,
      focusedBorderColor = MaterialTheme.colorScheme.primary,
      unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
    ),
    modifier = modifier
      .fillMaxWidth()
      .testTag(testTag)
  )
}

@Composable
fun SectionHeader(
  title: String,
  modifier: Modifier = Modifier,
  actionLabel: String? = null,
  onActionClick: (() -> Unit)? = null
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
    )
    if (actionLabel != null && onActionClick != null) {
      TextButton(onClick = onActionClick) {
        Text(
          text = actionLabel,
          style = MaterialTheme.typography.labelLarge.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
          )
        )
      }
    }
  }
}

@Composable
fun QuickActionButton(
  icon: ImageVector,
  label: String,
  onClick: () -> Unit,
  containerColor: Color = MaterialTheme.colorScheme.primary,
  contentColor: Color = Color.White,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .testTag("quick_action_${label.lowercase().replace(" ", "_")}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = containerColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 13.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(28.dp)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = contentColor,
          modifier = Modifier.size(16.dp)
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelLarge.copy(
          fontWeight = FontWeight.Bold,
          color = contentColor,
          fontSize = 13.sp
        ),
        maxLines = 1
      )
    }
  }
}

@Composable
fun EmptyStateView(
  title: String,
  subtitle: String,
  icon: ImageVector,
  modifier: Modifier = Modifier,
  actionButton: @Composable (() -> Unit)? = null
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(68.dp)
        .clip(RoundedCornerShape(18.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(32.dp)
      )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = subtitle,
      style = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    )
    if (actionButton != null) {
      Spacer(modifier = Modifier.height(16.dp))
      actionButton()
    }
  }
}

@Composable
fun ConfirmDialog(
  title: String,
  message: String,
  confirmButtonText: String = "Confirmar",
  dismissButtonText: String = "Cancelar",
  isDestructive: Boolean = false,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium
      )
    },
    confirmButton = {
      Button(
        onClick = onConfirm,
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
      ) {
        Text(confirmButtonText)
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text(dismissButtonText)
      }
    }
  )
}

@Composable
fun ActivityItemCard(
  actividad: Actividad,
  modifier: Modifier = Modifier
) {
  val (iconColor, bgColor) = when (actividad.tipo) {
    TipoActividad.CLIENTE_CREADO, TipoActividad.CLIENTE_EDITADO ->
      Pair(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
    TipoActividad.SERVICIO_CREADO, TipoActividad.SERVICIO_ACTUALIZADO ->
      Pair(ServixStatusColors.EnProcesoPrimary, ServixStatusColors.EnProcesoContainer)
    TipoActividad.SERVICIO_COMPLETADO ->
      Pair(ServixStatusColors.CompletadoPrimary, ServixStatusColors.CompletadoContainer)
    TipoActividad.SERVICIO_CANCELADO, TipoActividad.CLIENTE_ELIMINADO ->
      Pair(ServixStatusColors.CanceladoPrimary, ServixStatusColors.CanceladoContainer)
  }

  val timeAgoStr = remember(actividad.fecha) {
    val diff = System.currentTimeMillis() - actividad.fecha
    val minutes = diff / (60 * 1000)
    val hours = minutes / 60
    val days = hours / 24
    when {
      minutes < 2 -> "Hace un momento"
      minutes < 60 -> "Hace $minutes min"
      hours < 24 -> "Hace $hours h"
      days == 1L -> "Ayer"
      days < 30 -> "Hace $days días"
      else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(actividad.fecha))
    }
  }

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(bgColor),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = actividad.tipo.icon,
          contentDescription = null,
          tint = iconColor,
          modifier = Modifier.size(18.dp)
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
            text = actividad.titulo,
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
          )

          Text(
            text = timeAgoStr,
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
              fontSize = 10.sp
            )
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = actividad.descripcion,
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
          ),
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}
