# MotorFlow — Gestión de Taller Mecánico

Aplicación con frontend Vue 3 + Bootstrap y API RESTful Spring Boot. El panel incluye recepción, seguimiento de órdenes, asignación de mecánicos, condiciones iniciales, inconformidades, inventario, ganancias, cotización, entrega y una base de control de acceso por roles.

## Ver el frontend

```bash
cd frontend
python3 -m http.server 4173
```

Abrir `http://localhost:4173`. Es una maqueta navegable con datos de ejemplo y el formulario de recepción.

## Ejecutar la API contra MySQL en Docker

Configura el puerto publicado por tu contenedor y las credenciales; por ejemplo:

```bash
cd backend
DB_URL='jdbc:mysql://localhost:3307/springdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Mexico_City' DB_USER=tu_usuario DB_PASSWORD=tu_clave mvn spring-boot:run
```

También puedes importarlo como proyecto Maven y ejecutarlo desde tu IDE. La API expone `/api/orders`, `/api/dashboard` y `/api/auth` (registro, solicitud de recuperación y restablecimiento). Las contraseñas se persisten exclusivamente mediante BCrypt; la configuración define permisos para ADMINISTRADOR, SECRETARIA, MECANICO y CLIENTE. Para producción, sustituye HTTP Basic por JWT de corta duración, agrega vencimiento y almacenamiento hasheado al token de recuperación, envío real de correo y conserva los secretos fuera del repositorio.
