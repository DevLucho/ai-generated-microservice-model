@echo off
REM Script para verificación estricta de cobertura con perfil coverage-check

echo ==========================================
echo  VERIFICACIÓN ESTRICTA - PERFIL COVERAGE
echo ==========================================
echo.

echo [INFO] Ejecutando con perfil coverage-check...
call mvn clean test verify -Pcoverage-check

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCCESS] ===== VERIFICACIÓN EXITOSA =====
    echo ✅ Cobertura cumple estándares techcorp
    echo ✅ Perfil coverage-check aprobado
    echo.
) else (
    echo.
    echo [FAILURE] ===== VERIFICACIÓN FALLIDA =====
    echo ❌ La cobertura no cumple el 80% mínimo
    echo ❌ Revisar target\site\jacoco\index.html
    echo.
)

pause