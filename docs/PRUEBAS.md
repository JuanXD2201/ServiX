# Informe de pruebas

Entorno: Windows 10, 4 GB de RAM, sin virtualización (no se dispone de emulador) y sin dispositivo físico conectado (`adb devices` vacío). Por ello, las pruebas se limitan a las ejecutables mediante Gradle.

## Suite existente

Pruebas unitarias en `app/src/test/java/com/example`:

| Archivo | Objetivo |
|---|---|
| `ServixUnitTests.kt` | Lógica de casos de uso de clientes y servicios |
| `ServixAuthUnitTests.kt` | Autenticación |
| `ServixAtencionUnitTests.kt` | Solicitudes de atención |
| `ServixRepositoryTest.kt` | Repositorios simulados |
| `ServixMakeIntegrationTest.kt` | Envío de eventos al webhook (con doble de prueba) |
| `ServixRobolectricTest.kt`, `ExampleRobolectricTest.kt`, `GreetingScreenshotTest.kt` | Compose con Robolectric/Roborazzi |

Prueba instrumentada: `app/src/androidTest/.../ExampleInstrumentedTest.kt` (requiere dispositivo; **no ejecutada**).

## Resultados

| Verificación | Comando | Resultado | Evidencia |
|---|---|---|---|
| Compilación de fuentes y pruebas (antes de los cambios) | `gradlew testDebugUnitTest` | Compiló (`compileDebugUnitTestKotlin` OK); la ejecución se interrumpió por falta de memoria del equipo | Registro local |
| Compilación debug tras los cambios | `gradlew :app:assembleDebug` | **Exitosa** (BUILD SUCCESSFUL, 20 min 21 s, tras los cambios de roles y solicitudes) | Registro local de Gradle |
| Pruebas unitarias | `gradlew testDebugUnitTest` | **Exitosas: 29 pruebas, 0 fallos, 0 errores, 0 omitidas** | Informes JUnit XML en `app/build/test-results/testDebugUnitTest` |
| Android Lint | `gradlew lintDebug` | No ejecutado | — |
| Chat IA contra Gemini | Manual en dispositivo | No ejecutado | — |
| Navegación, formularios y estados en UI | Manual en dispositivo | No ejecutado | — |

Este documento no declara como superada ninguna prueba que no haya sido ejecutada. Se actualizará con los resultados reales.

## Alternativas sin virtualización

1. Dispositivo Android físico con depuración USB y `adb install` del APK debug.
2. Ejecución de Azure Pipelines (agentes en la nube) para `testDebugUnitTest` y `assembleDebug`.
3. Pruebas de UI con Robolectric (JVM, sin emulador), ya configuradas en el proyecto.
4. Servicios de dispositivos en la nube (por ejemplo Firebase Test Lab), sujetos a cuota.
