# ServiX

Aplicación Android para gestionar servicios técnicos del hogar: clientes, órdenes de servicio, historial, notificaciones y un asistente de chat con IA que puede escalar a un administrador.

## Tecnologías

- Kotlin 2.2, Jetpack Compose y Material 3
- Arquitectura por capas: UI → ViewModel → UseCase → Repository → Data Source
- Firebase Authentication y Cloud Firestore (proyecto `servix-453fb`)
- Firebase AI Logic (Gemini) para el chat: no hay claves de IA en el APK
- Webhook de Make para eventos de servicio
- Android Gradle Plugin 9.1, Gradle 9.3.1, `minSdk` 26, `targetSdk` 36

## Requisitos

- JDK 17 o superior
- Android SDK con la plataforma `android-36.1` (Android Studio la instala al sincronizar)
- Cuenta con acceso al proyecto Firebase `servix-453fb`

## Ejecución local

1. Clona el repositorio y ábrelo en Android Studio (o usa la línea de comandos).
2. Verifica que `app/google-services.json` exista (incluido en el repositorio).
3. Compila: `./gradlew assembleDebug` (Windows: `gradlew.bat assembleDebug`).
4. El APK queda en `app/build/outputs/apk/debug/`.
5. Instala en un dispositivo o emulador: `adb install -r app/build/outputs/apk/debug/app-debug.apk`.

Pruebas unitarias: `./gradlew testDebugUnitTest`.

Para el chat con IA, en la consola de Firebase debe estar habilitado **Firebase AI Logic** con el proveedor Gemini Developer API.

Integración opcional con Make: define `make.webhook.url=<URL del webhook>` en `local.properties` (o la variable de entorno `MAKE_WEBHOOK_URL`) antes de compilar. Sin esa URL, los eventos de servicio no se envían y la app funciona con normalidad.

## Seguridad

- Las reglas de Firestore están en `firestore.rules` y se despliegan con `firebase deploy --only firestore:rules`.
- Cada usuario solo accede a su propio árbol `usuarios/{uid}/...`; los administradores se identifican por el campo `role`.
- No se incluyen contraseñas, claves de firma ni claves de API privadas en el código.

## Integración continua

`azure-pipelines.yml` compila, ejecuta pruebas unitarias y publica el APK debug como artefacto en cada cambio sobre `main`.

## Equipo

| Integrante | Rol Scrum |
|---|---|
| Nicolas Quiroz Gomez | Product Owner |
| Juan Sebastian Garzon Suarez | Scrum Master |
| Juan Esteban Olaya Muñoz | Developer |

## Documentación

| Documento | Contenido |
|---|---|
| [docs/ARQUITECTURA.md](docs/ARQUITECTURA.md) | Capas, módulos, DI e integraciones |
| [docs/FIREBASE.md](docs/FIREBASE.md) | Modelo de datos y reglas de seguridad |
| [docs/CHAT_IA.md](docs/CHAT_IA.md) | Asistente con Gemini y escalamiento a administrador |
| [docs/PRUEBAS.md](docs/PRUEBAS.md) | Pruebas ejecutadas y limitaciones |
| [docs/AUDITORIA.md](docs/AUDITORIA.md) | Hallazgos y correcciones |
| [docs/AZURE_DEVOPS.md](docs/AZURE_DEVOPS.md) | Scrum, repositorios y pipeline |

## Contribuir

Consulta [CONTRIBUTING.md](CONTRIBUTING.md).
