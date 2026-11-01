# Capturas de la app ServiX

Fecha de generación: 01/11/2026.

**Qué son:** renders generados con Robolectric + Roborazzi (Pixel 8, API 34) a partir del código real de la app (`ServiXApp`) y los repositorios simulados (mock) con datos de demostración ficticios. **No son capturas de un dispositivo físico ni de un emulador.** Pueden presentar pequeñas diferencias de maquetación respecto a un dispositivo real (p. ej. algunos textos muy ajustados como "PENDIENTES" en la bandeja de atención).

## Contenido
| Archivo | Pantalla |
|---|---|
| 01_login.png | Acceso (inicio de sesión) |
| 02_registro.png | Registro |
| 03_home.png | Inicio (administrador) |
| 04_clientes.png | Lista de clientes |
| 05_cliente_detalle.png | Detalle de cliente |
| 06_servicios.png | Lista de servicios |
| 07_servicio_detalle.png | Detalle de servicio |
| 08_historial.png | Historial / Centro financiero |
| 09_perfil.png | Perfil y ajustes |
| 10_chat_ia_admin.png | Chat IA (administrador) |
| 11_chat_ia_cliente.png | Chat IA (cliente) |
| 12_notificaciones.png | Centro de avisos |
| 13_nuevo_cliente.png | Formulario nuevo cliente |
| 14_nuevo_servicio.png | Formulario nueva orden de servicio |
| 15_portal_cliente.png | Portal del cliente |
| 16_escalamientos_admin.png | Bandeja de escalamientos (administrador) |

## Cómo regenerarlas
Desde la raíz del proyecto, en PowerShell:

```
.\gradlew.bat testDebugUnitTest --tests "com.example.ScreenshotsDocTest" "-Proborazzi.test.record=true" --console=plain --no-daemon --max-workers=1 "-Dorg.gradle.jvmargs=-Xmx1536m"
```

Las PNG quedan en `app/src/test/screenshots/doc/`. La prueba está en `app/src/test/java/com/example/ScreenshotsDocTest.kt`. Sin `-Proborazzi.test.record=true` la prueba corre pero no escribe imágenes.
