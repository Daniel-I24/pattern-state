#!/bin/bash
echo "Compilando proyecto..."
javac -encoding UTF-8 -d out src/auth/*.java src/model/*.java src/state/task/*.java src/state/project/*.java src/service/*.java src/ui/*.java src/ui/shared/*.java src/ui/manager/*.java src/ui/developer/*.java src/ui/client/*.java src/main/*.java
if [ $? -eq 0 ]; then
    echo "Compilación exitosa!"
else
    echo "Error en la compilación."
    exit 1
fi
