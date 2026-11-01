package com.example.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.core.utils.ImageUtils
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.model.AppConfig
import com.example.domain.model.UserProfile
import com.example.presentation.dialogs.AboutDialog
import com.example.presentation.components.ConfirmDialog
import com.example.presentation.components.InitialsAvatar
import com.example.presentation.components.SectionHeader
import com.example.presentation.components.ServixLogo
import com.example.presentation.components.ServixBrandIcon
import com.example.presentation.components.ServixTagline
import com.example.core.theme.ServixBluePrimary

@Composable
fun PerfilScreen(
  userProfile: UserProfile,
  appConfig: AppConfig,
  onUpdateProfile: (UserProfile) -> Unit,
  onUpdateConfig: (AppConfig) -> Unit,
  onResetData: () -> Unit,
  onShowOnboarding: () -> Unit = {},
  onSignOut: () -> Unit = {},
  onTestMakeWebhook: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var showEditProfileDialog by remember { mutableStateOf(false) }
  var showPhotoOptions by remember { mutableStateOf(false) }
  var showResetConfirm by remember { mutableStateOf(false) }
  var showAboutDialog by remember { mutableStateOf(false) }
  var showGoalsDialog by remember { mutableStateOf(false) }

  val context = LocalContext.current
  val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
    if (uri != null) {
        try {
            val base64 = ImageUtils.processImageUri(context, uri)
            if (base64 != null) {
                onUpdateProfile(userProfile.copy(fotoPerfilBase64 = base64))
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "Error al cargar la imagen", Toast.LENGTH_LONG).show()
        }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .verticalScroll(rememberScrollState())
      .testTag("perfil_screen")
  ) {
    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = "Mi Negocio & Ajustes",
      style = MaterialTheme.typography.headlineMedium.copy(
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
    )

    Text(
      text = "Identidad profesional, metas de facturación y configuración",
      style = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Card de Perfil del Profesional
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("profile_card"),
      shape = RoundedCornerShape(22.dp),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
      )
    ) {
      val context = LocalContext.current
  val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
    if (uri != null) {
        try {
            val base64 = ImageUtils.processImageUri(context, uri)
            if (base64 != null) {
                onUpdateProfile(userProfile.copy(fotoPerfilBase64 = base64))
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "Error al cargar la imagen", Toast.LENGTH_LONG).show()
        }
    }
  }

  Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        val initials = userProfile.nombre.split(" ")
          .filter { it.isNotBlank() }
          .take(2)
          .map { it.take(1) }
          .joinToString("")
          .uppercase()

        InitialsAvatar(
          initials = if (initials.isNotBlank()) initials else "SX",
          size = 72.dp,
          useGradient = true,
          base64Image = userProfile.fotoPerfilBase64,
          modifier = Modifier.clickable { showPhotoOptions = true }
        )
        
        Text("Toca la foto para cambiarla", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = userProfile.nombre,
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        )

        Text(
          text = "${userProfile.rol} • ${userProfile.empresa}",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = ServixBluePrimary,
            fontWeight = FontWeight.SemiBold
          )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Call,
              contentDescription = null,
              tint = ServixBluePrimary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = userProfile.telefono,
              style = MaterialTheme.typography.bodyMedium
            )
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Email,
              contentDescription = null,
              tint = ServixBluePrimary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = userProfile.correo,
              style = MaterialTheme.typography.bodyMedium
            )
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = ServixBluePrimary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = userProfile.ciudad,
              style = MaterialTheme.typography.bodyMedium
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = { showEditProfileDialog = true },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = ServixBluePrimary),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("edit_profile_button")
        ) {
          Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Editar Perfil del Negocio", fontWeight = FontWeight.Bold)
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Metas y Objetivos Comerciales
    SectionHeader(
      title = "Metas y Objetivos Semanales",
      actionLabel = "Ajustar metas",
      onActionClick = { showGoalsDialog = true }
    )

    Spacer(modifier = Modifier.height(10.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.TrackChanges,
            contentDescription = null,
            tint = ServixBluePrimary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Meta Semanal Activa",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "${appConfig.objetivoServiciosSemanal} servicios • ${appConfig.monedaSimbolo} ${appConfig.objetivoIngresosSemanal.toInt()}",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }
        }

        OutlinedButton(
          onClick = { showGoalsDialog = true },
          shape = RoundedCornerShape(10.dp)
        ) {
          Text("Editar", fontSize = 12.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Moneda y Formato Financiero
    SectionHeader(title = "Moneda y Formato")

    Spacer(modifier = Modifier.height(10.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Símbolo de Moneda",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Selecciona el formato utilizado para los precios de servicios",
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Spacer(modifier = Modifier.height(12.dp))

        val currencyOptions = listOf("$", "COP $", "USD $", "€")
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          currencyOptions.forEach { symbol ->
            FilterChip(
              selected = appConfig.monedaSimbolo == symbol,
              onClick = { onUpdateConfig(appConfig.copy(monedaSimbolo = symbol)) },
              label = { Text(symbol, fontWeight = FontWeight.Bold) },
              shape = RoundedCornerShape(10.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ServixBluePrimary,
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Preferencias Operativas y Datos
    SectionHeader(title = "Preferencias & Almacenamiento")

    Spacer(modifier = Modifier.height(10.dp))

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      val context = LocalContext.current
  val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
    if (uri != null) {
        try {
            val base64 = ImageUtils.processImageUri(context, uri)
            if (base64 != null) {
                onUpdateProfile(userProfile.copy(fotoPerfilBase64 = base64))
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "Error al cargar la imagen", Toast.LENGTH_LONG).show()
        }
    }
  }

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
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Modo Oscuro (Tema Nocturno)",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
              text = "Alternar interfaz entre tonos oscuros y claros",
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
          Switch(
            checked = appConfig.modoOscuroForzado,
            onCheckedChange = {
              onUpdateConfig(appConfig.copy(modoOscuroForzado = it))
            },
            modifier = Modifier.testTag("dark_mode_switch")
          )
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Alertas Inteligentes",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
              text = "Avisos de servicios del día y trabajos pendientes",
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
          Switch(
            checked = appConfig.notificacionesActivas,
            onCheckedChange = {
              onUpdateConfig(appConfig.copy(notificacionesActivas = it))
            }
          )
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Autoguardado Local",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
              text = "Persistencia inmediata en memoria y alta velocidad",
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
          Switch(
            checked = appConfig.autoguardadoLocal,
            onCheckedChange = {
              onUpdateConfig(appConfig.copy(autoguardadoLocal = it))
            }
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Guía / Onboarding & Legal Actions
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      OutlinedButton(
        onClick = onShowOnboarding,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.weight(1f)
      ) {
        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Ver Tour / Guía", fontSize = 12.sp)
      }

      OutlinedButton(
        onClick = { showAboutDialog = true },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.weight(1f)
      ) {
        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Acerca de ServiX", fontSize = 12.sp)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Integración Make (Optativa II)
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("make_settings_card"),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
      ),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.CloudUpload,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Integración Externa Make",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "ServiX ↔ Make ↔ Sheets ↔ Gmail",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF10B981).copy(alpha = 0.15f)
          ) {
            Text(
              text = "ACTIVO",
              color = Color(0xFF059669),
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = if (com.example.core.utils.MakeWebhookConfig.isConfigured) "Integración con Make: activa" else "Integración con Make: no configurada",
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
          onClick = onTestMakeWebhook,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("btn_test_make_webhook")
        ) {
          Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Probar Conexión con Make Ahora", fontWeight = FontWeight.Bold)
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Cerrar Sesión / Cambiar Cuenta
    OutlinedButton(
      onClick = onSignOut,
      shape = RoundedCornerShape(14.dp),
      colors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
      ),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("sign_out_button")
    ) {
      Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar Sesión", modifier = Modifier.size(16.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Text("Cerrar Sesión / Cambiar Cuenta", fontWeight = FontWeight.SemiBold)
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Footer Branding
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(18.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      )
    ) {
      val context = LocalContext.current
  val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
    if (uri != null) {
        try {
            val base64 = ImageUtils.processImageUri(context, uri)
            if (base64 != null) {
                onUpdateProfile(userProfile.copy(fotoPerfilBase64 = base64))
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "Error al cargar la imagen", Toast.LENGTH_LONG).show()
        }
    }
  }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        ServixBrandIcon(size = 52.dp)
        Spacer(modifier = Modifier.height(10.dp))
        ServixLogo(showBadge = false)
        Spacer(modifier = Modifier.height(6.dp))
        ServixTagline(fontSize = 10.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Gestión profesional de clientes, servicios y seguimiento",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(80.dp))
  }


  if (showPhotoOptions) {
    Dialog(onDismissRequest = { showPhotoOptions = false }) {
      Card(shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
          Text("Foto de perfil", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
          Spacer(modifier = Modifier.height(16.dp))
          
          Text(
            text = "Seleccionar imagen",
            modifier = Modifier.fillMaxWidth().clickable {
                showPhotoOptions = false
                imagePickerLauncher.launch("image/*")
            }.padding(vertical = 12.dp)
          )
          
          if (userProfile.fotoPerfilBase64 != null) {
              Text(
                text = "Eliminar foto",
                color = Color.Red,
                modifier = Modifier.fillMaxWidth().clickable {
                    showPhotoOptions = false
                    onUpdateProfile(userProfile.copy(fotoPerfilBase64 = null))
                }.padding(vertical = 12.dp)
              )
          }
          
          Spacer(modifier = Modifier.height(8.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
              Button(onClick = { showPhotoOptions = false }) {
                  Text("Cancelar")
              }
          }
        }
      }
    }
  }

  // Diálogo de Edición de Perfil
  if (showEditProfileDialog) {
    var nom by remember { mutableStateOf(userProfile.nombre) }
    var rol by remember { mutableStateOf(userProfile.rol) }
    var emp by remember { mutableStateOf(userProfile.empresa) }
    var tel by remember { mutableStateOf(userProfile.telefono) }
    var cor by remember { mutableStateOf(userProfile.correo) }
    var ciu by remember { mutableStateOf(userProfile.ciudad) }

    Dialog(onDismissRequest = { showEditProfileDialog = false }) {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        val context = LocalContext.current
  val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
    if (uri != null) {
        try {
            val base64 = ImageUtils.processImageUri(context, uri)
            if (base64 != null) {
                onUpdateProfile(userProfile.copy(fotoPerfilBase64 = base64))
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "Error al cargar la imagen", Toast.LENGTH_LONG).show()
        }
    }
  }

  Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
        ) {
          Text(
            text = "Editar Perfil del Negocio",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = nom,
            onValueChange = { nom = it },
            label = { Text("Tu Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = rol,
            onValueChange = { rol = it },
            label = { Text("Oficio / Profesión") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = emp,
            onValueChange = { emp = it },
            label = { Text("Nombre del Negocio / Empresa") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = tel,
            onValueChange = { tel = it },
            label = { Text("Teléfono de Contacto") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = cor,
            onValueChange = { cor = it },
            label = { Text("Correo Electrónico") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = ciu,
            onValueChange = { ciu = it },
            label = { Text("Ciudad / Región") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(20.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            OutlinedButton(onClick = { showEditProfileDialog = false }) {
              Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
              onClick = {
                onUpdateProfile(
                  userProfile.copy(
                    nombre = nom,
                    rol = rol,
                    empresa = emp,
                    telefono = tel,
                    correo = cor,
                    ciudad = ciu
                  )
                )
                showEditProfileDialog = false
              },
              colors = ButtonDefaults.buttonColors(containerColor = ServixBluePrimary)
            ) {
              Text("Guardar")
            }
          }
        }
      }
    }
  }

  // Diálogo de Metas Semanales
  if (showGoalsDialog) {
    var targetSrvText by remember { mutableStateOf(appConfig.objetivoServiciosSemanal.toString()) }
    var targetRevText by remember { mutableStateOf(appConfig.objetivoIngresosSemanal.toInt().toString()) }

    Dialog(onDismissRequest = { showGoalsDialog = false }) {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        val context = LocalContext.current
  val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
    if (uri != null) {
        try {
            val base64 = ImageUtils.processImageUri(context, uri)
            if (base64 != null) {
                onUpdateProfile(userProfile.copy(fotoPerfilBase64 = base64))
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "Error al cargar la imagen", Toast.LENGTH_LONG).show()
        }
    }
  }

  Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          Text(
            text = "Configurar Metas Semanales",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Define tus objetivos de productividad y facturación semanal.",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )

          Spacer(modifier = Modifier.height(16.dp))

          OutlinedTextField(
            value = targetSrvText,
            onValueChange = { targetSrvText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Meta de servicios por semana") },
            placeholder = { Text("Ej. 6") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = targetRevText,
            onValueChange = { targetRevText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Meta de ingresos por semana (${appConfig.monedaSimbolo})") },
            placeholder = { Text("Ej. 1200000") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(20.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            OutlinedButton(onClick = { showGoalsDialog = false }) {
              Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
              onClick = {
                val srv = targetSrvText.toIntOrNull() ?: appConfig.objetivoServiciosSemanal
                val rev = targetRevText.toDoubleOrNull() ?: appConfig.objetivoIngresosSemanal
                onUpdateConfig(
                  appConfig.copy(
                    objetivoServiciosSemanal = srv,
                    objetivoIngresosSemanal = rev
                  )
                )
                showGoalsDialog = false
              },
              colors = ButtonDefaults.buttonColors(containerColor = ServixBluePrimary)
            ) {
              Text("Guardar Metas")
            }
          }
        }
      }
    }
  }

  if (showAboutDialog) {
    AboutDialog(onDismiss = { showAboutDialog = false })
  }
}
