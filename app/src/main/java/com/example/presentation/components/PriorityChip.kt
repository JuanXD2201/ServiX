package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.PrioridadServicio

@Composable
fun PriorityBadge(
  prioridad: PrioridadServicio,
  modifier: Modifier = Modifier,
  showIcon: Boolean = true
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(prioridad.containerColor)
      .padding(horizontal = 8.dp, vertical = 3.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      if (showIcon) {
        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(prioridad.color)
        )
        Spacer(modifier = Modifier.width(5.dp))
      }
      Text(
        text = prioridad.displayName.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp,
          color = prioridad.textColor,
          letterSpacing = 0.5.sp
        )
      )
    }
  }
}

@Composable
fun PrioritySelector(
  selectedPrioridad: PrioridadServicio,
  onSelect: (PrioridadServicio) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically
  ) {
    PrioridadServicio.values().forEach { prio ->
      val isSelected = prio == selectedPrioridad
      Box(
        modifier = Modifier
          .padding(end = 6.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(if (isSelected) prio.containerColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
          .border(
            width = if (isSelected) 1.5.dp else 0.5.dp,
            color = if (isSelected) prio.color else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            shape = RoundedCornerShape(10.dp)
          )
          .clickable { onSelect(prio) }
          .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(prio.color)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = prio.displayName,
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) prio.textColor else MaterialTheme.colorScheme.onSurface
            )
          )
        }
      }
    }
  }
}
