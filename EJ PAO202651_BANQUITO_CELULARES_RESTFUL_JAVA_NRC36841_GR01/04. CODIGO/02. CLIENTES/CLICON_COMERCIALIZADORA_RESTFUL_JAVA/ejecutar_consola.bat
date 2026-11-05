@echo off
REM Ejecuta el cliente de consola con colores y UTF-8 (Windows Terminal / CMD)
chcp 65001 > nul
cd /d "%~dp0"
if not exist target\CLICON_COMERCIALIZADORA_RESTFUL_JAVA.jar (
  echo Primero compile el proyecto en NetBeans: clic derecho - Clean and Build
  pause
  exit /b
)
java -Dstdout.encoding=UTF-8 -jar target\CLICON_COMERCIALIZADORA_RESTFUL_JAVA.jar %*
pause
