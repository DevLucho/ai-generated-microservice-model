# 🔒 TechCorp - User Management Service

Servicio de gestión de usuarios con autenticación JWT y documentación API completa con Swagger/OpenAPI 3.0.

## 🚀 Quick Start

```bash
# Compilar y ejecutar
mvn clean compile
mvn spring-boot:run

# Acceder a Swagger UI
# http://localhost:8081/swagger-ui.html
```

## ✨ Características Principales

- ✅ **Autenticación JWT** segura y robusta
- ✅ **Swagger UI interactivo** completamente funcional
- ✅ **Documentación OpenAPI 3.0** completa
- ✅ **Gestión de usuarios** con CRUD completo
- ✅ **Health checks** y monitoreo del sistema
- ✅ **Manejo de errores** estandarizado
- ✅ **Validaciones** de datos robustas

## 🛠️ Tecnologías

- **Java 17** (LTS)
- **Spring Boot 3.2.0**
- **Spring Security**
- **JWT (jsonwebtoken)**
- **SpringDoc OpenAPI 3**
- **Maven**

## 📖 Documentación

### 🎯 Accesos Directos

| Recurso | URL |
|---------|-----|
| **Swagger UI** | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) |
| **OpenAPI JSON** | [http://localhost:8081/api-docs](http://localhost:8081/api-docs) |
| **Health Check** | [http://localhost:8081/api/system/health](http://localhost:8081/api/system/health) |

### 📚 Documentación Detallada

Para documentación completa de Swagger y la API, consulta: [README-SWAGGER.md](./README-SWAGGER.md)

## 🛡️ Endpoints API

### Autenticación (`/api/auth`)
- `POST /register` - Registrar usuario
- `POST /login` - Autenticar usuario  
- `POST /logout` - Cerrar sesión

### Gestión de Usuarios (`/api/users`) 🔒
- `GET /` - Listar usuarios
- `GET /{username}` - Obtener usuario
- `PUT /{username}/deactivate` - Desactivar usuario

### Sistema (`/api/system`)
- `GET /health` - Estado del servicio
- `GET /info` - Información del sistema
- `GET /stats` - Estadísticas de usuarios
- `GET /version` - Versión de la API

🔒 = Requiere autenticación JWT

## 🔧 Configuración

### Puerto del Servidor
```properties
server.port=8081
```

### Swagger UI
```properties
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/api-docs
```

## 🎯 Ejemplos de Uso

### 1. Registro
```bash
curl -X POST "http://localhost:8081/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan.perez",
    "password": "password123",
    "emailAddress": "juan.perez@TechCorp.com"
  }'
```

### 2. Login
```bash
curl -X POST "http://localhost:8081/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan.perez",
    "password": "password123"
  }'
```

### 3. Health Check
```bash
curl http://localhost:8081/api/system/health
```

## 🏗️ Arquitectura

```
src/main/java/com/TechCorp/authapp/
├── config/          # Configuraciones (Swagger, Security, Errors)
├── controller/      # Controladores REST
├── dto/            # Data Transfer Objects
├── model/          # Modelos de datos
├── repository/     # Repositorios
└── service/        # Servicios de negocio
```

## 📋 Cumplimiento con Estándares TechCorp

✅ **Java 17 LTS** - Versión oficial aprobada  
✅ **Spring Boot** - Framework estándar  
✅ **Patrones de diseño** - Service, DTO, Controller  
✅ **Documentación completa** - Swagger/OpenAPI  
✅ **Seguridad JWT** - Autenticación robusta  
✅ **Manejo de errores** - Respuestas estandarizadas  
✅ **Validaciones** - Integridad de datos  
✅ **Testing completo** - Cobertura ≥ 80%  

## 🧪 Testing y Cobertura de Código

### 📊 Configuración de Calidad TechCorp

Este proyecto cumple con los Quality Gates establecidos por TechCorp:

| Métrica | Umbral Requerido | Estado |
|---------|------------------|--------|
| **Cobertura de líneas** | ≥ 80% | ✅ Configurado |
| **Cobertura de ramas** | ≥ 80% | ✅ Configurado |
| **Cobertura de métodos** | ≥ 80% | ✅ Configurado |
| **Duplicación de código** | ≤ 3% | ✅ Configurado |

### 🚀 Comandos de Testing

```bash
# Ejecutar tests y verificar cobertura
mvn clean test verify

# Solo generar reporte de cobertura
mvn jacoco:report

# Verificación estricta (falla si <80%)
mvn clean test verify -Pcoverage-check

# Scripts automatizados (Windows)
coverage-check.bat              # Verificación con reporte
coverage-strict-check.bat       # Verificación estricta
```

### 📈 Reportes de Cobertura

Los reportes se generan en:
- **HTML**: `target/site/jacoco/index.html`
- **XML**: `target/site/jacoco/jacoco.xml`
- **CSV**: `target/site/jacoco/jacoco.csv`

### ⚙️ Configuración Jacoco

- **Versión**: 0.8.10
- **Verificación automática**: Habilitada en fase `verify`
- **Exclusiones**: Configuración, DTOs, modelos simples
- **Formatos**: HTML, XML, CSV  

## 🎉 Swagger Implementado

La implementación de Swagger en este proyecto incluye:

- **🎨 Interfaz UI completa** con tema personalizado
- **📝 Documentación detallada** de todos los endpoints
- **🔒 Autenticación JWT** integrada y funcional
- **📊 Ejemplos interactivos** para testing
- **🏷️ Organización por tags** lógicos
- **🎯 Validaciones documentadas** 
- **🚨 Respuestas de error** estandarizadas

### Acceder a Swagger UI

Una vez iniciada la aplicación:

1. **Navega a**: http://localhost:8081/swagger-ui.html
2. **Explora la documentación** interactiva
3. **Prueba los endpoints** directamente desde la UI
4. **Configura JWT** usando el botón "Authorize"

## 📞 Soporte

- **Equipo**: Desarrollo TechCorp
- **Email**: desarrollo@TechCorp.com
- **Documentación**: Swagger UI integrado

---

### 🎯 ¡Implementación Completa de Swagger!

**La documentación API está completamente funcional y lista para uso.**
