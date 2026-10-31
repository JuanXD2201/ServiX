# Guía de contribución

## Ramas

- `main`: versión estable. Solo recibe cambios mediante Pull Request.
- `develop`: integración de funcionalidades.
- `feature/<descripcion-corta>`: una rama por funcionalidad o corrección, creada desde `develop`.

## Flujo

1. Crea o toma un elemento de trabajo en Azure Boards y asígnalo a un Sprint.
2. Crea la rama `feature/...` desde `develop`.
3. Implementa el cambio con commits pequeños y descriptivos.
4. Ejecuta `./gradlew testDebugUnitTest assembleDebug` antes de abrir el Pull Request.
5. Abre el Pull Request hacia `develop` (o hacia `main` en cada entrega) y vincula el elemento de trabajo.
6. Espera que el pipeline pase y que otra persona revise el cambio.
7. Tras la integración, actualiza el estado del elemento en el tablero.

## Reglas

- No subas secretos, claves de firma ni archivos `.env`.
- Mantén la arquitectura por capas; la lógica de negocio va en casos de uso, no en composables.
- Cada cambio relevante se registra en `REPORTE_CAMBIOS_SERVIX.txt` sin alterar las entradas previas.
