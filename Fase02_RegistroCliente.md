# Fase 02 — Registro de clientes

**Proyecto:** MotorFlow — Gestión de Taller Mecánico  
**Fecha:** 28 de septiembre de 2026  
**Alcance:** Registro seguro de clientes por administradores, secretarias o recepcionistas.

## 1. Resultado de la fase

Se implementó el registro de clientes con datos personales, dirección, fotografía, autorización por rol, validaciones de formato y bloqueo de duplicados. El caso de uso usa el patrón **Facade** entre Vue y REST, y entre REST y el patrón **Repository** de Spring Data JPA.

El cliente queda persistido en MySQL en la tabla `clients`; la dirección es embebida y la fotografía se guarda como binario BLOB junto con su tipo MIME. La entidad contiene `workshopId` nullable y sin uso en esta fase, reservada únicamente para una futura asociación con talleres o sucursales.

## 2. Estado consolidado

| Fase | Módulo | Estado | Entrega / pendiente |
|---|---|---|---|
| 1 | Login y registro de usuarios | Completo para fase inicial | Login HTTP Basic, registro de usuario, BCrypt y consulta de identidad/roles. Pendiente: JWT, persistencia de sesión y recuperación visual por correo. |
| 1 | Roles | Completo para endpoints definidos | Roles `ADMINISTRADOR`, `SECRETARIA`, `RECEPCIONISTA`, `MECANICO` y `CLIENTE`. |
| 2 | Facade frontend | Completo | `AuthFacade` y `ClientFacade` coordinan vista, reglas de UI y Repository REST. |
| 2 | Repository frontend | Completo | `AuthRepository` y `ClientRepository` encapsulan llamadas REST y multipart. |
| 2 | Facade backend | Completo | `AuthFacade` y `ClientFacade` concentran casos de uso y reglas antes de JPA. |
| 2 | Registro de clientes | Completo | Alta multipart con nombre, contactos, edad, nacimiento, correos, fotografía y dirección. |
| 2 | Autorización de clientes | Completo | `POST /api/clients` permite solo ADMINISTRADOR, SECRETARIA o RECEPCIONISTA. |
| 2 | Validación de datos | Completo | Bean Validation, formatos HTML, edad consistente con fecha, tipo/tamaño/firma binaria de imagen y dirección obligatoria. |
| 2 | Control de duplicados | Completo | No permite teléfonos o correos repetidos, incluso cruzados entre campo personal y laboral. |
| 2 | Notificación de guardado | Completo | La API devuelve `Cliente guardado` y Vue muestra una notificación verde tras `201 Created`. |
| 2 | Talleres/sucursales | Diseñado, no implementado | `workshopId` queda reservado; no existe relación, selector ni endpoint de sucursales. |
| 2 | Listado/edición de clientes | Pendiente | El alcance solo incluye creación; no se agregaron GET, PUT, DELETE ni pantalla de listado. |

## 3. Flujo de datos

```text
Administrador / Secretaria / Recepcionista autenticado
  → Vista Vue Clientes
  → ClientFacade (rol + foto)
  → ClientRepository (FormData multipart)
  → POST /api/clients
  → Spring Security (roles permitidos)
  → ClientController
  → ClientFacade (formato, edad, imagen, duplicados)
  → ClientRepository JPA
  → MySQL: clients
  → ClientResponse: "Cliente guardado"
  → Notificación Vue
```

El diagrama de componentes interactivo generado con Archify se encuentra en:

[`registro-cliente.html`](.archify/architecture-registro-cliente-20260928-183700/registro-cliente.html)

Su especificación fuente está en [`candidate.json`](.archify/architecture-registro-cliente-20260928-183700/candidate.json).

## 4. Datos y reglas del registro

| Dato | Obligatorio | Validación | Persistencia |
|---|---:|---|---|
| Nombre completo | Sí | 3–120 caracteres: letras, espacios, punto, apóstrofo y guion | `clients.full_name` |
| Contacto alternativo | No | Mismo formato de nombre | `clients.alternate_contact_name` |
| Edad | Sí | Entero entre 0 y 120; debe coincidir con fecha de nacimiento | `clients.age` |
| Fecha de nacimiento | Sí | Fecha pasada | `clients.birth_date` |
| Teléfono personal | Sí | 7–20 caracteres numéricos/símbolos telefónicos; único | `clients.personal_phone` |
| Teléfono de trabajo | No | Formato telefónico; único contra teléfonos personales y laborales | `clients.work_phone` |
| Email | Sí | Formato email; único | `clients.email` |
| Email de trabajo | No | Formato email; único contra correos personales y laborales | `clients.work_email` |
| Fotografía | Sí | JPG/JPEG, PNG o WEBP; máximo 15 MB; MIME y firma binaria consistentes | `clients.photo`, `clients.photo_content_type` |
| Calle, colonia, municipio, estado | Sí | Texto no vacío y límites de longitud | Columnas embebidas de `clients` |
| Código postal | Sí | Exactamente cinco dígitos | `clients.postal_code` |

## 5. API REST

### `POST /api/clients`

**Autorización:** HTTP Basic con rol `ADMINISTRADOR`, `SECRETARIA` o `RECEPCIONISTA`.

**Content-Type:** `multipart/form-data`

| Parte | Tipo | Descripción |
|---|---|---|
| `client` | JSON | Objeto `ClientRegistrationRequest` con datos y dirección. |
| `photo` | Archivo | Fotografía JPG, PNG o WEBP de máximo 15 MB. |

**Respuesta correcta (`201 Created`):**

```json
{
  "id": 1,
  "fullName": "Nombre del cliente",
  "email": "cliente@ejemplo.com",
  "message": "Cliente guardado"
}
```

**Respuestas de error:**

| Código | Motivo |
|---:|---|
| 400 | Datos inválidos, edad inconsistente, dirección incompleta, foto sin formato permitido, firma inválida o más de 15 MB. |
| 401 | No existe autenticación HTTP Basic válida. |
| 403 | El usuario autenticado no tiene un rol autorizado. |
| 409 | Teléfono o correo ya pertenece a un cliente. |

## 6. Documentación de código y métodos

### Fase 1 refactorizada: identidad

| Archivo / método | Responsabilidad |
|---|---|
| `backend/.../facade/AuthFacade.java#register` | Crea usuario, normaliza correo, aplica BCrypt y persiste roles delegando en `UserRepository`. |
| `AuthFacade#requestRecovery` | Genera token aleatorio, persiste su hash BCrypt y expiración de 15 minutos; no envía correo en esta fase. |
| `AuthFacade#resetPassword` | Busca token vigente, compara con BCrypt, reemplaza contraseña y elimina token usado. |
| `backend/.../controller/AuthController.java` | Adaptador REST: valida request, delega a `AuthFacade` y expone `/me` con correo y roles para Vue. |
| `frontend/services/api-client.js#request` | Ejecuta solicitudes REST, agrega Basic Auth cuando corresponde y convierte errores de API en `Error`. |
| `frontend/repositories/auth-repository.js#me` | Consulta identidad/roles desde `GET /api/auth/me`. |
| `frontend/repositories/auth-repository.js#register` | Envía alta de usuario a `POST /api/auth/register`. |
| `frontend/facades/auth-facade.js#login` | Construye Basic Auth, consulta sesión y elimina autorización temporal ante error. |
| `frontend/facades/auth-facade.js#hasAnyRole` | Devuelve si la sesión contiene al menos uno de los roles permitidos. |

### Fase 2: registro de clientes

| Archivo / método | Responsabilidad |
|---|---|
| `frontend/index.html` | Presenta módulo Clientes y formulario con campos requeridos, restricciones HTML y selector de archivos aceptados. Mantiene la línea visual del login. |
| `frontend/app.js#login` | Solicita autenticación a `AuthFacade`, almacena identidad de sesión en memoria y muestra errores al usuario. |
| `frontend/app.js#registerUser` | Registra un usuario y valida sus credenciales al iniciar sesión con ellas. |
| `frontend/app.js#onPhotoChange` | Guarda el archivo seleccionado y limpia errores anteriores de fotografía. |
| `frontend/app.js#saveClient` | Llama a `ClientFacade.register`; ante éxito limpia el formulario y muestra `Cliente guardado` durante 3.5 segundos. |
| `frontend/app.js#openClientForm` | Impide abrir el formulario a roles no permitidos. |
| `frontend/facades/client-facade.js#register` | Valida rol, existencia de foto, 15 MB y MIME JPG/PNG/WEBP antes de llamar al Repository. |
| `frontend/repositories/client-repository.js#create` | Crea `FormData`, coloca JSON en `client`, foto en `photo` y envía `POST /clients`. |
| `backend/.../domain/Client.java` | Entidad JPA de cliente con datos, foto BLOB, dirección embebida y `workshopId` futuro no usado. |
| `backend/.../domain/Address.java` | Objeto `@Embeddable` que agrupa la dirección sin crear otra tabla en esta fase. |
| `backend/.../dto/ClientRegistrationRequest.java` | Contrato de entrada y validaciones de campos del JSON multipart. |
| `backend/.../dto/ClientResponse.java` | Contrato de salida reducido, sin exponer foto binaria. |
| `backend/.../controller/ClientController.java#create` | Recibe las partes `client` y `photo`, valida JSON, invoca `ClientFacade` y responde `201`. |
| `backend/.../facade/ClientFacade#register` | Orquesta la validación completa, mapea DTO a entidad, persiste y devuelve mensaje de éxito. |
| `ClientFacade#validatePhoto` | Obliga imagen, limita tamaño, MIME permitido y firma binaria. |
| `ClientFacade#hasExpectedSignature` | Reconoce cabeceras JPEG, PNG y WEBP para rechazar contenido simulado. |
| `ClientFacade#validateAge` | Calcula edad con fecha actual y compara contra la edad enviada. |
| `ClientFacade#validateDuplicates` | Revisa que teléfonos y correos no existan en campos personales ni laborales. |
| `backend/.../repository/ClientRepository.java` | Repository JPA con consultas `existsByAnyEmail` y `existsByAnyPhone`. |
| `backend/.../config/SecurityConfig.java` | Restringe `/api/clients/**` a los tres roles autorizados. |
| `backend/.../exception/ApiExceptionHandler.java` | Devuelve errores REST coherentes para duplicados, validación y reglas de negocio. |

## 7. Pruebas ejecutadas

| Caso | Resultado | Evidencia |
|---|---:|---|
| Compilación Spring Boot | Correcto | `mvn -DskipTests package` finalizó sin errores. |
| Conexión MySQL Docker | Correcto | API iniciada contra `springdb` mediante Hikari/JPA. |
| Alta autorizada de cliente | `201` | Cliente con foto PNG, datos y dirección persistido. |
| Persistencia | Correcto | Verificados nombre, correo, teléfono, tipo/tamaño de BLOB y código postal en MySQL. |
| Solicitud sin autenticación | `401` | Endpoint protegido por Spring Security. |
| Fotografía de contenido falso | `400` | Un archivo de texto con MIME PNG fue rechazado por validación de firma binaria. |
| Registro duplicado | `409` | El mismo teléfono/correo no se volvió a insertar. |

Los datos usados para prueba son temporales y se eliminan al cierre técnico de la verificación.

## 8. Archify

Se instaló Archify mediante:

```bash
npx -y skills add tt-a1i/archify --skill archify --agent codex --global --copy --yes
```

El diagrama pasó los controles de **validación**, **entrega** y **comprobación de artefacto**. La comprobación visual automática de Chrome quedó sin ejecutar porque el entorno no cuenta con ejecutable Chrome o Chromium; el archivo HTML permanece disponible para apertura local.

## 9. Pendientes fuera del alcance de esta fase

1. Listar, consultar, editar y eliminar clientes.
2. Asociar cliente con taller/sucursal a partir del campo preparado `workshopId`.
3. Guardar fotografías en almacenamiento de objetos y conservar solamente una URL segura en MySQL.
4. Migrar HTTP Basic a JWT, restringir CORS a dominios conocidos y habilitar HTTPS para producción.
5. Añadir pruebas automatizadas JUnit, integración REST y pruebas de interfaz.
