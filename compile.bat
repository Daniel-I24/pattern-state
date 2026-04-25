@echo off
echo Compilando proyecto...
javac -encoding UTF-8 -d out src/auth/*.java src/model/*.java src/state/task/*.java src/state/project/*.java src/service/*.java src/ui/*.java src/ui/shared/*.java src/ui/manager/*.java src/ui/developer/*.java src/ui/client/*.java src/main/*.java
if %errorlevel% == 0 (
    echo Compilacion exitosa!
) else (
    echo Error en la compilacion.
    pause
    exit /b 1
)
