package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.EstadoServicio

@Composable
fun StatusChip(
  estado: EstadoServicio,
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null
) {
  val shape = RoundedCornerShape(100.dp)
  val clickModifier = if (onClick != null) {
    Modifier.clickable { onClick() }
  } else {
    Modifier
  }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .clip(shape)
      .background(estado.containerColor)
      .then(clickModifier)
      .padding(horizontal = 10.dp, vertical = 4.dp)
  ) {
    Icon(
      imageVector = estado.icon,
      contentDescription = estado.displayName,
      tint = estado.textColor,
      modifier = Modifier.size(13.dp)
    )
    Spacer(modifier = Modifier.width(5.dp))
    Text(
      text = estado.displayName.uppercase(),
      style = MaterialTheme.typography.labelSmall.copy(
        color = estado.textColor,
        fontWeight = FontWeight.Bold,
        fontSize = 10.5.sp,
        letterSpacing = 0.4.sp
      )
    )
  }
}
