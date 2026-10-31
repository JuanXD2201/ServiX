package com.example.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FavoriteStarButton(
  isFavorite: Boolean,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier,
  size: Dp = 22.dp,
  activeColor: Color = Color(0xFFF59E0B),
  inactiveColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
  testTag: String = "favorite_star_button"
) {
  val scale by animateFloatAsState(
    targetValue = if (isFavorite) 1.15f else 1.0f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
    label = "star_scale"
  )
  val tintColor by animateColorAsState(
    targetValue = if (isFavorite) activeColor else inactiveColor,
    label = "star_tint"
  )

  Box(
    modifier = modifier
      .clip(CircleShape)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(bounded = false, radius = size),
        onClick = onToggle
      )
      .padding(4.dp)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
      contentDescription = if (isFavorite) "Quitar de favoritos" else "Marcar como favorito",
      tint = tintColor,
      modifier = Modifier
        .size(size)
        .scale(scale)
    )
  }
}
