package com.example.presentation.dialogs

import com.example.presentation.components.ServixLogo
import com.example.presentation.components.ServixOfficialBadgeCard
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.core.theme.ServixBlueAccent
import com.example.core.theme.ServixBlueDeep
import com.example.core.theme.ServixBluePrimary

@Composable
fun AboutDialog(
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
    Surface(
      modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(22.dp))
        .testTag("about_dialog"),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar")
          }
        }

        ServixOfficialBadgeCard(size = 80.dp)
        Spacer(modifier = Modifier.height(12.dp))
        ServixLogo(showBadge = false, showTagline = true)

        Spacer(modifier = Modifier.height(10.dp))

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ServixBluePrimary.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = "VERSIÓN 1.0 • PROTOTIPO EMPRESARIAL",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = ServixBluePrimary,
              letterSpacing = 0.5.sp
            )
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "ServiX es una herramienta integral de gestión y control operativo diseñada especialmente para trabajadores independientes, técnicos especializados y pequeños negocios de servicios.",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
          )
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(14.dp))

        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = Color(0xFF059669),
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Privacidad y Almacenamiento Local",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Toda la información reside de forma segura en la memoria de la aplicación sin rastreo externo.",
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedButton(
          onClick = onDismiss,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Entendido", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
