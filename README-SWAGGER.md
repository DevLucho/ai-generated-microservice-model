# 🔒 techcorp - Servicio de Gestión de Usuarios con Swagger/OpenAPI

Este proyecto implementa un servicio completo de autenticación y gestión de usuarios para techcorp con documentación API integral usando Swagger/OpenAPI 3.0.

## 📋 Tabla de Contenidos

- [Características](#características)
- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Documentación API](#documentación-api)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Endpoints API](#endpoints-api)
- [Swagger UI](#swagger-ui)
- [Seguridad](#seguridad)
- [Ejemplos de Uso](#ejemplos-de-uso)

## ✨ Características

### 🎯 Funcionalidades Core
- ✅ Registro de usuarios con validación completa
- ✅ Autenticación JWT con tokens seguros
- ✅ Gestión de sesiones de usuario
- ✅ Operaciones CRUD de usuarios
- ✅ Health checks y monitoreo del sistema
- ✅ Manejo global de errores estandarizado

### 📖 Documentación API
- ✅ Swagger UI interactivo completamente funcional
- ✅ Documentación OpenAPI 3.0 completa
- ✅ Ejemplos de request/response en cada endpoint
- ✅ Esquemas de validación documentados
- ✅ Seguridad JWT documentada
- ✅ Tags organizados por funcionalidad
- ✅ Respuestas de error detalladas

## 🛠️ Tecnologías

- **Java 17** - Lenguaje de programación (LTS)
- **Spring Boot 3.2.0** - Framework principal
- **Spring Security** - Seguridad y autenticación
- **JWT (jsonwebtoken)** - Tokens de autenticación
- **SpringDoc OpenAPI 3** - Documentación Swagger
- **Maven** - Gestión de dependencias
- **Tomcat** - Servidor web embebido

## 🏗️ Arquitectura

```
src/main/java/com/techcorp/authapp/
├── config/
│   ├── SwaggerConfiguration.java      # Configuración completa de OpenAPI
│   ├── SecurityConfiguration.java    # Configuración de seguridad
│   └── GlobalExceptionHandler.java   # Manejo global de errores
├── controller/
│   ├── UserAuthenticationController.java  # Endpoints de autenticación
│   ├── UserManagementController.java     # Endpoints de gestión
│   └── SystemController.java             # Endpoints de sistema
├── dto/
│   ├── ApiResponseDto.java               # DTO de respuesta estándar
│   ├── LoginRequestDto.java              # DTO de login
│   ├── UserRegistrationDto.java          # DTO de registro
│   └── ErrorResponseDto.java             # DTO de errores
├── model/
│   └── SystemUser.java                   # Modelo de usuario
├── repository/
│   └── InMemoryUserRepository.java       # Repositorio en memoria
├── service/
│   ├── AuthenticationService.java        # Lógica de autenticación
│   └── TokenGenerationService.java       # Generación de tokens
└── UserManagementApplication.java        # Clase principal
```

## 📖 Documentación API

### 🎯 Accesos Rápidos

| Recurso | URL | Descripción |
|---------|-----|-------------|
| **Swagger UI** | `http://localhost:8081/swagger-ui.html` | Interfaz interactiva de la API |
| **OpenAPI JSON** | `http://localhost:8081/api-docs` | Especificación OpenAPI en formato JSON |
| **Health Check** | `http://localhost:8081/api/system/health` | Estado del servicio |

### 🏷️ Tags de la API

1. **Autenticación** - Operaciones de login, registro y logout
2. **Gestión de Usuarios** - CRUD y administración de usuarios
3. **Sistema** - Health checks, información y estadísticas

### 🔐 Esquema de Seguridad

La API implementa autenticación JWT con el esquema:
```yaml
securitySchemes:
  Bearer Authentication:
    type: http
    scheme: bearer
    bearerFormat: JWT
```

## 🚀 Instalación

### Prerrequisitos
- Java 17+ instalado
- Maven 3.6+ instalado
- Puerto 8081 disponible

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone <repository-url>
cd techcorp-GHC-2
```

2. **Compilar el proyecto**
```bash
mvn clean compile
```

3. **Ejecutar la aplicación**
```bash
mvn spring-boot:run
```

4. **Verificar la instalación**
```bash
curl http://localhost:8081/api/system/health
```

## ⚙️ Configuración

### `application.properties`

```properties
# Configuración del servidor
server.port=8081
server.servlet.context-path=/

# Información de la aplicación
spring.application.name=user-management-service
app.version=1.0.0

# Configuración de Swagger/OpenAPI
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha

# Configuración JWT
app.jwt.secret=techcorp-secret-key-for-development-only
app.jwt.expiration=86400
```

### Personalización de Swagger

La configuración de Swagger incluye:
- **Información de la API**: Título, descripción, versión, contacto
- **Servidores**: Desarrollo, Testing, Producción
- **Seguridad**: Esquema JWT Bearer
- **Tags**: Organización por funcionalidad
- **Ejemplos**: Request/Response completos

## 🛡️ Endpoints API

### 🔐 Autenticación (`/api/auth`)

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|---------------|
| `POST` | `/register` | Registrar nuevo usuario | ❌ No |
| `POST` | `/login` | Autenticar usuario | ❌ No |
| `POST` | `/logout` | Cerrar sesión | ❌ No |

### 👥 Gestión de Usuarios (`/api/users`)

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|---------------|
| `GET` | `/` | Listar todos los usuarios | ✅ JWT |
| `GET` | `/{username}` | Obtener usuario específico | ✅ JWT |
| `PUT` | `/{username}/deactivate` | Desactivar usuario | ✅ JWT |

### 🏥 Sistema (`/api/system`)

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|---------------|
| `GET` | `/health` | Health check del servicio | ❌ No |
| `GET` | `/info` | Información del sistema | ❌ No |
| `GET` | `/stats` | Estadísticas de usuarios | ❌ No |
| `GET` | `/version` | Versión de la API | ❌ No |

## 🎨 Swagger UI

### Características de la Interfaz

- **🎯 Navegación intuitiva** con tags organizados
- **📝 Documentación completa** de cada endpoint
- **🧪 Testing interactivo** con formularios
- **📊 Ejemplos en vivo** de requests y responses
- **🔒 Autenticación JWT** integrada
- **📱 Responsive design** para móviles
- **🎨 Tema techcorp personalizado**

### Ejemplos de Uso en Swagger UI

1. **Registrar Usuario**
   - Navegar a `Autenticación > POST /api/auth/register`
   - Usar el ejemplo pre-cargado o personalizar
   - Ejecutar y ver la respuesta

2. **Autenticarse**
   - Usar `POST /api/auth/login` con las credenciales
   - Copiar el token de la respuesta
   - Usar el botón "Authorize" para configurar JWT

3. **Explorar Usuarios**
   - Con JWT configurado, probar `GET /api/users`
   - Ver la lista completa de usuarios

## 🔒 Seguridad

### Implementación JWT

```java
// Ejemplo de token JWT generado
{
  "username": "juan.perez",
  "authToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

### Headers de Seguridad

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

## 📋 Ejemplos de Uso

### 1. Registro de Usuario

```bash
curl -X POST "http://localhost:8081/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan.perez",
    "password": "password123",
    "emailAddress": "juan.perez@techcorp.com"
  }'
```

**Respuesta:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "userId": "USR-12345",
    "username": "juan.perez",
    "emailAddress": "juan.perez@techcorp.com",
    "registrationDate": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 2. Autenticación

```bash
curl -X POST "http://localhost:8081/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan.perez",
    "password": "password123"
  }'
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "username": "juan.perez",
    "authToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer"
  },
  "timestamp": "2024-01-15T10:31:00Z"
}
```

### 3. Listar Usuarios (con JWT)

```bash
curl -X GET "http://localhost:8081/api/users" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 4. Health Check

```bash
curl -X GET "http://localhost:8081/api/system/health"
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Service is healthy and running",
  "data": {
    "status": "UP",
    "service": "user-management-service",
    "version": "1.0.0",
    "timestamp": "2024-01-15T12:00:00"
  },
  "timestamp": "2024-01-15T12:00:00Z"
}
```

## 🎯 Características Avanzadas de Swagger

### 1. Validaciones Documentadas
- **Campos requeridos** claramente marcados
- **Formatos específicos** (email, password)
- **Rangos de longitud** para strings
- **Patrones de validación** documentados

### 2. Respuestas de Error Estructuradas
```json
{
  "statusCode": 400,
  "error": "Validation Failed",
  "message": "Username must be between 3 and 50 characters",
  "path": "/api/auth/register",
  "timestamp": "2024-01-15T10:30:00",
  "errorId": "ERR-20240115-001"
}
```

### 3. Ejemplos Interactivos
- **Datos de prueba** pre-cargados
- **Múltiples escenarios** de uso
- **Respuestas de éxito y error**

### 4. Documentación de Modelos
- **Esquemas JSON** completos
- **Descripciones detalladas** de cada campo
- **Ejemplos de valores** esperados

## 🔧 Desarrollo y Contribución

### Estructura de Anotaciones Swagger

```java
@Operation(
    summary = "Breve descripción",
    description = "Descripción detallada del endpoint",
    tags = {"Categoría"}
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Éxito",
        content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
    @ApiResponse(responseCode = "400", description = "Error de validación")
})
```

### Mejores Prácticas Implementadas

1. **📝 Documentación Completa**: Cada endpoint tiene descripción detallada
2. **🏷️ Organización por Tags**: Agrupación lógica de endpoints
3. **🔒 Seguridad Integrada**: JWT documentado y funcional
4. **📊 Ejemplos Realistas**: Datos de ejemplo representativos
5. **🎯 Validaciones Claras**: Reglas de negocio documentadas
6. **🚨 Manejo de Errores**: Respuestas de error estandarizadas

## 📞 Soporte y Contacto

- **Email**: desarrollo@techcorp.com
- **Equipo**: Desarrollo techcorp
- **Documentación**: Disponible en Swagger UI
- **Versión**: 1.0.0

---

## 🎉 ¡Implementación Completa!

✅ **Swagger UI funcionando completamente**  
✅ **Documentación OpenAPI 3.0 completa**  
✅ **Seguridad JWT integrada**  
✅ **Ejemplos interactivos funcionales**  
✅ **Cumple con estándares techcorp**  

**Accede a la documentación en:** `http://localhost:8081/swagger-ui.html`