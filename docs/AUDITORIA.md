# Informe de auditoría y correcciones

Alcance: únicamente el proyecto ServiX. Metodología: inspección del repositorio, lectura de configuración y de los archivos críticos, búsqueda de secretos y revisión de reglas de seguridad.

## Hallazgos y estado

| ID | Prioridad | Hallazgo | Archivo | Estado |
|---|---|---|---|---|
| H-01 | P0 | Reglas de Firestore abiertas: `\|\| true` en todas las reglas y comodín `/{document=**}` | `firestore.rules` | **Corregido y desplegado** |
| H-02 | P0 | `GEMINI_API_KEY` empaquetada en el APK (`BuildConfig`) y enviada en la URL | `app/build.gradle.kts`, `GeminiAiChatRepository.kt` | **Corregido**: migrado a Firebase AI Logic |
| H-03 | P0 | Respuestas predefinidas presentadas como respuestas de IA cuando fallaba la API | `GeminiAiChatRepository.kt` | **Corregido**: se devuelve error explícito |
| H-04 | P0 | Contraseñas de cuentas demo y administrador escritas en `AuthScreen.kt`, divididas en dos cadenas | `ui/screens/AuthScreen.kt` | **Corregido**: se eliminaron las contraseñas del código; los atajos solo rellenan el correo y solo en builds `debug` |
| H-05 | P1 | URL del webhook de Make en el código fuente | `core/utils/MakeWebhookConfig.kt` | **Corregido**: la URL se lee de `local.properties` (`make.webhook.url`) o de `MAKE_WEBHOOK_URL`; sin ella la integración queda desactivada. Persiste el riesgo de que quien tenga el APK pueda extraerla; para producción debe pasar por un backend |
| H-06 | P1 | Faltaban `gradlew`, `gradlew.bat` y `gradle-wrapper.jar` | raíz | **Corregido** |
| H-07 | P1 | `debug.keystore` inexistente y excluido de Git; la compilación debug fallaba en un equipo limpio | `app/build.gradle.kts` | **Corregido**: se usa la firma debug estándar de Android |
| H-08 | P2 | 29 scripts `fix_*.py`, `split_*.py`, etc. generados por IA en la raíz | raíz | **Archivados** fuera del proyecto (`Servix_archivo_scripts_IA`) |
| H-09 | P2 | README de AI Studio con instrucciones incorrectas (clave en `.env`, edición manual de firma) | `README.md` | **Reescrito** |
| H-10 | P2 | Carpeta sin Git, sin pipeline CI | — | **Corregido**: repositorio, `azure-pipelines.yml` |
| H-11 | P2 | `AppContainer` cae silenciosamente a repositorios simulados si Firebase falla | `core/di/AppContainer.kt` | Pendiente |
| H-12 | P2 | Navegación duplicada (`core/navigation` y `ui/navigation`) | `ui/navigation/ServixNavigation.kt` | **Corregido**: archivo sin referencias eliminado (compilación verificada) |
| H-13 | P2 | `RetellAIRepository` y `N8nWebhookRepository` son interfaces sin implementación | `domain/repository` | Pendiente de decisión |
| H-14 | P2 | Cualquier usuario autenticado podía leer `solicitudes_atencion` | `FirebaseAtencionRepository.kt`, reglas | **Corregido**: el cliente solo consulta y lee sus solicitudes; el administrador, todas. Reglas redesplegadas |
| H-15 | P2 | El registro permite elegir el rol `ADMINISTRADOR` | `FirebaseAuthRepository.kt` | Pendiente (riesgo documentado) |
| H-17 | P1 | Si fallaba la lectura del rol, el usuario quedaba como `ADMINISTRADOR`; el DTO tenía `ADMINISTRADOR` por defecto | `FirebaseAuthRepository.kt`, `FirestoreDtos.kt` | **Corregido**: mínimo privilegio (`CLIENTE`) |
| H-18 | P1 | En el inicio de sesión mandaba el rol elegido en pantalla, no el almacenado; un cliente veía la interfaz de administrador | `FirebaseAuthRepository.kt` | **Corregido**: el rol sale de la cuenta; si no coincide se rechaza el acceso |
| H-19 | P1 | El listener de solicitudes se abría al construir el repositorio, antes del login; con reglas autenticadas habría fallado de forma permanente | `FirebaseAtencionRepository.kt` | **Corregido**: usa `AuthStateListener` |
| H-20 | P2 | `SignUpUseCase`, `AuthRepository` y `AuthUser` tenían `ADMINISTRADOR` como rol por defecto | `AuthUseCases.kt`, `AuthRepository.kt`, `AuthUser.kt`, `MainViewModel.kt` | **Corregido**: el rol por defecto es `CLIENTE` (mínimo privilegio) |
| H-21 | P2 | El proyecto trata datos personales de clientes (nombre, correo, teléfono, documento) sin autorización ni política de tratamiento, y el repositorio no tiene archivo `LICENSE` | app, repositorio | Pendiente (ver marco legal del SRS) |
| H-16 | P3 | `.env.example` conserva la variable obsoleta `GEMINI_API_KEY` | `.env.example` | **Corregido**: `.env.example` ya no contiene `GEMINI_API_KEY` |

## Acciones que requieren al propietario

- El archivo local `.env` contiene una clave de Gemini que ya no se usa. Se recomienda **revocarla** en Google AI Studio.
- Restringir la clave de API de `google-services.json` por paquete y SHA-1 en Google Cloud Console.
- Habilitar Firebase AI Logic en la consola (ver `CHAT_IA.md`).

## Despliegue de reglas

Las reglas nuevas se desplegaron en `servix-453fb` con Firebase CLI (compilación sin errores). El propietario confirmó que la base de datos no tenía datos, por lo que no hubo riesgo de bloqueo de información existente.
