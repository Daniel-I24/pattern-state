@echo off
if not exist out\main\AppLauncher.class (
    echo El proyecto no esta compilado. Ejecutando compile.bat...
    call compile.bat
    if %errorlevel% neq 0 exit /b 1
)
echo Ejecutando aplicacion...
java -cp out main.AppLauncher
