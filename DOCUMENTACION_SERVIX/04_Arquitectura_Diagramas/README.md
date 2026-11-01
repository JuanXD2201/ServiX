# Diagramas técnicos de ServiX

Versión v1.0 · 2026-11-01. Cada diagrama existe en cuatro formatos generados a partir del mismo archivo `.drawio`.

| N.º | Diagrama | Contenido |
|---|---|---|
| 01 | Contexto del sistema | Actores, ServiX, Firebase, Make (opcional), Retell/n8n (reservado), GitHub y Azure DevOps |
| 02 | Arquitectura por capas | Presentación, dominio, datos y núcleo; dirección de las dependencias |
| 03 | Componentes | Pantallas, ViewModel, casos de uso, interfaces de repositorio, implementaciones y servicios externos |
| 04 | Casos de uso | Cliente, Administrador y actor genérico Usuario; include/extend del chat |
| 05 | Clases del dominio | Modelos con atributos, enumeraciones y relaciones por identificador |
| 06 | Modelo de datos Firestore | Colecciones, documentos, referencias lógicas y reglas de acceso |
| 07 | Secuencia: inicio de sesión | Rol almacenado frente al rol elegido; rol mínimo CLIENTE |
| 08 | Secuencia: alta de servicio | Persistencia, actividad, notificación urgente y webhook opcional a Make |
| 09 | Secuencia: chat con IA | Clasificación de intención, escalamiento o Gemini (Firebase AI Logic); error explícito |
| 10 | Despliegue | Dispositivo Android, Firebase, Make, GitHub y Azure DevOps (ubuntu-latest, JDK 21) |
| 11 | Flujo CI/CD | Pull Request, política de main (1 revisor), pipeline ServiX-CI y publicación de APK |

## Carpetas

- `Drawio/`: fuente editable (`NN_Nombre.drawio`).
- `PNG/`: imagen a 2x (3400 px o más de ancho).
- `SVG/`: vector.
- `PDF/`: una página apaisada por diagrama.
- `herramientas/`: generador de los `.drawio` y renderizador propio (Python).

## Cómo editarlos en app.diagrams.net

1. Abrir https://app.diagrams.net y elegir *Open Existing Diagram* (o *Archivo > Abrir desde > Dispositivo*).
2. Seleccionar el archivo de `Drawio/`.
3. Editar. Los contenedores (capas, Firebase, Azure) agrupan sus hijos; las flechas de las secuencias son libres y deben moverse a mano.
4. Exportar: *Archivo > Exportar como > PNG* (escala 200 %), SVG o PDF.
5. Guardar con *Archivo > Guardar como…* y reemplazar el archivo en `Drawio/`.

Estilos: azul oscuro `#091E42`, azul `#0066F5`, azul claro `#00A3FF`, fondo `#E0F2FE`, ámbar `#F59E0B`. Línea punteada ámbar: integración opcional. Línea punteada gris: elemento reservado, sin implementación.

## Regenerar PNG, SVG y PDF

Requiere Python 3 con `matplotlib` y `Pillow` y la fuente Arial. Desde `herramientas/`:

```
python build.py          # los 11 diagramas
python build.py 05 09    # solo algunos
```

`build.py` reescribe los `.drawio` desde `dNN.py` y, a continuación, renderiza PNG, SVG y PDF leyendo esos `.drawio`. Para conservar ediciones hechas en app.diagrams.net, no ejecutar el paso de generación: llamar solo a `render.render(ruta_drawio, ruta_salida_sin_extension)`.

## Limitaciones del renderizador

Soporta rectángulo, rectángulo redondeado, elipse, rombo, cilindro (`cylinder3`), actor UML, swimlane, texto, línea, aristas con puntos intermedios (esquinas redondeadas), flechas block/open/classic/diamond/oval, trazos discontinuos y etiquetas de arista. Otros estilos de draw.io se ignoran. El texto de una arista se coloca por longitud de arco; en app.diagrams.net su posición puede variar unos píxeles.
