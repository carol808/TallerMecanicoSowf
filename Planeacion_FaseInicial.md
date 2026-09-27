# Planeación y documentación técnica — Fase inicial

**Proyecto:** MotorFlow — Gestión de Taller Mecánico  
**Versión documentada:** primera fase  
**Fecha de revisión:** 26 de septiembre de 2026  
**Repositorio:** `carol808/TallerMecanicoSowf`

## 1. Propósito y alcance de la fase

Esta fase establece la base técnica y la primera interfaz navegable para administrar un taller mecánico. Incluye una aplicación web estática basada en Vue 3 y Bootstrap, una API REST en Spring Boot, persistencia MySQL y una primera capa de autenticación y autorización por roles.

El alcance entregado debe interpretarse como una **fase inicial funcional**: autenticación, alta de usuarios y APIs principales fueron probadas contra MySQL; varios componentes del tablero representan datos de demostración mientras se completa su conexión con la API.

## 2. Desglose de entregables y estado de módulos

| Módulo | Datos principales | Estado | Descripción objetiva de la entrega |
|---|---|---|---|
| Base del proyecto | `frontend/`, `backend/`, Maven, Java 21 | Terminado | Estructura separada de cliente y servidor. Spring Boot 3.5.6, Spring Data JPA, Security, Validation y MySQL Connector/J están declarados. |
| Interfaz y diseño | Login, registro, menú, panel y formulario de recepción | Terminado visualmente | Interfaz responsiva con Vue 3, Bootstrap 5 e iconos Bootstrap; paleta azul, navegación lateral y componentes de tablero. |
| Autenticación | Correo, contraseña y estado de sesión de la UI | Terminado para fase inicial | La UI registra usuarios y valida credenciales mediante HTTP Basic contra `GET /api/dashboard`. El acceso correcto muestra el tablero. |
| Registro de usuarios | Nombre, correo, contraseña y rol | Terminado y validado | `POST /api/auth/register` valida la solicitud, evita correos duplicados y persiste el usuario y rol en MySQL. La prueba ejecutada devolvió `201 Created`. |
| Autorización | ADMINISTRADOR, SECRETARIA, MECANICO, CLIENTE | Terminado para endpoints definidos | Spring Security protege rutas: administración, órdenes/inventario y rutas autenticadas; cada rol se convierte en autoridad `ROLE_*`. |
| Recuperación de cuenta | Correo, token de recuperación, expiración y nueva contraseña | API inicial terminada | Se genera un token aleatorio, se guarda su hash BCrypt y vence a los 15 minutos. Falta el envío de correo y un flujo visual para introducir el token. |
| Órdenes de trabajo y recepción | Folio, cliente, teléfono, vehículo, placas, problema y condiciones iniciales | API terminada; UI pendiente de integración | `POST /api/orders` crea la orden y `GET /api/orders` la consulta. El modal de recepción existe, pero aún no envía sus datos a la API. |
| Cotización y entrega | Total cotizado y fecha/hora prometida | API inicial terminada | `PATCH /api/orders/{id}/quote` guarda total y hora de entrega prometida. Falta interfaz de cotización, desglose de partidas y notificación real al cliente. |
| Inconformidades | Comentario del cliente | API inicial terminada | `PATCH /api/orders/{id}/feedback` guarda el comentario asociado a una orden. Falta pantalla del cliente y gestión administrativa. |
| Inventario | SKU, nombre, existencia, mínimo y precio unitario | Modelo y consulta de alertas terminados | La entidad identifica stock bajo mediante `stock <= minStock`; el dashboard devuelve la lista de alertas. La tabla visual usa datos de demostración y no hay CRUD de piezas aún. |
| Dashboard operativo | Vehículos del día, reparación, listos y stock bajo | API parcial + UI demo | `GET /api/dashboard` calcula contadores desde órdenes y piezas. Las tarjetas, agenda, equipo y gráfica mostradas en el frontend están cargadas localmente. |
| Ganancias | Periodo semanal, quincenal y mensual | Prototipo visual | El selector y gráfica están implementados en Vue con valores de demostración. Falta modelo de pagos/ingresos y cálculo persistente. |
| Notificaciones | Hora de entrega y destinatario | Pendiente | La hora prometida se persiste, pero no existe integración con correo, SMS, WhatsApp ni servicio de colas. |
| Pruebas de integración | Registro, login, MySQL y hash | Terminado para el flujo validado | Se comprobó conexión a `springdb` en Docker, registro con `201`, login con `200`, rol persistido y hash BCrypt de 60 caracteres. La cuenta temporal de QA fue eliminada tras la verificación. |

## 3. Arquitectura y flujo de datos

```text
Navegador
  └─ Frontend estático: Vue 3 + Bootstrap
       ├─ POST /api/auth/register  → alta de usuario
       └─ GET  /api/dashboard      → autenticación Basic y panel
             │
             ▼
API REST: Spring Boot 3
  ├─ Seguridad: Spring Security + BCrypt + roles
  ├─ Persistencia: Spring Data JPA / Hibernate
  └─ MySQL Docker: springdb (puerto local publicado 3307)
```

El frontend se comunica actualmente con `http://localhost:8082` desde `frontend/app.js`. Para publicar el sistema, dicha URL debe reemplazarse por el dominio HTTPS del backend o extraerse a una configuración de entorno de frontend.

## 4. Modelo de datos de la fase

| Entidad / tabla | Campos implementados | Uso |
|---|---|---|
| `users` / `User` | `id`, `name`, `email`, `passwordHash`, `recoveryToken`, `recoveryExpiresAt` | Identidad de usuarios. El correo es único y la contraseña se almacena hasheada. |
| `user_roles` / colección `roles` | `user_id`, `roles` | Relación de roles de cada usuario. |
| `work_order` / `WorkOrder` | `id`, `folio`, cliente, teléfono, vehículo, placas, problema, condición inicial, estado, mecánico, recepción, entrega prometida, total, comentario | Orden de reparación y trazabilidad inicial del vehículo. |
| `part` / `Part` | `id`, `sku`, `name`, `stock`, `minStock`, `unitPrice` | Catálogo base de piezas y alerta de inventario bajo. |
| Enumeración `Role` | `ADMINISTRADOR`, `SECRETARIA`, `MECANICO`, `CLIENTE` | Autorización de usuarios. |
| Enumeración `OrderStatus` | `RECIBIDO`, `EN_DIAGNOSTICO`, `EN_REPARACION`, `ESPERA_REFACCION`, `LISTO`, `ENTREGADO`, `CANCELADO` | Estado del ciclo de reparación. |

## 5. Documentación del código generado

### 5.1 Frontend

| Archivo | Responsabilidad | Elementos documentados |
|---|---|---|
| `frontend/index.html` | Estructura de la SPA | Carga Bootstrap, Bootstrap Icons, Vue y estilos. Define el login/registro, dashboard, menú, agenda, inventario, gráfica y modal de recepción mediante directivas Vue (`v-if`, `v-for`, `v-model`). |
| `frontend/app.js` | Estado y comportamiento Vue | `data()` mantiene formularios, sesión, menú y datos demo; `gainValue` cambia la cifra por periodo; `login()` construye Basic Auth y consulta el dashboard; `register()` registra, rellena el formulario de login e inicia sesión. |
| `frontend/styles.css` | Diseño y responsividad | Variables de color azul, estructura de login, sidebar, tarjetas, tablas, gráfica de barras, modal y media queries para escritorio y móvil. |

**Limitación técnica actual del frontend:** no existe una capa de servicios reutilizable ni almacenamiento de sesión; el dashboard no consume aún todos los endpoints y `authenticated` vive únicamente en memoria. Un refresh vuelve al login.

### 5.2 Backend

| Archivo | Responsabilidad | Detalle técnico |
|---|---|---|
| `backend/pom.xml` | Dependencias y compilación | Define Spring Boot 3.5.6, Java 21, Web, JPA, Security, Validation, MySQL y tests; incluye el plugin de empaquetado Spring Boot. |
| `MotorflowApplication.java` | Punto de arranque | Clase `@SpringBootApplication` que inicia el contexto y servidor embebido. |
| `config/SecurityConfig.java` | Seguridad | Declara `BCryptPasswordEncoder`, obtiene usuarios por correo para Spring Security, mapea roles a autoridades y restringe endpoints. CSRF está deshabilitado para la API REST y el esquema de la fase es HTTP Basic. |
| `domain/User.java` | Entidad de identidad | Mapea `users`, correo único, hash de contraseña, roles en `user_roles` y token/expiración de recuperación. |
| `domain/WorkOrder.java` | Entidad de reparación | Persiste datos de recepción, diagnóstico inicial, estado, responsable, entrega, cotización e inconformidad. |
| `domain/Part.java` | Entidad de refacción | Conserva SKU, nivel de stock, mínimo y precio; `isLowStock()` determina la alerta. |
| `domain/Role.java` y `domain/OrderStatus.java` | Catálogos tipados | Evitan valores libres para permisos y estados de órdenes. |
| `repository/UserRepository.java` | Acceso a usuarios | Proporciona búsquedas por correo y token. |
| `repository/WorkOrderRepository.java` | Acceso a órdenes | Añade conteos por rango de recepción y por estado para el dashboard. |
| `repository/PartRepository.java` | Acceso a inventario | Ofrece operaciones JPA estándar sobre piezas. |
| `controller/AuthController.java` | Registro y recuperación | Expone registro, solicitud de recuperación y cambio de contraseña; aplica validación, BCrypt, token aleatorio de 32 bytes y expiración de 15 minutos. |
| `controller/WorkOrderController.java` | Gestión de órdenes | Expone consulta/alta de órdenes y parches para cotización e inconformidad. El folio se genera con prefijo `OT-` y timestamp. |
| `controller/DashboardController.java` | Resumen operativo | Retorna vehículos recibidos hoy, reparaciones, listos y piezas en stock bajo. |
| `src/main/resources/application.yml` | Configuración de ejecución | Configura datasource MySQL, JPA e identificador de puerto mediante variables de entorno. |

### 5.3 Contrato REST disponible

| Método y ruta | Seguridad | Entrada | Resultado |
|---|---|---|---|
| `POST /api/auth/register` | Pública | Nombre, correo, contraseña de 12–72 caracteres y rol | Crea usuario (`201`) o informa correo duplicado (`409`). |
| `POST /api/auth/recovery` | Pública | Correo | Genera token hasheado si el correo existe y devuelve mensaje no revelador. |
| `POST /api/auth/reset-password` | Pública | Token y nueva contraseña | Cambia contraseña si token vigente; invalida token utilizado. |
| `GET /api/dashboard` | Autenticada | Basic Auth | Resumen de vehículos, reparaciones, listos y alertas. |
| `GET /api/orders` | ADMINISTRADOR, SECRETARIA o MECANICO | — | Lista órdenes de trabajo. |
| `POST /api/orders` | ADMINISTRADOR, SECRETARIA o MECANICO | Datos de recepción | Crea y devuelve orden (`201`). |
| `PATCH /api/orders/{id}/quote` | ADMINISTRADOR, SECRETARIA o MECANICO | Total y fecha/hora de entrega | Actualiza cotización y entrega prometida. |
| `PATCH /api/orders/{id}/feedback` | ADMINISTRADOR, SECRETARIA o MECANICO | Comentario | Guarda la inconformidad del cliente. |

## 6. Credenciales y configuración de base de datos

Las claves de configuración de la conexión están en:

```text
backend/src/main/resources/application.yml
```

El archivo contiene las variables `DB_URL`, `DB_USER` y `DB_PASSWORD`, así como valores de desarrollo predeterminados. **No se deben versionar ni publicar credenciales reales.** En el entorno local validado, MySQL está en Docker y publica el puerto `3307`; la base usada es `springdb`.

Para desarrollo local, las credenciales efectivas se inyectan como variables de entorno antes de iniciar Spring Boot:

```bash
cd backend
DB_URL='jdbc:mysql://localhost:3307/springdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Mexico_City' \
DB_USER='tu_usuario_mysql' \
DB_PASSWORD='tu_contraseña_mysql' \
mvn spring-boot:run
```

En producción, use secretos configurados en el proveedor de hosting. Como mejora posterior, elimine los valores predeterminados `root` del archivo YAML y cree un archivo local no versionado, por ejemplo `application-local.yml` o `.env`, incluido en `.gitignore`.

## 7. Ejecución y verificación local

### Requisitos

- JDK 21 o compatible con el proyecto.
- Maven 3.9 o posterior.
- Docker con MySQL disponible y acceso a la base configurada.
- Navegador moderno e Internet para las bibliotecas CDN del frontend.

### Frontend

```bash
cd frontend
python3 -m http.server 4173
```

Abrir `http://localhost:4173`.

### Backend

```bash
cd backend
mvn -DskipTests package
DB_URL='jdbc:mysql://localhost:3307/springdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Mexico_City' \
DB_USER='tu_usuario_mysql' \
DB_PASSWORD='tu_contraseña_mysql' \
PORT=8082 \
java -jar target/motorflow-api-0.0.1-SNAPSHOT.jar
```

Validaciones realizadas durante la fase:

- Compilación Maven exitosa.
- Conexión JPA/HikariCP exitosa contra MySQL 9.7.2 en Docker.
- Alta de cuenta de prueba: `201 Created`.
- Login API con Basic Auth: `200 OK`.
- Fila de usuario y `ADMINISTRADOR` en `user_roles` verificados.
- Hash BCrypt confirmado con prefijo `$2a$` y longitud 60.
- Cuenta temporal eliminada después de la prueba.

## 8. Despliegue recomendado (hosting)

### 8.1 Frontend: Vercel

**Plataforma recomendada:** [Vercel](https://vercel.com/docs/deployments), porque el frontend actual es un sitio estático HTML/CSS/JavaScript y el repositorio se puede importar directamente desde GitHub. Vercel genera despliegues automáticos en cada push a la rama configurada y permite previsualizaciones por commit.

Pasos:

1. Antes de publicar, sustituir en `frontend/app.js` la URL `http://localhost:8082` por la URL pública HTTPS de la API. La solución recomendada es exponerla mediante un archivo de configuración de entorno que no se versiona.
2. Crear cuenta en Vercel y seleccionar **Add New → Project**.
3. Importar el repositorio `carol808/TallerMecanicoSowf` desde GitHub.
4. Establecer **Root Directory** en `frontend` y usar el preset **Other**; no se requiere comando de build para los archivos estáticos actuales.
5. Pulsar **Deploy**, comprobar la URL de previsualización e instalar el dominio personalizado cuando la validación sea satisfactoria.
6. Configurar el dominio de Vercel como origen permitido por CORS en la API; no mantener `origins="*"` en producción.

### 8.2 Backend: Render

**Plataforma recomendada:** [Render Web Service](https://render.com/docs/your-first-deploy), ya que puede conectar un repositorio Git y ejecutar un servicio HTTP del lado servidor. Render permite despliegues automáticos al recibir cambios en la rama configurada.

Pasos:

1. Crear un servicio **Web Service** y conectar el mismo repositorio de GitHub.
2. Definir **Root Directory**: `backend`.
3. Definir **Build Command**: `mvn -DskipTests package`.
4. Definir **Start Command**: `java -jar target/motorflow-api-0.0.1-SNAPSHOT.jar`.
5. Crear secretos/variables de entorno en Render: `DB_URL`, `DB_USER`, `DB_PASSWORD` y, si Render lo suministra, respetar `PORT`. `application.yml` lee `PORT` y usa `8082` como valor local de respaldo.
6. Configurar `DB_URL` con la URL TLS del MySQL de producción; no publicar un puerto Docker local ni `allowPublicKeyRetrieval=true` en Internet.
7. Desplegar y comprobar `GET /api/dashboard` con una cuenta válida. Cambiar HTTP Basic por JWT y HTTPS obligatorio antes de la liberación pública.

### 8.3 Base de datos: MySQL administrado

**Plataforma recomendada:** [DigitalOcean Managed MySQL](https://docs.digitalocean.com/products/databases/mysql/how-to/create/). Ofrece MySQL administrado, copias de seguridad y conexión cifrada. Para conectar la API, descargar el certificado CA y usar TLS, conforme a la [guía de conexión oficial](https://docs.digitalocean.com/products/databases/mysql/how-to/connect/).

Pasos:

1. Crear un clúster MySQL y una base de datos dedicada para MotorFlow.
2. Crear un usuario de aplicación con privilegios mínimos sobre esa base; no utilizar `root`.
3. Autorizar solo la red o IP del backend y obtener el host, puerto, usuario, contraseña y certificado CA.
4. Cargar esos valores como secretos en Render y activar TLS en la URL JDBC.
5. Ejecutar una migración controlada antes de producción. Mientras persista `ddl-auto: update`, probar las actualizaciones de esquema en staging antes de aplicarlas en producción.

## 9. Riesgos y siguientes acciones técnicas

1. Sustituir HTTP Basic por JWT con expiración, refresh tokens y cierre de sesión; no guardar contraseñas ni tokens en el cliente.
2. Implementar envío de recuperación mediante proveedor de correo y no exponer tokens en registros ni respuestas.
3. Limitar CORS a los dominios publicados y habilitar HTTPS, cabeceras de seguridad, rate limiting y auditoría.
4. Conectar el dashboard, recepción, inventario, cotizaciones, ganancias e inconformidades a sus endpoints REST reales.
5. Implementar CRUD completo para clientes, mecánicos, piezas, órdenes, cotizaciones y pagos.
6. Incorporar el cálculo persistente de ganancias por semana, quincena y mes.
7. Añadir un proveedor de notificaciones para la entrega del vehículo.
8. Introducir migraciones versionadas con Flyway o Liquibase, pruebas automatizadas, CI y monitoreo.

## 10. Criterio de cierre de la fase inicial

La fase se considera cerrada como base técnica y prototipo funcional: el backend compila, se conecta a MySQL, registra usuarios, verifica credenciales y aplica roles; el frontend ofrece login, registro y panel navegable. La siguiente fase debe concentrarse en sustituir datos de demostración por datos reales, completar CRUDs, robustecer seguridad y preparar despliegue productivo.
