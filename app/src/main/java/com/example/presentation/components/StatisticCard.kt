package com.example.presentation.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatisticCard(
  title: String,
  value: String,
  icon: ImageVector,
  iconTint: Color,
  subtitle: String? = null,
  cardBg: Color? = null,
  cardContentColor: Color? = null,
  trendText: String? = null,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val containerColor = cardBg ?: MaterialTheme.colorScheme.surface
  val onColor = cardContentColor ?: MaterialTheme.colorScheme.onSurface

  val clickModifier = if (onClick != null) {
    Modifier.clickable { onClick() }
  } else {
    Modifier
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .then(clickModifier)
      .testTag("stat_card_${title.lowercase().replace(" ", "_")}"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = containerColor,
      contentColor = onColor
    ),
    border = if (cardBg == null) BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)) else null,
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(iconTint.copy(alpha = 0.12f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
          )
        }

        if (trendText != null) {
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = trendText,
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 10.sp
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = value,
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.ExtraBold,
          color = onColor,
          letterSpacing = (-0.5).sp
        ),
        maxLines = 1
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.SemiBold,
          color = onColor.copy(alpha = 0.75f)
        )
      )

      if (subtitle != null) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall.copy(
            color = onColor.copy(alpha = 0.6f),
            fontSize = 11.sp,
            lineHeight = 14.sp
          ),
          maxLines = 1
        )
      }
    }
  }
}
