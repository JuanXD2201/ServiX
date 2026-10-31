package com.example.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.theme.ServixBlueAccent
import com.example.core.theme.ServixBlueDark
import com.example.core.theme.ServixBlueLight
import com.example.core.theme.ServixBluePrimary
import com.example.core.theme.ServixBlueSecondary
import com.example.core.theme.ServixBlueVeryLight
import com.example.core.utils.ImageUtils

/**
 * Icono de marca oficial ServiX.
 * Utiliza el isotipo y badge oficial 3D con bordes redondeados y estilo moderno.
 */
@Composable
fun ServixBrandIcon(
  modifier: Modifier = Modifier,
  size: Dp = 36.dp,
  backgroundColor: Color = Color.White,
  iconTint: Color = MaterialTheme.colorScheme.onPrimary
) {
  Box(
    modifier = modifier
      .size(size)
      .shadow(elevation = 2.dp, shape = RoundedCornerShape(size * 0.25f))
      .clip(RoundedCornerShape(size * 0.25f))
      .background(backgroundColor)
      .border(0.5.dp, ServixBlueLight.copy(alpha = 0.35f), RoundedCornerShape(size * 0.25f)),
    contentAlignment = Alignment.Center
  ) {
    Image(
      painter = painterResource(id = R.drawable.servix_logo),
      contentDescription = "ServiX Logo",
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.Fit
    )
  }
}

/**
 * Logotipo completo ServiX con soporte para modo claro/oscuro y lema oficial.
 * "CLIENTES • SERVICIOS • SEGUIMIENTO"
 */
@Composable
fun ServixLogo(
  modifier: Modifier = Modifier,
  iconSize: Dp = 36.dp,
  showBadge: Boolean = true,
  showTagline: Boolean = false,
  onDarkBackground: Boolean = false
) {
  val baseTextColor = if (onDarkBackground) Color.White else ServixBlueDark
  val xColor = if (onDarkBackground) ServixBlueSecondary else ServixBluePrimary

  Column(modifier = modifier) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      if (showBadge) {
        ServixBrandIcon(
          size = iconSize,
          backgroundColor = Color.White
        )
        Spacer(modifier = Modifier.width(10.dp))
      }
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "Servi",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.ExtraBold,
            color = baseTextColor,
            letterSpacing = 0.5.sp,
            fontSize = (iconSize.value * 0.65f).coerceIn(16f, 26f).sp
          )
        )
        Text(
          text = "X",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Black,
            color = xColor,
            letterSpacing = 0.5.sp,
            fontSize = (iconSize.value * 0.72f).coerceIn(18f, 28f).sp
          )
        )
      }
    }
    if (showTagline) {
      Spacer(modifier = Modifier.height(2.dp))
      ServixTagline(onDarkBackground = onDarkBackground, fontSize = 9.sp)
    }
  }
}

/**
 * Lema oficial de ServiX tal como figura en el logotipo oficial.
 */
@Composable
fun ServixTagline(
  modifier: Modifier = Modifier,
  onDarkBackground: Boolean = false,
  fontSize: androidx.compose.ui.unit.TextUnit = 10.sp
) {
  val textColor = if (onDarkBackground) Color.White.copy(alpha = 0.85f) else ServixBlueDark.copy(alpha = 0.75f)
  val dotColor = if (onDarkBackground) ServixBlueLight else ServixBlueSecondary

  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Text(
      text = "CLIENTES",
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        color = textColor
      )
    )
    Text(
      text = "•",
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = fontSize,
        fontWeight = FontWeight.Black,
        color = dotColor
      )
    )
    Text(
      text = "SERVICIOS",
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        color = textColor
      )
    )
    Text(
      text = "•",
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = fontSize,
        fontWeight = FontWeight.Black,
        color = dotColor
      )
    )
    Text(
      text = "SEGUIMIENTO",
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        color = textColor
      )
    )
  }
}

/**
 * Tarjeta de presentación con el badge insignia oficial de ServiX.
 */
@Composable
fun ServixOfficialBadgeCard(
  modifier: Modifier = Modifier,
  size: Dp = 96.dp
) {
  Surface(
    shape = RoundedCornerShape(24.dp),
    color = Color.White,
    shadowElevation = 6.dp,
    border = androidx.compose.foundation.BorderStroke(1.dp, ServixBlueVeryLight),
    modifier = modifier.size(size)
  ) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(4.dp)) {
      Image(
        painter = painterResource(id = R.drawable.servix_logo),
        contentDescription = "ServiX Badge Oficial",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit
      )
    }
  }
}

@Composable
fun InitialsAvatar(
  initials: String,
  modifier: Modifier = Modifier,
  size: Dp = 44.dp,
  useGradient: Boolean = false,
  backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
  textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
  base64Image: String? = null
) {
  val backgroundModifier = if (useGradient) {
    Modifier.background(
      Brush.linearGradient(
        colors = listOf(
          ServixBlueDark,
          ServixBluePrimary
        )
      )
    )
  } else {
    Modifier.background(backgroundColor)
  }

  val finalTextColor = if (useGradient) Color.White else textColor

  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .then(backgroundModifier)
      .border(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), CircleShape),
    contentAlignment = Alignment.Center
  ) {
    var showInitials = true
    if (!base64Image.isNullOrBlank()) {
      val bitmap = remember(base64Image) { ImageUtils.decodeBase64ToBitmap(base64Image) }
      if (bitmap != null) {
        showInitials = false
        Image(
          bitmap = bitmap.asImageBitmap(),
          contentDescription = "Avatar",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
      }
    }
    
    if (showInitials) {
      Text(
        text = initials,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = finalTextColor,
          fontSize = (size.value * 0.38f).sp
        )
      )
    }
  }
}
