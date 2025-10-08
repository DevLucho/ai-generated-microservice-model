# 🧪 Testing Structure - TechCorp User Management Service

Esta documentación describe la estructura de testing implementada siguiendo los lineamientos TechCorp para el proyecto.

## 📂 Estructura de Carpetas

```
src/test/
├── java/
│   └── com/TechCorp/authapp/
│       ├── controller/       # Tests de controladores REST (@WebMvcTest)
│       ├── service/          # Tests unitarios de lógica de negocio
│       ├── dto/              # Tests de validación de DTOs
│       ├── model/            # Tests de entidades del dominio
│       ├── repository/       # Tests de acceso a datos
│       ├── config/           # Tests de configuraciones
│       └── integration/      # Tests de integración (@SpringBootTest)
└── resources/
    └── application-test.properties   # Configuración para entorno de testing
```

## 🛠️ Tecnologías de Testing Configuradas

### Frameworks Principales
- **JUnit 5.10.0** - Framework de testing obligatorio según TechCorp
- **Mockito 5.5.0** - Para mocks y stubs
- **AssertJ 3.24.2** - Para aserciones avanzadas
- **Spring Boot Test** - Testing de integración
- **Spring Security Test** - Testing de seguridad

### Herramientas de Cobertura
- **Jacoco 0.8.10** - Medición de cobertura (umbral mínimo 80%)
- **TestContainers 1.19.0** - Tests de integración con contenedores

## 📋 Convenciones de Naming

### Clases de Test
```java
// Para la clase UserService, crear:
UserServiceTest.java

// Para la clase UserController, crear:
UserControllerTest.java
```

### Métodos de Test
```java
@Test
void testMethodName_WhenCondition_ThenExpectedResult() {
    // Implementación del test
}
```

## 🧪 Tipos de Tests por Carpeta

### 1. controller/ - Tests de Controladores
```java
@WebMvcTest(UserAuthenticationController.class)
class UserAuthenticationControllerTest {
    // Tests de endpoints REST
}
```

### 2. service/ - Tests Unitarios de Servicios
```java
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    // Tests de lógica de negocio
}
```

### 3. repository/ - Tests de Repositorios
```java
@DataJpaTest
class UserRepositoryTest {
    // Tests de acceso a datos
}
```

### 4. integration/ - Tests de Integración
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserManagementIntegrationTest {
    // Tests end-to-end
}
```

## 🔧 Configuración de Testing

### application-test.properties
- Configuración específica para entorno de testing
- Base de datos H2 en memoria
- Logging debug habilitado
- JWT con claves de testing

### Jacoco Configuration
- Umbral mínimo de cobertura: 80%
- Exclusiones: Application classes, DTOs, Config classes
- Reportes automáticos en `target/site/jacoco/`

## 📊 Quality Gates TechCorp

| Métrica | Umbral | Herramienta |
|---------|--------|-------------|
| Cobertura de pruebas | ≥ 80% | Jacoco |
| Duplicación de código | ≤ 3% | SonarQube |
| Mantenibilidad | Ratio A | SonarQube |
| Fiabilidad | Ratio A | SonarQube |
| Seguridad | Ratio A | SonarQube |

## 🚀 Comandos Útiles

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests con reporte de cobertura
mvn clean test jacoco:report

# Ejecutar solo tests unitarios
mvn test -Dtest="**/*Test"

# Ejecutar solo tests de integración
mvn test -Dtest="**/*IntegrationTest"

# Ver reporte de cobertura
open target/site/jacoco/index.html
```

## 📝 Estructura de Test Recomendada

```java
@Test
void testGetUserNameReturnsCorrectName() {
    // Arrange: se configura el mock y los datos de entrada
    when(userRepository.findById(1)).thenReturn(new User("Alice"));
    
    // Act: se ejecuta el método a probar
    String result = userService.getUserName(1);
    
    // Assert con JUnit: validación básica
    assertEquals("Alice", result);
    
    // Assert con AssertJ: validaciones adicionales
    assertThat(result).isEqualTo("Alice").isNotEmpty();
}
```

## ⚠️ Notas Importantes

- **Nomenclatura**: Clases de test deben terminar en `Test`
- **Idioma**: Nombres de métodos en inglés
- **Cobertura**: Cubrir casos positivos, negativos y de borde
- **Mocks**: Usar Mockito para dependencias externas
- **Aserciones**: Preferir AssertJ sobre JUnit assertions

---

**Documentación generada automáticamente por GitHub Copilot siguiendo lineamientos TechCorp**
