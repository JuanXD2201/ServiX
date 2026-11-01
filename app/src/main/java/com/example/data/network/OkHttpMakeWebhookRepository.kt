package com.example.data.network

import android.util.Log
import com.example.core.utils.MakeWebhookConfig
import com.example.domain.model.Cliente
import com.example.domain.model.Servicio
import com.example.domain.model.UserProfile
import com.example.domain.repository.MakeWebhookRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class OkHttpMakeWebhookRepository(
    private val webhookUrl: String = MakeWebhookConfig.WEBHOOK_URL
) : MakeWebhookRepository {

    private companion object {
        const val SIN_CORREO = "sin-correo@servix.invalid"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val scope = CoroutineScope(Dispatchers.IO)

    private fun buildPayload(
        evento: String,
        servicio: Servicio,
        cliente: Cliente?,
        tecnico: UserProfile?,
        usuarioId: String?
    ): JSONObject {
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
        val userAuthEmail = try { FirebaseAuth.getInstance().currentUser?.email } catch (e: Throwable) { null }
        val effectiveEmail = cliente?.correo?.trim().takeIf { !it.isNullOrBlank() }
            ?: userAuthEmail?.takeIf { it.isNotBlank() }
            ?: SIN_CORREO

        return JSONObject().apply {
            put("evento", evento)
            put("timestamp", System.currentTimeMillis())
            put("fechaHoraEnvio", now)
            put("servicioId", servicio.servicioId)
            put("nombre", servicio.nombre)
            put("descripcion", servicio.descripcion)
            put("categoria", servicio.categoria.ifBlank { "General" })
            put("precio", servicio.precio)
            put("fecha", servicio.fecha)
            put("fechaEntrega", servicio.fechaEntrega)
            put("estado", servicio.estado.name)
            put("prioridad", servicio.prioridad.name)
            put("observaciones", servicio.observaciones)
            put("clienteId", servicio.clienteId)
            put("clienteNombre", cliente?.nombre ?: "Cliente General")
            put("clienteCorreo", effectiveEmail)
            put("clienteTelefono", cliente?.telefono ?: "")
            put("clienteDocumento", cliente?.documento ?: "")
            put("usuarioId", usuarioId ?: "usuario-servix")
            put("tecnicoNombre", tecnico?.nombre ?: "Carlos Alberto Vega")
            put("tecnicoEmpresa", tecnico?.empresa ?: "Vega Servicios Técnicos")
            put("tecnicoCorreo", userAuthEmail ?: SIN_CORREO)
            put("correoNotificacion", effectiveEmail)
        }
    }

    override fun sendServicioEvent(
        evento: String,
        servicio: Servicio,
        cliente: Cliente?,
        tecnico: UserProfile?,
        usuarioId: String?
    ) {
        if (webhookUrl.isBlank()) {
            Log.w("MakeWebhook", "Webhook de Make no configurado; evento $evento omitido")
            return
        }
        scope.launch {
            try {
                val json = buildPayload(evento, servicio, cliente, tecnico, usuarioId)
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = json.toString().toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(webhookUrl)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                Log.d("MakeWebhook", "Evento $evento enviado a Make. Código HTTP: ${response.code}")
                response.close()
            } catch (e: Exception) {
                Log.e("MakeWebhook", "Error al enviar webhook a Make: ${e.message}", e)
            }
        }
    }

    override suspend fun sendServicioEventSync(
        evento: String,
        servicio: Servicio,
        cliente: Cliente?,
        tecnico: UserProfile?,
        usuarioId: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        if (webhookUrl.isBlank()) {
            return@withContext Result.failure(IllegalStateException("Webhook de Make no configurado"))
        }
        try {
            val json = buildPayload(evento, servicio, cliente, tecnico, usuarioId)
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = json.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val code = response.code
            val responseBody = response.body?.string().orEmpty()
            response.close()

            if (response.isSuccessful) {
                Log.d("MakeWebhook", "Webhook sincrónico enviado a Make. Código HTTP: $code")
                Result.success("HTTP $code: $responseBody")
            } else {
                Log.w("MakeWebhook", "Make respondió con error HTTP $code: $responseBody")
                Result.failure(Exception("Make respondió con código HTTP $code: $responseBody"))
            }
        } catch (e: Exception) {
            Log.e("MakeWebhook", "Fallo de conexión con Make: ${e.message}", e)
            Result.failure(e)
        }
    }
}

