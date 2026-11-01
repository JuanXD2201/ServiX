# Asistente de chat con IA

## Flujo

1. `ChatScreen` envía el texto al `MainViewModel`.
2. `SendAiChatMessageUseCase` clasifica la intención por palabras clave (`IntencionUsuario`).
3. Si la intención es queja, cancelación, problema, error de la app o solicitud de atención humana, se **escala**: se crea una `SolicitudAtencion` en Firestore (`solicitudes_atencion`) con la prioridad correspondiente y se informa al usuario.
4. En otro caso se construye un contexto de sistema con los datos del usuario (servicios, clientes y perfil) y se consulta a Gemini mediante `GeminiAiChatRepository`.
5. Si la consulta a la IA falla, el caso de uso devuelve un mensaje de error genérico que sugiere reintentar o pedir un asesor. **No se simulan respuestas de IA.**

## Proveedor

- Firebase AI Logic con el backend *Gemini Developer API*.
- Modelo: `gemini-2.5-flash`, `temperature` 0.7, `maxOutputTokens` 800.
- Historial enviado: hasta los últimos 6 mensajes, comenzando por un turno del usuario.
- La credencial no está en el APK: la gestiona el proyecto Firebase.

## Requisitos de configuración

En la consola de Firebase del proyecto `servix-453fb`:

1. Abrir **AI Logic** y completar *Get started* eligiendo **Gemini Developer API**.
2. Verificar que la API `firebasevertexai.googleapis.com` / Gemini Developer API esté habilitada.
3. (Recomendado) activar App Check para limitar el uso al APK legítimo.

Sin este paso, el chat responde con el mensaje de error del punto 5.

**Estado (31/10/2026):** Firebase AI Logic está activado en `servix-453fb` con *Gemini Developer API* para la app `com.example.servix`. AI Monitoring quedó desactivado y App Check figura como "No registrada", por lo que no bloquea las solicitudes. Si se registra App Check, la app deberá inicializar su proveedor (por ejemplo, Play Integrity).

## Cambios respecto a la versión anterior

| Antes | Ahora |
|---|---|
| Clave `GEMINI_API_KEY` en `BuildConfig` (extraíble del APK) y enviada en la URL | Sin clave en el cliente |
| Si fallaba la API, respuestas predefinidas presentadas como IA | Error explícito al usuario |
| Modelo `gemini-3.5-flash` no verificado | `gemini-2.5-flash` |

## Estado de verificación

- Compilación: pendiente de confirmar en `PRUEBAS.md`.
- Comunicación real con Gemini: **pendiente**. Requiere dispositivo o emulador y el paso de configuración anterior.
