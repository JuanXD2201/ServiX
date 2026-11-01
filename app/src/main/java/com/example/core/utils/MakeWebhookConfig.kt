package com.example.core.utils

import com.example.BuildConfig

/**
 * URL del webhook de Make. No se versiona: se lee de `make.webhook.url` en
 * `local.properties` o de la variable de entorno `MAKE_WEBHOOK_URL` al compilar.
 * Si está vacía, el envío de eventos a Make queda desactivado.
 */
object MakeWebhookConfig {
    val WEBHOOK_URL: String = BuildConfig.MAKE_WEBHOOK_URL
    val isConfigured: Boolean get() = WEBHOOK_URL.isNotBlank()
}
