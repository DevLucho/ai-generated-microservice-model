@echo off
REM Script para verificar cobertura de código según lineamientos techcorp
REM Cobertura mínima requerida: 80%

echo ==========================================
echo  VERIFICACIÓN DE COBERTURA techcorp - 80%
echo ==========================================
echo.

echo [INFO] Ejecutando tests y generando reporte de cobertura...
call mvn clean test jacoco:report

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Falló la ejecución de tests
    exit /b 1
)

echo.
echo [INFO] Verificando umbrales de cobertura...
call mvn jacoco:check

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [WARNING] ===== COBERTURA INSUFICIENTE =====
    echo La cobertura actual no cumple el mínimo del 80%
    echo.
    echo Revisa el reporte en: target\site\jacoco\index.html
    echo.
    echo [ACTIONS] Necesitas:
    echo  1. Agregar más tests unitarios
    echo  2. Asegurar cobertura de líneas ≥ 80%
    echo  3. Asegurar cobertura de ramas ≥ 80%
    echo  4. Asegurar cobertura de métodos ≥ 80%
    echo.
    echo [SKIP] Para continuar sin verificar: mvn package -DskipTests
    echo.
    pause
    exit /b 1
) else (
    echo.
    echo [SUCCESS] ===== COBERTURA APROBADA =====
    echo ✅ Cobertura cumple el estándar techcorp del 80%
    echo ✅ Todas las verificaciones de calidad pasaron
    echo.
    echo Reporte disponible en: target\site\jacoco\index.html
    echo.
)

echo [INFO] Proceso completado.
pause