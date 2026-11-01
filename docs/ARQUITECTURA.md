# Arquitectura de ServiX

Documento técnico verificado contra el código fuente (`app/src/main/java/com/example`).

## 1. Visión general

ServiX es una aplicación Android de una sola actividad (`app/MainActivity.kt`) construida con Jetpack Compose y Material 3. La lógica se organiza en capas con dependencias dirigidas hacia el dominio.

```text
┌──────────────────────────────────────────────────────────┐
│ Presentación   presentation/ , ui/                       │
│   Screens · Components · Dialogs · MainViewModel         │
├──────────────────────────────────────────────────────────┤
│ Dominio        domain/                                   │
│   UseCases · Interfaces de Repository · Modelos          │
├──────────────────────────────────────────────────────────┤
│ Datos          data/                                     │
│   firebase/ (Auth, Firestore) · ai/ · network/ · mock/   │
├──────────────────────────────────────────────────────────┤
│ Núcleo         core/  (DI, tema, navegación, utilidades) │
└──────────────────────────────────────────────────────────┘
```

Flujo típico: `Screen → MainViewModel → UseCase → Repository (interfaz) → implementación Firebase/OkHttp`.

## 2. Módulos y responsabilidades

| Paquete | Contenido | Responsabilidad |
|---|---|---|
| `presentation/screens` | Home, Clientes, ClienteDetalle, Servicios, ServicioDetalle, Historial, Perfil, Chat, ClientePortal | Pantallas Compose |
| `presentation/components`, `dialogs` | Tarjetas, chips, stepper de progreso, formularios, búsqueda global, centro de notificaciones | UI reutilizable |
| `presentation/viewmodel` | `MainViewModel`, `MainViewModelFactory` | Estado de UI y orquestación de casos de uso |
| `ui/screens` | `AuthScreen` | Inicio de sesión, registro y recuperación de contraseña |
| `domain/model` | Cliente, Servicio, Notificacion, Actividad, SolicitudAtencion, UserProfile, UserRole… | Entidades de negocio |
| `domain/usecase` | Alta, edición, borrado y restauración de clientes y servicios; notificaciones; autenticación; atención; chat IA | Reglas de negocio |
| `domain/repository` | Interfaces de acceso a datos | Contratos entre dominio y datos |
| `data/firebase` | `Firebase*Repository` y DTO de Firestore | Persistencia en Cloud Firestore y Firebase Auth |
| `data/ai` | `GeminiAiChatRepository` | Chat con Gemini vía Firebase AI Logic |
| `data/network` | `OkHttpMakeWebhookRepository` | Notificación de eventos de servicio a Make |
| `data/mock` | Repositorios en memoria | Pruebas unitarias y modo sin Firebase |
| `core/di` | `AppContainer` | Inyección manual de dependencias |

## 3. Inyección de dependencias

No se usa Hilt ni Koin. `AppContainer` expone los repositorios como singletons perezosos y ensambla los casos de uso. `MainViewModelFactory` construye el `MainViewModel` con esas dependencias, lo que permite sustituirlas por dobles en las pruebas.

## 4. Roles

`UserRole` define `CLIENTE` y `ADMINISTRADOR`. El rol se guarda en `usuarios/{uid}.role` al registrar la cuenta y condiciona la interfaz (portal del cliente frente al panel de administración) y las reglas de Firestore.

## 5. Integraciones externas

- **Firebase Authentication**: correo y contraseña.
- **Cloud Firestore**: datos por usuario y solicitudes de atención (ver `FIREBASE.md`).
- **Firebase AI Logic (Gemini)**: asistente de chat (ver `CHAT_IA.md`).
- **Make (webhook)**: `AddServicioUseCase`, `UpdateServicioUseCase` y `UpdateServicioEstadoUseCase` notifican los cambios de servicio.

## 6. Limitaciones conocidas de diseño

- `MainViewModel` concentra gran parte del estado de la aplicación (≈25 KB); es candidato a dividirse por funcionalidad.
- `AppContainer` degrada a repositorios simulados si falla la inicialización de Firebase; el comportamiento está registrado en logs, pero la interfaz no lo indica al usuario.
- La URL del webhook de Make está incluida en el código (`core/utils/MakeWebhookConfig.kt`). Cualquiera con el APK puede extraerla. Para producción debe moverse a un backend.
