# 📊 Configuración de Cobertura Jacoco - techcorp

Este documento describe la configuración completa de Jacoco para verificación de cobertura de código según los lineamientos techcorp.

## 🎯 Objetivos de Calidad

Según los estándares techcorp, el proyecto debe cumplir con los siguientes Quality Gates:

| Métrica | Umbral Mínimo | Configurado |
|---------|---------------|-------------|
| **Cobertura de líneas** | ≥ 80% | ✅ |
| **Cobertura de instrucciones** | ≥ 80% | ✅ |
| **Cobertura de ramas** | ≥ 80% | ✅ |
| **Cobertura de métodos** | ≥ 80% | ✅ |
| **Cobertura de clases** | ≥ 80% | ✅ |
| **Duplicación de código** | ≤ 3% | ⏳ |
| **Mantenibilidad** | Ratio A | ⏳ |
| **Fiabilidad** | Ratio A | ⏳ |
| **Seguridad** | Ratio A | ⏳ |

## ⚙️ Configuración Técnica

### 🔧 Plugin Jacoco en pom.xml

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <!-- Preparar agente para tests unitarios -->
        <execution>
            <id>prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        
        <!-- Generar reporte de cobertura -->
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        
        <!-- Verificar cobertura mínima -->
        <execution>
            <id>check-coverage</id>
            <phase>verify</phase>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                            <!-- Más counters... -->
                        </limits>
                    </rule>
                </rules>
                <haltOnFailure>true</haltOnFailure>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 📝 Propiedades Configuradas

```xml
<properties>
    <jacoco.version>0.8.10</jacoco.version>
    <jacoco.minimum.coverage>0.80</jacoco.minimum.coverage>
</properties>
```

## 🚫 Exclusiones

Las siguientes clases están excluidas del análisis de cobertura según lineamientos techcorp:

### ✅ Clases Excluidas

- **Aplicación principal**: `**/Application.class`, `**/UserManagementApplication.class`
- **Configuración**: `**/config/**` (SecurityConfiguration, SwaggerConfiguration, etc.)
- **DTOs**: `**/dto/**` (Solo transferencia de datos)
- **Modelos simples**: `**/model/User.class`, `**/model/SystemUser.class`

### 🎯 Clases Incluidas (Requieren cobertura ≥80%)

- **Controladores**: `UserAuthenticationController.class`
- **Servicios**: `AuthenticationService.class`, `TokenGenerationService.class`
- **Repositorios**: `InMemoryUserRepository.class`

## 🚀 Comandos de Ejecución

### 📊 Comandos Maven

```bash
# Ejecutar tests y generar reporte
mvn clean test jacoco:report

# Verificar cobertura (falla si <80%)
mvn clean test verify

# Perfil estricto
mvn clean test verify -Pcoverage-check

# Solo generar reporte sin tests
mvn jacoco:report
```

### 🖥️ Scripts Automatizados (Windows)

```bash
# Verificación con reporte detallado
coverage-check.bat

# Verificación estricta
coverage-strict-check.bat
```

## 📈 Reportes Generados

### 📁 Ubicación de Reportes

- **HTML Interactivo**: `target/site/jacoco/index.html`
- **XML para CI/CD**: `target/site/jacoco/jacoco.xml`
- **CSV para análisis**: `target/site/jacoco/jacoco.csv`

### 🎨 Formato HTML

El reporte HTML incluye:

- **Resumen general** de cobertura por paquete
- **Detalles por clase** con líneas cubiertas/no cubiertas
- **Navegación interactiva** por código fuente
- **Métricas detalladas** por método
- **Código coloreado** (verde=cubierto, rojo=no cubierto)

## 🔍 Interpretación de Métricas

### 📊 Counters Jacoco

| Counter | Descripción | Umbral |
|---------|-------------|--------|
| **INSTRUCTION** | Instrucciones bytecode ejecutadas | ≥ 80% |
| **BRANCH** | Ramas condicionales ejecutadas | ≥ 80% |
| **LINE** | Líneas de código ejecutadas | ≥ 80% |
| **METHOD** | Métodos ejecutados | ≥ 80% |
| **CLASS** | Clases con al menos un método ejecutado | ≥ 80% |

### 🎯 Ejemplo de Análisis

```
ANALYSIS: AuthenticationService
├── Lines: 20/25 (80%) ✅ PASSED
├── Branches: 6/8 (75%) ❌ FAILED - Need 2 more branches
├── Methods: 4/5 (80%) ✅ PASSED
└── Instructions: 45/50 (90%) ✅ PASSED

ACTION REQUIRED: Add tests for missing branch scenarios
```

## 🚨 Manejo de Fallos

### ❌ Cuando Falla la Verificación

```
[WARNING] Rule violated for bundle user-management-service: 
lines covered ratio is 0.75, but expected minimum is 0.80

[ERROR] Coverage checks have not been met. See log for details.
```

### 🔧 Acciones Correctivas

1. **Revisar reporte HTML**: `target/site/jacoco/index.html`
2. **Identificar líneas no cubiertas** (marcadas en rojo)
3. **Agregar tests unitarios** para casos faltantes
4. **Validar cobertura de ramas** (if/else, switch, try/catch)
5. **Re-ejecutar verificación**

## 🏗️ Integración CI/CD

### 🔄 Pipeline Recomendado

```yaml
# Ejemplo para Azure DevOps
- task: Maven@3
  displayName: 'Test with Coverage'
  inputs:
    mavenPomFile: 'pom.xml'
    goals: 'clean test verify'
    options: '-Pcoverage-check'
    
- task: PublishCodeCoverageResults@1
  inputs:
    codeCoverageTool: 'JaCoCo'
    summaryFileLocation: 'target/site/jacoco/jacoco.xml'
    reportDirectory: 'target/site/jacoco'
```

## 📋 Checklist de Calidad

### ✅ Pre-Commit

- [ ] Tests ejecutan exitosamente
- [ ] Cobertura ≥ 80% en todas las métricas
- [ ] No hay warnings de reglas violadas
- [ ] Reporte HTML generado correctamente

### ✅ Pre-Deploy

- [ ] Perfil `coverage-check` aprobado
- [ ] Jacoco XML disponible para CI/CD
- [ ] Documentación actualizada
- [ ] Scripts de verificación funcionales

## 🎓 Mejores Prácticas

### 📝 Testing Efectivo

1. **Cobertura ≠ Calidad**: 80% cobertura no garantiza tests de calidad
2. **Test casos de borde**: if/else, excepciones, validaciones
3. **Nombres descriptivos**: `testRegisterUser_WhenValidData_ReturnsSuccess()`
4. **Arrange-Act-Assert**: Estructura clara en tests
5. **Mock dependencies**: Aislar unidad bajo prueba

### 🎯 Optimización

1. **Excluir código generado**: Configuraciones, DTOs simples
2. **Medir lo importante**: Lógica de negocio, servicios, controladores
3. **Balance coverage/mantenimiento**: No sobre-testear código trivial
4. **Reportes regulares**: Monitoreo continuo de tendencias

---

## 📞 Soporte

Para dudas sobre configuración de cobertura:

- **Lineamientos techcorp**: Ver `copilot-instructions.md`
- **Documentación Jacoco**: https://www.jacoco.org/jacoco/
- **Quality Gates**: Configuración en SonarQube/proceso CI/CD

---

### 🎯 ¡Configuración Completa!

**La verificación de cobertura está configurada y lista para garantizar la calidad del código según estándares techcorp.**