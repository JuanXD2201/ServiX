# Firebase en ServiX

| Dato | Valor |
|---|---|
| Proyecto | Servix |
| ID del proyecto | `servix-453fb` |
| Paquete Android | `com.example.servix` |
| Servicios usados | Authentication (correo y contraseña), Cloud Firestore, Firebase AI Logic |

`app/google-services.json` contiene identificadores públicos del cliente. La clave de API que incluye debe restringirse en Google Cloud Console al paquete y a la huella SHA-1 de la aplicación.

## Modelo de datos (Firestore)

```text
usuarios/{uid}                       perfil: role, nombre, correo…
  ├─ clientes/{clienteId}
  ├─ servicios/{servicioId}
  ├─ notificaciones/{notificacionId}
  ├─ actividades/{actividadId}
  └─ configuracion/
       ├─ app_config
       └─ user_profile
solicitudes_atencion/{solicitudId}   escalamientos del chat hacia el administrador
```

Los repositorios correspondientes están en `app/src/main/java/com/example/data/firebase/`.

## Reglas de seguridad

Archivo: `firestore.rules`. Desplegadas en el proyecto `servix-453fb` con `firebase deploy --only firestore:rules`.

| Ruta | Lectura | Escritura |
|---|---|---|
| `usuarios/{uid}` | Dueño o administrador | Creación: solo el dueño, con `role` ∈ {`CLIENTE`, `ADMINISTRADOR`}. Actualización: dueño sin cambiar `role`, o administrador. Borrado: nadie |
| `usuarios/{uid}/{subcolección}/*` | Dueño o administrador | Dueño o administrador; solo subcolecciones listadas |
| `solicitudes_atencion/{id}` | Usuario autenticado | Creación: autenticado con campos de texto acotados. Actualización y borrado: administrador |
| Cualquier otra ruta | Denegada | Denegada |

Una versión anterior permitía lectura y escritura sin autenticación en todo el proyecto (`|| true` en cada regla). Fue sustituida y las colecciones raíz heredadas (`clientes`, `servicios`, `notificaciones`, `actividades`, `configuracion`) quedaron bloqueadas porque el código no las utiliza.

### Riesgos residuales

1. **Autoasignación de rol.** El registro permite elegir `ADMINISTRADOR`. Para producción, la asignación debe hacerse con un claim de administrador (Admin SDK o Cloud Function).
2. **Solicitudes visibles entre usuarios.** Cualquier usuario autenticado puede leer `solicitudes_atencion`, porque el repositorio escucha la colección completa. Corregir requiere filtrar por cliente en la consulta y restringir la regla.
3. **App Check** no está activado.

## Cómo desplegar las reglas

```bash
firebase login
firebase deploy --only firestore:rules --project servix-453fb
```
