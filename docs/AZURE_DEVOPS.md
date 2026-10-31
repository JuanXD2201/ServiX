# Azure DevOps (Optativa II)

Fuente de requisitos: *Guía Final – Azure DevOps* (Optativa II, ing. Jairo Armando Salcedo Aranda). En la guía, ServiX corresponde al **Proyecto 3: proyecto integrador desarrollado desde el inicio**.

## Plan de Sprints (de la guía)

| Sprint | Actividades |
|---|---|
| 1 | Análisis, requisitos y diseño |
| 2 | Desarrollo de funcionalidades principales |
| 3 | Integración, pruebas y despliegue |

Duración de cada Sprint: 2 semanas. Las fechas deben fijarse según el calendario académico real.

## Pipeline

`azure-pipelines.yml` (raíz del repositorio):

- Disparador: cambios en `main` y Pull Requests hacia `main`.
- Pasos: JDK 17, Android SDK 36.1, caché de Gradle, resolución de dependencias, `testDebugUnitTest`, `assembleDebug`, publicación de resultados de pruebas y del APK debug como artefacto `servix-apk-debug`.
- No contiene secretos. El APK publicado es de depuración; la firma de *release* requeriría un archivo seguro en *Pipelines → Library*.

## Estado frente a los requisitos de la guía

| # | Requisito | Estado | Evidencia |
|---|---|---|---|
| 1 | Organización Azure DevOps | Por verificar | Captura pendiente |
| 2 | Tres proyectos Scrum privados | Por verificar. ServiX es uno; los otros dos pertenecen a otros trabajos | Captura pendiente |
| 3 | Configuración Scrum (equipo, roles, Sprints) | Pendiente | — |
| 4 | Product Backlog (PBI, Tasks, Bugs) | Pendiente | — |
| 5 | Tablero Scrum | Pendiente | — |
| 6 | Repositorio Git en Azure Repos | Pendiente (el código está en GitHub) | — |
| 7 | Ramas `main`, `develop`, `feature/*` y Pull Requests | Ramas listas en GitHub; falta Azure Repos | — |
| 8 | Dashboard con 6 widgets | Pendiente | — |
| 9 | Pipeline ejecutado correctamente | YAML listo; falta ejecutarlo en Azure | — |
| 10 | Enlaces a los proyectos | Pendiente | — |

GitHub y Azure Repos son entregables distintos: subir el código a uno no sustituye al otro.

## Procedimiento para completar la configuración

1. **Organización y proyecto** — dev.azure.com → *Create new organization*; *New project* con visibilidad **Private** y proceso **Scrum**.
2. **Equipo** — *Project settings → Teams*: añadir a los integrantes reales y asignar Product Owner, Scrum Master y Developers.
3. **Sprints** — *Boards → Team configuration → Iterations*: crear Sprint 1, 2 y 3 de dos semanas con fechas reales.
4. **Backlog** — crear los PBI a partir de las funcionalidades (autenticación, clientes, servicios, historial, notificaciones, chat IA), con Tasks y estimaciones; asignarlos a los Sprints.
5. **Repositorio** — *Repos → Files → Import repository* con la URL de GitHub, o `git remote add azure <url>` y `git push azure --all`.
6. **Políticas** — *Repos → Branches → `main` → Branch policies*: exigir Pull Request, un revisor como mínimo y validación de build con el pipeline.
7. **Pipeline** — *Pipelines → New pipeline → Azure Repos Git → Existing Azure Pipelines YAML file → `/azure-pipelines.yml`*; ejecutar *Run pipeline*.
8. **Dashboard** — *Overview → Dashboards → New Dashboard* “Dashboard Scrum - ServiX” (tipo Team) con: Sprint Burndown, Sprint Overview, Velocity, Cumulative Flow Diagram, Query Results y Work Assigned to Me.
9. **Evidencias** — capturar cada elemento de la lista anterior con los datos reales.

Nota: los agentes de Microsoft alojados requieren que la organización disponga de paralelismo gratuito concedido; si la solicitud está pendiente, el pipeline queda en cola.
