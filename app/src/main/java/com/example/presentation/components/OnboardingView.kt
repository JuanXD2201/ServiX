package com.example.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.theme.ServixBlueAccent
import com.example.core.theme.ServixBlueDark
import com.example.core.theme.ServixBlueDeep
import com.example.core.theme.ServixBluePrimary

data class OnboardingSlide(
  val title: String,
  val subtitle: String,
  val description: String,
  val icon: ImageVector,
  val badge: String
)

@Composable
fun OnboardingView(
  onComplete: () -> Unit,
  modifier: Modifier = Modifier
) {
  var currentStep by remember { mutableStateOf(0) }

  val slides = listOf(
    OnboardingSlide(
      title = "Bienvenido a ServiX",
      subtitle = "Clientes • Servicios • Seguimiento",
      description = "Centraliza el registro de clientes, cotizaciones y órdenes de servicio con total claridad y rapidez.",
      icon = Icons.Default.Handyman,
      badge = "Paso 1 de 4"
    ),
    OnboardingSlide(
      title = "Organiza tus Trabajos",
      subtitle = "Controla estados y prioridades",
      description = "Supervisa el avance de cada tarea: Pendiente, En Proceso y Completado con avisos inmediatos.",
      icon = Icons.Default.People,
      badge = "Paso 2 de 4"
    ),
    OnboardingSlide(
      title = "Conoce tus Finanzas",
      subtitle = "Ingresos y metas semanales",
      description = "Visualiza el dinero recaudado, ticket promedio y define objetivos de productividad para hacer crecer tu negocio.",
      icon = Icons.Default.Analytics,
      badge = "Paso 3 de 4"
    ),
    OnboardingSlide(
      title = "Listo para Trabajar",
      subtitle = "100% privado y autónomo",
      description = "Todos tus datos residen de forma segura en tu dispositivo. Comienza ahora registrando tu primer cliente.",
      icon = Icons.Default.RocketLaunch,
      badge = "Paso 4 de 4"
    )
  )

  val currentSlide = slides[currentStep]

  Surface(
    modifier = modifier
      .fillMaxSize()
      .testTag("onboarding_screen"),
    color = MaterialTheme.colorScheme.background
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top row: Skip button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        if (currentStep < slides.size - 1) {
          TextButton(onClick = onComplete) {
            Text(
              text = "Saltar",
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        } else {
          Spacer(modifier = Modifier.height(48.dp))
        }
      }

      Spacer(modifier = Modifier.weight(0.5f))

      // Icon Hero Container
      if (currentStep == 0) {
        ServixOfficialBadgeCard(size = 130.dp)
      } else {
        Box(
          modifier = Modifier
            .size(130.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(
              Brush.linearGradient(
                colors = listOf(ServixBluePrimary, ServixBlueDark)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = currentSlide.icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(60.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Badge
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(ServixBluePrimary.copy(alpha = 0.12f))
          .padding(horizontal = 12.dp, vertical = 4.dp)
      ) {
        Text(
          text = currentSlide.badge.uppercase(),
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = ServixBluePrimary,
            letterSpacing = 1.sp
          )
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = currentSlide.title,
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        ),
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = currentSlide.subtitle,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.SemiBold,
          color = ServixBluePrimary
        ),
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = currentSlide.description,
        style = MaterialTheme.typography.bodyLarge.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 22.sp
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
      )

      Spacer(modifier = Modifier.weight(1f))

      // Dots Indicator
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        slides.indices.forEach { index ->
          val isSelected = index == currentStep
          Box(
            modifier = Modifier
              .size(width = if (isSelected) 24.dp else 8.dp, height = 8.dp)
              .clip(CircleShape)
              .background(
                if (isSelected) ServixBluePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
              )
          )
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Bottom Navigation Button
      Button(
        onClick = {
          if (currentStep < slides.size - 1) {
            currentStep++
          } else {
            onComplete()
          }
        },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ServixBluePrimary),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("onboarding_next_button")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = if (currentStep == slides.size - 1) "Comenzar ahora" else "Siguiente",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          )
          Spacer(modifier = Modifier.width(8.dp))
          Icon(
            imageVector = if (currentStep == slides.size - 1) Icons.Default.Check else Icons.Default.ArrowForward,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
