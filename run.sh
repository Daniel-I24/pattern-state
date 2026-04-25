#!/bin/bash
if [ ! -f out/main/AppLauncher.class ]; then
    echo "El proyecto no está compilado. Ejecutando compile.sh..."
    ./compile.sh
    if [ $? -ne 0 ]; then exit 1; fi
fi
echo "Ejecutando aplicación..."
java -cp out main.AppLauncher
