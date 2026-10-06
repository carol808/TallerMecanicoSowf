# Registro y Administración de Clientes

## Alcance implementado

Esta entrega amplía la Fase 2 sobre el login existente. La aplicación conserva Vue 3 + Bootstrap en el frontend, Spring Boot con servicios REST en el backend y MySQL en Docker como persistencia. El flujo principal es: **login → dashboard → taller → cliente → consulta y administración**.

| Funcionalidad o requisito | Trabajo realizado | Estado | Trabajo pendiente | Observaciones |
|---|---|---|---|---|
| Login y control de acceso | Login HTTP Basic y consulta de identidad/roles en `/api/auth/me`. | COMPLETADO | — | Las contraseñas se almacenan con BCrypt. |
| Recuperación de contraseña | Vista de solicitud y endpoints de token temporal con vencimiento de 15 minutos. | EN PROCESO | Configurar proveedor de correo y pantalla segura que reciba el token. | La API no revela si el correo existe. |
| Cerrar sesión | El frontend elimina el encabezado Basic y el estado de sesión y muestra el login. | COMPLETADO | — | HTTP Basic es sin estado; no existe cookie de servidor que invalidar. |
| Roles | Administrador y secretaria pueden consumir clientes; talleres y suspensión se protegen para administrador. | COMPLETADO | Crear administración autenticada de cuentas si se requiere alta de usuarios posterior. | El backend bloquea las rutas, no solo la interfaz. |
| Navegación | Barra lateral limitada a **Registro de clientes** y **Clientes registrados**. | COMPLETADO | — | El alta de taller es una acción contextual exclusiva de administrador. |
| Talleres | Entidad, Repository, Facade, multipart REST y formulario con foto, RFC, contacto y dirección. | COMPLETADO | Edición de taller si se solicita en otra fase. | Bloquea RFC, correo y combinación nombre/CP duplicados. |
| Asociación cliente–taller | `workshopId` obligatorio al crear/editar y filtros por taller. | COMPLETADO | Migrar a relación JPA explícita si el dominio requiere navegación bidireccional. | La información se consulta aislada por taller. |
| Registro de clientes | Captura nombres, CURP, RFC, nacimiento/edad, correos/teléfonos, fotografía y dirección. | COMPLETADO | — | La interfaz confirma **Cliente guardado** solo tras `201 Created`. |
| Validaciones y duplicados | Reglas en Vue y Spring: formato, edad, foto, CURP, CP y campos únicos. | COMPLETADO | Pruebas automatizadas unitarias y de integración en CI. | Se validan de nuevo en el backend. |
| Dirección automática | API local CP → estado/municipio/colonia y dirección → CP. | EN PROCESO | Cargar el catálogo completo de SEPOMEX para los estados autorizados. | Catálogo inicial: Hidalgo, Querétaro, San Luis Potosí, Veracruz, Puebla, Tlaxcala y Estado de México. |
| Administración de clientes | Listado paginado del servidor (10), orden asc/desc, edición, transferencia y suspensión. | COMPLETADO | Filtros de texto si se requieren. | Suspensión únicamente para administrador. |
| Diagrama del proceso | Diagrama HTML autocontenido generado con Archify. | COMPLETADO | — | Artefacto: `.archify/workflow-registro-clientes-20261006-205000/diagrama_proceso_clientes.html`. |

## Diseño técnico y responsabilidades

### Frontend

| Archivo | Responsabilidad | Métodos o funciones principales |
|---|---|---|
| `frontend/app.js` | Estado Vue, navegación y eventos de formularios. | `login`, `logout`, `requestRecovery`, `saveWorkshop`, `saveClient`, `loadClients`, `changeWorkshop`, `suspend`, `lookupAddress`, `reverseAddress`. |
| `frontend/services/api-client.js` | Cliente HTTP único: encabezado de autorización y transformación de errores REST. | `request`. |
| `frontend/facades/auth-facade.js` | Orquesta autenticación, recuperación y limpieza de sesión. | `login`, `recovery`, `logout`, `hasAnyRole`. |
| `frontend/facades/client-facade.js` | Aplica autorización de interfaz y validación de archivo antes del Repository. | `register`, `update`, `list`, `transfer`, `suspend`. |
| `frontend/facades/workshop-facade.js` | Coordina talleres y catálogo postal. | `create`, `list`, `postal`, `reverse`, `canManage`. |
| `frontend/repositories/*.js` | Adaptadores REST para autenticación, clientes y talleres. | Construyen JSON/multipart y llaman a `ApiClient`. |

### Backend

| Archivo o capa | Responsabilidad | Métodos o endpoints relevantes |
|---|---|---|
| `SecurityConfig` | CORS, BCrypt, Basic Auth y autorización de rutas. | Protege `/api/clients`, `/api/workshops` y `/api/postal-codes` por rol. |
| `ClientController` | Adaptador REST de clientes. | `POST /api/clients`, `GET /api/clients`, `PUT /api/clients/{id}`, transferencia y suspensión. |
| `ClientFacade` | Caso de uso y reglas de negocio del cliente. | `register`, `update`, `list`, `transfer`, `suspend`; valida taller, foto, edad, dirección y duplicados. |
| `WorkshopController` / `WorkshopFacade` | Registro/listado de talleres. | `POST` solo administrador y `GET` para roles operativos. |
| `PostalCatalogController` / `PostalCatalogService` | Consulta bidireccional del catálogo regional. | `GET /api/postal-codes/{cp}` y `GET /api/postal-codes?state=...`. |
| Repositories JPA | Acceso a MySQL. | `ClientRepository` pagina por `workshopId`; `WorkshopRepository` detecta duplicados. |
| `ApiExceptionHandler` | Contrato de errores legible para Vue. | Convierte validación, duplicado, tamaño, recurso ausente y autorización en respuestas HTTP. |

## Contrato REST

| Método | Ruta | Roles | Resultado |
|---|---|---|---|
| `POST` | `/api/workshops` | Administrador | Crea taller con `multipart/form-data`. |
| `GET` | `/api/workshops` | Administrador, secretaria | Devuelve talleres permitidos. |
| `GET` | `/api/postal-codes/{cp}` | Administrador, secretaria | Resuelve estado, municipio y colonias. |
| `GET` | `/api/postal-codes` | Administrador, secretaria | Resuelve CP desde estado/municipio/colonia. |
| `POST` | `/api/clients` | Administrador, secretaria | Registra cliente y fotografía. |
| `GET` | `/api/clients?workshopId=&page=&direction=` | Administrador, secretaria | Lista paginada, máximo 10 registros. |
| `PUT` | `/api/clients/{id}` | Administrador, secretaria | Edita datos; fotografía opcional. |
| `PATCH` | `/api/clients/{id}/workshop` | Administrador, secretaria | Cambia de taller. |
| `PATCH` | `/api/clients/{id}/suspend` | Administrador | Marca el cliente como suspendido. |

## Catálogo postal y fuentes

El catálogo inicial se limita a Hidalgo y las entidades que colindan con él: Querétaro, San Luis Potosí, Veracruz, Puebla, Tlaxcala y Estado de México. Su diseño aislado en `PostalCatalogService` permite reemplazarlo por una importación completa sin cambiar formularios ni controladores.

- SEPOMEX de referencia: [ripper2hl/sepomex](https://github.com/ripper2hl/sepomex).
- División geográfica y municipios: [INEGI — Catálogo Único](https://www.inegi.org.mx/servicios/catalogounico.html).

## Verificación realizada

1. `mvn -q -DskipTests package`: compilación exitosa.
2. `node --check` sobre los módulos modificados y `git diff --check`: sin errores sintácticos ni de espacios.
3. Prueba REST con datos temporales, eliminados después: alta de taller `201`, alta de cliente `201`, listado paginado `200`, catálogo postal `200` y alta de taller por secretaria `403`.
4. Archify validó el artefacto HTML y su procedencia; su comprobación con Chrome quedó omitida porque el entorno no tiene Chrome/Chromium instalado.

## Despliegue y configuración

- **Frontend:** publicar el directorio `frontend/` en Netlify, Vercel o GitHub Pages. Configurar la URL de API en `frontend/services/api-client.js` para el dominio del backend en producción.
- **Backend:** empaquetar con Maven y publicar el JAR en Render, Railway, Fly.io, una VM con Java 21+ o Docker. Exponer `PORT`, `DB_URL`, `DB_USER` y `DB_PASSWORD` como variables de entorno.
- **Base de datos:** MySQL administrado o contenedor Docker. En desarrollo la configuración está en `backend/src/main/resources/application.yml`; las credenciales se leen de `DB_USER` y `DB_PASSWORD`, no deben versionarse.
