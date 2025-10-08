# 📋 Test Cases Documentation - techcorp User Management Service

Esta documentación define los casos de prueba específicos para el sistema de autenticación y gestión de usuarios.

## 🧪 Casos de Prueba Definidos

### 📊 Resumen de Casos de Prueba

| **Total de Casos** | **Casos Positivos** | **Casos Negativos** |
|---------------------|---------------------|---------------------|
| 9                   | 3                   | 6                   |

---

## 📝 Detalle de Casos de Prueba

### 🔐 **Funcionalidad: Registro**

#### **TC001 - Registro Exitoso**
- **ID**: TC001
- **Tipo**: Positiva
- **Precondición**: Usuario no registrado
- **Datos de Entrada**: 
  - Nombre de usuario válido
  - Contraseña válida
- **Resultado Esperado**: 
  - HTTP 200 OK
  - Mensaje "Registro exitoso"

---

#### **TC002 - Registro con Nombre de Usuario Existente**
- **ID**: TC002
- **Tipo**: Negativa
- **Precondición**: Usuario ya registrado
- **Datos de Entrada**: 
  - Nombre duplicado
  - Contraseña válida
- **Resultado Esperado**: 
  - HTTP 409 Conflict
  - Mensaje "Nombre de usuario ya registrado"

---

#### **TC003 - Registro con Datos Inválidos**
- **ID**: TC003
- **Tipo**: Negativa
- **Precondición**: Usuario no registrado
- **Datos de Entrada**: 
  - Nombre vacío
  - Contraseña corta
- **Resultado Esperado**: 
  - HTTP 400 Bad Request
  - Error de validación

---

### 🔑 **Funcionalidad: Login**

#### **TC004 - Login Exitoso**
- **ID**: TC004
- **Tipo**: Positiva
- **Precondición**: Usuario registrado
- **Datos de Entrada**: 
  - Nombre y contraseña correctos
- **Resultado Esperado**: 
  - HTTP 200 OK
  - Token de autenticación

---

#### **TC005 - Login con Contraseña Incorrecta**
- **ID**: TC005
- **Tipo**: Negativa
- **Precondición**: Usuario registrado
- **Datos de Entrada**: 
  - Nombre válido
  - Contraseña incorrecta
- **Resultado Esperado**: 
  - HTTP 401 Unauthorized
  - Mensaje "Credenciales inválidas"

---

#### **TC006 - Login con Usuario Inexistente**
- **ID**: TC006
- **Tipo**: Negativa
- **Precondición**: Usuario no registrado
- **Datos de Entrada**: 
  - Nombre no registrado
  - Contraseña cualquiera
- **Resultado Esperado**: 
  - HTTP 404 Not Found
  - Mensaje "Usuario no encontrado"

---

### 🚪 **Funcionalidad: Logout**

#### **TC007 - Logout Exitoso**
- **ID**: TC007
- **Tipo**: Positiva
- **Precondición**: Usuario logueado
- **Datos de Entrada**: 
  - Token válido
- **Resultado Esperado**: 
  - HTTP 200 OK
  - Mensaje "Logout exitoso"

---

#### **TC008 - Logout con Token Inválido**
- **ID**: TC008
- **Tipo**: Negativa
- **Precondición**: Usuario logueado
- **Datos de Entrada**: 
  - Token mal formado o inexistente
- **Resultado Esperado**: 
  - HTTP 401 Unauthorized
  - Mensaje "Token inválido"

---

#### **TC009 - Logout sin Token**
- **ID**: TC009
- **Tipo**: Negativa
- **Precondición**: Usuario logueado
- **Datos de Entrada**: 
  - Sin encabezado Authorization
- **Resultado Esperado**: 
  - HTTP 400 Bad Request
  - Mensaje "Token requerido"

---

## 📊 Tabla Resumen Completa

| ID    | Funcionalidad | Caso de Prueba                             | Precondición          | Datos de Entrada                                | Resultado Esperado                                  | Tipo     |
|-------|---------------|--------------------------------------------|----------------------|------------------------------------------------|-----------------------------------------------------|----------|
| TC001 | Registro      | Registro exitoso                           | Usuario no registrado | Nombre de usuario válido, contraseña válida    | HTTP 200 OK, mensaje "Registro exitoso"             | Positiva |
| TC002 | Registro      | Registro con nombre de usuario existente  | Usuario ya registrado | Nombre duplicado, contraseña válida            | HTTP 409 Conflict, "Nombre de usuario ya registrado" | Negativa |
| TC003 | Registro      | Registro con datos inválidos              | Usuario no registrado | Nombre vacío, contraseña corta                 | HTTP 400 Bad Request, error de validación           | Negativa |
| TC004 | Login         | Login exitoso                              | Usuario registrado    | Nombre y contraseña correctos                  | HTTP 200 OK, token de autenticación                 | Positiva |
| TC005 | Login         | Login con contraseña incorrecta           | Usuario registrado    | Nombre válido, contraseña incorrecta           | HTTP 401 Unauthorized, "Credenciales inválidas"     | Negativa |
| TC006 | Login         | Login con usuario inexistente             | Usuario no registrado | Nombre no registrado, contraseña cualquiera    | HTTP 404 Not Found, "Usuario no encontrado"         | Negativa |
| TC007 | Logout        | Logout exitoso                             | Usuario logueado      | Token válido                                   | HTTP 200 OK, mensaje "Logout exitoso"               | Positiva |
| TC008 | Logout        | Logout con token inválido                 | Usuario logueado      | Token mal formado o inexistente                | HTTP 401 Unauthorized, "Token inválido"             | Negativa |
| TC009 | Logout        | Logout sin token                          | Usuario logueado      | Sin encabezado Authorization                   | HTTP 400 Bad Request, "Token requerido"             | Negativa |

---

## 🎯 Cobertura de Testing

### Por Funcionalidad:
- **Registro**: 3 casos (1 positivo, 2 negativos)
- **Login**: 3 casos (1 positivo, 2 negativos)  
- **Logout**: 3 casos (1 positivo, 2 negativos)

### Por Tipo:
- **Casos Positivos**: 33.3% (3/9)
- **Casos Negativos**: 66.7% (6/9)

### Por Códigos HTTP:
- **200 OK**: 3 casos
- **400 Bad Request**: 2 casos
- **401 Unauthorized**: 2 casos
- **404 Not Found**: 1 caso
- **409 Conflict**: 1 caso

---

## 📋 Notas de Implementación

### Responsabilidades por Clase de Test:
- **UserAuthenticationControllerTest**: TC001, TC002, TC003, TC004, TC005, TC006, TC007, TC008, TC009
- **AuthenticationServiceTest**: Lógica de validación para todos los casos
- **TokenGenerationServiceTest**: Validación de tokens para TC007, TC008, TC009

### Herramientas de Testing:
- **@WebMvcTest**: Para tests de controladores (TC001-TC009)
- **@MockBean**: Para mockear servicios en tests de controladores
- **@Test**: Para casos individuales
- **AssertJ**: Para validaciones de respuesta HTTP y mensajes

---

**Documentación generada siguiendo lineamientos techcorp - Solo casos de prueba especificados**