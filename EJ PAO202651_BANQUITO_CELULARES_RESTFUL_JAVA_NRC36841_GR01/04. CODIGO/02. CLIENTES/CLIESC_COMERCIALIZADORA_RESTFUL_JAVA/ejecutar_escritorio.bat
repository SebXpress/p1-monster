@echo off
cd /d "%~dp0"
if not exist target\CLIESC_COMERCIALIZADORA_RESTFUL_JAVA.jar (
  echo Primero compile el proyecto en NetBeans: clic derecho - Clean and Build
  pause
  exit /b
)
start "" javaw -jar target\CLIESC_COMERCIALIZADORA_RESTFUL_JAVA.jar %*
