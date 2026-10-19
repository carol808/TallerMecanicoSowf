# Fase 03 — Registro y asociación de vehículos

**Proyecto:** MotorFlow — Taller mecánico
**Alcance de la fase:** registrar vehículos y asociarlos a clientes activos mediante los catálogos locales de marca, modelo y versión.
**Estado general:** implementado y verificado a nivel de backend, frontend y base de datos.

## Objetivo

Permitir que una persona con rol **Administrador** o **Recepcionista** registre un vehículo para un cliente activo, utilizando datos normalizados, validaciones de formato y restricciones de unicidad en MySQL.

## Componentes implementados

| Componente | Implementación | Estado |
|---|---|---|
| Autorización | Spring Security protege `/api/vehicles/**` y `/api/vehicle-catalog/**` para los roles Administrador y Recepcionista. | Completo |
| Asociación con cliente | La entidad `vehicles` utiliza `client_id` como clave foránea hacia `clients`. El backend comprueba que el cliente exista y esté activo. | Completo |
| Registro REST | `POST /api/vehicles` recibe el vehículo, valida el catálogo seleccionado y lo persiste. | Completo |
| Normalización | VIN se convierte a mayúsculas; la placa elimina separadores y se convierte a mayúsculas antes de validar y guardar. | Completo |
| Validaciones | VIN de 17 caracteres sin I, O o Q; placa de 5 a 12 caracteres alfanuméricos; año 1886–2100; kilometraje no negativo; campos obligatorios. | Completo |
| Prevención de duplicados | VIN y placa son únicos en la entidad, base de datos y validación de servicio. | Completo |
| Catálogo de vehículos | Tablas relacionadas `vehicle_brands`, `vehicle_models` y `vehicle_versions`; endpoints dependientes para Marca → Modelo → Versión. | Completo |
| Interfaz Vue | Acción **Asociar vehículo** por cliente activo, modal de captura, selects dependientes y errores junto a cada campo. | Completo |
| Mensajes de operación | Notificación temporal “Vehículo asociado” cuando el registro se guarda correctamente. | Completo |

## Flujo funcional

1. El usuario inicia sesión con rol Administrador o Recepcionista.
2. Desde **Administración de Clientes**, abre la acción **Asociar vehículo** de un cliente activo.
3. Vue solicita las marcas a `/api/vehicle-catalog/brands`.
4. Al seleccionar una marca, solicita sus modelos; al seleccionar un modelo, solicita sus versiones.
5. El usuario captura VIN, placa, marca, modelo, versión y, de forma opcional, año, color, número de serie y kilometraje.
6. El frontend envía la solicitud a `POST /api/vehicles` con la autorización de la sesión.
7. El backend normaliza VIN y placa, valida formatos, verifica que no existan duplicados y comprueba la coherencia Marca → Modelo → Versión.
8. MySQL guarda el vehículo relacionado por `vehicles.client_id`.
9. La interfaz cierra el modal y muestra la confirmación de guardado.

## Modelo de datos y relaciones

| Tabla | Relación y propósito |
|---|---|
| `clients` | Cliente propietario del vehículo. Solo puede asociarse cuando su Status es Activo. |
| `vehicles` | Vehículo registrado; contiene `client_id`, VIN, placa, catálogos seleccionados y datos opcionales. |
| `vehicle_brands` | Catálogo de marcas. |
| `vehicle_models` | Catálogo de modelos relacionados a una marca. |
| `vehicle_versions` | Catálogo de versiones relacionadas a un modelo. |

```text
clients (1) ──── (N) vehicles
vehicle_brands (1) ──── (N) vehicle_models (1) ──── (N) vehicle_versions
vehicles ──── marca / modelo / versión seleccionados
```

## Endpoints de la fase

| Método | Endpoint | Uso | Roles autorizados |
|---|---|---|---|
| `GET` | `/api/vehicle-catalog/brands` | Lista las marcas disponibles. | Administrador, Recepcionista |
| `GET` | `/api/vehicle-catalog/brands/{brandId}/models` | Lista modelos de la marca seleccionada. | Administrador, Recepcionista |
| `GET` | `/api/vehicle-catalog/models/{modelId}/versions` | Lista versiones del modelo seleccionado. | Administrador, Recepcionista |
| `POST` | `/api/vehicles` | Registra y relaciona un vehículo con un cliente activo. | Administrador, Recepcionista |

## Validaciones y reglas de negocio

| Regla | Resultado si no se cumple |
|---|---|
| El cliente debe existir y tener Status Activo. | No se registra el vehículo; se informa que el cliente debe estar activo. |
| El VIN debe cumplir `^[A-HJ-NPR-Z0-9]{17}$`. | Se muestra error en el campo VIN. |
| La placa debe cumplir `^[A-Za-z0-9-]{5,12}$`. | Se muestra error en el campo Placa. |
| VIN y placa no pueden repetirse. | Se informa específicamente cuál dato ya está registrado. |
| Marca, modelo y versión deben pertenecer a la misma cadena de catálogo. | Se marcan los selectores involucrados. |
| Año y kilometraje deben respetar sus límites. | Se muestra el error junto al campo correspondiente. |

## Verificación realizada

La verificación de MySQL realizada al cierre de la fase confirmó:

| Comprobación | Resultado |
|---|---|
| Vehículos registrados | 23 |
| Clientes distintos con vehículo asociado | 23 |
| Vehículos sin cliente válido | 0 |
| VIN duplicados | 0 |
| Placas duplicadas | 0 |
| Restricción de cliente activo | Aplicada por el backend antes de guardar |

## Estado actual y tareas pendientes

| Elemento | Estado | Pendiente |
|---|---|---|
| Registro y asociación de vehículo | Completo | — |
| Catálogos dependientes de marca, modelo y versión | Completo | — |
| Seguridad por rol y validaciones | Completo | — |
| Consulta visual de vehículos ya asociados por cliente | Pendiente | Incorporar una vista o detalle de lectura de vehículos existentes; el modal actual está orientado al registro. |
| Técnico independiente asignado al vehículo | Fuera del alcance actual | Diseñar e implementar cuando se defina el modelo de técnicos. |
| Edición, baja lógica y consulta paginada de vehículos | Pendiente | Definir reglas funcionales para su siguiente fase. |

## Archivos principales de la fase

- `backend/src/main/java/com/taller/gestion/controller/VehicleController.java`
- `backend/src/main/java/com/taller/gestion/controller/VehicleCatalogController.java`
- `backend/src/main/java/com/taller/gestion/domain/Vehicle.java`
- `backend/src/main/java/com/taller/gestion/dto/VehicleRequest.java`
- `backend/src/main/java/com/taller/gestion/repository/VehicleRepository.java`
- `frontend/app.js`
- `frontend/index.html`
- `Registrar_vehiculo.md`

## Diagramas HTML incluidos en el repositorio

- `.archify/architecture-registro-cliente-20260928-183700/registro-cliente.html`
- `.archify/workflow-registro-clientes-20261006-205000/diagrama_proceso_clientes.html`
