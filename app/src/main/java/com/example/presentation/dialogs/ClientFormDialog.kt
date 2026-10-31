package com.example.presentation.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.model.Cliente
import com.example.core.utils.Validators

@Composable
fun ClientFormDialog(
  clienteToEdit: Cliente? = null,
  onDismiss: () -> Unit,
  onSave: (nombre: String, documento: String, telefono: String, correo: String, direccion: String, notas: String) -> Unit
) {
  var nombre by remember { mutableStateOf(clienteToEdit?.nombre ?: "") }
  var documento by remember { mutableStateOf(clienteToEdit?.documento ?: "") }
  var telefono by remember { mutableStateOf(clienteToEdit?.telefono ?: "") }
  var correo by remember { mutableStateOf(clienteToEdit?.correo ?: "") }
  var direccion by remember { mutableStateOf(clienteToEdit?.direccion ?: "") }
  var notas by remember { mutableStateOf(clienteToEdit?.notas ?: "") }

  var nombreError by remember { mutableStateOf<String?>(null) }
  var telefonoError by remember { mutableStateOf<String?>(null) }
  var correoError by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("client_form_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
      )
    ) {
      Column(
        modifier = Modifier
        .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Text(
          text = if (clienteToEdit == null) "Nuevo Cliente" else "Editar Cliente",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        )

        Spacer(modifier = Modifier
        .height(14.dp))

        OutlinedTextField(
          value = nombre,
          onValueChange = {
            nombre = it
            if (Validators.isValidName(it)) nombreError = null
          },
          label = { Text("Nombre Completo *") },
          isError = nombreError != null,
          supportingText = {
            nombreError?.let { Text(it) }
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_client_name")
        )

        Spacer(modifier = Modifier
        .height(8.dp))

        OutlinedTextField(
          value = documento,
          onValueChange = { documento = it },
          label = { Text("Documento / NIT") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_client_document")
        )

        Spacer(modifier = Modifier
        .height(8.dp))

        OutlinedTextField(
          value = telefono,
          onValueChange = {
            telefono = it
            if (Validators.isValidPhone(it)) telefonoError = null
          },
          label = { Text("Teléfono de Contacto") },
          isError = telefonoError != null,
          supportingText = {
            telefonoError?.let { Text(it) }
          },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_client_phone")
        )

        Spacer(modifier = Modifier
        .height(8.dp))

        OutlinedTextField(
          value = correo,
          onValueChange = {
            correo = it
            if (Validators.isValidEmail(it)) correoError = null
          },
          label = { Text("Correo Electrónico") },
          isError = correoError != null,
          supportingText = {
            correoError?.let { Text(it) }
          },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_client_email")
        )

        Spacer(modifier = Modifier
        .height(8.dp))

        OutlinedTextField(
          value = direccion,
          onValueChange = { direccion = it },
          label = { Text("Dirección / Ubicación") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_client_address")
        )

        Spacer(modifier = Modifier
        .height(8.dp))

        OutlinedTextField(
          value = notas,
          onValueChange = { notas = it },
          label = { Text("Notas u Observaciones") },
          minLines = 2,
          maxLines = 4,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_client_notes")
        )

        Spacer(modifier = Modifier
        .height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("cancel_client_button")
          ) {
            Text("Cancelar")
          }

          Spacer(modifier = Modifier
        .width(10.dp))

          Button(
            onClick = {
              var hasError = false
              if (!Validators.isValidName(nombre)) {
                nombreError = "El nombre debe tener al menos 3 caracteres"
                hasError = true
              }
              if (!Validators.isValidPhone(telefono)) {
                telefonoError = "Ingresa un teléfono válido (al menos 7 dígitos)"
                hasError = true
              }
              if (!Validators.isValidEmail(correo)) {
                correoError = "Formato de correo no válido"
                hasError = true
              }

              if (!hasError) {
                onSave(nombre.trim(), documento.trim(), telefono.trim(), correo.trim(), direccion.trim(), notas.trim())
              }
            },
            modifier = Modifier.testTag("save_client_button")
          ) {
            Text("Guardar")
          }
        }
      }
    }
  }
}
