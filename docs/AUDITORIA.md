# Informe de auditoría y correcciones

Alcance: únicamente el proyecto ServiX. Metodología: inspección del repositorio, lectura de configuración y de los archivos críticos, búsqueda de secretos y revisión de reglas de seguridad.

## Hallazgos y estado

| ID | Prioridad | Hallazgo | Archivo | Estado |
|---|---|---|---|---|
| H-01 | P0 | Reglas de Firestore abiertas: `\|\| true` en todas las reglas y comodín `/{document=**}` | `firestore.rules` | **Corregido y desplegado** |
| H-02 | P0 | `GEMINI_API_KEY` empaquetada en el APK (`BuildConfig`) y enviada en la URL | `app/build.gradle.kts`, `GeminiAiChatRepository.kt` | **Corregido**: migrado a Firebase AI Logic |
| H-03 | P0 | Respuestas predefinidas presentadas como respuestas de IA cuando fallaba la API | `GeminiAiChatRepository.kt` | **Corregido**: se devuelve error explícito |
| H-04 | P0 | Credenciales `[contrasena-eliminada]` / `[contrasena-eliminada]` en `AuthScreen.kt`, divididas en dos cadenas | `ui/screens/AuthScreen.kt` | **Mitigado**: atajos solo en builds `debug` |
| H-05 | P1 | URL del webhook de Make en el código fuente | `core/utils/MakeWebhookConfig.kt` | **Aceptado** para la entrega académica; ver riesgo en `ARQUITECTURA.md` |
| H-06 | P1 | Faltaban `gradlew`, `gradlew.bat` y `gradle-wrapper.jar` | raíz | **Corregido** |
| H-07 | P1 | `debug.keystore` inexistente y excluido de Git; la compilación debug fallaba en un equipo limpio | `app/build.gradle.kts` | **Corregido**: se usa la firma debug estándar de Android |
| H-08 | P2 | 29 scripts `fix_*.py`, `split_*.py`, etc. generados por IA en la raíz | raíz | **Archivados** fuera del proyecto (`Servix_archivo_scripts_IA`) |
| H-09 | P2 | README de AI Studio con instrucciones incorrectas (clave en `.env`, edición manual de firma) | `README.md` | **Reescrito** |
| H-10 | P2 | Carpeta sin Git, sin pipeline CI | — | **Corregido**: repositorio, `azure-pipelines.yml` |
| H-11 | P2 | `AppContainer` cae silenciosamente a repositorios simulados si Firebase falla | `core/di/AppContainer.kt` | Pendiente |
| H-12 | P2 | Navegación duplicada (`core/navigation` y `ui/navigation`) | — | Pendiente |
| H-13 | P2 | `RetellAIRepository` y `N8nWebhookRepository` son interfaces sin implementación | `domain/repository` | Pendiente de decisión |
| H-14 | P2 | Cualquier usuario autenticado puede leer `solicitudes_atencion` | `FirebaseAtencionRepository.kt`, reglas | Pendiente |
| H-15 | P2 | El registro permite elegir el rol `ADMINISTRADOR` | `FirebaseAuthRepository.kt` | Pendiente (riesgo documentado) |
| H-16 | P3 | `.env.example` conserva la variable obsoleta `GEMINI_API_KEY` | `.env.example` | Pendiente |

## Acciones que requieren al propietario

- El archivo local `.env` contiene una clave de Gemini que ya no se usa. Se recomienda **revocarla** en Google AI Studio.
- Restringir la clave de API de `google-services.json` por paquete y SHA-1 en Google Cloud Console.
- Habilitar Firebase AI Logic en la consola (ver `CHAT_IA.md`).

## Despliegue de reglas

Las reglas nuevas se desplegaron en `servix-453fb` con Firebase CLI (compilación sin errores). El propietario confirmó que la base de datos no tenía datos, por lo que no hubo riesgo de bloqueo de información existente.
